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
import java.util.List;

/**
 * @author Roberto E. Escobar
 */
public class ODMGraph {

   private final List<DataType> types;
   private final DataTypeCache cache;

   public ODMGraph(DataTypeCache cache) {
      this.types = new ArrayList<DataType>();
      this.cache = cache;
   }

   public List<DataType> getTypes() {
      return types;
   }

   public boolean add(DataType dataType) {
      return types.add(dataType);
   }

   public boolean addAll(Collection<? extends DataType> dataTypes) {
      return types.addAll(dataTypes);
   }

   public boolean remove(DataType dataType) {
      return this.types.remove(dataType);
   }

   public boolean removeAll(Collection<? extends DataType> dataTypes) {
      return types.removeAll(dataTypes);
   }

   public DataTypeCache getCache() {
      return cache;
   }
}
