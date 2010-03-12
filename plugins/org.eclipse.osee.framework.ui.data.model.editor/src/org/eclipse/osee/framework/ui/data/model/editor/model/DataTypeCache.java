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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Roberto E. Escobar
 */
public class DataTypeCache {

   private final Map<String, DataTypeSource> dataTypeSources;

   public DataTypeCache() {
      dataTypeSources = new HashMap<String, DataTypeSource>();
   }

   public void addDataTypeSource(DataTypeSource dataTypeSource) {
      dataTypeSources.put(dataTypeSource.getSourceId(), dataTypeSource);
   }

   public void removeDataTypeSource(String dataTypeSourceId) {
      dataTypeSources.remove(dataTypeSourceId);
   }

   public Set<String> getDataTypeSourceIds() {
      return dataTypeSources.keySet();
   }

   public DataTypeSource getDataTypeSourceById(String id) {
      return dataTypeSources.get(id);
   }

   public int getNumberOfSources() {
      return dataTypeSources.size();
   }

   public void clear() {
      dataTypeSources.clear();
   }
}
