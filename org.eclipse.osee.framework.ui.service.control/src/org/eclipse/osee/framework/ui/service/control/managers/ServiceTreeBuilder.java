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
package org.eclipse.osee.framework.ui.service.control.managers;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.eclipse.osee.framework.jdk.core.type.InputManager;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.type.TreeObject;
import org.eclipse.osee.framework.jdk.core.type.TreeParent;
import org.eclipse.osee.framework.ui.service.control.data.CategoryParent;
import org.eclipse.osee.framework.ui.service.control.data.GroupParent;
import org.eclipse.osee.framework.ui.service.control.data.ServiceNode;
import org.eclipse.osee.framework.ui.service.control.data.ServiceNodeFactory;
import net.jini.core.lookup.ServiceID;
import net.jini.core.lookup.ServiceItem;

/**
 * @author Roberto E. Escobar
 */
public class ServiceTreeBuilder {

   private static ServiceNodeFactory serviceNodeFactory = null;
   private InputManager<TreeParent> inputManager;
   private Map<ServiceID, ServiceItem> map;

   public ServiceTreeBuilder() {
      serviceNodeFactory = ServiceNodeFactory.getInstance();
      this.inputManager = new InputManager<TreeParent>();
      this.map = Collections.synchronizedMap(new HashMap<ServiceID, ServiceItem>());
   }

   private Set<GroupParent> findGroup(ServiceNode serviceNode) {
      Set<GroupParent> toReturn = new HashSet<GroupParent>();
      List<TreeParent> nodes = inputManager.getInputList();
      for (TreeParent node : nodes) {
         if (node instanceof GroupParent) {
            GroupParent groupParent = ((GroupParent) node);
            if (serviceNode.isMemberOf(groupParent.getName())) {
               if (!toReturn.contains(groupParent)) {
                  toReturn.add(groupParent);
               }
            }
         }
      }
      return toReturn;
   }

   private CategoryParent findCategory(GroupParent groupParent, ServiceNode serviceNode) {
      if (groupParent != null && groupParent.hasChildren()) {
         TreeObject[] children = groupParent.getChildren();
         for (TreeObject child : children) {
            if (child instanceof CategoryParent) {
               CategoryParent categoryParent = ((CategoryParent) child);
               if (categoryParent.getName().equals(serviceNode.getName())) {
                  return categoryParent;
               }
            }
         }
      }
      return null;
   }

   private Pair<CategoryParent, ServiceNode> findService(GroupParent groupParent, ServiceNode searchNode) {
      CategoryParent category = findCategory(groupParent, searchNode);
      if (category != null && category.hasChildren()) {
         TreeObject[] children = category.getChildren();
         for (TreeObject child : children) {
            if (child instanceof ServiceNode) {
               ServiceNode serviceNode = ((ServiceNode) child);
               if (serviceNode.getServiceID().equals(searchNode.getServiceID())) {
                  return new Pair<CategoryParent, ServiceNode>(category, serviceNode);
               }
            }
         }
      }
      return null;
   }

   public void serviceAdded(ServiceItem serviceItem) {
      ServiceID serviceId = serviceItem.serviceID;
      // System.out.println("Service Added: " + serviceId);
      synchronized (map) {
         if (!map.containsKey(serviceId)) {
            map.put(serviceId, serviceItem);

            ServiceNode serviceNode = serviceNodeFactory.createServiceNode(serviceId, serviceItem);
            Set<GroupParent> groupParents = findGroup(serviceNode);
            Set<String> existingGroupNames = new TreeSet<String>();

            if (groupParents.size() > 0) {
               for (GroupParent groupParent : groupParents) {
                  if (!existingGroupNames.contains(groupParent.getName())) {
                     existingGroupNames.add(groupParent.getName());
                  }
                  CategoryParent categoryParent = findCategory(groupParent, serviceNode);
                  if (categoryParent == null) {
                     categoryParent = new CategoryParent(serviceNode.getName());
                     groupParent.addChild(categoryParent);
                     categoryParent.addChild(serviceNode);
                  } else {
                     Pair<CategoryParent, ServiceNode> node = findService(groupParent, serviceNode);
                     if (node != null) {
                        node.getValue().setServiceItem(serviceItem);
                     } else {
                        categoryParent.addChild(serviceNode);
                     }
                  }
               }
               inputManager.inputChanged();
            }

            for (String group : serviceNode.getGroups()) {
               if (!existingGroupNames.contains(group)) {
                  GroupParent groupParent = new GroupParent(group);
                  CategoryParent categoryParent = new CategoryParent(serviceNode.getName());
                  groupParent.addChild(categoryParent);
                  categoryParent.addChild(serviceNode);
                  inputManager.addNode(groupParent);
               }
            }
         }
      }
   }

   public void serviceChanged(ServiceItem serviceItem) {
      synchronized (map) {
         boolean handleAsServiceAddedEvent = false;
         ServiceID serviceId = serviceItem.serviceID;
         // System.out.println("Service Changed: " + serviceId);
         if (map.containsKey(serviceId)) {
            map.put(serviceId, serviceItem);

            ServiceNode serviceNode = serviceNodeFactory.createServiceNode(serviceId, serviceItem);
            Set<GroupParent> groupParents = findGroup(serviceNode);
            Set<String> existingGroupNames = new TreeSet<String>();
            if (groupParents.size() > 0) {
               for (GroupParent groupParent : groupParents) {
                  if (!existingGroupNames.contains(groupParent.getName())) {
                     existingGroupNames.add(groupParent.getName());
                  }
                  Pair<CategoryParent, ServiceNode> node = findService(groupParent, serviceNode);
                  if (node != null) {
                     node.getValue().setServiceItem(serviceItem);
                     inputManager.inputChanged();
                  } else {
                     handleAsServiceAddedEvent = true;
                  }
               }
            }

            for (String group : serviceNode.getGroups()) {
               if (!existingGroupNames.contains(group)) {
                  handleAsServiceAddedEvent = true;
                  break;
               }
            }
         } else {
            handleAsServiceAddedEvent = true;
         }

         if (handleAsServiceAddedEvent) {
            serviceAdded(serviceItem);
         }
      }
   }

   public void serviceRemoved(ServiceItem serviceItem) {
      synchronized (map) {
         ServiceID serviceId = serviceItem.serviceID;
         // System.out.println("Service Removed: " + serviceId);
         if (map.containsKey(serviceId)) {
            ServiceNode serviceNode = serviceNodeFactory.createServiceNode(serviceId, serviceItem);
            Set<GroupParent> groupParents = findGroup(serviceNode);
            if (groupParents.size() > 0) {
               for (GroupParent groupParent : groupParents) {
                  Pair<CategoryParent, ServiceNode> node = findService(groupParent, serviceNode);
                  if (node != null) {
                     node.getKey().removeChild(node.getValue());
                     if (!node.getKey().hasChildren()) {
                        groupParent.removeChild(node.getKey());
                     }
                     inputManager.inputChanged();
                  }
                  if (!groupParent.hasChildren()) {
                     inputManager.removeNode(groupParent);
                  }
               }
            }
            map.remove(serviceId);
         }
      }
   }

   public void clear() {
      synchronized (map) {
         map.clear();
         inputManager.removeAll();
      }
   }

   public InputManager<TreeParent> getInputManager() {
      return inputManager;
   }
}
