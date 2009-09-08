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
package org.eclipse.osee.framework.skynet.core.commit;

import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.enums.ConflictStatus;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.skynet.core.conflict.Conflict;
import org.eclipse.osee.framework.skynet.core.conflict.ConflictManagerExternal;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author Roberto E. Escobar
 */
public class ComputeNetChangeOperation extends AbstractOperation {
   private final List<OseeChange> rawChanges;
   private final ConflictManagerExternal conflictManager;
   private final IChangeResolver resolver;

   /**
    * the rawChanges will be transformed into the net changes
    * 
    * @param rawChanges
    * @param resolver
    */
   public ComputeNetChangeOperation(List<OseeChange> rawChanges, ConflictManagerExternal conflictManager, IChangeResolver resolver) {
      super("Compute Net Change", Activator.PLUGIN_ID);
      this.rawChanges = rawChanges;
      this.conflictManager = conflictManager;
      this.resolver = resolver;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      compute();
      if (rawChanges.isEmpty()) {
         resolver.reset();
         double workPercentage = 1.0 / rawChanges.size();
         for (OseeChange oseeChange : rawChanges) {
            checkForCancelledStatus(monitor);
            oseeChange.accept(resolver);
            monitor.worked(calculateWork(workPercentage));
         }
         resolver.resolve();
      }
   }

   private void compute() throws OseeCoreException {

      Iterator<OseeChange> iterator = rawChanges.iterator();
      while (iterator.hasNext()) {
         OseeChange change = iterator.next();

         if (change.wasNewOrIndtroducedOnSource() && change.getCurrentSourceModType().isDeleted() || change.isAlreadyOnDestination()) {
            iterator.remove();
            continue;
         }

         if (change.wasNewOnSource()) {
            change.setResultantModType(ModificationType.NEW);
         } else if (change.wasIntroducedOnSource()) {
            change.setResultantModType(ModificationType.INTRODUCED);
         } else {
            change.setResultantModType(change.getCurrentSourceModType());
         }
         change.setResultantGammaId(change.getCurrentSourceGammaId());

         // check for case where destination branch is missing an artifact that was modified (not new) on the source branch
         if (change.getDesinationModType() == null && !change.wasNewOrIndtroducedOnSource()) {
            throw new OseeStateException("missing from destination: " + change);
         }

         for (Conflict conflict : conflictManager.getOriginalConflicts()) {
            if (!conflict.statusResolved()) {
               throw new OseeStateException("All conflicts must be resolved before commit.");
            }
            if (conflict.getSourceGamma() == change.getCurrentSourceGammaId()) {
               change.setResultantGammaId(conflict.getMergeGammaId());
               conflict.setStatus(ConflictStatus.COMMITTED);
               change.setResultantModType(ModificationType.MERGED);
               break;
            }
         }
      }

      if (rawChanges.isEmpty()) {
         throw new OseeStateException(" A branch can not be commited without any changes made.");
      }
   }
}
