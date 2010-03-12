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
package org.eclipse.osee.framework.ui.data.model.editor.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Roberto E. Escobar
 */
public class ODMDiagram extends NodeModel {

   private final Set<DataType> types;
   private final DataTypeCache cache;

   public ODMDiagram(DataTypeCache cache) {
      this.types = new HashSet<DataType>();
      this.cache = cache;
   }

   public List<DataType> getContent() {
      return new ArrayList<DataType>(types);
   }

   public boolean add(DataType dataType) {
      boolean result = types.add(dataType);
      if (result) {
         fireModelEvent(null);
      }
      return result;
   }

   public boolean addAll(Collection<? extends DataType> dataTypes) {
      boolean result = types.addAll(dataTypes);
      if (result) {
         fireModelEvent(null);
      }
      return result;
   }

   public boolean remove(DataType dataType) {
      boolean result = this.types.remove(dataType);
      if (result) {
         fireModelEvent(null);
      }
      return result;
   }

   public boolean removeAll(Collection<? extends DataType> dataTypes) {
      boolean result = types.removeAll(dataTypes);
      if (result) {
         fireModelEvent(null);
      }
      return result;
   }

   public DataTypeCache getCache() {
      return cache;
   }
}
