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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.info.SupportedDatabase;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.osee.framework.ui.skynet.results.html.XResultPage.Manipulations;

/**
 * @author Theron Virgin
 */
public class HealthHelper {
   public static final String ALL_BACKING_GAMMAS =
         "(SELECT gamma_id FROM osee_artifact_version UNION SELECT gamma_id FROM osee_attribute UNION SELECT gamma_id FROM osee_relation_link)";

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

   private static final String[] NO_TX_CURRENT_CLEANUP =
         {
               "UPDATE osee_txs set tx_current = CASE WHEN mod_type = 3 THEN 2 WHEN mod_type = 5 THEN 3 ELSE 1 END WHERE (gamma_id, transaction_id) = (SELECT txs1.gamma_id, txs1.transaction_id FROM osee_txs txs1, ",
               " t1 WHERE t1.",
               " = ? AND t1.gamma_id = txs1.gamma_id AND txs1.transaction_id = (SELECT max(txs.transaction_id) FROM osee_tx_details det, osee_txs txs, ",
               " t2 WHERE det.branch_id = ? AND det.transaction_id = txs.transaction_id AND txs.gamma_id = t2.gamma_id AND t2.",
               " = ?))"};

   private static final String[] DUPLICATE_TX_CURRENT_CLEANUP =
         {
               "UPDATE osee_txs set tx_current = 0 WHERE (gamma_id, transaction_id) in (SELECT txs1.gamma_id, txs1.transaction_id FROM osee_txs txs1, ",
               " t1 WHERE t1.",
               " = ? AND t1.gamma_id = txs1.gamma_id AND txs1.transaction_id != (SELECT max(txs.transaction_id) FROM osee_tx_details det, osee_txs txs, ",
               " t2 WHERE det.branch_id = ? AND txs.tx_current != 0 AND det.transaction_id = txs.transaction_id AND txs.gamma_id = t2.gamma_id AND t2.",
               " = ?)AND txs1.transaction_id in (Select transaction_id FROM osee_tx_details WHERE branch_id = ?))"};

   private static final boolean DEBUG =
         "TRUE".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.osee.framework.ui.skynet/debug/Blam"));

   public static void displayForCleanUp(String header, StringBuffer sbFull, StringBuilder builder, boolean verify, List<Object[]> set, String toPrint) {
      int count = 0;
      sbFull.append(AHTML.addHeaderRowMultiColumnTable(new String[] {header}));
      sbFull.append(AHTML.addRowSpanMultiColumnTable(header + toPrint, 1));
      for (Object[] value : set) {
         count++;
         sbFull.append(AHTML.addRowMultiColumnTable(new String[] {value[0].toString()}));
      }
      builder.append(verify ? "Found " : "Fixed ");
      builder.append(count);
      builder.append(" ");
      builder.append(header);
      builder.append(toPrint);
   }

   public static List<Object[]> runSingleResultQuery(String sql, String dbColumn) throws OseeCoreException {
      List<Object[]> foundItems = new LinkedList<Object[]>();
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      try {
         chStmt.runPreparedQuery(sql);
         while (chStmt.next()) {
            foundItems.add(new Object[] {chStmt.getInt(dbColumn)});
         }
      } finally {
         chStmt.close();
      }
      return foundItems;
   }

   public static void endTable(StringBuffer sbFull, String taskName) throws OseeCoreException {
      sbFull.append(AHTML.endMultiColumnTable());
      XResultData rd = new XResultData();
      rd.addRaw(sbFull.toString());
      rd.report(taskName, Manipulations.RAW_HTML);

   }

   public static HashSet<Pair<Integer, Integer>> getNoTxCurrentSet(String dataId, String dataTable, StringBuilder builder, String data) throws OseeCoreException {
      String sql =
            NO_TX_CURRENT_SET[0] + dataId + NO_TX_CURRENT_SET[1] + dataTable + String.format(NO_TX_CURRENT_SET[2],
                  SupportedDatabase.getComplementSql()) + dataId + NO_TX_CURRENT_SET[3] + dataTable + NO_TX_CURRENT_SET[4];
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      HashSet<Pair<Integer, Integer>> noneSet = new HashSet<Pair<Integer, Integer>>();

      long time = System.currentTimeMillis();
      try {
         chStmt.runPreparedQuery(sql);
         while (chStmt.next()) {
            noneSet.add(new Pair<Integer, Integer>(chStmt.getInt(dataId), chStmt.getInt("branch_id")));
         }
      } finally {
         chStmt.close();
      }
      builder.append("Found ");
      builder.append(noneSet.size());
      builder.append(data);
      builder.append(" that have no tx_current value set\n");
      if (DEBUG) {
         System.out.println(String.format("%sTxCurrent: The get No TX Set Query took %s", data,
               Lib.getElapseString(time)));
      }
      return noneSet;
   }

   public static HashSet<LocalTxData> getMultipleTxCurrentSet(String dataId, String dataTable, StringBuilder builder, String data) throws OseeCoreException {
      String sql =
            MULTIPLE_TX_CURRENT_SET[0] + dataId + MULTIPLE_TX_CURRENT_SET[1] + dataId + MULTIPLE_TX_CURRENT_SET[2] + dataTable + MULTIPLE_TX_CURRENT_SET[3] + dataId + MULTIPLE_TX_CURRENT_SET[4];
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      HashSet<LocalTxData> multipleSet = new HashSet<LocalTxData>();

      long time = System.currentTimeMillis();
      try {
         chStmt.runPreparedQuery(sql);
         while (chStmt.next()) {
            multipleSet.add(new LocalTxData(chStmt.getInt(dataId), chStmt.getInt("branch_id"),
                  chStmt.getInt("numoccurrences")));
         }
         builder.append("Found ");
         builder.append(multipleSet.size());
         builder.append(data);
         builder.append(" that have multiple tx_current values set\n");

      } finally {
         chStmt.close();
      }
      if (DEBUG) {
         System.out.println(String.format("%sTxCurrent: The get%s Query took %s", data, data, Lib.getElapseString(time)));
      }
      return multipleSet;
   }

   public static void cleanMultipleTxCurrent(String dataId, String dataTable, StringBuilder builder, HashSet<LocalTxData> multipleSet) throws OseeCoreException {
      String sql =
            DUPLICATE_TX_CURRENT_CLEANUP[0] + dataTable + DUPLICATE_TX_CURRENT_CLEANUP[1] + dataId + DUPLICATE_TX_CURRENT_CLEANUP[2] + dataTable + DUPLICATE_TX_CURRENT_CLEANUP[3] + dataId + DUPLICATE_TX_CURRENT_CLEANUP[4];

      List<Object[]> insertParameters = new LinkedList<Object[]>();

      for (LocalTxData link : multipleSet) {
         insertParameters.add(new Object[] {link.dataId, link.branchId, link.dataId, link.branchId});
      }
      int total = 0;
      if (insertParameters.size() > 0) {
         total = ConnectionHandler.runBatchUpdate(sql, insertParameters);
      }
      builder.append("Fixed " + total + " Tx_Current duplication errors\n");
   }

   public static void cleanNoTxCurrent(String dataId, String dataTable, StringBuilder builder, HashSet<Pair<Integer, Integer>> noneSet) throws OseeCoreException {
      String sql =
            NO_TX_CURRENT_CLEANUP[0] + dataTable + NO_TX_CURRENT_CLEANUP[1] + dataId + NO_TX_CURRENT_CLEANUP[2] + dataTable + NO_TX_CURRENT_CLEANUP[3] + dataId + NO_TX_CURRENT_CLEANUP[4];

      List<Object[]> insertParameters = new LinkedList<Object[]>();

      for (Pair<Integer, Integer> pair : noneSet) {
         insertParameters.add(new Object[] {pair.getKey(), pair.getValue(), pair.getKey()});
      }
      int total = 0;
      if (insertParameters.size() > 0) {
         total = ConnectionHandler.runBatchUpdate(sql, insertParameters);
      }
      builder.append("Fixed " + total + " Tx_Current not set errors\n");
   }

   public static void dumpDataNone(StringBuffer sbFull, HashSet<Pair<Integer, Integer>> noneSet) {
      int counter = 0;
      for (Pair<Integer, Integer> pairs : noneSet) {
         sbFull.append(AHTML.addRowMultiColumnTable(new String[] {String.valueOf(counter++),
               String.valueOf(pairs.getKey()), String.valueOf(pairs.getValue())}));
      }
   }

   public static void dumpDataMultiple(StringBuffer sbFull, HashSet<LocalTxData> multipleSet) {
      int counter = 0;
      for (LocalTxData link : multipleSet) {
         sbFull.append(AHTML.addRowMultiColumnTable(new String[] {String.valueOf(counter++),
               String.valueOf(link.dataId), String.valueOf(link.branchId), String.valueOf(link.number)}));
      }
   }
}
