/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.data;

import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.jdk.core.type.Triplet;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractBranchCacheMessage {

   private final List<BranchRow> rows;
   private final Map<Integer, Integer> childToParent;
   private final Map<Integer, Integer> branchToBaseTx;
   private final Map<Integer, Integer> branchToSourceTx;
   private final Map<Integer, Integer> branchToAssocArt;
   private final Map<Integer, String[]> branchToAliases;
   private final List<Triplet<Integer, Integer, Integer>> srcDestMerge;

   protected AbstractBranchCacheMessage(List<BranchRow> rows, Map<Integer, Integer> childToParent, Map<Integer, Integer> branchToBaseTx, Map<Integer, Integer> branchToSourceTx, Map<Integer, Integer> branchToAssocArt, Map<Integer, String[]> branchToAliases, List<Triplet<Integer, Integer, Integer>> srcDestMerge) {
      this.rows = rows;
      this.childToParent = childToParent;
      this.branchToBaseTx = branchToBaseTx;
      this.branchToSourceTx = branchToSourceTx;
      this.branchToAssocArt = branchToAssocArt;
      this.branchToAliases = branchToAliases;
      this.srcDestMerge = srcDestMerge;
   }

   public List<BranchRow> getBranchRows() {
      return rows;
   }

   public Map<Integer, Integer> getChildToParent() {
      return childToParent;
   }

   public Map<Integer, Integer> getBranchToBaseTx() {
      return branchToBaseTx;
   }

   public Map<Integer, Integer> getBranchToSourceTx() {
      return branchToSourceTx;
   }

   public Map<Integer, Integer> getBranchToAssocArt() {
      return branchToAssocArt;
   }

   public Map<Integer, String[]> getBranchAliases() {
      return branchToAliases;
   }

   public List<Triplet<Integer, Integer, Integer>> getMergeBranches() {
      return srcDestMerge;
   }

}
