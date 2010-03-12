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
package org.eclipse.osee.framework.ui.service.control.data;

import org.eclipse.osee.framework.jini.service.core.SimpleFormattedEntry;
import org.eclipse.osee.framework.jini.utility.StartJini;
import net.jini.core.entry.Entry;
import net.jini.core.lookup.ServiceID;
import net.jini.core.lookup.ServiceItem;

/**
 * @author Roberto E. Escobar
 */
public class ReggieMonitorServiceNode extends ServiceNode {

   private String spawnedReggieId;
   private String spawnedReggieOnHost;

   /**
    * @param serviceID
    * @param serviceItem
    */
   public ReggieMonitorServiceNode(ServiceID serviceID, ServiceItem serviceItem) {
      super(serviceID, serviceItem);
      spawnedReggieId = parseInfo(serviceItem, StartJini.SPAWNED_REGGIE_SERVICE_ID);
      spawnedReggieOnHost = parseInfo(serviceItem, StartJini.SPAWNED_REGGIE_ON_HOST);
   }

   @Override
   public void setServiceItem(ServiceItem serviceItem) {
      super.setServiceItem(serviceItem);
      spawnedReggieId = parseInfo(serviceItem, StartJini.SPAWNED_REGGIE_SERVICE_ID);
      spawnedReggieOnHost = parseInfo(serviceItem, StartJini.SPAWNED_REGGIE_ON_HOST);
   }

   private String parseInfo(ServiceItem serviceItem, String key) {
      String toReturn = "";
      Entry[] entryArray = serviceItem.attributeSets;
      for (Entry entry : entryArray) {
         if (entry instanceof SimpleFormattedEntry) {
            String name = ((SimpleFormattedEntry) entry).name;
            String value = ((SimpleFormattedEntry) entry).value;
            if (name != null && name.length() > 0 && value != null && value.length() > 0) {
               if (name.equals(key)) {
                  toReturn = value;
               }
            }
         }
      }
      return toReturn;
   }

   public String getSpawnedReggieServiceId() {
      return spawnedReggieId;
   }

   public String getSpawnedReggieOnHost() {
      return spawnedReggieOnHost;
   }

   public static boolean isReggieMonitor(ServiceItem serviceItem) {
      boolean isReggieMonitor = false;
      Entry[] entryArray = serviceItem.attributeSets;
      for (Entry entry : entryArray) {
         if (entry instanceof SimpleFormattedEntry) {
            String name = ((SimpleFormattedEntry) entry).name;
            String value = ((SimpleFormattedEntry) entry).value;
            if (name != null && name.length() > 0 && value != null && value.length() > 0) {
               if (name.equals("Spawned Reggie Id")) {
                  isReggieMonitor = true;
               }
            }
         }
      }
      return isReggieMonitor;
   }

}
