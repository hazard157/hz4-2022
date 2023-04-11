package com.hazard157.psx24.planning.e4.uiparts;

import static org.toxsoft.core.tsgui.m5.gui.mpc.IMultiPaneComponentConstants.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;

import javax.inject.*;

import org.eclipse.swt.widgets.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.bricks.ctx.impl.*;
import org.toxsoft.core.tsgui.bricks.stdevents.*;
import org.toxsoft.core.tsgui.graphics.icons.*;
import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.gui.panels.*;
import org.toxsoft.core.tsgui.m5.model.*;
import org.toxsoft.core.tsgui.mws.bases.*;
import org.toxsoft.core.tsgui.mws.services.currentity.*;

import com.hazard157.psx.proj3.pleps.*;
import com.hazard157.psx24.planning.e4.services.*;
import com.hazard157.psx24.planning.m5.*;

/**
 * Вью просмотра и правки {@link IPlep#stirs()} текущего {@link ICurrentPlepService#current()}.
 *
 * @author hazard157
 */
public class UipartUnitPlepStirs
    extends MwsAbstractPart {

  private final ICurrentEntityChangeListener<IPlep> currentPlepChangeListener = aCurrent -> updateOnCurrentPlep();

  private final ITsSelectionChangeListener<IStir> selectedStirChangeListener =
      ( aSource, aSelectedItem ) -> this.currentStirService.setCurrent( aSelectedItem );

  @Inject
  ICurrentPlepService currentPlepService;

  @Inject
  ICurrentStirService currentStirService;

  IM5CollectionPanel<IStir> stirsPanel;

  @Override
  protected void doInit( Composite aParent ) {
    currentPlepService.addCurrentEntityChangeListener( currentPlepChangeListener );
    ITsGuiContext ctx = new TsGuiContext( tsContext() );
    IM5Model<IStir> stirsModel = m5().getModel( StirM5Model.MODEL_ID, IStir.class );
    OPDEF_NODE_ICON_SIZE.setValue( ctx.params(), avValobj( EIconSize.IS_64X64 ) );
    stirsPanel = stirsModel.panelCreator().createCollEditPanel( ctx, null, null );
    stirsPanel.createControl( aParent );
    stirsPanel.addTsSelectionListener( selectedStirChangeListener );
    updateOnCurrentPlep();
  }

  void updateOnCurrentPlep() {
    IPlep plep = currentPlepService.current();
    if( plep != null ) {
      IM5Model<IStir> stirsModel = m5().getModel( StirM5Model.MODEL_ID, IStir.class );
      IM5LifecycleManager<IStir> lm = stirsModel.getLifecycleManager( plep );
      stirsPanel.setItemsProvider( lm.itemsProvider() );
      stirsPanel.setLifecycleManager( lm );
    }
    else {
      stirsPanel.setItemsProvider( null );
      stirsPanel.setLifecycleManager( null );
    }
    stirsPanel.refresh();
    currentStirService.setCurrent( stirsPanel.selectedItem() );
  }

}