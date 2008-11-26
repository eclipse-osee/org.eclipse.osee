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

package org.eclipse.osee.framework.skynet.core.test.nonproduction.components;

import junit.framework.TestCase;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.info.SupportedDatabase;

/**
 * @author Theron Virgin
 */
public class CommitTest extends TestCase {

   private static final String COMMITTED_NEW_AND_DELETED_ARTIFACTS =
         "SELECT txs1.gamma_id, txs1.transaction_id, det1.branch_id, art1.art_id, 0 as attr_id, 0 as rel_link_id FROM osee_tx_details det1, osee_txs txs1, osee_artifact_version art1 WHERE txs1.tx_current = " + TxChange.DELETED.getValue() + " AND det1.transaction_id = txs1.transaction_id AND txs1.gamma_id = art1.gamma_id  AND  NOT EXISTS (SELECT ('x') FROM osee_tx_details det2, osee_txs txs2, osee_artifact_version art2 WHERE txs2.mod_type != " + ModificationType.DELETED.getValue() + " AND det1.branch_id = det2.branch_id AND det2.transaction_id = txs2.transaction_id AND txs2.gamma_id = art2.gamma_id AND art2.art_id = art1.art_id)";
   private static final String COMMITTED_NEW_AND_DELETED_ATTRIBUTES =
         "SELECT txs1.gamma_id, txs1.transaction_id, det1.branch_id, 0 as art_id, att1.attr_id, 0 as rel_link_id FROM osee_tx_details det1, osee_txs txs1, osee_attribute att1 WHERE txs1.tx_current = " + TxChange.DELETED.getValue() + " AND det1.transaction_id = txs1.transaction_id AND txs1.gamma_id = att1.gamma_id  AND  NOT EXISTS (SELECT ('x') FROM osee_tx_details det2, osee_txs txs2, osee_attribute att2 WHERE txs2.mod_type != " + ModificationType.DELETED.getValue() + " AND det1.branch_id = det2.branch_id AND det2.transaction_id = txs2.transaction_id AND txs2.gamma_id = att2.gamma_id AND att2.attr_id = att1.attr_id)";
   private static final String COMMITTED_NEW_AND_DELETED_RELATIONS =
         "SELECT txs1.gamma_id, txs1.transaction_id, det1.branch_id, 0 as art_id, 0 as attr_id, rel1.rel_link_id FROM osee_tx_details det1, osee_txs txs1, osee_relation_link rel1 WHERE txs1.tx_current = " + TxChange.DELETED.getValue() + " AND det1.transaction_id = txs1.transaction_id AND txs1.gamma_id = rel1.gamma_id  AND  NOT EXISTS (SELECT ('x') FROM osee_tx_details det2, osee_txs txs2, osee_relation_link rel2 WHERE txs2.mod_type != " + ModificationType.DELETED.getValue() + " AND det1.branch_id = det2.branch_id AND det2.transaction_id = txs2.transaction_id AND txs2.gamma_id = rel2.gamma_id AND rel2.rel_link_id = rel1.rel_link_id)";
   private static final String[] NO_TX_CURRENT_SET =
         {
               "SELECT distinct t1.",
               ", det.branch_id FROM osee_tx_details det, osee_txs txs, ",
               " t1 WHERE det.transaction_id = txs.transaction_id AND txs.gamma_id = t1.gamma_id AND txs.tx_current = 0 %s SELECT distinct t2.",
               ", det.branch_id FROM osee_tx_details det, osee_txs txs, ",
               " t2 WHERE det.transaction_id = txs.transaction_id AND txs.gamma_id = t2.gamma_id AND txs.tx_current != 0"};

   private static final String[] MULTIPLE_TX_CURRENT_SET =
         {
               "SELECT resulttable.branch_id, resulttable.",
               ", COUNT(resulttable.branch_id) AS numoccurrences FROM (SELECT txd1.branch_id, t1.",
               " FROM osee_tx_details txd1, osee_txs txs1, ",
               " t1 WHERE txd1.transaction_id = txs1.transaction_id AND txs1.gamma_id = t1.gamma_id AND txs1.tx_current != 0) resulttable GROUP BY resulttable.branch_id, resulttable.",
               " HAVING(COUNT(resulttable.branch_id) > 1) order by branch_id"};

   /**
    * @param name
    */
   public CommitTest(String name) {
      super(name);
   }

   /* (non-Javadoc)
    * @see junit.framework.TestCase#setUp()
    */
   protected void setUp() throws Exception {
      super.setUp();
   }

   /* (non-Javadoc)
    * @see junit.framework.TestCase#tearDown()
    */
   protected void tearDown() throws Exception {
      super.tearDown();
   }

   public void testCommitFiltering() throws OseeCoreException {
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      try {
         chStmt.runPreparedQuery(COMMITTED_NEW_AND_DELETED_ARTIFACTS);
         if (chStmt.next()) {
            fail(String.format(
                  "Committed New and Deleted Artifact snuck through gamma_id = %d and transaction_id = %d",
                  chStmt.getInt("gamma_id"), chStmt.getInt("transaction_id")));
         }
      } finally {
         chStmt.close();
      }
      try {
         chStmt.runPreparedQuery(COMMITTED_NEW_AND_DELETED_ATTRIBUTES);
         if (chStmt.next()) {
            fail(String.format(
                  "Committed New and Deleted Attribute snuck through gamma_id = %d and transaction_id = %d",
                  chStmt.getInt("gamma_id"), chStmt.getInt("transaction_id")));
         }
      } finally {
         chStmt.close();
      }
      try {
         chStmt.runPreparedQuery(COMMITTED_NEW_AND_DELETED_RELATIONS);
         if (chStmt.next()) {
            fail(String.format(
                  "Committed New and Deleted Relation Links snuck through gamma_id = %d and transaction_id = %d",
                  chStmt.getInt("gamma_id"), chStmt.getInt("transaction_id")));
         }
      } finally {
         chStmt.close();
      }
      checkNoTxCurrent("art_id", "osee_artifact_version");
      checkNoTxCurrent("attr_id", "osee_attribute");
      checkNoTxCurrent("rel_link_id", "osee_relation_link");
      checkMultipleTxCurrent("art_id", "osee_artifact_version");
      checkMultipleTxCurrent("attr_id", "osee_attribute");
      checkMultipleTxCurrent("rel_link_id", "osee_relation_link");

   }

   private void checkNoTxCurrent(String dataId, String dataTable) throws OseeCoreException {
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      StringBuilder builder = new StringBuilder();
      builder.append(NO_TX_CURRENT_SET[0]);
      builder.append(dataId);
      builder.append(NO_TX_CURRENT_SET[1]);
      builder.append(dataTable);
      builder.append(String.format(NO_TX_CURRENT_SET[2], SupportedDatabase.getComplementSql()));
      builder.append(dataId);
      builder.append(NO_TX_CURRENT_SET[3]);
      builder.append(dataTable);
      builder.append(NO_TX_CURRENT_SET[4]);

      try {
         chStmt.runPreparedQuery(builder.toString());
         if (chStmt.next()) {
            fail(String.format("No TX Current Set Failed for dataId = %s and dataTable = %s", dataId, dataTable));
         }
      } finally {
         chStmt.close();
      }
   }

   private void checkMultipleTxCurrent(String dataId, String dataTable) throws OseeCoreException {
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      StringBuilder builder = new StringBuilder();
      builder.append(MULTIPLE_TX_CURRENT_SET[0]);
      builder.append(dataId);
      builder.append(MULTIPLE_TX_CURRENT_SET[1]);
      builder.append(dataId);
      builder.append(MULTIPLE_TX_CURRENT_SET[2]);
      builder.append(dataTable);
      builder.append(MULTIPLE_TX_CURRENT_SET[3]);
      builder.append(dataId);
      builder.append(MULTIPLE_TX_CURRENT_SET[4]);

      try {
         chStmt.runPreparedQuery(builder.toString());
         if (chStmt.next()) {
            fail(String.format("Multiple TX Current Set Failed for dataId = %s and dataTable = %s", dataId, dataTable));
         }
      } finally {
         chStmt.close();
      }
   }
}
