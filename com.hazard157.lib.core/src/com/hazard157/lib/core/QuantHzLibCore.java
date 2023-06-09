package com.hazard157.lib.core;

import org.eclipse.e4.core.contexts.*;
import org.toxsoft.core.tsgui.bricks.quant.*;

/**
 * Plugin quant.
 *
 * @author hazard157
 */
public class QuantHzLibCore
    extends AbstractQuant {

  /**
   * Constructor.
   */
  public QuantHzLibCore() {
    super( QuantHzLibCore.class.getSimpleName() );
    //
    // registerQuant( new QuantVisumple() );
  }

  @Override
  protected void doInitApp( IEclipseContext aAppContext ) {
    // IMediaPlayerService mps = new MediaPlayerService();
    // aAppContext.set( IMediaPlayerService.class, mps );
  }

  @Override
  protected void doInitWin( IEclipseContext aWinContext ) {
    IHzLibConstants.init( aWinContext );
    //
    // IValedControlFactoriesRegistry vr = aWinContext.get( IValedControlFactoriesRegistry.class );
    // vr.registerFactory( ValedAvIntHhmmss.FACTORY );
    // vr.registerFactory( ValedSecintFactory.FACTORY );
    // vr.registerFactory( ValedAvSecintFactory.FACTORY );
    // vr.registerFactory( ValedAvValobjRadioPropEnumStars.FACTORY );
    // vr.registerFactory( ValedRadioPropEnumStars.FACTORY );
    //
    // IM5Domain m5 = aWinContext.get( IM5Domain.class );
    // m5.addModel( new SecintM5Model() );
  }

}
