/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.jdk.core.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jeff C. Phillips
 * @author Robert A. Fisher
 */
public class ThreadKeyLocal<K, T> {

   private ThreadLocal<Map<K, T>> keyToValueMaps = new ThreadLocal<Map<K, T>>() {

      /*
       * (non-Javadoc)
       * 
       * @see java.lang.ThreadLocal#initialValue()
       */
      @Override
      protected Map<K, T> initialValue() {
         return new HashMap<K, T>();
      }
   };

   public T get(K key) {
      Map<K, T> map = keyToValueMaps.get();

      T value = map.get(key);

      if (value == null) {
         value = initialValue();
         map.put(key, value);
      }

      return value;
   }

   public void remove(K key) {
      keyToValueMaps.get().remove(key);
   }

   public void set(K key, T value) {
      keyToValueMaps.get().put(key, value);
   }

   /**
    * Typically an anonymous inner class will be used and this method will be overridden to provide desired initial
    * value.
    * 
    * @return Returns value
    */
   protected T initialValue() {
      return null;
   }
}
