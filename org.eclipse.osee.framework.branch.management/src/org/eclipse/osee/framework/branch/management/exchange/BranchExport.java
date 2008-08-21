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
package org.eclipse.osee.framework.branch.management.exchange;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.branch.management.IBranchExport;
import org.eclipse.osee.framework.branch.management.exchange.export.AbstractExportItem;
import org.eclipse.osee.framework.branch.management.exchange.export.ManifestExportItem;
import org.eclipse.osee.framework.branch.management.exchange.export.MetadataExportItem;
import org.eclipse.osee.framework.branch.management.exchange.export.RelationalExportItem;
import org.eclipse.osee.framework.branch.management.exchange.export.RelationalExportItemWithType;
import org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase;
import org.eclipse.osee.framework.resource.management.Options;

/**
 * @author Roberto E. Escobar
 */
public class BranchExport implements IBranchExport {

   private static final String ARTIFACT_TYPE_ID = "art_type_id";
   private static final String ATTRIBUTE_TYPE_ID = "attr_type_id";
   private static final String RELATION_TYPE_ID = "rel_link_type_id";

   private static final String BRANCH_TABLE_QUERY =
         "SELECT br1.* FROM osee_define_branch br1, osee_join_export_import jex1 WHERE br1.branch_id = jex1.id1 AND jex1.query_id=?";

   private static final String ATTRIBUTE_TABLE_QUERY =
         "SELECT attr1.*, txd1.branch_id as part_of_branch_id FROM osee_define_attribute attr1, osee_define_txs txs1, osee_define_tx_details txd1, osee_join_export_import jex1 WHERE attr1.gamma_id = txs1.gamma_id AND txs1.transaction_id = txd1.transaction_id AND txd1.branch_id = jex1.id1 AND jex1.query_id=?";

   private static final String RELATION_LINK_TABLE_QUERY =
         "SELECT rel1.*, txd1.branch_id as part_of_branch_id FROM osee_define_rel_link rel1, osee_define_txs txs1, osee_define_tx_details txd1, osee_join_export_import jex1 WHERE rel1.gamma_id = txs1.gamma_id AND txs1.transaction_id = txd1.transaction_id AND txd1.branch_id = jex1.id1 AND jex1.query_id=?";

   private static final String TX_DETAILS_TABLE_QUERY =
         "SELECT txd1.*, txd1.branch_id as part_of_branch_id FROM osee_define_tx_details txd1, osee_join_export_import jex1 WHERE txd1.branch_id = jex1.id1 AND jex1.query_id=?";

   private static final String TXS_TABLE_QUERY =
         "SELECT txs1.*, txd1.branch_id as part_of_branch_id FROM osee_define_txs txs1, osee_define_tx_details txd1, osee_join_export_import jex1 WHERE txs1.transaction_id = txd1.transaction_id AND txd1.branch_id = jex1.id1 AND jex1.query_id=?";

   private static final String ARTIFACT_TABLE_QUERY =
         "SELECT art1.*, txd1.branch_id as part_of_branch_id FROM osee_define_artifact art1, osee_define_artifact_version artv1, osee_define_txs txs1, osee_define_tx_details txd1, osee_join_export_import jex1 WHERE art1.art_id = artv1.art_id AND artv1.gamma_id = txs1.gamma_id AND txs1.transaction_id = txd1.transaction_id AND txd1.branch_id = jex1.id1 AND jex1.query_id=?";

   private static final String ARTIFACT_VERSION_QUERY =
         "SELECT artv1.*, txd1.branch_id as part_of_branch_id FROM osee_define_artifact_version artv1, osee_define_txs txs1, osee_define_tx_details txd1, osee_join_export_import jex1 WHERE artv1.gamma_id = txs1.gamma_id AND txs1.transaction_id = txd1.transaction_id AND txd1.branch_id = jex1.id1 AND jex1.query_id=?";

   private static final String ARTIFACT_TYPE_QUERY =
         "SELECT type1.name, type1.art_type_id FROM osee_define_artifact_type type1, osee_join_export_import jex1 WHERE type1.art_type_id = jex1.id1 AND jex1.query_id = ?";

   private static final String ATTRIBUTE_TYPE_QUERY =
         "SELECT type1.name, type1.attr_type_id FROM osee_define_attribute_type type1, osee_join_export_import jex1 WHERE type1.attr_type_id = jex1.id1 AND jex1.query_id = ?";

   private static final String RELATION_TYPE_QUERY =
         "SELECT type1.type_name, type1.rel_link_type_id FROM osee_define_rel_link_type type1, osee_join_export_import jex1 WHERE type1.rel_link_type_id = jex1.id1 AND jex1.query_id = ?";

   public BranchExport() {
   }

   public void export(String exportName, Options options, List<Integer> branchIds) throws Exception {
      int[] branchIdsArray = new int[branchIds.size()];
      for (int index = 0; index < branchIds.size(); index++) {
         branchIdsArray[index] = branchIds.get(index);
      }
      export(exportName, options, branchIdsArray);
   }

   public void export(String exportName, Options options, int... branchIds) throws Exception {
      ExportController controller = new ExportController(this, exportName, options, branchIds);
      controller.execute();
   }

   protected List<AbstractExportItem> getTaskList() {
      List<AbstractExportItem> items = new ArrayList<AbstractExportItem>();
      items.add(new ManifestExportItem(0, "export.manifest", items));
      items.add(new MetadataExportItem(0, "export.db.schema", items));

      items.add(new RelationalExportItem(1, "osee.branch.data", SkynetDatabase.BRANCH_TABLE.toString(),
            BRANCH_TABLE_QUERY));
      items.add(new RelationalExportItem(2, "osee.tx.details.data", SkynetDatabase.TRANSACTION_DETAIL_TABLE.toString(),
            TX_DETAILS_TABLE_QUERY));
      items.add(new RelationalExportItem(3, "osee.txs.data", SkynetDatabase.TRANSACTIONS_TABLE.toString(),
            TXS_TABLE_QUERY));
      items.add(new RelationalExportItem(5, "osee.artifact.version.data",
            SkynetDatabase.ARTIFACT_VERSION_TABLE.toString(), ARTIFACT_VERSION_QUERY));

      items.add(new RelationalExportItemWithType(4, "osee.artifact.data", SkynetDatabase.ARTIFACT_TABLE.toString(),
            ARTIFACT_TYPE_ID, ARTIFACT_TABLE_QUERY, ARTIFACT_TYPE_QUERY));
      items.add(new RelationalExportItemWithType(6, "osee.attribute.data", SkynetDatabase.ATTRIBUTE_TABLE.toString(),
            ATTRIBUTE_TYPE_ID, ATTRIBUTE_TABLE_QUERY, ATTRIBUTE_TYPE_QUERY));
      items.add(new RelationalExportItemWithType(7, "osee.relation.link.data",
            SkynetDatabase.RELATION_LINK_TABLE.toString(), RELATION_TYPE_ID, RELATION_LINK_TABLE_QUERY,
            RELATION_TYPE_QUERY));
      return items;
   }
}
