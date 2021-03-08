/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.skynet.core.conflict;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.enums.ConflictStatus;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.revision.ConflictManagerInternal;

/**
 * @author Donald G. Dunne
 */
public class ConflictManagerExternal {

   private static final IProgressMonitor monitor = new NullProgressMonitor();

   private final BranchToken destinationBranch;
   private final BranchToken sourceBranch;
   private List<Conflict> originalConflicts;

   public ConflictManagerExternal(BranchId destinationBranch, BranchId sourceBranch) {
      this.destinationBranch = BranchManager.getBranchToken(destinationBranch);
      this.sourceBranch = BranchManager.getBranchToken(sourceBranch);
   }

   public List<Conflict> getOriginalConflicts() {
      if (originalConflicts == null) {
         originalConflicts = ConflictManagerInternal.getConflictsPerBranch(sourceBranch, destinationBranch,
            BranchManager.getBaseTransaction(sourceBranch), monitor);
      }
      return originalConflicts;
   }

   public boolean originalConflictsExist() {
      return !getOriginalConflicts().isEmpty();
   }

   public List<Conflict> getRemainingConflicts() {
      List<Conflict> remainingConflicts = new ArrayList<>();
      for (Conflict conflict : getOriginalConflicts()) {
         ConflictStatus status = conflict.getStatus();
         if (!status.isResolved() && !status.isCommitted() && !status.isInformational()) {
            remainingConflicts.add(conflict);
         }
      }
      return remainingConflicts;
   }

   public boolean remainingConflictsExist() {
      return !getRemainingConflicts().isEmpty();
   }

   public BranchToken getDestinationBranch() {
      return destinationBranch;
   }

   public BranchToken getSourceBranch() {
      return sourceBranch;
   }
}
