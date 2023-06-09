package com.hazard157.prisex24.e4.uiparts.refbooks;

import javax.inject.*;

import org.eclipse.swt.widgets.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.bricks.ctx.impl.*;
import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.gui.panels.*;
import org.toxsoft.core.tsgui.m5.model.*;

import com.hazard157.prisex24.e4.uiparts.*;
import com.hazard157.prisex24.m5.*;
import com.hazard157.psx.proj3.todos.*;

/**
 * UIpart: project todos refbook.
 *
 * @author hazard157
 */
public class UipartRefbookTodos
    extends PsxAbstractUipart {

  @Inject
  IUnitTodos unitTodos;

  @Override
  protected void doInit( Composite aParent ) {
    IM5Model<ITodo> todoModel = m5().getModel( IPsxM5Constants.MID_TODO, ITodo.class );
    ITsGuiContext ctx = new TsGuiContext( tsContext() );
    IM5LifecycleManager<ITodo> lm = todoModel.getLifecycleManager( unitTodos );
    IM5CollectionPanel<ITodo> todoPanel = todoModel.panelCreator().createCollEditPanel( ctx, lm.itemsProvider(), lm );
    todoPanel.createControl( aParent );
  }

}
