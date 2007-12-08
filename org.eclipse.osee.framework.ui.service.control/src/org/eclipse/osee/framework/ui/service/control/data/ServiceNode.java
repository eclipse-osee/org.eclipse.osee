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

import java.util.Set;
import java.util.TreeSet;
import net.jini.core.entry.Entry;
import net.jini.core.lookup.ServiceID;
import net.jini.core.lookup.ServiceItem;
import org.eclipse.osee.framework.jdk.core.type.TreeObject;
import org.eclipse.osee.framework.jini.service.core.GroupEntry;
import org.eclipse.osee.framework.ui.service.control.renderer.ServiceItemHandler;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Roberto E. Escobar
 */
public class ServiceNode extends TreeObject implements IJiniService {

   private ServiceID serviceID;
   private ServiceItem serviceItem;
   private ServiceItemHandler handler;
   private boolean connected;
   private Set<String> groups;

   public ServiceNode(ServiceID serviceID, ServiceItem serviceItem) {
      super();
      this.serviceID = serviceID;
      this.serviceItem = serviceItem;
      this.handler = new ServiceItemHandler(serviceItem);
      this.connected = false;
      this.groups = parseGroups(serviceItem);
   }

   public void setServiceItem(ServiceItem serviceItem) {
      this.serviceID = serviceItem.serviceID;
      this.serviceItem = serviceItem;
      this.handler = new ServiceItemHandler(serviceItem);
      this.groups = parseGroups(serviceItem);
   }

   public ServiceID getServiceID() {
      return serviceID;
   }

   public ServiceItem getServiceItem() {
      return serviceItem;
   }

   public boolean isConnected() {
      return connected;
   }

   public void setConnected(boolean connected) {
      this.connected = connected;
   }

   public Control renderInComposite(Composite parent) {
      return handler.renderInComposite(parent);
   }

   public void setGroups(Set<String> groups) {
      this.groups = groups;
   }

   public boolean isMemberOf(String value) {
      return groups.contains(value);
   }

   public Set<String> getGroups() {
      return groups;
   }

   private Set<String> parseGroups(ServiceItem serviceItem) {
      Set<String> toReturn = new TreeSet<String>();
      Entry[] entryArray = serviceItem.attributeSets;
      for (Entry entry : entryArray) {
         if (entry instanceof GroupEntry) {
            String[] groups = ((GroupEntry) entry).group;
            for (String temp : groups) {
               if (temp != null) {
                  temp = temp.trim();
               }
               if (temp == null || temp.length() == 0) {
                  toReturn.add("Public");
               } else {
                  toReturn.add(temp);
               }
            }
         }
      }
      if (toReturn.size() == 0) {
         toReturn.add("Public");
      }
      return toReturn;
   }
}
