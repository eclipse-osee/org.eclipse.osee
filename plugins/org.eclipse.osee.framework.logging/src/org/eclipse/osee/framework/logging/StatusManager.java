/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.logging;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Andrew M. Finkbeiner
 */
class StatusManager {
   private final Map<String, IHealthStatus> services;
   private final List<IStatusListener> listeners;
   private final Map<IStatusListener, IStatusListenerFilter> filters;

   public StatusManager() {
      this.services = Collections.synchronizedMap(new TreeMap<String, IHealthStatus>());
      this.listeners = new CopyOnWriteArrayList<>();
      this.filters = new ConcurrentHashMap<>();
   }

   public void report(IHealthStatus status) {
      if (status != null) {
         IHealthStatus storedStatus = services.get(status.getSourceName());
         if (storedStatus == null) {
            services.put(status.getSourceName(), status);
         }
         //         serviceStatus.setHealthStatus(status);
         for (IStatusListener listener : listeners) {
            IStatusListenerFilter filter = filters.get(listener);
            if (filter == null || filter.isInterested(status)) {
               listener.onStatus(status);
            }
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

   public Collection<IHealthStatus> getHealthStatus() {
      return services.values();
   }

   public boolean isStatusOk() {
      boolean result = true;
      for (IHealthStatus status : getHealthStatus()) {
         result &= status.isOk();
      }
      return result;
   }

   public String getReport() {
      StringBuilder message = new StringBuilder();
      Collection<IHealthStatus> serviceInfos = getHealthStatus();
      for (IHealthStatus status : serviceInfos) {
         message.append(status.getSourceName().toUpperCase());
         if (!status.isOk()) {
            message.append(" - ERROR - ");
         } else {
            message.append(" - OK - ");
         }
         message.append(status.getMessage());
         message.append("\n\n");
      }
      return message.toString();
   }

   /**
    * @return health status
    */
   public IHealthStatus getHealthStatusByName(String sourceName) {
      return services.get(sourceName);
   }

}
