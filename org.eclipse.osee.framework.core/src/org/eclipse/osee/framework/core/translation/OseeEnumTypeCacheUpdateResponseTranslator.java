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
import org.eclipse.osee.framework.core.data.OseeEnumTypeCacheUpdateResponse.OseeEnumTypeRow;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;

/**
 * @author Roberto E. Escobar
 */
public class OseeEnumTypeCacheUpdateResponseTranslator implements ITranslator<OseeEnumTypeCacheUpdateResponse> {

   private enum Fields {
      BRANCH_COUNT,
      BRANCH_ROW;
   }

   @Override
   public OseeEnumTypeCacheUpdateResponse convert(PropertyStore store) throws OseeCoreException {
      List<OseeEnumTypeRow> rows = new ArrayList<OseeEnumTypeRow>();
      int rowCount = store.getInt(Fields.BRANCH_COUNT.name());
      for (int index = 0; index < rowCount; index++) {
         String[] rowData = store.getArray(createKey(Fields.BRANCH_ROW, index));
         rows.add(OseeEnumTypeRow.fromArray(rowData));
      }
      return new OseeEnumTypeCacheUpdateResponse(rows);
   }

   @Override
   public PropertyStore convert(OseeEnumTypeCacheUpdateResponse object) throws OseeCoreException {
      PropertyStore store = new PropertyStore();

      return store;
   }

   private String createKey(Fields key, int index) {
      return String.format("%s_%s", key.name(), index);
   }
}
