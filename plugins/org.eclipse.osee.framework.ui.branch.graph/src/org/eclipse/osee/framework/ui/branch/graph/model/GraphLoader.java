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
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.JoinUtility;
import org.eclipse.osee.framework.database.core.JoinUtility.TransactionJoinQuery;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.branch.graph.BranchGraphActivator;
import org.eclipse.osee.framework.ui.branch.graph.operation.IProgressListener;

/**
 * @author Roberto E. Escobar
 */
public class GraphLoader {
   private static final String GET_TRANSACTION_DATA =
         "SELECT otd.* FROM osee_join_transaction ojt, osee_tx_details otd WHERE ojt.transaction_id = otd.transaction_id and ojt.query_id = ? ORDER BY otd.transaction_id desc";

   private GraphLoader() {
   }

   public static void load(GraphCache graphCache, IProgressListener progress) throws OseeCoreException {
      //      graphCache.reset();
      load(graphCache, graphCache.getRootModel(), true, progress);
   }

   protected static void load(GraphCache graphCache, BranchModel modelToLoad, boolean recurse, IProgressListener progress) throws OseeCoreException {
      //      graphCache.addBranchModel(modelToLoad);
      loadBranches(graphCache, modelToLoad, recurse, progress);
      addParentTxData(graphCache, modelToLoad, recurse, progress);
      updateConnections(graphCache, modelToLoad, recurse, progress);
   }

   protected static void loadBranches(GraphCache graphCache, BranchModel current, boolean recurse, IProgressListener listener) throws OseeCoreException {
      for (Branch child : current.getBranch().getChildBranches()) {
         BranchModel childModel = graphCache.getOrCreateBranchModel(child);
         childModel.setDepth(current.getDepth() + 1);
         if (recurse) {
            loadBranches(graphCache, childModel, true, listener);
         }
         current.addChildBranchModel(childModel);
         listener.worked();
      }
   }

   private static void addParentTxData(GraphCache graphCache, BranchModel current, boolean recurse, IProgressListener listener) throws OseeCoreException {
      TransactionJoinQuery txJoinQuery = JoinUtility.createTransactionJoinQuery();
      try {
         List<Branch> branches = new ArrayList<Branch>(current.getBranch().getChildBranches(recurse));
         branches.add(current.getBranch());
         for (Branch branch : branches) {
            txJoinQuery.add(-1L, branch.getSourceTransaction().getId());
         }
         txJoinQuery.store();

         for (TxData txData : getTxData(txJoinQuery.getQueryId())) {
            BranchModel branchModel = graphCache.getOrCreateBranchModel(txData.getBranch());
            branchModel.addTx(graphCache.getOrCreateTxModel(txData));
         }
      } finally {
         txJoinQuery.delete();
      }
   }

   private static void updateConnections(GraphCache graphCache, BranchModel current, boolean recurse, IProgressListener listener) {
      TxModel systemRootTx = null;

      List<BranchModel> models = new ArrayList<BranchModel>();
      //      models.addAll(current.getChildren());
      //      if (recurse) {
      models.addAll(current.getAllChildrenBelow());
      //      }
      models.add(current);

      //      models.addAll(graphCache.getBranchModels());

      for (BranchModel branchModel : models) {
         if (branchModel.getBranch().getBranchType().isSystemRootBranch()) {
            systemRootTx = branchModel.getFirstTx();
         } else {
            long parentTxId = 0;
            try {
               parentTxId = branchModel.getBranch().getSourceTransaction().getId();
            } catch (OseeCoreException ex) {
               OseeLog.log(BranchGraphActivator.class, Level.SEVERE, ex);
            }
            if (parentTxId > 0) {
               TxModel txModel = branchModel.getFirstTx();
               if (txModel != null) {
                  TxModel source = graphCache.getTxModel(parentTxId);
                  if (source != null) {
                     connect(source, txModel);
                  } else {
                     OseeLog.log(BranchGraphActivator.class, Level.SEVERE,
                           String.format("Invalid parent transaction id of [%s] for branch [%s]", parentTxId,
                                 branchModel.getBranch()));
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
               Branch branch = branchModel.getBranch();
               if (branch.hasParentBranch() && branch.getParentBranch().getBranchType().isSystemRootBranch()) {
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

   private static List<TxData> getTxData(int queryId) throws OseeCoreException {
      List<TxData> txDatas = new ArrayList<TxData>();
      IOseeStatement chStmt = null;
      try {
         chStmt = ConnectionHandler.getStatement();
         chStmt.runPreparedQuery(GET_TRANSACTION_DATA, queryId);
         while (chStmt.next()) {
            Branch branch = BranchManager.getBranch(chStmt.getInt("branch_id"));
            TxData txData =
                  new TxData(branch, chStmt.getInt("author"), chStmt.getTimestamp("time"),
                        chStmt.getString("osee_comment"), chStmt.getInt("tx_type"), chStmt.getInt("commit_art_id"),
                        chStmt.getInt("transaction_id"));
            txDatas.add(txData);
         }
      } finally {
         if (chStmt != null) {
            chStmt.close();
         }
      }
      return txDatas;
   }
}
