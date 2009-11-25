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
import java.util.Map;
import org.eclipse.osee.framework.core.data.BranchCacheUpdateResponse;
import org.eclipse.osee.framework.core.data.BranchCacheUpdateResponse.BranchRow;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;

/**
 * @author Roberto E. Escobar
 */
public class RelationTypeCacheUpdateResponseTranslator implements ITranslator<BranchCacheUpdateResponse> {

   private enum Fields {
      BRANCH_COUNT,
      BRANCH_ROW,
      CHILD_TO_PARENT,
      BRANCH_TO_BASE_TX,
      BRANCH_TO_SRC_TX,
      BRANCH_TO_ASSOC_ART,
      BRANCH_TO_ALIASES;
   }

   @Override
   public BranchCacheUpdateResponse convert(PropertyStore store) throws OseeCoreException {
      List<BranchRow> rows = new ArrayList<BranchRow>();
      int rowCount = store.getInt(Fields.BRANCH_COUNT.name());
      for (int index = 0; index < rowCount; index++) {
         String[] rowData = store.getArray(createKey(Fields.BRANCH_ROW, index));
         rows.add(BranchRow.fromArray(rowData));
      }

      Map<Integer, Integer> childToParent = TranslationUtil.getMap(store, Fields.CHILD_TO_PARENT);
      Map<Integer, Integer> branchToBaseTx = TranslationUtil.getMap(store, Fields.BRANCH_TO_BASE_TX);
      Map<Integer, Integer> branchToSourceTx = TranslationUtil.getMap(store, Fields.BRANCH_TO_SRC_TX);
      Map<Integer, Integer> associatedArtifact = TranslationUtil.getMap(store, Fields.BRANCH_TO_ASSOC_ART);
      Map<Integer, String[]> branchAliases = TranslationUtil.getArrayMap(store, Fields.BRANCH_TO_ALIASES);
      return new BranchCacheUpdateResponse(rows, childToParent, branchToBaseTx, branchToSourceTx, associatedArtifact,
            branchAliases);
   }

   @Override
   public PropertyStore convert(BranchCacheUpdateResponse object) throws OseeCoreException {
      PropertyStore store = new PropertyStore();
      List<BranchRow> rows = object.getBranchRows();
      for (int index = 0; index < rows.size(); index++) {
         BranchRow row = rows.get(index);
         store.put(createKey(Fields.BRANCH_ROW, index), row.toArray());
      }
      store.put(Fields.BRANCH_COUNT.name(), rows.size());

      TranslationUtil.putMap(store, Fields.CHILD_TO_PARENT, object.getChildToParent());
      TranslationUtil.putMap(store, Fields.BRANCH_TO_BASE_TX, object.getBranchToBaseTx());
      TranslationUtil.putMap(store, Fields.BRANCH_TO_SRC_TX, object.getBranchToSourceTx());
      TranslationUtil.putMap(store, Fields.BRANCH_TO_ASSOC_ART, object.getBranchToAssocArt());
      TranslationUtil.putArrayMap(store, Fields.BRANCH_TO_ALIASES, object.getBranchAliases());
      return store;
   }

   private String createKey(Fields key, int index) {
      return String.format("%s_%s", key.name(), index);
   }
}
