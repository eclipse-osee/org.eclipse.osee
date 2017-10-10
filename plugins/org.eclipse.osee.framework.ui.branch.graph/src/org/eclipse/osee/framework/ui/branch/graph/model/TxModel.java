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

/**
 * @author Roberto E. Escobar
 */
public class TxModel extends Node implements Comparable<TxModel> {

   private static final long serialVersionUID = -2246486595572509094L;

   private final TxData txData;
   private BranchModel branchModel;
   private TxModel parentTxModel;
   private TxModel sourceTxModel;
   private List<TxModel> mergedTxs;

   public TxModel(TxData txData) {
      this.txData = txData;
   }

   public TxData getTxData() {
      return txData;
   }

   public void setBranchModel(BranchModel branchModel) {
      this.branchModel = branchModel;
   }

   public BranchModel getParentBranchModel() {
      return branchModel;
   }

   public TxModel getParentTx() {
      return parentTxModel;
   }

   public void setParentTx(TxModel parent) {
      this.parentTxModel = parent;
   }

   public TxModel getSourceTx() {
      return sourceTxModel;
   }

   public void setSourceTx(TxModel sourceTxModel) {
      this.sourceTxModel = sourceTxModel;
   }

   public List<TxModel> getMergedTx() {
      return mergedTxs;
   }

   public void addMergedTx(TxModel node) {
      if (mergedTxs == null) {
         mergedTxs = new ArrayList<>();
      }
      mergedTxs.add(node);
   }

   public Long getRevision() {
      return getTxData().getTxId();
   }

   @Override
   public int compareTo(TxModel other) {
      return getRevision().compareTo(other.getRevision());
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof TxModel) {
         TxModel other = (TxModel) obj;
         return other.getTxData().equals(getTxData());
      }
      return false;
   }

   @Override
   public int hashCode() {
      return txData.hashCode();
   }

   @Override
   public String toString() {
      return txData.toString();
   }
}
