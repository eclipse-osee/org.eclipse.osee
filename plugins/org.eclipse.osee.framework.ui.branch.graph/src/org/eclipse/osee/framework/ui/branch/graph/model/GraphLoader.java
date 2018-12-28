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
package org.eclipse.osee.framework.ui.branch.graph.model;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.utility.ConnectionHandler;
import org.eclipse.osee.framework.skynet.core.utility.IdJoinQuery;
import org.eclipse.osee.framework.skynet.core.utility.JoinUtility;
import org.eclipse.osee.framework.ui.branch.graph.Activator;
import org.eclipse.osee.framework.ui.branch.graph.operation.IProgressListener;
import org.eclipse.osee.jdbc.JdbcStatement;

/**
 * @author Roberto E. Escobar
 */
public class GraphLoader {
   private static final String GET_TRANSACTION_DATA =
      "SELECT otd.* FROM osee_join_id ojt, osee_tx_details otd WHERE ojt.id = otd.transaction_id and ojt.query_id = ? ORDER BY otd.transaction_id desc";

   private GraphLoader() {
   }

   public static void load(GraphCache graphCache, IProgressListener progress) {
      //      graphCache.reset();
      load(graphCache, graphCache.getRootModel(), true, progress);
   }

   protected static void load(GraphCache graphCache, BranchModel modelToLoad, boolean recurse, IProgressListener progress) {
      //      graphCache.addBranchModel(modelToLoad);
      loadBranches(graphCache, modelToLoad, recurse, progress);
      addParentTxData(graphCache, modelToLoad, recurse, progress);
      updateConnections(graphCache, modelToLoad, recurse, progress);
   }

   protected static void loadBranches(GraphCache graphCache, BranchModel current, boolean recurse, IProgressListener listener) {
      for (BranchId child : BranchManager.getChildBranches(current.getBranch(), false)) {
         BranchModel childModel = graphCache.getOrCreateBranchModel(child);
         childModel.setDepth(current.getDepth() + 1);
         if (recurse) {
            loadBranches(graphCache, childModel, true, listener);
         }
         current.addChildBranchModel(childModel);
         listener.worked();
      }
   }

   private static void addParentTxData(GraphCache graphCache, BranchModel current, boolean recurse, IProgressListener listener) {
      try (IdJoinQuery joinQuery = JoinUtility.createIdJoinQuery()) {
         List<BranchId> branches = new ArrayList<>(BranchManager.getChildBranches(current.getBranch(), recurse));
         branches.add(current.getBranch());
         for (BranchId branch : branches) {
            TransactionRecord tr = BranchManager.getSourceTransaction(branch);
            if (tr != null) {
               joinQuery.add(tr);
            }
         }
         joinQuery.store();

         for (TxData txData : getTxData(joinQuery.getQueryId())) {
            BranchModel branchModel = graphCache.getOrCreateBranchModel(txData.getBranch());
            branchModel.addTx(graphCache.getOrCreateTxModel(txData));
         }
      }
   }

   private static void updateConnections(GraphCache graphCache, BranchModel current, boolean recurse, IProgressListener listener) {
      TxModel systemRootTx = null;

      List<BranchModel> models = new ArrayList<>();
      //      models.addAll(current.getChildren());
      //      if (recurse) {
      models.addAll(current.getAllChildrenBelow());
      //      }
      models.add(current);

      //      models.addAll(graphCache.getBranchModels());

      for (BranchModel branchModel : models) {
         if (BranchManager.getType(branchModel.getBranch()).isSystemRootBranch()) {
            systemRootTx = branchModel.getFirstTx();
         } else {
            long parentTxId = 0;
            try {
               TransactionRecord tr = BranchManager.getSourceTransaction(branchModel.getBranch());
               if (tr != null) {
                  parentTxId = tr.getId();
               }
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
            if (parentTxId > 0) {
               TxModel txModel = branchModel.getFirstTx();
               if (txModel != null) {
                  TxModel source = graphCache.getTxModel(parentTxId);
                  if (source != null) {
                     connect(source, txModel);
                  } else {
                     OseeLog.logf(Activator.class, Level.SEVERE,
                        "Invalid parent transaction id of [%s] for branch [%s]", parentTxId, branchModel.getBranch());
                     //                     StubBranchModel stubModel = graphCache.getStubBranchModel();
                     //                     TxModel stubTxModel = stubModel.addTx(parentTxId);
                     //                     graphCache.addTxModel(stubTxModel);
                     //                     connect(stubTxModel, txModel);

                  }
               }
            }
         }
      }

      if (systemRootTx != null) {
         for (BranchModel branchModel : models) {
            try {
               BranchId branch = branchModel.getBranch();
               if (BranchManager.isParentSystemRoot(branch)) {
                  TxModel txModel = branchModel.getFirstTx();
                  if (txModel != null) {
                     connect(systemRootTx, txModel);
                  }
               }
            } catch (Exception ex) {
               // do nothing
            }
         }
      }
   }

   private static void connect(TxModel source, TxModel target) {
      target.setSourceTx(source);
   }

   private static List<TxData> getTxData(int queryId) {
      List<TxData> txDatas = new ArrayList<>();
      JdbcStatement chStmt = ConnectionHandler.getStatement();
      try {
         chStmt.runPreparedQuery(GET_TRANSACTION_DATA, queryId);
         while (chStmt.next()) {
            BranchId branch = BranchId.valueOf(chStmt.getLong("branch_id"));
            TxData txData = new TxData(branch, UserId.valueOf(chStmt.getLong("author")), chStmt.getTimestamp("time"),
               chStmt.getString("osee_comment"), TransactionDetailsType.valueOf(chStmt.getInt("tx_type")),
               chStmt.getInt("commit_art_id"), chStmt.getLong("transaction_id"));
            txDatas.add(txData);
         }
      } finally {
         chStmt.close();
      }
      return txDatas;
   }
}
