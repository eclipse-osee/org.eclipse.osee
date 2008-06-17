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
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.DbUtil;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyQuadHashMap;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;

/**
 * @author Andrew M. Finkbeiner
 * @author Roberto E. Escobar
 */
public class DuplicateRelationOpertions extends AbstractBlam {
   private static final String update_txs =
         "update osee_define_txs set tx_current = ?, mod_type = ? where gamma_id = ? and transaction_id = ?";
   private static final String update_version =
         "update osee_define_rel_link set modification_id = ? where gamma_id = ?";
   private static final String SELECT_DUPLICATE_RELATIONS =
         "select rel1.a_art_id, rel1.b_art_id, rel1.rel_link_type_id, txd1.branch_id, rel1.REL_LINK_ID, rel1.gamma_id, txd1.transaction_id from osee_define_rel_link rel1, osee_define_txs txs1, osee_define_tx_details txd1 , (select rel2.*, txd2.branch_id, txd2.transaction_id from osee_define_rel_link rel2, osee_define_txs txs2, osee_define_tx_details txd2 where txs2.transaction_id = txd2.transaction_id and txs2.tx_current = 1 and txs2.gamma_id = rel2.gamma_id) other_rel_link where txs1.transaction_id = txd1.transaction_id and txs1.tx_current = 1 and txs1.gamma_id = rel1.gamma_id and txd1.branch_id = other_rel_link.branch_id and rel1.a_art_id = other_rel_link.a_art_id and rel1.b_art_id = other_rel_link.b_art_id and rel1.REL_LINK_TYPE_ID = other_rel_link.rel_link_type_id and rel1.REL_LINK_ID <> other_rel_link.rel_link_id order by txd1.branch_id, rel1.rel_link_type_id, rel1.a_art_id, rel1.b_art_id, rel1.rel_link_id";
   private CompositeKeyQuadHashMap<Integer, Integer, Integer, Integer, RelationInfo> relationInfo =
         new CompositeKeyQuadHashMap<Integer, Integer, Integer, Integer, RelationInfo>(1000);

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.AbstractBlam#wrapOperationForBranch(org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap)
    */
   @Override
   public Branch wrapOperationForBranch(BlamVariableMap variableMap) {
      return variableMap.getBranch("Parent Branch");
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#runOperation(org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap, org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.core.runtime.IProgressMonitor)
    */
   public void runOperation(BlamVariableMap variableMap, IProgressMonitor monitor) throws Exception {

      //      Branch branch = variableMap.getBranch("Parent Branch");
      //      List<Artifact> artifacts = ArtifactQuery.getArtifactsFromBranch(branch, false);
      //      int count = 0;
      //      for (Artifact art : artifacts) {
      //         if (art.isDirty(true)) {
      //            count++;
      //         }
      //         art.persistRelations();
      //      }
      //      System.out.println(count);
      //*
      ConnectionHandlerStatement stmt = null;
      try {
         stmt = ConnectionHandler.runPreparedQuery(SELECT_DUPLICATE_RELATIONS);
         while (stmt.getRset().next()) {
            addRelationEntry(stmt.getRset().getInt(1), stmt.getRset().getInt(2), stmt.getRset().getInt(3),
                  stmt.getRset().getInt(4), stmt.getRset().getInt(5), stmt.getRset().getLong(6), stmt.getRset().getInt(
                        7));
         }
      } finally {
         DbUtil.close(stmt);
      }
      Collection<RelationInfo> values = relationInfo.values();
      this.appendResultLine(String.format("Found [%d] potential conflicts.", values.size()));

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

      //ModificationType.DELETED;/
      //      TxChange.DELETED;
      //      FileOutputStream fos = new FileOutputStream(new File("DuplicateRelationRecovery.txt"));
      for (RelationInfo info : values) {
         for (int i = 1; i < info.relLinksToGammas.size(); i++) {
            if (info.art_a == 81891 && info.art_b == 107083) {
               System.out.println("look at me");
            }
            gammas.add(info.relLinksToGammas.get(i).getKey());
            //            fos.write(String.format("%d, %d\n", info.relLinksToGammas.get(i).getKey(),
            //                  info.relLinksToGammas.get(i).getValue()).getBytes());
            //            batchedTxsUpdates.add(new Object[] {SQL3DataType.INTEGER, TxChange.DELETED.getValue(),
            //                  SQL3DataType.INTEGER, ModificationType.DELETED.getValue(), SQL3DataType.BIGINT,
            //                  info.relLinksToGammas.get(i).getKey(), SQL3DataType.INTEGER, info.relLinksToGammas.get(i).getValue()});
            //            batchedVersionUpdates.add(new Object[] {SQL3DataType.INTEGER, ModificationType.DELETED.getValue(),
            //                  SQL3DataType.BIGINT, info.relLinksToGammas.get(i).getKey()});
         }
      }
      //      fos.close();
      //      System.out.println("here we go.");
      //      System.out.println("wait here");

      System.out.println(String.format("%d gamma id's that should be modified.", gammas.size()));

      System.out.println(Arrays.deepToString(gammas.toArray()));

      //      ConnectionHandler.runPreparedUpdateBatch(this.update_txs, batchedTxsUpdates);
      //      ConnectionHandler.runPreparedUpdateBatch(this.update_version, batchedVersionUpdates);

      //      System.out.println(values.size());
      //*/
   }

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
      private int rel_link_type;
      private int art_a;
      private int art_b;
      private List<Pair<Long, Integer>> relLinksToGammas;
      private int branch;
      private int transaction;

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

}