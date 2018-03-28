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
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;

/**
 * @author Roberto E. Escobar
 */
public class BranchModel extends Node {

   private static final long serialVersionUID = 1017422142119868019L;
   private BranchModel parent;
   private final LinkedList<TxModel> txs;
   private final List<BranchModel> children;

   private transient BranchId branch;
   private transient boolean isLoaded;
   private transient int depth;
   private transient boolean txsVisible;

   public BranchModel(BranchId branch) {
      super();
      this.branch = branch;
      this.children = new ArrayList<>();
      this.txs = new LinkedList<>();
      this.parent = null;
      this.isLoaded = false;
      this.depth = -1;
      this.txsVisible = true;
   }

   protected void reset() {
      this.children.clear();
      this.parent = null;
   }

   protected void resetTxs() {
      this.txs.clear();
   }

   protected boolean isLoaded() {
      return isLoaded;
   }

   protected void setIsLoaded(boolean loaded) {
      this.isLoaded = loaded;
   }

   public BranchId getBranch() {
      return branch;
   }

   public boolean isDefaultBranch() {
      //      Branch defaultBranch = BranchManager.getDefaultBranch();
      //      if (defaultBranch != null && branch != null) {
      //         return defaultBranch.equals(branch);
      //      }
      return false;
   }

   public BranchModel getParentBranch() {
      return parent;
   }

   protected void setParentBranch(BranchModel parent) {
      this.parent = parent;
   }

   public List<BranchModel> getChildren() {
      return children;
   }

   public List<BranchModel> getAllChildrenBelow() {
      List<BranchModel> children = new ArrayList<>(getChildren());
      for (BranchModel child : getChildren()) {
         children.addAll(child.getAllChildrenBelow());
      }
      return children;
   }

   protected void addChildBranchModel(BranchModel branchModel) {
      if (!children.contains(branchModel)) {
         branchModel.setParentBranch(this);
         int toSearch = branchModel.getAllChildrenBelow().size();
         int insertAt = -1;
         for (BranchModel child : children) {
            if (child.getAllChildrenBelow().size() > toSearch) {
               break;
            }
            insertAt++;
         }
         int size = children.size();
         if (size != 0 && insertAt >= 0 && insertAt < size) {
            children.add(insertAt, branchModel);
         } else {
            children.add(branchModel);
         }
      }
   }

   protected void addTx(TxModel txModel) {
      if (!txs.contains(txModel)) {
         txModel.setBranchModel(this);

         Long toSearch = txModel.getRevision();
         int insertAt = -1;
         for (int index = 0; index < txs.size(); index++) {
            TxModel toCheck = txs.get(index);
            if (toCheck.getRevision() > toSearch) {
               insertAt = index;
               break;
            }
         }
         if (insertAt > -1) {
            TxModel greater = txs.get(insertAt);
            TxModel parent = greater.getParentTx();
            if (parent != null) {
               txModel.setParentTx(parent);
            } else {
               int toGet = insertAt - 1;
               if (toGet >= 0) {
                  txModel.setParentTx(txs.get(toGet));
               }
            }
            greater.setParentTx(txModel);
            txs.add(insertAt, txModel);
         } else {
            TxModel last = getLastTx();
            if (last != null) {
               txModel.setParentTx(last);
            }
            txs.add(txModel);
         }
      }
   }

   public List<TxModel> getTxs() {
      return txs;
   }

   public TxModel getFirstTx() {
      return txs.isEmpty() ? null : txs.get(0);
   }

   public TxModel getLastTx() {
      return txs.isEmpty() ? null : txs.get(txs.size() - 1);
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof BranchModel) {
         BranchModel other = (BranchModel) obj;
         return other.getBranch().equals(getBranch());
      }
      return false;
   }

   @Override
   public int hashCode() {
      return branch.hashCode();
   }

   @Override
   public String toString() {
      return String.format("Branch:[%s] Type:[%s] Children:[%s] TxNodes:[%s]", branch.getId(),
         BranchManager.getType(branch).getName(), children.size(), txs.size());
   }

   public boolean areTxsVisible() {
      return txsVisible;
   }

   public void setTxsVisible(boolean txsVisible) {
      if (areTxsVisible() != txsVisible) {
         this.txsVisible = txsVisible;
         for (TxModel tx : txs) {
            tx.setVisible(txsVisible);
         }
      }
   }

   public void setDepth(int depth) {
      this.depth = depth;
   }

   public int getDepth() {
      return depth;
   }
}
