package com.hazard157.lib.core.excl_done.dirwatch;

import org.eclipse.swt.widgets.*;
import org.toxsoft.core.tslib.coll.*;

/**
 * Message about changes in file system generated by {@link DirTreeWatcher}.
 * <p>
 * Methods of this interface are called from main GUI thread using {@link Display#asyncExec(Runnable)}.
 *
 * @author hazard157
 */
public interface IDtwChangeListener {

  /**
   * Informs about changes in file system
   *
   * @param aEvents IList&lt;{@link DtwChangeEvent}&gt; - events happened in file system
   */
  void onFileSystemChanged( IList<DtwChangeEvent> aEvents );

}
