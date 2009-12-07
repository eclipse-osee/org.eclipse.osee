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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.data.ArtifactTypeCacheUpdateResponse;
import org.eclipse.osee.framework.core.data.ArtifactTypeCacheUpdateResponse.ArtifactTypeRow;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.jdk.core.type.Triplet;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactTypeCacheUpdateResponseTranslator implements ITranslator<ArtifactTypeCacheUpdateResponse> {

   private enum Fields {
      ITEM_COUNT,
      ITEM_ROW,
      BASE_TO_SUPER_TYPES,
      BASE_BRANCH_ATTR;
   }

   @Override
   public ArtifactTypeCacheUpdateResponse convert(PropertyStore store) throws OseeCoreException {
      List<ArtifactTypeRow> rows = new ArrayList<ArtifactTypeRow>();
      Map<Integer, Integer[]> baseToSuper = new HashMap<Integer, Integer[]>();
      List<Triplet<Integer, Integer, Integer>> artAttrs = new ArrayList<Triplet<Integer, Integer, Integer>>();

      int rowCount = store.getInt(Fields.ITEM_COUNT.name());
      for (int index = 0; index < rowCount; index++) {
         String[] rowData = store.getArray(createKey(Fields.ITEM_ROW, index));
         rows.add(ArtifactTypeRow.fromArray(rowData));
      }

      TranslationUtil.loadIntArrayMap(baseToSuper, store, Fields.BASE_TO_SUPER_TYPES);
      TranslationUtil.loadTripletList(artAttrs, store, Fields.BASE_BRANCH_ATTR);
      return new ArtifactTypeCacheUpdateResponse(rows, baseToSuper, artAttrs);
   }

   @Override
   public PropertyStore convert(ArtifactTypeCacheUpdateResponse object) throws OseeCoreException {
      PropertyStore store = new PropertyStore();
      List<ArtifactTypeRow> rows = object.getArtTypeRows();
      for (int index = 0; index < rows.size(); index++) {
         ArtifactTypeRow row = rows.get(index);
         store.put(createKey(Fields.ITEM_ROW, index), row.toArray());
      }
      store.put(Fields.ITEM_COUNT.name(), rows.size());

      TranslationUtil.putIntArrayMap(store, Fields.BASE_TO_SUPER_TYPES, object.getBaseToSuperTypes());
      TranslationUtil.putTripletList(store, Fields.BASE_BRANCH_ATTR, object.getAttributeTypes());
      return store;
   }

   private String createKey(Fields key, int index) {
      return String.format("%s_%s", key.name(), index);
   }
}
