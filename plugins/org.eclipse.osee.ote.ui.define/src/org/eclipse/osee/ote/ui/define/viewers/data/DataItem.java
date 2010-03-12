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
package org.eclipse.osee.ote.ui.define.viewers.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Roberto E. Escobar
 */
public abstract class DataItem {

   private DataItem parentItem;
   private Map<Object, DataItem> childrenMap;

   public DataItem(DataItem parentItem) {
      this.parentItem = parentItem;
      this.childrenMap = new HashMap<Object, DataItem>();
   }

   public void addChild(Object key, DataItem child) {
      childrenMap.put(key, child);
      child.setParent(this);
   }

   public void removeChild(DataItem child) {
      childrenMap.remove(child.getKey());
      child.setParent(null);
      child.dispose();
   }

   public DataItem[] getChildren() {
      Collection<DataItem> collection = childrenMap.values();
      return collection.toArray(new DataItem[collection.size()]);
   }

   public boolean hasChildren() {
      return childrenMap.size() > 0;
   }

   public void setParent(DataItem parent) {
      this.parentItem = parent;
   }

   public DataItem getParent() {
      return this.parentItem;
   }

   public void dispose() {
      if (hasChildren() != false) {
         for (DataItem item : getChildren()) {
            item.dispose();
         }
      }
   }

   public abstract Object getData();

   public abstract Object getKey();

   public boolean hasItem(Object key) {
      return childrenMap.containsKey(key);
   }

   public DataItem getChild(Object key) {
      return childrenMap.get(key);
   }
}
