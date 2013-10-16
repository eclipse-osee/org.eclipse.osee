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
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.ConflictStatus;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.revision.ConflictManagerInternal;

/**
 * @author Donald G. Dunne
 */
public class ConflictManagerExternal {

   private static final IProgressMonitor monitor = new NullProgressMonitor();

   private final Branch destinationBranch;
   private final Branch sourceBranch;
   private List<Conflict> originalConflicts;

   public ConflictManagerExternal(IOseeBranch destinationBranch, IOseeBranch sourceBranch) throws OseeCoreException {
      this.destinationBranch = BranchManager.getBranch(destinationBranch);
      this.sourceBranch = BranchManager.getBranch(sourceBranch);
   }

   public List<Conflict> getOriginalConflicts() throws OseeCoreException {
      if (originalConflicts == null) {
         originalConflicts =
            ConflictManagerInternal.getConflictsPerBranch(sourceBranch, destinationBranch,
               sourceBranch.getBaseTransaction(), monitor);
      }
      return originalConflicts;
   }

   public boolean originalConflictsExist() throws OseeCoreException {
      return !getOriginalConflicts().isEmpty();
   }

   public List<Conflict> getRemainingConflicts() throws OseeCoreException {
      List<Conflict> remainingConflicts = new ArrayList<Conflict>();
      for (Conflict conflict : getOriginalConflicts()) {
         ConflictStatus status = conflict.getStatus();
         if (!status.isResolved() && !status.isCommitted() && !status.isInformational()) {
            remainingConflicts.add(conflict);
         }
      }
      return remainingConflicts;
   }

   public boolean remainingConflictsExist() throws OseeCoreException {
      return !getRemainingConflicts().isEmpty();
   }

   public Branch getDestinationBranch() {
      return destinationBranch;
   }

   public Branch getSourceBranch() {
      return sourceBranch;
   }
}
