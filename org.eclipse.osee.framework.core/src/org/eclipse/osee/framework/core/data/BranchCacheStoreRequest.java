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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.osee.framework.core.cache.BranchCache;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.type.Triplet;

/**
 * @author Roberto E. Escobar
 */
public class BranchCacheStoreRequest extends AbstractBranchCacheMessage {

   public BranchCacheStoreRequest(List<BranchRow> rows, Map<Integer, Integer> childToParent, Map<Integer, Integer> branchToBaseTx, Map<Integer, Integer> branchToSourceTx, Map<Integer, Integer> branchToAssocArt, Map<Integer, String[]> branchToAliases, List<Triplet<Integer, Integer, Integer>> srcDestMerge) {
      super(rows, childToParent, branchToBaseTx, branchToSourceTx, branchToAssocArt, branchToAliases, srcDestMerge);
   }

   public static BranchCacheStoreRequest fromCache(BranchCache cache, Collection<Branch> types) throws OseeCoreException {
      List<BranchRow> rowData = new ArrayList<BranchRow>();
      Map<Integer, Integer> childToParent = new HashMap<Integer, Integer>();
      Map<Integer, Integer> branchToBaseTx = new HashMap<Integer, Integer>();
      Map<Integer, Integer> branchToSourceTx = new HashMap<Integer, Integer>();
      Map<Integer, Integer> branchToAssocArt = new HashMap<Integer, Integer>();
      Map<Integer, String[]> branchToAliases = new HashMap<Integer, String[]>();

      for (Branch br : types) {
         Integer branchId = br.getId();
         rowData.add(new BranchRow(br.getId(), br.getGuid(), br.getName(), br.getBranchType(), br.getBranchState(),
               br.getArchiveState(), br.getModificationType()));
         Collection<String> aliases = br.getAliases();
         if (!aliases.isEmpty()) {
            branchToAliases.put(branchId, aliases.toArray(new String[aliases.size()]));
         }

         if (br.hasParentBranch()) {
            childToParent.put(branchId, br.getParentBranch().getId());
         }

         TransactionRecord txBase = br.getBaseTransaction();
         if (txBase != null) {
            branchToBaseTx.put(branchId, txBase.getId());
         }
         TransactionRecord srcBase = br.getSourceTransaction();
         if (srcBase != null) {
            branchToSourceTx.put(branchId, srcBase.getId());
         }

         IBasicArtifact<?> art = br.getAssociatedArtifact();
         if (art != null) {
            branchToAssocArt.put(branchId, art.getArtId());
         }
      }

      List<Triplet<Integer, Integer, Integer>> srcDestMerge = new ArrayList<Triplet<Integer, Integer, Integer>>();
      for (Entry<Pair<Branch, Branch>, Branch> entry : cache.getMergeBranches().entrySet()) {
         Integer src = entry.getKey().getFirst().getId();
         Integer dest = entry.getKey().getSecond().getId();
         Integer merge = entry.getValue().getId();
         srcDestMerge.add(new Triplet<Integer, Integer, Integer>(src, dest, merge));
      }
      return new BranchCacheStoreRequest(rowData, childToParent, branchToBaseTx, branchToSourceTx, branchToAssocArt,
            branchToAliases, srcDestMerge);
   }

}
