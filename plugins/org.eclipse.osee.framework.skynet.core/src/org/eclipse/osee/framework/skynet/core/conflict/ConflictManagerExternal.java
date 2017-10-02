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
package org.eclipse.osee.framework.skynet.core.conflict;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.ConflictStatus;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.revision.ConflictManagerInternal;

/**
 * @author Donald G. Dunne
 */
public class ConflictManagerExternal {

   private static final IProgressMonitor monitor = new NullProgressMonitor();

   private final IOseeBranch destinationBranch;
   private final IOseeBranch sourceBranch;
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

   public IOseeBranch getDestinationBranch() {
      return destinationBranch;
   }

   public IOseeBranch getSourceBranch() {
      return sourceBranch;
   }
}
