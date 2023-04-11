package com.hazard157.psx24.core.e4.uiparts.eps;

import static com.hazard157.psx.common.IPsxHardConstants.*;
import static com.hazard157.psx24.core.IPsxAppActions.*;
import static com.hazard157.psx24.core.e4.uiparts.eps.IPsxResources.*;
import static org.toxsoft.core.tsgui.bricks.actions.ITsStdActionDefs.*;

import javax.inject.*;

import org.eclipse.swt.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.toxsoft.core.tsgui.bricks.ctx.impl.*;
import org.toxsoft.core.tsgui.bricks.tsnodes.*;
import org.toxsoft.core.tsgui.bricks.tstree.*;
import org.toxsoft.core.tsgui.bricks.tstree.impl.*;
import org.toxsoft.core.tsgui.bricks.uievents.*;
import org.toxsoft.core.tsgui.dialogs.*;
import org.toxsoft.core.tsgui.graphics.*;
import org.toxsoft.core.tsgui.graphics.icons.*;
import org.toxsoft.core.tsgui.mws.services.e4helper.*;
import org.toxsoft.core.tsgui.panels.toolbar.*;
import org.toxsoft.core.tsgui.utils.layout.*;
import org.toxsoft.core.tsgui.widgets.*;
import org.toxsoft.core.tslib.bricks.events.change.*;
import org.toxsoft.core.tslib.bricks.strid.*;
import org.toxsoft.core.tslib.bricks.strid.impl.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.impl.*;
import org.toxsoft.core.tslib.coll.primtypes.*;
import org.toxsoft.core.tslib.coll.primtypes.impl.*;
import org.toxsoft.core.tslib.utils.*;
import org.toxsoft.core.tslib.utils.errors.*;

import com.hazard157.lib.core.quants.secint.*;
import com.hazard157.lib.core.utils.animkind.*;
import com.hazard157.psx.common.stuff.frame.*;
import com.hazard157.psx.common.stuff.fsc.*;
import com.hazard157.psx.common.stuff.svin.*;
import com.hazard157.psx.proj3.episodes.*;
import com.hazard157.psx.proj3.episodes.story.*;
import com.hazard157.psx.proj3.tags.*;
import com.hazard157.psx24.core.e4.services.currframeslist.*;
import com.hazard157.psx24.core.e4.services.filesys.*;
import com.hazard157.psx24.core.e4.services.playmenu.*;
import com.hazard157.psx24.core.e4.services.prisex.*;
import com.hazard157.psx24.core.e4.services.selsvins.*;
import com.hazard157.psx24.core.glib.tagint.*;
import com.hazard157.psx24.core.glib.tagline.*;
import com.hazard157.psx24.core.legacy.*;

/**
 * Вью промотра и правки {@link IEpisode#tagLine()}.
 *
 * @author hazard157
 */
public class UipartEpisodeTags
    extends AbstractEpisodeUipart
    implements ITsKeyInputListener {

  @Inject
  ICurrentFramesListService currentFramesListService;

  @Inject
  IPsxSelectedSvinsService selectedSvinsService;

  @Inject
  IUnitTags unitTags;

  /**
   * Слушатель обновления содержимого.
   * <p>
   * Устанавливается на текущий теглайн и на менеджер ярлыков.
   */
  private final IGenericChangeListener tagRelatedChangeListener = aSource -> refreshView();

  // TODO добавить keyDownListener, например, для INS = ADD

  TsToolbar              toolbar;
  ITsTreeViewer          treeViewer;
  TextFilterContribution filterContribution;

  /**
   * Нижняя строка с описанием выбранного ярылка/группы.
   */
  Text txtTagDecription = null;

  /**
   * Нижняя строка со перечнем использованных интервалов выбранного ярлыка/группы.
   */
  Text txtTagSecline = null;

  /**
   * Нижняя строка со перечнем НЕ-использованных интервалов (пропусков) выбранного ярлыка/группы.
   */
  Text txtTagGaps = null;

  /**
   * Конструктор.
   */
  public UipartEpisodeTags() {
    // nop
  }

  @Override
  protected void doCreatePartContent( Composite aParent ) {
    TsComposite board = new TsComposite( aParent );
    board.setLayout( new BorderLayout() );
    // toolbar
    toolbar = TsToolbar.create( board, tsContext(), EIconSize.IS_16X16, //
        ACDEF_ADD, ACDEF_EDIT, ACDEF_REMOVE, ACDEF_SEPARATOR, //
        AI_PLAY_MENU, ACDEF_SEPARATOR, ACDEF_COLLAPSE_ALL, ACDEF_EXPAND_ALL, ACDEF_SEPARATOR, //
        AI_SHOW_TAGLINE_AS_LIST, AI_INCLUDE_ONLY_USED_TAGS, ACDEF_SEPARATOR //
    );
    toolbar.getControl().setLayoutData( BorderLayout.NORTH );
    filterContribution = new TextFilterContribution( toolbar );
    filterContribution.genericChangeEventer().addListener( aSource -> refreshView() );
    toolbar.addListener( this::processAction );
    // treeVider
    treeViewer = new TsTreeViewer( new TsGuiContext( tsContext() ) );
    treeViewer.createControl( board );
    treeViewer.addTsKeyInputListener( this );
    ITsViewerColumn col1 = treeViewer.addColumn( STR_H_NAME, EHorAlignment.LEFT, aItem -> {
      if( aItem.entity() instanceof ITag ) {
        ITag tag = (ITag)aItem.entity();
        boolean modeAsList = toolbar.isActionChecked( AID_SHOW_TAGLINE_AS_LIST );
        if( modeAsList ) {
          return tag.id();
        }
        return tag.nmName();
      }
      // return aItem.entity().toString();
      return aItem.name();
    } );
    col1.setWidth( 220 );
    treeViewer.addColumn( STR_H_DESCRIPTION, EHorAlignment.LEFT, aItem -> {
      if( aItem.entity() instanceof ITag ) {
        ITag tag = (ITag)aItem.entity();
        return tag.description();
      }
      return TsLibUtils.EMPTY_STRING;
    } );
    treeViewer.getControl().setLayoutData( BorderLayout.CENTER );
    treeViewer.addTsSelectionListener( ( aSource, aSelectedItem ) -> {
      addPlayMenu( aSelectedItem );
      updateFramesListFromView( aSelectedItem );
      updateActionsState();
    } );
    treeViewer.addTsDoubleClickListener( ( aSource, aSelectedItem ) -> processAction( ACTID_EDIT ) );
    // details pane
    TsComposite detailtsPane = new TsComposite( board );
    detailtsPane.setLayoutData( BorderLayout.SOUTH );
    detailtsPane.setLayout( new GridLayout( 2, false ) );
    Label l = new Label( detailtsPane, SWT.LEFT );
    l.setText( STR_L_TAG_DESCRIPTION );
    txtTagDecription = new Text( detailtsPane, SWT.BORDER );
    txtTagDecription.setEnabled( false );
    txtTagDecription.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
    l = new Label( detailtsPane, SWT.LEFT );
    l.setText( STR_L_TAG_SECLINE );
    txtTagSecline = new Text( detailtsPane, SWT.BORDER );
    txtTagSecline.setEnabled( false );
    txtTagSecline.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
    l = new Label( detailtsPane, SWT.LEFT );
    l.setText( STR_L_TAG_GAPS );
    txtTagGaps = new Text( detailtsPane, SWT.BORDER );
    txtTagGaps.setEnabled( false );
    txtTagGaps.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
    // setup
    unitTags.genericChangeEventer().addListener( tagRelatedChangeListener );
    updateActionsState();
  }

  @Override
  protected void doSetEpisode() {
    refreshView();
  }

  @Override
  protected void doHandleEpisodeContentChange() {
    doSetEpisode();
  }

  /**
   * Обновляет выпадающее меню кнопки вопроизведения на панели инструметнов.
   * <p>
   * Должен вызываться каждый раз, когда меняется выделение в дереве.
   *
   * @param aSelectedItem {@link ITsNode} - текущий узел дерева, может быть null
   */
  void addPlayMenu( ITsNode aSelectedItem ) {
    IPlayMenuSupport pms = tsContext().get( IPlayMenuSupport.class );
    toolbar.setActionMenu( AID_PLAY, pms.getPlayMenuCreator( tsContext(), new IPlayMenuParamsProvider() {

      @Override
      public Svin playParams() {
        return getItemSvinOrNull( aSelectedItem );
      }

      @Override
      public int spotlightSec() {
        return -1;
      }
    } ) );
  }

  void updateFramesListFromView( ITsNode aNode ) {
    if( aNode == null || episode() == null ) {
      currentFramesListService.setCurrentAsSvin( null );
      selectedSvinsService.setSvin( null );
      return;
    }
    // покажем ВСЕ анимированные кадры внутри интервала
    if( aNode.kind() == TsNodeTreeTagInterval.KIND ) {
      TsNodeTreeTagInterval node = (TsNodeTreeTagInterval)aNode;
      int start = node.entity().start() - ANIMATED_GIF_SECS / 2;
      if( start < 0 ) {
        start = 0;
      }
      Svin svin = new Svin( episode().id(), IStridable.NONE_ID, new Secint( start, node.entity().end() ) );
      currentFramesListService.setCurrentAsSvin( svin );
      selectedSvinsService.setSvin( svin );
      return;
    }
    // покажем ПО ОДНОМУ кадру на интервал
    if( aNode.kind() == TsNodeTreeTagLeaf.KIND || aNode.kind() == TsNodeListTag.KIND ) {
      IListEdit<IFrame> ll = new ElemArrayList<>();
      IListEdit<Svin> svins = new ElemArrayList<>();
      for( ITsNode child : aNode.childs() ) {
        if( child.kind() == TsNodeTreeTagInterval.KIND ) {
          TsNodeTreeTagInterval node = (TsNodeTreeTagInterval)child;
          int start = node.entity().start() - ANIMATED_GIF_SECS / 2;
          if( start < 0 ) {
            start = 0;
          }
          Svin svin = new Svin( episode().id(), IStridable.NONE_ID, new Secint( start, node.entity().end() ) );
          svins.add( svin );
          IPsxFileSystem fs = tsContext().get( IPsxFileSystem.class );
          FrameSelectionCriteria criteria = new FrameSelectionCriteria( svin, EAnimationKind.ANIMATED, false );
          IFramesList nodeFrames = new FramesList( fs.listEpisodeFrames( criteria ) );
          // если нет анимированных, подберем неподвижный кадр
          if( nodeFrames.isEmpty() ) {
            criteria = new FrameSelectionCriteria( svin, EAnimationKind.SINGLE, false );
            nodeFrames = new FramesList( fs.listEpisodeFrames( criteria ) );
          }
          // выберем единственный кадр
          IFrame singlePerInterval =
              nodeFrames.findNearest( svin.interval().start() + svin.interval().duration() / 2, IStridable.NONE_ID );
          if( singlePerInterval != null ) { // только если интервал выходит за пределы эпизода
            ll.add( singlePerInterval );
          }
        }
      }
      currentFramesListService.setCurrent( ll );
      selectedSvinsService.setSvins( svins );
      return;
    }
    currentFramesListService.setCurrentAsSvin( null );
    selectedSvinsService.setSvin( null );
  }

  /**
   * Находит {@link ITag} от запрошенного узла дерева.
   * <p>
   * Если узел содержит {@link ITag}, то возвращает его, если содержит интервал ярлыка, то возвращает рожительский узел.
   * Если аргумент null, возвращает null.
   *
   * @param aTreeNode {@link ITsNode} - узел в дереве, может быть null
   * @return {@link ITag} - найденный {@link ITag} или null
   */
  private static ITag findOwnerTag( ITsNode aTreeNode ) {
    if( aTreeNode == null ) {
      return null;
    }
    if( aTreeNode.kind() == TsNodeTreeTagInterval.KIND ) {
      return ((TsNodeTreeTagInterval)aTreeNode).owner();
    }
    return (ITag)aTreeNode.entity();
  }

  /**
   * Обрабатывает нажатие казанной кнопки на панели управления.
   *
   * @param aItemId int идентификатор кнопки
   */
  void processAction( String aItemId ) {
    if( episode() == null || !toolbar.isActionEnabled( aItemId ) ) {
      return;
    }
    ITsNode sel = treeViewer.selectedItem();
    switch( aItemId ) {
      case ACTID_ADD: {
        String currTagId = null;
        Secint currIn = Secint.ZERO;
        if( sel != null ) {
          ITag tn = findOwnerTag( sel );
          if( tn != null ) {
            currTagId = tn.id();
          }
          if( sel.kind() == TsNodeTreeTagInterval.KIND ) {
            Secint tagIn = ((TsNodeTreeTagInterval)sel).entity();
            int end = Math.min( tagIn.end() + 30, episode().info().duration() - 1 );
            int start = Math.min( tagIn.end() + 1, end );
            currIn = new Secint( start, end );
          }
        }
        String tagId = currTagId != null ? currTagId : IStridable.NONE_ID;
        Pair<String, Secint> vals = new Pair<>( tagId, currIn );
        Secint allowIn = new Secint( 0, episode().info().duration() - 1 );
        vals = DialogTagInterval.select( tsContext(), vals, allowIn );
        if( vals == null ) {
          return;
        }
        episode().tagLine().addMark( vals.left(), vals.right() );
        selectTreeItem( vals.left(), vals.right() );
        break;
      }
      case ACTID_EDIT: {
        TsNodeTreeTagInterval node = (TsNodeTreeTagInterval)sel;
        Pair<String, Secint> vals = new Pair<>( node.owner().id(), node.entity() );
        Secint allowIn = new Secint( 0, episode().info().duration() - 1 );
        vals = DialogTagInterval.select( tsContext(), vals, allowIn );
        if( vals == null ) {
          return;
        }
        episode().tagLine().removeMark( node.owner().id(), node.entity().start() );
        episode().tagLine().addMark( vals.left(), vals.right() );
        refreshView();
        selectTreeItem( vals.left(), vals.right() );
        break;
      }
      case ACTID_REMOVE: {
        TsNodeTreeTagInterval node = (TsNodeTreeTagInterval)sel;
        if( TsDialogUtils.askYesNoCancel( getShell(), ASK_DELETE_TAG_INTERVAL, node.owner().id(),
            node.entity().toString() ) == ETsDialogCode.YES ) {
          IIntListEdit path = new IntLinkedBundleList( treeViewer.getSelectedPath() );
          int siblingsCount = node.parent().childs().size();
          if( siblingsCount <= 1 ) {
            if( path.size() >= 2 ) {
              path.removeByIndex( path.size() - 1 );
            }
          }
          else {
            int lastIndex = path.size() - 1;
            if( path.getValue( lastIndex ) >= siblingsCount ) {
              path.set( lastIndex, path.getValue( lastIndex ) - 1 );
            }
          }
          episode().tagLine().removeMark( node.owner().id(), node.entity() );
          refreshView();
          treeViewer.setSelectedPath( path );
        }
        break;
      }
      case ACTID_COLLAPSE_ALL:
        treeViewer.console().collapseAll();
        break;
      case ACTID_EXPAND_ALL:
        treeViewer.console().expandAll();
        break;
      case AID_PLAY:
        if( sel != null ) {
          playItem( sel );
        }
        break;
      case AID_SHOW_TAGLINE_AS_LIST:
        refreshView();
        break;
      case AID_INCLUDE_ONLY_USED_TAGS:
        refreshView();
        break;
      case ACTID_FILTER:
        // обрабатывается в filterContribution
        break;
      default:
        throw new TsNotAllEnumsUsedRtException();
    }
    updateActionsState();
  }

  /**
   * Выбырает в элемент в дереве.
   *
   * @param aTagId String - идентификатор выделяемого ярлыка
   * @param aInterval {@link Secint} - если не null, то выделяется этот интервал ярлыка
   */
  private void selectTreeItem( String aTagId, Secint aInterval ) {
    ITag tagNode = unitTags.root().allNodesBelow().findByKey( aTagId );
    if( tagNode == null ) {
      return;
    }
    ITsNode nodeToSelect = treeViewer.findByEntity( tagNode, true );
    if( nodeToSelect == null || aInterval == null ) {
      internalSelectTreeNode( nodeToSelect );
      treeViewer.console().expandNode( nodeToSelect );
      return;
    }
    ITsNode subnode = nodeToSelect.findByEntity( aInterval, true );
    if( subnode != null ) {
      nodeToSelect = subnode;
    }
    internalSelectTreeNode( nodeToSelect );
    treeViewer.console().expandNode( nodeToSelect );
  }

  private void internalSelectTreeNode( ITsNode aNode ) {
    if( aNode != null ) {
      ensureParentExpanded( aNode );
      treeViewer.console().expandNodeTo( aNode, 1 );
      treeViewer.setSelectedItem( aNode );
    }
  }

  private void ensureParentExpanded( ITsNode aNode ) {
    ITsNode parent = aNode.parent();
    if( parent != null ) {
      ensureParentExpanded( parent );
      treeViewer.console().expandNodeTo( parent, 1 );
    }
  }

  /**
   * Возвращает ярлык (может быть, группу), соответствующий выбранному узлу.
   *
   * @param aItemOrNull {@link ITsNode} - текущий узел дерева, может быть null
   * @return {@link ITag} - ярлык/группа или null
   */
  ITag getItemTagOrNull( ITsNode aItemOrNull ) {
    if( aItemOrNull == null ) {
      return null;
    }
    ITsNode node = aItemOrNull;
    if( node.kind() == TsNodeTreeTagInterval.KIND ) {
      node = node.parent();
    }
    switch( node.kind().id() ) {
      case TsNodeTreeTagGroup.KIND_ID:
        return ((TsNodeTreeTagGroup)node).entity();
      case TsNodeTreeTagLeaf.KIND_ID:
        return ((TsNodeTreeTagLeaf)node).entity();
      case TsNodeListTag.KIND_ID:
        return ((TsNodeListTag)node).entity();
      default:
        throw new TsNotAllEnumsUsedRtException();
    }
  }

  /**
   * Возвращает воспроизводимый интервал, соотвутетсвующий текущему узлу дерева.
   *
   * @param aItemOrNull {@link ITsNode} - текущий узел дерева, может быть null
   * @return {@link Svin} - интервал вопрозведения или null
   */
  Svin getItemSvinOrNull( ITsNode aItemOrNull ) {
    if( aItemOrNull == null ) {
      return null;
    }
    if( aItemOrNull.kind() == TsNodeTreeTagInterval.KIND ) {
      TsNodeTreeTagInterval node = (TsNodeTreeTagInterval)aItemOrNull;
      Secint in = node.entity();
      IStory story = episode().story();
      IScene s = story.findBestSceneFor( in, true );
      String camId = IStridable.NONE_ID;
      if( s != null ) {
        if( s.frame().isDefined() ) {
          camId = s.frame().cameraId();
        }
      }
      return new Svin( episode().id(), camId, in );
    }
    return null;
  }

  /**
   * Воспроизводит видео, соответствующий текущему узлу дерева.
   *
   * @param aItemOrNull {@link ITsNode} - текущий узел дерева, может быть null
   */
  void playItem( ITsNode aItemOrNull ) {
    Svin svin = getItemSvinOrNull( aItemOrNull );
    if( svin != null ) {
      tsContext().get( IPrisexService.class ).playEpisodeVideo( svin );
    }
  }

  /**
   * Обновляет состояние кнопок на панели управления.
   */
  void updateActionsState() {
    boolean isAlive = episode() != null;
    ITsNode sel = treeViewer.selectedItem();
    boolean isInterval = sel != null && sel.kind() == TsNodeTreeTagInterval.KIND;
    toolbar.setActionEnabled( ACTID_ADD, isAlive );
    toolbar.setActionEnabled( ACTID_EDIT, isAlive && isInterval );
    toolbar.setActionEnabled( ACTID_REMOVE, isAlive && isInterval );
    toolbar.setActionEnabled( AID_PLAY, isAlive && isInterval );
    tsContext().get( ITsE4Helper.class ).updateHandlersCanExecuteState();
    updateDetailsPane( sel );
  }

  private void updateDetailsPane( ITsNode aSelectedNode ) {
    ITag tagNode = getItemTagOrNull( aSelectedNode );
    if( tagNode != null ) {
      String desc = tagNode.id();
      if( !tagNode.isRoot() ) {
        desc = StridUtils.printf( StridUtils.FORMAT_ID_NAME_DESCRIPTION, tagNode );
      }
      txtTagDecription.setText( desc );
      ISecintsList sl = episode().tagLine().calcMarks( tagNode.allNodesBelow().ids() );
      txtTagSecline.setText( sl.toString() );
      IList<Secint> siList = sl.listGaps( new Secint( 0, episode().info().duration() - 1 ) );
      sl = new SecintsList( siList );
      txtTagGaps.setText( sl.toString() );
    }
    else {
      txtTagDecription.setText( TsLibUtils.EMPTY_STRING );
      txtTagSecline.setText( TsLibUtils.EMPTY_STRING );
      txtTagGaps.setText( TsLibUtils.EMPTY_STRING );
    }
  }

  private boolean acceptTextFilter( String aTagId ) {
    if( !filterContribution.isFilterOn() ) {
      return true;
    }
    String text = filterContribution.getFilterText().toLowerCase();
    if( text.isEmpty() ) {
      return true;
    }
    return aTagId.toLowerCase().contains( text );
  }

  /**
   * Обновляет {@link #treeViewer} с учетом текущих устновок показа.
   */
  void refreshView() {
    if( episode() == null ) {
      treeViewer.clear();
      return;
    }
    ITsNode selNode = treeViewer.selectedItem();
    Object selEntity = selNode != null ? selNode.entity() : null;
    boolean modeAsList = toolbar.isActionChecked( AID_SHOW_TAGLINE_AS_LIST );
    boolean includeOnlyUsed = toolbar.isActionChecked( AID_INCLUDE_ONLY_USED_TAGS );
    IStringListEdit ll = new StringLinkedBundleList();
    for( ITag tn : unitTags.root().allNodesBelow() ) {
      if( !acceptTextFilter( tn.id() ) ) {
        continue;
      }
      if( !includeOnlyUsed || episode().tagLine().usedTagIds().hasElem( tn.id() ) ) {
        ll.add( tn.id() );
      }
    }

    IListEdit<ITsNode> roots = new ElemLinkedBundleList<>();
    // показываем как список
    if( modeAsList ) {
      TagIdsFilter filter = new TagIdsFilter( ll, false );
      for( ITag tn : unitTags.root().allNodesBelow() ) {
        if( filter.accept( tn.id() ) ) {
          ITsNode node = new TsNodeListTag( treeViewer, episode().tagLine(), tn );
          roots.add( node );
        }
      }
    }
    // показываем как дерево
    else {
      TagIdsFilter filter = new TagIdsFilter( ll, true );
      for( ITag tn : unitTags.root().childNodes() ) {
        if( filter.accept( tn.id() ) ) {
          ITsNode node = new TsNodeTreeTagGroup( treeViewer, episode().tagLine(), tn, filter );
          roots.add( node );
        }
      }
    }
    treeViewer.setRootNodes( roots );
    if( selEntity != null ) {
      internalSelectTreeNode( treeViewer.findByEntity( selEntity, true ) );
    }
    updateActionsState();
  }

  // ------------------------------------------------------------------------------------
  // ITsKeyInputListener
  //

  @Override
  public boolean onKeyDown( Object aSource, int aCode, char aChar, int aState ) {
    switch( aCode ) {
      case SWT.INSERT:
        processAction( ACTID_ADD );
        return true;
      // обрабатывается в SWT - Enter = double click
      // case SWT.CR:
      // case SWT.KEYPAD_CR:
      // processAction( ACDEF_EDIT );
      // return true;
      case SWT.DEL:
        processAction( ACTID_REMOVE );
        return true;
      default:
        return false;
    }
  }

}