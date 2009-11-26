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
package org.eclipse.osee.framework.core.translation;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.data.OseeEnumTypeCacheUpdateResponse;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;

/**
 * @author Roberto E. Escobar
 */
public class OseeEnumTypeCacheUpdateResponseTranslator implements ITranslator<OseeEnumTypeCacheUpdateResponse> {

   private enum Fields {
      ENUM_TYPE_ROW,
      ENUM_TYPE_COUNT,
      ENUM_ENTRY_ROW,
      ENUM_ENTRY_COUNT;
   }

   @Override
   public OseeEnumTypeCacheUpdateResponse convert(PropertyStore store) throws OseeCoreException {

      int enumTypeRowCount = store.getInt(Fields.ENUM_TYPE_COUNT.name());
      List<String[]> enumTypeRows = new ArrayList<String[]>(enumTypeRowCount);

      for (int index = 0; index < enumTypeRowCount; index++) {
         String[] rowData = store.getArray(createKey(Fields.ENUM_TYPE_ROW, index));
         enumTypeRows.add(rowData);
      }

      int enumEntryRowCount = store.getInt(Fields.ENUM_ENTRY_COUNT.name());
      List<String[]> enumEntryRows = new ArrayList<String[]>(enumEntryRowCount);

      for (int index = 0; index < enumEntryRowCount; index++) {
         String[] rowData = store.getArray(createKey(Fields.ENUM_ENTRY_ROW, index));
         enumEntryRows.add(rowData);
      }

      return new OseeEnumTypeCacheUpdateResponse(enumTypeRows, enumEntryRows);
   }

   @Override
   public PropertyStore convert(OseeEnumTypeCacheUpdateResponse response) throws OseeCoreException {
      PropertyStore store = new PropertyStore();

      List<String[]> enumTypeRows = response.getEnumTypeRows();
      for (int index = 0; index < enumTypeRows.size(); index++) {
         store.put(createKey(Fields.ENUM_TYPE_ROW, index), enumTypeRows.get(index));
      }
      store.put(Fields.ENUM_TYPE_COUNT.name(), enumTypeRows.size());

      List<String[]> enumEntryRows = response.getEnumEntryRows();
      for (int index = 0; index < enumEntryRows.size(); index++) {
         store.put(createKey(Fields.ENUM_ENTRY_ROW, index), enumEntryRows.get(index));
      }
      store.put(Fields.ENUM_ENTRY_COUNT.name(), enumEntryRows.size());

      return store;
   }

   private String createKey(Fields key, int index) {
      return String.format("%s_%s", key.name(), index);
   }
}
