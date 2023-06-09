package com.hazard157.psx24.core.m5.visumple;

import static com.hazard157.psx24.core.m5.visumple.IPsxResources.*;
import static org.toxsoft.core.tsgui.m5.IM5Constants.*;
import static org.toxsoft.core.tsgui.m5.gui.mpc.IMultiPaneComponentConstants.*;
import static org.toxsoft.core.tsgui.m5.valeds.IM5ValedConstants.*;
import static org.toxsoft.core.tsgui.valed.api.IValedControlConstants.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;

import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tsgui.m5.valeds.multimodown.*;
import org.toxsoft.core.tsgui.utils.layout.*;
import org.toxsoft.core.tslib.coll.*;

import com.hazard157.common.quants.visumple.*;

/**
 * M5 field definition of {@link IVisumplable#visumples()}.
 *
 * @author hazard157
 * @param <T> - тип моделированного объекта
 */
public class VisumpleM5FieldDef<T extends IVisumplable>
    extends M5MultiModownFieldDef<T, Visumple> {

  /**
   * Constructor.
   */
  public VisumpleM5FieldDef() {
    super( IVisumpleConstants.FID_VISUMPLES, VisumpleM5Model.MODEL_ID );
  }

  @Override
  protected void doInit() {
    setNameAndDescription( STR_N_FIELD_VISUMPLES, STR_D_FIELD_VISUMPLES );
    setFlags( M5FF_DETAIL );
    OPDEF_NO_FIELD_LABEL.setValue( params(), AV_TRUE );
    setValedEditor( ValedMultiModownEditor.FACTORY_NAME );
    M5_VALED_OPDEF_WIDGET_TYPE_ID.setValue( params(), avStr( M5VWTID_TABLE ) );
    OPDEF_VERTICAL_SPAN.setValue( params(), avInt( 8 ) );
    OPDEF_DETAILS_PANE_PLACE.setValue( params(), avValobj( EBorderLayoutPlacement.SOUTH ) );
    OPDEF_IS_ACTIONS_REORDER.setValue( params(), AV_TRUE );
  }

  @Override
  protected IList<Visumple> doGetFieldValue( T aEntity ) {
    return aEntity.visumples();
  }

}
