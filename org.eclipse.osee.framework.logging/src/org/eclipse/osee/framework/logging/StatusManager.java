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

package org.eclipse.osee.framework.logging;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author afinkbei
 */
class StatusManager {
   List<IStatusListener> listeners;
   Map<IStatusListener, IStatusListenerFilter> filters;

   public StatusManager() {
      listeners = new CopyOnWriteArrayList<IStatusListener>();
      filters = new ConcurrentHashMap<IStatusListener, IStatusListenerFilter>();
   }

   public void report(IHealthStatus status) {
      for (IStatusListener listener : listeners) {
         IStatusListenerFilter filter = filters.get(listener);
         if (filter == null || filter.isInterested(status)) {
            listener.onStatus(status);
         }
      }
   }

   public boolean register(IStatusListener listener, IStatusListenerFilter filter) {
      filters.put(listener, filter);
      return listeners.add(listener);
   }

   public boolean register(IStatusListener listener) {
      return listeners.add(listener);
   }

   public boolean deregister(IStatusListener listener) {
      return listeners.remove(listener);
   }
}
