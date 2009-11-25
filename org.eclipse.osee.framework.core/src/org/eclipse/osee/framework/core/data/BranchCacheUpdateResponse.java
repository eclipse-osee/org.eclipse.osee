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
import org.eclipse.osee.framework.core.cache.IOseeCache;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;

/**
 * @author Roberto E. Escobar
 */
public class BranchCacheUpdateResponse {

   private final List<BranchRow> rows;
   private final Map<Integer, Integer> childToParent;
   private final Map<Integer, Integer> branchToBaseTx;
   private final Map<Integer, Integer> branchToSourceTx;
   private final Map<Integer, Integer> branchToAssocArt;
   private final Map<Integer, String[]> branchToAliases;

   public BranchCacheUpdateResponse(List<BranchRow> rows, Map<Integer, Integer> childToParent, Map<Integer, Integer> branchToBaseTx, Map<Integer, Integer> branchToSourceTx, Map<Integer, Integer> branchToAssocArt, Map<Integer, String[]> branchToAliases) {
      this.rows = rows;
      this.childToParent = childToParent;
      this.branchToBaseTx = branchToBaseTx;
      this.branchToSourceTx = branchToSourceTx;
      this.branchToAssocArt = branchToAssocArt;
      this.branchToAliases = branchToAliases;
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

   public final static class BranchRow {
      private final int branchId;
      private final String branchGuid;

      private final String branchName;
      private final BranchType branchType;
      private final BranchState branchState;
      private final BranchArchivedState branchArchived;
      private final ModificationType modType;

      protected BranchRow(int branchId, String branchGuid, String branchName, BranchType branchType, BranchState branchState, BranchArchivedState branchArchived, ModificationType modType) {
         super();
         this.branchId = branchId;
         this.branchGuid = branchGuid;
         this.branchName = branchName;
         this.branchType = branchType;
         this.branchState = branchState;
         this.branchArchived = branchArchived;
         this.modType = modType;
      }

      public int getBranchId() {
         return branchId;
      }

      public String getBranchGuid() {
         return branchGuid;
      }

      public String getBranchName() {
         return branchName;
      }

      public BranchType getBranchType() {
         return branchType;
      }

      public BranchState getBranchState() {
         return branchState;
      }

      public BranchArchivedState getBranchArchived() {
         return branchArchived;
      }

      public ModificationType getModType() {
         return modType;
      }

      public String[] toArray() {
         return new String[] {getBranchArchived().name(), getBranchGuid(), String.valueOf(getBranchId()),
               getBranchName(), getBranchState().name(), getBranchType().name(), getModType().name()};
      }

      public static BranchRow fromArray(String[] data) {
         BranchArchivedState archived = BranchArchivedState.valueOf(data[0]);
         String branchGuid = data[1];
         int branchId = Integer.valueOf(data[2]);
         String branchName = data[3];
         BranchState branchState = BranchState.valueOf(data[4]);
         BranchType branchType = BranchType.valueOf(data[5]);
         ModificationType modType = ModificationType.valueOf(data[6]);

         return new BranchRow(branchId, branchGuid, branchName, branchType, branchState, archived, modType);
      }
   }

   public static BranchCacheUpdateResponse fromCache(IOseeCache<Branch> cache) throws OseeCoreException {
      List<BranchRow> rowData = new ArrayList<BranchRow>();
      Map<Integer, Integer> childToParent = new HashMap<Integer, Integer>();
      Map<Integer, Integer> branchToBaseTx = new HashMap<Integer, Integer>();
      Map<Integer, Integer> branchToSourceTx = new HashMap<Integer, Integer>();
      Map<Integer, Integer> branchToAssocArt = new HashMap<Integer, Integer>();
      Map<Integer, String[]> branchToAliases = new HashMap<Integer, String[]>();

      for (Branch br : cache.getAll()) {
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
      return new BranchCacheUpdateResponse(rowData, childToParent, childToParent, branchToSourceTx, branchToAssocArt,
            branchToAliases);
   }
}
