package com.hazard157.psx24.core.m5.songs;

import static com.hazard157.psx24.core.IPsxAppActions.*;
import static org.toxsoft.core.tsgui.bricks.actions.ITsStdActionDefs.*;
import static org.toxsoft.core.tsgui.m5.gui.mpc.IMultiPaneComponentConstants.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;

import org.toxsoft.core.tsgui.bricks.actions.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.graphics.icons.*;
import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.gui.mpc.impl.*;
import org.toxsoft.core.tsgui.m5.model.*;
import org.toxsoft.core.tsgui.panels.toolbar.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.utils.errors.*;

import com.hazard157.psx.proj3.songs.*;
import com.hazard157.psx24.core.e4.services.prisex.*;

class SongsListMultiPaneComponent
    extends MultiPaneComponentModown<ISong> {

  public SongsListMultiPaneComponent( ITsGuiContext aContext, IM5Model<ISong> aModel,
      IM5ItemsProvider<ISong> aItemsProvider, IM5LifecycleManager<ISong> aLifecycleManager ) {
    super( aContext, aModel, aItemsProvider, aLifecycleManager );
    OPDEF_IS_ACTIONS_CRUD.setValue( aContext.params(), AV_TRUE );
    OPDEF_DBLCLICK_ACTION_ID.setValue( aContext.params(), avStr( AID_PLAY ) );
  }

  @Override
  protected ITsToolbar doCreateToolbar( ITsGuiContext aContext, String aName, EIconSize aIconSize,
      IListEdit<ITsActionDef> aActs ) {
    aActs.addAll( ACDEF_SEPARATOR, AI_PLAY );
    return super.doCreateToolbar( aContext, aName, aIconSize, aActs );
  }

  @Override
  protected void doProcessAction( String aActionId ) {
    if( !toolbar().isActionEnabled( AID_PLAY ) ) {
      return;
    }
    ISong sel = selectedItem();
    switch( aActionId ) {
      case AID_PLAY:
        if( sel != null ) {
          IPrisexService ps = tsContext().get( IPrisexService.class );
          ps.playAudioFile( sel.filePath() );
        }
        break;

      default:
        throw new TsNotAllEnumsUsedRtException();
    }
  }

  @Override
  protected void doUpdateActionsState( boolean aIsAlive, boolean aIsSel, ISong aSel ) {
    toolbar().setActionEnabled( AID_PLAY, aIsAlive && aIsSel );
  }

}
