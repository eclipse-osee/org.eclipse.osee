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
import org.eclipse.osee.framework.core.data.RelationTypeCacheUpdateResponse;
import org.eclipse.osee.framework.core.data.RelationTypeCacheUpdateResponse.RelationTypeRow;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;

/**
 * @author Roberto E. Escobar
 */
public class RelationTypeCacheUpdateResponseTranslator implements ITranslator<RelationTypeCacheUpdateResponse> {

   private enum Fields {
      COUNT,
      ROW
   }

   @Override
   public RelationTypeCacheUpdateResponse convert(PropertyStore store) throws OseeCoreException {
      List<RelationTypeRow> rows = new ArrayList<RelationTypeRow>();
      int rowCount = store.getInt(Fields.COUNT.name());
      for (int index = 0; index < rowCount; index++) {
         String[] rowData = store.getArray(createKey(Fields.ROW, index));
         rows.add(RelationTypeRow.fromArray(rowData));
      }
      return new RelationTypeCacheUpdateResponse(rows);
   }

   @Override
   public PropertyStore convert(RelationTypeCacheUpdateResponse object) throws OseeCoreException {
      PropertyStore store = new PropertyStore();
      List<RelationTypeRow> rows = object.getRelationTypeRows();
      for (int index = 0; index < rows.size(); index++) {
         RelationTypeRow row = rows.get(index);
         store.put(createKey(Fields.ROW, index), row.toArray());
      }
      store.put(Fields.COUNT.name(), rows.size());
      return store;
   }

   private String createKey(Fields key, int index) {
      return String.format("%s_%s", key.name(), index);
   }
}
