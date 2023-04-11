package com.hazard157.psx24.exe.e4.addons;

import static com.hazard157.psx24.exe.IPsx24ExeConstants.*;
import static org.toxsoft.core.tsgui.graphics.icons.EIconSize.*;

import org.eclipse.e4.core.contexts.*;
import org.eclipse.e4.ui.model.application.*;
import org.eclipse.e4.ui.model.application.ui.basic.*;
import org.eclipse.e4.ui.workbench.modeling.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;
import org.toxsoft.core.tsgui.bricks.quant.*;
import org.toxsoft.core.tsgui.graphics.icons.impl.*;
import org.toxsoft.core.tsgui.mws.*;
import org.toxsoft.core.tsgui.mws.bases.*;
import org.toxsoft.core.tsgui.rcp.*;

import com.hazard157.lib.core.*;
import com.hazard157.psx.proj3.*;
import com.hazard157.psx24.exe.*;
import com.hazard157.psx24.exe.Activator;

/**
 * Plugin's addon.
 *
 * @author hazard157
 */
public class AddonPsx24Exe
    extends MwsAbstractAddon {

  /**
   * Constructor.
   */
  public AddonPsx24Exe() {
    super( Activator.PLUGIN_ID );
  }

  @Override
  protected void doRegisterQuants( IQuantRegistrator aQuantRegistrator ) {
    aQuantRegistrator.registerQuant( new QuantTsGuiRcp() );
    aQuantRegistrator.registerQuant( new QuantHzLibCore() );
    aQuantRegistrator.registerQuant( new QuantPsx3Project() );
  }

  @Override
  protected void initApp( IEclipseContext aAppContext ) {
    // задаем значок окна и приложения
    MApplication app = aAppContext.get( MApplication.class );
    EModelService modelService = aAppContext.get( EModelService.class );
    MTrimmedWindow mainWindow = (MTrimmedWindow)modelService.find( IMwsCoreConstants.MWSID_WINDOW_MAIN, app );
    // mainWindow.setIconURI( TsIconManagerUtils.makeStdIconUriString( com.hazard157.psx24.core.Activator.PLUGIN_ID,
    // ICONID_APP_ICON, IS_48X48 ) );
    mainWindow.setIconURI( TsIconManagerUtils.makeStdIconUriString( Activator.PLUGIN_ID, ICONID_APP_ICON, IS_48X48 ) );
    // начальные размеры окна
    Display display = aAppContext.get( Display.class );
    Rectangle dBounds = display.getBounds();
    mainWindow.setX( dBounds.x + 8 );
    mainWindow.setY( 0 );
    mainWindow.setWidth( dBounds.width - 4 * 8 );
    mainWindow.setHeight( dBounds.height );
  }

  @Override
  protected void initWin( IEclipseContext aWinContext ) {
    IPsx24ExeConstants.init( aWinContext );
  }

}