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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;

/**
 * @author Roberto E. Escobar
 */
public class GraphCache {

   private final BranchModel rootModel;
   private final Map<BranchId, BranchModel> branchToBranchModelMap;
   private final Map<Long, TxModel> txNumberToTxModelMap;

   public GraphCache(BranchId rootBranch)  {
      this.branchToBranchModelMap = new HashMap<>();
      this.txNumberToTxModelMap = new HashMap<>();
      this.rootModel = this.getOrCreateBranchModel(rootBranch);
      this.rootModel.setDepth(0);
   }

   public BranchModel getRootModel() {
      return rootModel;
   }

   public List<Node> getNodes() {
      List<Node> nodes = new ArrayList<>();
      nodes.addAll(branchToBranchModelMap.values());
      nodes.addAll(txNumberToTxModelMap.values());
      return nodes;
   }

   protected void reset() {
      branchToBranchModelMap.clear();
      txNumberToTxModelMap.clear();
      rootModel.reset();
      rootModel.resetTxs();
      rootModel.setIsLoaded(false);
   }

   protected void addBranchModel(BranchModel model) {
      branchToBranchModelMap.put(model.getBranch(), model);
   }

   protected void addTxModel(TxModel model) {
      txNumberToTxModelMap.put(model.getRevision(), model);
   }

   protected void removeBranchModel(BranchId branch) {
      branchToBranchModelMap.remove(branch);
   }

   protected void removeTxModel(Long txId) {
      txNumberToTxModelMap.remove(txId);
   }

   public BranchModel getBranchModel(BranchId branch) {
      return branchToBranchModelMap.get(branch);
   }

   public TxModel getTxModel(Long txId) {
      return txNumberToTxModelMap.get(txId);
   }

   public Collection<BranchModel> getBranchModels() {
      return branchToBranchModelMap.values();
   }

   public Collection<TxModel> getTxModels() {
      return txNumberToTxModelMap.values();
   }

   protected TxModel getOrCreateTxModel(TxData txData) {
      TxModel toReturn = getTxModel(txData.getTxId());
      if (toReturn == null) {
         toReturn = new TxModel(txData);
         addTxModel(toReturn);
      }
      return toReturn;
   }

   protected BranchModel getOrCreateBranchModel(BranchId branch)  {
      BranchModel toReturn = null;
      if (branch.equals(StubBranchModel.STUB_BRANCH)) {
         toReturn = getStubBranchModel();
      } else {
         toReturn = getBranchModel(branch);
      }
      if (toReturn == null) {
         toReturn = new BranchModel(branch);

         TransactionRecord headTransaction =
            TransactionManager.getTransaction(TransactionManager.getHeadTransaction(branch));
         TransactionRecord baseTransaction = BranchManager.getBaseTransaction(branch);
         addTxsToBranchModel(toReturn, baseTransaction);
         if (baseTransaction.equals(headTransaction)) {
            addTxsToBranchModel(toReturn, headTransaction);
         }
         addBranchModel(toReturn);
      }
      return toReturn;
   }

   private void addTxsToBranchModel(BranchModel branchModel, TransactionRecord toAdd)  {
      TxModel txModel = getOrCreateTxModel(TxData.createTxData(toAdd));
      branchModel.addTx(txModel);
   }

   public StubBranchModel getStubBranchModel() {
      StubBranchModel toReturn = (StubBranchModel) getBranchModel(StubBranchModel.STUB_BRANCH);
      if (toReturn == null) {
         toReturn = new StubBranchModel();
         addTxModel(toReturn.addTx((long) -1));
         addBranchModel(toReturn);
      }
      return toReturn;
   }
}
