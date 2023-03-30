package com.hazard157.psx24.core.e4.uiparts.eps;

import static com.hazard157.psx24.core.m5.IPsxM5Constants.*;

import org.eclipse.swt.widgets.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.bricks.ctx.impl.*;
import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.gui.panels.*;

import com.hazard157.psx.proj3.episodes.*;

/**
 * Вью просмотра свойств эпизода.
 *
 * @author goga
 */
public class UipartEpisodeProps
    extends AbstractEpisodeUipart {
  // FIXME редактирование во вью не работает!

  IM5EntityPanel<IEpisode> ep;

  @Override
  protected void doCreatePartContent( Composite aParent ) {
    ITsGuiContext ctx = new TsGuiContext( tsContext() );
    IM5Model<IEpisode> model = m5().getModel( MID_EPISODE, IEpisode.class );
    ep = model.panelCreator().createEntityViewerPanel( ctx );
    ep.createControl( aParent );
  }

  @Override
  protected void doSetEpisode() {
    ep.setEntity( episode() );
  }

  @Override
  protected void doHandleEpisodeContentChange() {
    doSetEpisode();
  }

}
