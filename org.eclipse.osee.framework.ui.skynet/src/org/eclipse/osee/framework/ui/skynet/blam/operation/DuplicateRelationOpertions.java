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
package org.eclipse.osee.framework.ui.skynet.blam.operation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyQuadHashMap;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyTripleHashMap;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;

/**
 * @author Andrew M. Finkbeiner
 * @author Roberto E. Escobar
 */
public class DuplicateRelationOpertions extends AbstractBlam {

   private static final String checkGammaCase =
         "select rel1.rel_link_type_id,  rel1.b_art_id, txd1.branch_id, rel1.a_order, txs1.gamma_id, rel1.a_art_id, rel1.b_order_value, rel1.REL_LINK_ID, txd1.tx_type as real_tx_type, txs1.* from osee_tx_details txd1, osee_relation_link rel1, osee_txs txs1  where   txd1.transaction_id = txs1.transaction_id         and txs1.gamma_id = rel1.gamma_id         and txs1.tx_current = 1          and rel1.b_art_id = ?        and rel1.a_art_id = ?         and rel1.rel_link_type_id = ?       and txd1.TX_TYPE = 0    order by txd1.branch_id, rel1.rel_link_type_id, rel1.b_art_id, rel1.b_order_value";

   private static final String update_txs =
         "update osee_txs set tx_current = ?, mod_type = ? where gamma_id = ? and transaction_id = ?";
   private static final String update_version = "update osee_relation_link set modification_id = ? where gamma_id = ?";
   private static final String SELECT_DUPLICATE_RELATIONS =
         "select rel1.a_art_id, rel1.b_art_id, rel1.rel_link_type_id, txd1.branch_id, rel1.REL_LINK_ID, rel1.gamma_id, txd1.transaction_id from osee_relation_link rel1, osee_txs txs1, osee_tx_details txd1 , (select rel2.*, txd2.branch_id, txd2.transaction_id from osee_relation_link rel2, osee_txs txs2, osee_tx_details txd2 where txs2.transaction_id = txd2.transaction_id and txs2.tx_current = 1 and txs2.gamma_id = rel2.gamma_id) other_rel_link where txs1.transaction_id = txd1.transaction_id and txs1.tx_current = 1 and txs1.gamma_id = rel1.gamma_id and txd1.branch_id = other_rel_link.branch_id and rel1.a_art_id = other_rel_link.a_art_id and rel1.b_art_id = other_rel_link.b_art_id and rel1.REL_LINK_TYPE_ID = other_rel_link.rel_link_type_id and rel1.REL_LINK_ID <> other_rel_link.rel_link_id order by txd1.branch_id, rel1.rel_link_type_id, rel1.a_art_id, rel1.b_art_id, rel1.rel_link_id";
   private final CompositeKeyQuadHashMap<Integer, Integer, Integer, Integer, RelationInfo> relationInfo =
         new CompositeKeyQuadHashMap<Integer, Integer, Integer, Integer, RelationInfo>(1000);

   int[] gammaIds =
         new int[] {852176, 1806465, 1806464, 1806466, 1806468, 852190, 852191, 1543816, 1807037, 1806492, 1806481,
               1806485, 1713318, 177456, 1806507, 177457, 177458, 177459, 177460, 177461, 177462, 177463, 1806499,
               1806498, 1806497, 1806502, 1806501, 1806500, 1806522, 173862, 852192, 852199, 1611799, 1498880, 177449,
               1495752, 177451, 1611800, 1806513, 177453, 177455, 1806516, 177454, 1575261, 223175, 1807087, 1807086,
               1807085, 1807084, 1800592, 1164028, 1807088, 1807089, 3249936, 1575289, 1575288, 1575284, 1575281,
               1575276, 1508988, 2848359, 1575272, 2848358, 2848361, 1508983, 2848360, 2848363, 1575268, 2848362,
               1508979, 1575265, 1137539, 898640, 173020, 1806903, 174666, 1806908, 1806364, 1806904, 1806863, 1806650,
               839426, 1812929, 1806391, 1806389, 1589156, 1806393, 264571, 1806392, 1589154, 1589155, 1806400, 264757,
               1806412, 1806413, 1806414, 3198866, 1806411, 1812923, 1812922, 467177, 1806422, 1806417, 1812925,
               1812924, 1806418, 1311395, 1482583, 1311387, 1806438, 1806244, 1806439, 1806245, 1806914, 1806246,
               1806436, 1806247, 1806437, 2078493, 2078494, 1806243, 1806446, 1806252, 1806447, 1806444, 2092598,
               1806445, 1806442, 1806443, 1806249, 1806440, 1806441, 1806251, 1806454, 1806453, 1806452, 1806451,
               1953629, 1806450, 2080927, 1806449, 1806448, 1806463, 1806462, 1806461, 1806460, 1806459, 261366,
               1806458, 1806457, 1806456, 261365};

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.AbstractBlam#getName()
    */
   @Override
   public String getName() {
      return "Duplicate Relation Opertions";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#runOperation(org.eclipse.osee.framework.ui.skynet.blam.VariableMap, org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.core.runtime.IProgressMonitor)
    */
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      try {
         chStmt.runPreparedQuery(SELECT_DUPLICATE_RELATIONS);
         while (chStmt.next()) {
            addRelationEntry(chStmt.getInt("a_art_id"), chStmt.getInt("b_art_id"), chStmt.getInt("rel_link_type_id"),
                  chStmt.getInt("branch_id"), chStmt.getInt("rel_link_id"), chStmt.getLong("gamma_id"),
                  chStmt.getInt("transaction_id"));
         }
      } finally {
         chStmt.close();
      }
      Collection<RelationInfo> values = relationInfo.values();
      this.print(String.format("Found [%d] potential conflicts.", values.size()));

      int oddCases = 0;
      int goodCases = 0;
      int interestingCases = 0;
      int casesWithDuplicateItemsInSameTransaction = 0;
      for (RelationInfo info : values) {

         for (int i = 0; i < info.relLinksToGammas.size(); i++) {
            for (int j = i + 1; j < info.relLinksToGammas.size(); j++) {
               if (info.relLinksToGammas.get(i).getValue().intValue() == info.relLinksToGammas.get(j).getValue().intValue()) {
                  casesWithDuplicateItemsInSameTransaction++;
               }
            }
         }
         if (info.art_a == 81891 && info.art_b == 107083) {
            System.out.println("look at me");

         }
         if (info.relLinksToGammas.size() < 2) {
            oddCases++;
         } else if (info.relLinksToGammas.size() > 2) {
            OseeLog.log(DuplicateRelationOpertions.class, Level.INFO, String.format("aArt[%d], bArt[%d]", info.art_a,
                  info.art_b));
            interestingCases++;
         } else {
            goodCases++;
         }
      }

      //      List<Object[]> batchedTxsUpdates = new ArrayList<Object[]>();
      //      List<Object[]> batchedVersionUpdates = new ArrayList<Object[]>();
      HashSet<Long> gammas = new HashSet<Long>();
      CompositeKeyTripleHashMap<Integer, Integer, Integer, String> messages =
            new CompositeKeyTripleHashMap<Integer, Integer, Integer, String>();
      //ModificationType.DELETED;/
      //      TxChange.DELETED;
      //      FileOutputStream fos = new FileOutputStream(new File("DuplicateRelationRecovery.txt"));
      for (RelationInfo info : values) {
         int count = 0;
         int modCount = 0;
         long gammaId = 0;
         try {
            chStmt.runPreparedQuery(checkGammaCase, info.art_b, info.art_a, info.rel_link_type);
            while (chStmt.next()) {
               if (chStmt.getInt("mod_type") == 2) {
                  modCount++;
                  gammaId = chStmt.getLong("gamma_id");
               }
               count++;
            }
         } catch (OseeCoreException ex) {
            ex.printStackTrace();
         } finally {
            chStmt.close();
         }
         boolean added = false;
         if (modCount == 1 && count == 2) {
            for (int id : gammaIds) {
               if (gammaId == id) {
                  gammas.add(gammaId);
                  added = true;
                  break;
               }
            }
            if (!added) {
               System.out.println("Other Odd Case");
            }
         } else {
            if (!messages.containsKey(info.art_a, info.art_b, info.rel_link_type)) {
               messages.put(info.art_a, info.art_b, info.rel_link_type, String.format(
                     "modCount[%d] count[%d] aArt[%d] bArt[%d] linkType[%d]", modCount, count, info.art_a, info.art_b,
                     info.rel_link_type));
            }
         }

         //         for (int i = 0; i < info.relLinksToGammas.size(); i++) {
         //            if (info.art_a == 81891 && info.art_b == 107083) {
         //               System.out.println("look at me");
         //            }
         //            gammas.add(info.relLinksToGammas.get(i).getKey());
         //            fos.write(String.format("%d, %d\n", info.relLinksToGammas.get(i).getKey(),
         //                  info.relLinksToGammas.get(i).getValue()).getBytes());
         //            batchedTxsUpdates.add(new Object[] { TxChange.DELETED.getValue(),
         //                   ModificationType.DELETED.getValue(), 
         //                  info.relLinksToGammas.get(i).getKey(),  info.relLinksToGammas.get(i).getValue()});
         //            batchedVersionUpdates.add(new Object[] { ModificationType.DELETED.getValue(),
         //                   info.relLinksToGammas.get(i).getKey()});
         //         }
      }
      //      fos.close();
      //      System.out.println("here we go.");
      //      System.out.println("wait here");

      System.out.println(String.format("%d gamma id's that should be modified.", gammas.size()));
      System.out.println(Arrays.deepToString(gammas.toArray()));
      for (String str : messages.values()) {
         System.out.println(str);
      }

      //      ConnectionHandler.runPreparedUpdateBatch(this.update_txs, batchedTxsUpdates);
      //      ConnectionHandler.runPreparedUpdateBatch(this.update_version, batchedVersionUpdates);

      //      System.out.println(values.size());
      //*/
   }

   @Override
   public String getXWidgetsXml() {
      return "<xWidgets><XWidget xwidgetType=\"XText\" displayName=\"Branch List\" /><XWidget xwidgetType=\"XBranchSelectWidget\" displayName=\"Parent Branch\" /></xWidgets>";
   }

   /**
    * @param int1
    * @param long1
    * @param int2
    * @param long2
    * @param int3
    * @param int4
    * @param int5
    * @param int6
    */
   private void addRelationEntry(int a_art_id, int b_art_id, int relLinkTypeId, int branchId, int relLinkId, long gammaId, int transactionId) {
      RelationInfo info = relationInfo.get(a_art_id, b_art_id, relLinkTypeId, branchId);
      if (info == null) {
         info = new RelationInfo(a_art_id, b_art_id, relLinkTypeId, branchId);
         relationInfo.put(a_art_id, b_art_id, relLinkTypeId, branchId, info);
      }
      info.add(gammaId, transactionId);
   }

   private class RelationInfo {
      private final int rel_link_type;
      private final int art_a;
      private final int art_b;
      private final List<Pair<Long, Integer>> relLinksToGammas;

      RelationInfo(int a_art_id, int b_art_id, int relLinkTypeId, int branchId) {
         this.rel_link_type = relLinkTypeId;
         this.art_a = a_art_id;
         this.art_b = b_art_id;
         relLinksToGammas = new ArrayList<Pair<Long, Integer>>();
      }

      public void add(long gammaId, int transactionId) {
         for (int i = 0; i < relLinksToGammas.size(); i++) {
            if (relLinksToGammas.get(i).getKey().longValue() == gammaId && relLinksToGammas.get(i).getValue().intValue() == transactionId) {
               return;
            }
         }
         for (int i = 0; i < relLinksToGammas.size(); i++) {
            if (transactionId > relLinksToGammas.get(i).getValue().intValue()) {
               relLinksToGammas.add(i, new Pair<Long, Integer>(gammaId, transactionId));
               return;
            }
         }
         relLinksToGammas.add(new Pair<Long, Integer>(gammaId, transactionId));
      }
   }
   public Collection<String> getCategories() {
      return Arrays.asList("Admin.Health");
   }
}