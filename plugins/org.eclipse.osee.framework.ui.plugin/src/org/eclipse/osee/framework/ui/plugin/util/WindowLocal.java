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
package org.eclipse.osee.framework.ui.plugin.util;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.ui.PlatformUI;

/**
 * An Eclipse window-local variable. This will provide variables that appear to be scoped based on the active window
 * that accesses the variable. All access to the variable from a non-window is considered from the same scope.
 * 
 * @author Robert A. Fisher
 */
public class WindowLocal<T> {
   private static final Object NON_WINDOW = new Object();
   private Map<Object, T> valueMap;
   private WindowKey windowKey;

   public WindowLocal() {
      this.valueMap = new HashMap<Object, T>();
      windowKey = new WindowKey();
   }

   /**
    * Provide the initial value to be used when accessed from a scope for the first time. This should be overriden by
    * the application in most cases. The default implementation returns a null.<br/><br/> This method is not called
    * when the first access to the variable in a scope is a set call.
    */
   protected T initialValue() {
      return null;
   }

   /**
    * Get the value of the variable for the current scope.
    */
   public T get() {
      Object key = getWindowLocalKey();
      T value = valueMap.get(key);

      if (value == null) {
         value = initialValue();
         set(value);
      }

      return value;
   }

   /**
    * Assign the value of the variable for the current scope.
    */
   public void set(T value) {
      valueMap.put(getWindowLocalKey(), value);
   }

   /**
    * Remove the variable from the current scope. This will cause the next access from this scope to appear as the
    * first.
    */
   public void remove() {
      valueMap.remove(getWindowLocalKey());
   }

   private Object getWindowLocalKey() {
      Object key;

      if (PlatformUI.isWorkbenchRunning()) {
         Displays.ensureInDisplayThread(windowKey);
         key = windowKey.getKey();
      } else {
         key = NON_WINDOW;
      }

      return key;
   }

   private class WindowKey implements Runnable {
      private Object key;

      public void run() {
         key = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
      }

      public Object getKey() {
         return key;
      }
   }
}
