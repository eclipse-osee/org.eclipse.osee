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
import org.eclipse.osee.framework.branch.management.exchange.export.AbstractExportItem;
import org.eclipse.osee.framework.branch.management.exchange.export.ManifestExportItem;
import org.eclipse.osee.framework.branch.management.exchange.export.MetadataExportItem;
import org.eclipse.osee.framework.branch.management.exchange.export.RelationalExportItem;
import org.eclipse.osee.framework.branch.management.exchange.export.RelationalExportItemWithType;
import org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase;

/**
 * @author Roberto E. Escobar
 */
public class BranchExportTaskConfig {

   private static final String ARTIFACT_TYPE_ID = "art_type_id";
   private static final String ATTRIBUTE_TYPE_ID = "attr_type_id";
   private static final String RELATION_TYPE_ID = "rel_link_type_id";

   private static final String BRANCH_TABLE_QUERY =
         "SELECT br1.* FROM osee_define_branch br1, osee_join_export_import jex1 WHERE br1.branch_id = jex1.id1 AND jex1.query_id=? ORDER BY br1.branch_id";

   private static final String BRANCH_DEFINITION_QUERY =
         "SELECT br1.* FROM osee_branch_definitions br1, osee_join_export_import jex1 WHERE br1.mapped_branch_id = jex1.id1 AND jex1.query_id=? ORDER BY br1.mapped_branch_id";

   private static final String ATTRIBUTE_TABLE_QUERY =
         "SELECT DISTINCT (attr1.ATTR_ID), attr1.ART_ID, attr1.MODIFICATION_ID, attr1.VALUE, attr1.GAMMA_ID, attr1.ATTR_TYPE_ID, attr1.URI FROM osee_define_attribute attr1, osee_define_txs txs1, osee_define_tx_details txd1, osee_join_export_import jex1 WHERE attr1.gamma_id = txs1.gamma_id AND txs1.transaction_id = txd1.transaction_id AND txd1.branch_id = jex1.id1 AND jex1.query_id=? %s";

   private static final String RELATION_LINK_TABLE_QUERY =
         "SELECT DISTINCT (rel1.GAMMA_ID), rel1.REL_LINK_ID, rel1.B_ART_ID, rel1.A_ART_ID, rel1.MODIFICATION_ID, rel1.RATIONALE, rel1.REL_LINK_TYPE_ID, rel1.A_ORDER, rel1.B_ORDER FROM osee_define_rel_link rel1, osee_define_txs txs1, osee_define_tx_details txd1, osee_join_export_import jex1 WHERE rel1.gamma_id = txs1.gamma_id AND txs1.transaction_id = txd1.transaction_id AND txd1.branch_id = jex1.id1 AND jex1.query_id=? %s";

   private static final String TX_DETAILS_TABLE_QUERY =
         "SELECT DISTINCT (txd1.TRANSACTION_ID), txd1.TIME, txd1.AUTHOR, txd1.OSEE_COMMENT, txd1.BRANCH_ID, txd1.COMMIT_ART_ID, txd1.TX_TYPE FROM osee_define_tx_details txd1, osee_join_export_import jex1 WHERE txd1.branch_id = jex1.id1 AND jex1.query_id=? %s ORDER BY txd1.transaction_id";

   private static final String TXS_TABLE_QUERY =
         "SELECT DISTINCT (txs1.GAMMA_ID), txs1.TRANSACTION_ID, txs1.TX_CURRENT, txs1.MOD_TYPE FROM osee_define_txs txs1, osee_define_tx_details txd1, osee_join_export_import jex1 WHERE txs1.transaction_id = txd1.transaction_id AND txd1.branch_id = jex1.id1 AND jex1.query_id=? %s";

   private static final String ARTIFACT_TABLE_QUERY =
         "SELECT DISTINCT (art1.art_id), art1.GUID, art1.HUMAN_READABLE_ID, art1.ART_TYPE_ID FROM osee_define_artifact art1, osee_define_artifact_version artv1, osee_define_txs txs1, osee_define_tx_details txd1, osee_join_export_import jex1 WHERE art1.art_id = artv1.art_id AND artv1.gamma_id = txs1.gamma_id AND txs1.transaction_id = txd1.transaction_id AND txd1.branch_id = jex1.id1 AND jex1.query_id=? %s";

   private static final String ARTIFACT_VERSION_QUERY =
         "SELECT DISTINCT (artv1.GAMMA_ID), artv1.ART_ID, artv1.MODIFICATION_ID FROM osee_define_artifact_version artv1, osee_define_txs txs1, osee_define_tx_details txd1, osee_join_export_import jex1 WHERE artv1.gamma_id = txs1.gamma_id AND txs1.transaction_id = txd1.transaction_id AND txd1.branch_id = jex1.id1 AND jex1.query_id=? %s";

   private static final String ARTIFACT_TYPE_QUERY =
         "SELECT type1.name, type1.art_type_id FROM osee_define_artifact_type type1, osee_join_export_import jex1 WHERE type1.art_type_id = jex1.id1 AND jex1.query_id = ?";

   private static final String ATTRIBUTE_TYPE_QUERY =
         "SELECT type1.name, type1.attr_type_id FROM osee_define_attribute_type type1, osee_join_export_import jex1 WHERE type1.attr_type_id = jex1.id1 AND jex1.query_id = ?";

   private static final String RELATION_TYPE_QUERY =
         "SELECT type1.type_name, type1.rel_link_type_id FROM osee_define_rel_link_type type1, osee_join_export_import jex1 WHERE type1.rel_link_type_id = jex1.id1 AND jex1.query_id = ?";

   protected static List<AbstractExportItem> getTaskList() {
      List<AbstractExportItem> items = new ArrayList<AbstractExportItem>();
      items.add(new ManifestExportItem(0, "export.manifest", items));
      items.add(new MetadataExportItem(0, "export.db.schema", items));
      items.add(new RelationalExportItem(1, "osee.branch.data", SkynetDatabase.BRANCH_TABLE.toString(),
            BRANCH_TABLE_QUERY));
      items.add(new RelationalExportItem(2, "osee.branch.definitions", SkynetDatabase.BRANCH_DEFINITIONS.toString(),
            BRANCH_DEFINITION_QUERY));
      items.add(new RelationalExportItem(3, "osee.tx.details.data", SkynetDatabase.TRANSACTION_DETAIL_TABLE.toString(),
            TX_DETAILS_TABLE_QUERY));
      items.add(new RelationalExportItem(4, "osee.txs.data", SkynetDatabase.TRANSACTIONS_TABLE.toString(),
            TXS_TABLE_QUERY));
      items.add(new RelationalExportItemWithType(5, "osee.artifact.data", SkynetDatabase.ARTIFACT_TABLE.toString(),
            ARTIFACT_TYPE_ID, ARTIFACT_TABLE_QUERY, ARTIFACT_TYPE_QUERY));
      items.add(new RelationalExportItem(6, "osee.artifact.version.data",
            SkynetDatabase.ARTIFACT_VERSION_TABLE.toString(), ARTIFACT_VERSION_QUERY));
      items.add(new RelationalExportItemWithType(7, "osee.attribute.data",
            SkynetDatabase.ATTRIBUTE_VERSION_TABLE.toString(), ATTRIBUTE_TYPE_ID, ATTRIBUTE_TABLE_QUERY,
            ATTRIBUTE_TYPE_QUERY));
      items.add(new RelationalExportItemWithType(8, "osee.relation.link.data",
            SkynetDatabase.RELATION_LINK_VERSION_TABLE.toString(), RELATION_TYPE_ID, RELATION_LINK_TABLE_QUERY,
            RELATION_TYPE_QUERY));
      return items;
   }

   private BranchExportTaskConfig() {
   }
}
