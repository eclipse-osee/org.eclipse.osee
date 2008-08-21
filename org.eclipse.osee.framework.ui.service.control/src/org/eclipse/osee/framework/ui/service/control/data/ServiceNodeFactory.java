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

import net.jini.core.entry.Entry;
import net.jini.core.lookup.ServiceID;
import net.jini.core.lookup.ServiceItem;
import net.jini.lookup.entry.Name;
import net.jini.lookup.entry.ServiceInfo;

import org.eclipse.osee.framework.jini.service.core.PropertyEntry;

/**
 * @author Roberto E. Escobar
 */
public class ServiceNodeFactory {

   private static ServiceNodeFactory instance = null;

   private ServiceNodeFactory() {
   }

   public static ServiceNodeFactory getInstance() {
      if (instance == null) {
         instance = new ServiceNodeFactory();
      }
      return instance;
   }

   public ServiceNode createServiceNode(ServiceID serviceID, ServiceItem serviceItem) {
      ServiceNode serviceNode = null;

      if (ReggieMonitorServiceNode.isReggieMonitor(serviceItem)) {
         serviceNode = new ReggieMonitorServiceNode(serviceID, serviceItem);
      } else {
         serviceNode = new ServiceNode(serviceID, serviceItem);
      }

      String label = getLabelBasedOnName(serviceItem);
      if (label.equals("")) {
         label = getLabelBasedOnServiceInfo(serviceItem);
      }
      serviceNode.setName(label);
      return serviceNode;
   }

   private String getLabelBasedOnName(ServiceItem serviceItem) {
      Entry[] entryArray = serviceItem.attributeSets;
      for (Entry entry : entryArray) {
         if (entry instanceof Name) {
            return ((Name) entry).name.toString();
         } else if (entry instanceof PropertyEntry){
        	return (String)((PropertyEntry)entry).getProperty("name", "unknown"); 
         }
      }
      return "";
   }

   private String getLabelBasedOnServiceInfo(ServiceItem serviceItem) {
      Entry[] entryArray = serviceItem.attributeSets;
      for (Entry entry : entryArray) {
         if (entry instanceof ServiceInfo) {
            return ((ServiceInfo) entry).name.toString();
         }
      }
      return serviceItem.service.getClass().getName();
   }
}
