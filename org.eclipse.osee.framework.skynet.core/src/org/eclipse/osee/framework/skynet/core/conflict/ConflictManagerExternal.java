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
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.revision.ConflictManagerInternal;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;

/**
 * @author Donald G. Dunne
 */
public class ConflictManagerExternal {

   private final Branch destinationBranch;
   private final Branch sourceBranch;
   private List<Conflict> originalConflicts;

   public ConflictManagerExternal(Branch destinationBranch, Branch sourceBranch) {
      this.destinationBranch = destinationBranch;
      this.sourceBranch = sourceBranch;
   }

   public List<Conflict> getOriginalConflicts() throws OseeCoreException {
      if (originalConflicts == null) {
         originalConflicts =
               ConflictManagerInternal.getConflictsPerBranch(sourceBranch, destinationBranch,
                     TransactionIdManager.getStartEndPoint(sourceBranch).getFirst(), new NullProgressMonitor());
      }
      return originalConflicts;
   }

   public boolean originalConflictsExist() throws OseeCoreException {
      return getOriginalConflicts().size() > 0;
   }

   public List<Conflict> getRemainingConflicts() throws OseeCoreException {
      List<Conflict> remainingConflicts = new ArrayList<Conflict>();
      if (originalConflictsExist()) {
         for (Conflict conflict : getOriginalConflicts()) {
            if (!conflict.statusResolved() && !conflict.statusCommitted() && !conflict.statusInformational()) {
               remainingConflicts.add(conflict);
            }
         }
      }
      return remainingConflicts;
   }

   public boolean remainingConflictsExist() throws OseeCoreException {
      return getRemainingConflicts().size() > 0;
   }

   public Branch getDestinationBranch() {
      return destinationBranch;
   }

   public Branch getSourceBranch() {
      return sourceBranch;
   }
}
