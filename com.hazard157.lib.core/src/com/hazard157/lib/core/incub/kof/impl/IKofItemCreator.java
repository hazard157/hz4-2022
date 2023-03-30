package com.hazard157.lib.core.incub.kof.impl;

import java.io.File;

import com.hazard157.lib.core.incub.optedfile.OptedFile;

/**
 * {@link OptedFile} subclass creator.
 *
 * @author hazard157
 * @param <T> - type of T subclass
 */
public interface IKofItemCreator<T extends OptedFile> {

  /**
   * Returns created items class.
   *
   * @return {@link Class}&lt;T&gt; - the item class
   */
  Class<T> itemClass();

  /**
   * Cretaes an instance.
   *
   * @param aFile {@link File} - path to the denoted file or directory
   * @return &lt;T&gt; - created instance
   */
  T create( File aFile );

}
