package com.hazard157.prisex24.e4.services.selsvins;

import org.toxsoft.core.tslib.bricks.events.change.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.impl.*;
import org.toxsoft.core.tslib.utils.errors.*;

import com.hazard157.prisex24.utils.frasel.*;
import com.hazard157.psx.common.stuff.svin.*;

/**
 * {@link IPsxSelectedSvinsService} implementation.
 *
 * @author hazard157
 */
public class PsxSelectedSvinsService
    implements IPsxSelectedSvinsService {

  private final GenericChangeEventer eventer;
  private final IListEdit<Svin>      svins                 = new ElemArrayList<>();
  private final ISvinFramesParams    framesSelectionParams = new SvinFramesParams();

  /**
   * Constructor.
   */
  public PsxSelectedSvinsService() {
    eventer = new GenericChangeEventer( this );
  }

  // ------------------------------------------------------------------------------------
  // IPsxSelectedSvinsService
  //

  @Override
  public IList<Svin> svins() {
    return svins;
  }

  @Override
  public void setSvins( IList<Svin> aSvins ) {
    TsNullArgumentRtException.checkNull( aSvins );
    if( svins.equals( aSvins ) ) {
      return;
    }
    svins.setAll( aSvins );
    eventer.fireChangeEvent();
  }

  @Override
  public ISvinFramesParams framesSelectionParams() {
    return framesSelectionParams;
  }

  @Override
  public IGenericChangeEventer eventer() {
    return eventer;
  }

}
