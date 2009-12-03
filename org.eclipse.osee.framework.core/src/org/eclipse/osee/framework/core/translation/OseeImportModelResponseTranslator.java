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
import org.eclipse.osee.framework.core.data.OseeImportModelResponse;
import org.eclipse.osee.framework.core.data.TableData;
import org.eclipse.osee.framework.core.enums.CoreTranslatorId;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.services.IDataTranslationService;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;

/**
 * @author Roberto E. Escobar
 */
public class OseeImportModelResponseTranslator implements ITranslator<OseeImportModelResponse> {

   private enum Fields {
      WAS_PERSISTED,
      EMF_COMPARE_REPORT,
      EMF_COMPARE_NAME,
      DIRTY_REPORT,
      TABLE_COUNT,
      TABLE,
      TITLE,
      HEADERS,
      ROW,
      ROW_COUNT;
   }

   private final IDataTranslationService service;

   public OseeImportModelResponseTranslator(IDataTranslationService service) {
      this.service = service;
   }

   @Override
   public OseeImportModelResponse convert(PropertyStore store) throws OseeCoreException {
      OseeImportModelResponse response = new OseeImportModelResponse();
      response.setPersisted(store.getBoolean(Fields.WAS_PERSISTED.name()));
      response.setComparisonSnapshotModelName(store.get(Fields.EMF_COMPARE_NAME.name()));
      response.setComparisonSnapshotModel(store.get(Fields.EMF_COMPARE_REPORT.name()));
      List<TableData> data = new ArrayList<TableData>();

      int numberOfTables = store.getInt(Fields.TABLE_COUNT.name());
      for (int index = 0; index < numberOfTables; index++) {
         PropertyStore innerStore = store.getPropertyStore(createKey(Fields.TABLE, index));
         TableData table = service.convert(innerStore, CoreTranslatorId.TABLE_DATA);
         data.add(table);
      }
      response.setReportData(data);
      return response;
   }

   @Override
   public PropertyStore convert(OseeImportModelResponse object) throws OseeCoreException {
      PropertyStore store = new PropertyStore();
      store.put(Fields.WAS_PERSISTED.name(), object.wasPersisted());
      store.put(Fields.EMF_COMPARE_NAME.name(), object.getComparisonSnapshotModelName());
      store.put(Fields.EMF_COMPARE_REPORT.name(), object.getComparisonSnapshotModel());

      List<TableData> tableData = object.getReportData();
      for (int index = 0; index < tableData.size(); index++) {
         TableData data = tableData.get(index);
         PropertyStore innerStore = service.convert(data, CoreTranslatorId.TABLE_DATA);
         store.put(createKey(Fields.TABLE, index), innerStore);
      }
      store.put(Fields.TABLE_COUNT.name(), tableData.size());
      return store;
   }

   private String createKey(Enum<?> prefix, int index) {
      return prefix.name() + "_" + index;
   }
}
