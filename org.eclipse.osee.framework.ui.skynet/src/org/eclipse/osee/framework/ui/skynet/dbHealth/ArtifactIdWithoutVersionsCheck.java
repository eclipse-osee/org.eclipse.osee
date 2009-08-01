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
package org.eclipse.osee.framework.ui.skynet.dbHealth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.ConnectionHandlerStatement;
import org.eclipse.osee.framework.jdk.core.util.AHTML;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactIdWithoutVersionsCheck extends DatabaseHealthOperation {

   private static final String GET_INVALID_A_ART_IDS =
         "select item.a_art_id as artId, item.rel_link_id as itemId from osee_relation_link item where NOT EXISTS (select oav.art_id from osee_artifact_version oav where oav.art_id = item.a_art_id)";

   private static final String GET_INVALID_B_ART_IDS =
         "select item.b_art_id as artId, item.rel_link_id as itemId from osee_relation_link item where NOT EXISTS (select oav.art_id from osee_artifact_version oav where oav.art_id = item.b_art_id)";

   private static final String GET_INVALID_ATTR_IDS_ART_IDS =
         "select item.art_id as artId, item.attr_id as itemId from osee_attribute item where NOT EXISTS (select oav.art_id from osee_artifact_version oav where oav.art_id = item.art_id)";

   private static final String GET_INVALID_ART_IDS =
         "select item.art_id as artId from osee_artifact item where NOT EXISTS (select oav.art_id from osee_artifact_version oav where oav.art_id = item.art_id)";

   private static final String GET_INVALID_ACL_ART_IDS =
         "select item.art_id as artId from osee_artifact_acl item where NOT EXISTS (select oav.art_id from osee_artifact_version oav where oav.art_id = item.art_id)";

   /**
    * @param operationName
    */
   public ArtifactIdWithoutVersionsCheck() {
      super("Artifact Id Without osee_artifact_version Table Entry");
   }

   @Override
   protected void doHealthCheck(IProgressMonitor monitor) throws Exception {
      Set<Integer> allInvalidArtIds = new HashSet<Integer>();
      List<ItemEntry> itemsToDelete = new ArrayList<ItemEntry>();

      itemsToDelete.add(new ItemEntry("osee_relation_link", "rel_link_id", "a_art_id", // 
            getInvalidEntries(monitor, allInvalidArtIds, GET_INVALID_A_ART_IDS, true)));

      itemsToDelete.add(new ItemEntry("osee_relation_link", "rel_link_id", "b_art_id", //
            getInvalidEntries(monitor, allInvalidArtIds, GET_INVALID_B_ART_IDS, true)));

      itemsToDelete.add(new ItemEntry("osee_attribute", "attr_id", "art_id", //
            getInvalidEntries(monitor, allInvalidArtIds, GET_INVALID_ATTR_IDS_ART_IDS, true)));

      itemsToDelete.add(new ItemEntry("osee_artifact_acl", "art_id", "art_id", //
            getInvalidEntries(monitor, allInvalidArtIds, GET_INVALID_ACL_ART_IDS, false)));

      int beforeArtifactCheck = allInvalidArtIds.size();
      itemsToDelete.add(new ItemEntry("osee_artifact", "art_id", "art_id", //
            getInvalidEntries(monitor, allInvalidArtIds, GET_INVALID_ART_IDS, false)));

      setItemsToFix(allInvalidArtIds.size());
      createReport(monitor, beforeArtifactCheck, getItemsToFixCount(), itemsToDelete);

      if (isFixOperationEnabled() && getItemsToFixCount() > 0) {
         for (ItemEntry entry : itemsToDelete) {
            if (!entry.invalids.isEmpty()) {
               String deleteSql = String.format("delete from %s where %s = ?", entry.table, entry.itemIdName);
               List<Object[]> dataList = new ArrayList<Object[]>();
               for (Integer item : entry.invalids) {
                  dataList.add(new Object[] {item});
               }
               ConnectionHandler.runBatchUpdate(deleteSql, dataList);
            }
         }
      }
      getSummary().append(String.format("Found %s invalid artIds referenced\n", getItemsToFixCount()));
      monitor.worked(calculateWork(0.50));
   }

   private void createReport(IProgressMonitor monitor, int totalBeforeCheck, int totalArtIds, List<ItemEntry> itemsToDelete) throws IOException {
      appendToDetails(AHTML.beginMultiColumnTable(100, 1));
      appendToDetails(AHTML.beginMultiColumnTable(100, 1));
      appendToDetails(AHTML.addHeaderRowMultiColumnTable(new String[] {"TABLE", "REFERENCED_BY", "TOTAL INVALIDS"}));
      for (ItemEntry entry : itemsToDelete) {
         appendToDetails(AHTML.addRowMultiColumnTable(new String[] {entry.table, entry.invalidField,
               String.valueOf(entry.invalids.size())}));
      }
      appendToDetails(AHTML.endMultiColumnTable());
      monitor.worked(calculateWork(0.10));
      checkForCancelledStatus(monitor);
   }

   private Set<Integer> getInvalidEntries(IProgressMonitor monitor, Set<Integer> allInvalidArtIds, String query, boolean hasItemId) throws OseeDataStoreException {
      Set<Integer> toReturn = new HashSet<Integer>();
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      try {
         chStmt.runPreparedQuery(query);
         while (chStmt.next()) {
            if (hasItemId) {
               toReturn.add(chStmt.getInt("itemId"));
            }
            allInvalidArtIds.add(chStmt.getInt("artId"));
         }
      } finally {
         chStmt.close();
      }
      monitor.worked(calculateWork(0.10));
      checkForCancelledStatus(monitor);
      return hasItemId ? toReturn : allInvalidArtIds;
   }

   private final class ItemEntry {
      private final String table;
      private final String itemIdName;
      private final String invalidField;
      private final Set<Integer> invalids;

      public ItemEntry(String table, String itemIdName, String invalidField, Set<Integer> invalids) {
         super();
         this.table = table;
         this.itemIdName = itemIdName;
         this.invalidField = invalidField;
         this.invalids = invalids;
      }

   }

   @Override
   public String getCheckDescription() {
      return "Verifies that artifact entries in the relation, attribute and artifact tables have a valid entry in the osee_artifact_version table.";
   }

   @Override
   public String getFixDescription() {
      return "Removes invalid data from the corresponding tables, however does not clean up any addressing that might be left behind.";
   }
}
