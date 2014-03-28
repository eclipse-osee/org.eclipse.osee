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
package org.eclipse.osee.framework.core.message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.jdk.core.type.Triplet;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractBranchCacheMessage {

   private final List<BranchRow> rows;
   private final Map<Long, Long> childToParent;
   private final Map<Long, Integer> branchToBaseTx;
   private final Map<Long, Integer> branchToSourceTx;
   private final Map<Long, Integer> branchToAssocArt;
   private final Map<Long, String[]> branchToAliases;
   private final List<Triplet<Long, Long, Long>> srcDestMerge;

   protected AbstractBranchCacheMessage() {
      this.rows = new ArrayList<BranchRow>();
      this.childToParent = new HashMap<Long, Long>();
      this.branchToBaseTx = new HashMap<Long, Integer>();
      this.branchToSourceTx = new HashMap<Long, Integer>();
      this.branchToAssocArt = new HashMap<Long, Integer>();
      this.branchToAliases = new HashMap<Long, String[]>();
      this.srcDestMerge = new ArrayList<Triplet<Long, Long, Long>>();
   }

   public List<BranchRow> getBranchRows() {
      return rows;
   }

   public Map<Long, Long> getChildToParent() {
      return childToParent;
   }

   public Map<Long, Integer> getBranchToBaseTx() {
      return branchToBaseTx;
   }

   public Map<Long, Integer> getBranchToSourceTx() {
      return branchToSourceTx;
   }

   public Map<Long, Integer> getBranchToAssocArt() {
      return branchToAssocArt;
   }

   public Map<Long, String[]> getBranchAliases() {
      return branchToAliases;
   }

   public List<Triplet<Long, Long, Long>> getMergeBranches() {
      return srcDestMerge;
   }

}
