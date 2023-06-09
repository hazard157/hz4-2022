package com.hazard157.psx24.core.m5.todos;

import static com.hazard157.common.IHzConstants.*;
import static com.hazard157.psx24.core.m5.todos.ITodoM5Constants.*;

import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.utils.errors.*;

import com.hazard157.psx.proj3.todos.*;

/**
 * Создатель контроллера жизненного цикала объектов типа {@link ITodo}.
 *
 * @author hazard157
 */
class TodoM5LifecycleManager
    extends M5LifecycleManager<ITodo, IUnitTodos> {

  public TodoM5LifecycleManager( IM5Model<ITodo> aModel, IUnitTodos aMaster ) {
    super( aModel, true, true, true, true, aMaster );
  }

  @Override
  protected ITodo doCreate( IM5Bunch<ITodo> aValues ) {
    String text = aValues.getAsAv( FID_TEXT ).asString();
    ITodo t = master().addTodo( text );
    String note = aValues.getAsAv( FID_NOTE ).asString();
    t.setTexts( text, note );
    t.setDone( aValues.getAsAv( FID_IS_DONE ).asBool() );
    t.setPriority( aValues.getAs( FID_PRIORITY, EPriority.class ) );
    t.setReminder( aValues.getAs( FID_REMINDER, IReminder.class ) );
    for( ITodo todo : (IList<ITodo>)aValues.getAs( FID_RELATED_TODO_IDS, IList.class ) ) {
      t.addRelatedTodo( todo.creationTime() );
    }
    t.fulfilStages().setAll( aValues.getAs( FID_FULFIL_STAGES, IList.class ) );
    return t;
  }

  @Override
  protected ITodo doEdit( IM5Bunch<ITodo> aValues ) {
    ITodo t = aValues.originalEntity();
    TsInternalErrorRtException.checkNull( t );
    String text = aValues.getAsAv( FID_TEXT ).asString();
    String note = aValues.getAsAv( FID_NOTE ).asString();
    t.setTexts( text, note );
    t.setDone( aValues.getAsAv( FID_IS_DONE ).asBool() );
    t.setPriority( aValues.getAs( FID_PRIORITY, EPriority.class ) );
    t.setReminder( aValues.getAs( FID_REMINDER, IReminder.class ) );
    while( !t.relatedTodoIds().isEmpty() ) {
      t.removeRelatedTodo( t.relatedTodoIds().get( 0 ).longValue() );
    }
    for( ITodo todo : (IList<ITodo>)aValues.getAs( FID_RELATED_TODO_IDS, IList.class ) ) {
      t.addRelatedTodo( todo.creationTime() );
    }
    t.fulfilStages().setAll( aValues.getAs( FID_FULFIL_STAGES, IList.class ) );
    return t;
  }

  @Override
  protected void doRemove( ITodo aEntity ) {
    master().removeTodo( aEntity.id() );
  }

  @Override
  protected IList<ITodo> doListEntities() {
    return master().todos().values();
  }

}
