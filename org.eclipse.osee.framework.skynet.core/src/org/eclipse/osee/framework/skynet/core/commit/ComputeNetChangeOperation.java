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

import java.util.Collection;
import java.util.Iterator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author Roberto E. Escobar
 */
public class ComputeNetChangeOperation extends AbstractOperation {
   private final Collection<CommitItem> changes;

   public ComputeNetChangeOperation(Collection<CommitItem> changes) {
      super("Compute Net Change", Activator.PLUGIN_ID);
      this.changes = changes;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      if (!changes.isEmpty()) {
         double workPercentage = 1.0 / changes.size();
         Iterator<CommitItem> iterator = changes.iterator();
         while (iterator.hasNext()) {
            checkForCancelledStatus(monitor);
            CommitItem change = iterator.next();

            if (change.wasNewOrIntroducedOnSource() && change.getCurrentSourceModType().isDeleted() || change.isAlreadyOnDestination()) {
               iterator.remove();
            } else {
               checkForInvalidStates(change);

               if (change.getNetModType() != ModificationType.MERGED) {
                  if (change.wasNewOnSource()) {
                     change.setNetModType(ModificationType.NEW);
                  } else if (change.wasIntroducedOnSource()) {
                     change.setNetModType(ModificationType.INTRODUCED);
                  } else {
                     change.setNetModType(change.getCurrentSourceModType());
                  }
                  change.setNetGammaId(change.getCurrentSourceGammaId());
               }
            }
            monitor.worked(calculateWork(workPercentage));
         }
      }
   }

   private void checkForInvalidStates(CommitItem change) throws OseeCoreException {
      // check for case where destination branch is missing an artifact that was modified (not new) on the source branch
      if (change.getDestinationModType() == null && !change.wasNewOrIntroducedOnSource()) {
         throw new OseeStateException(
               "This should be supported in the future - destination branch is not the source's parent: " + change);
      }

      if (change.getNetModType() != ModificationType.MERGED) {
         if (change.getCurrentSourceModType() == ModificationType.NEW && change.getDestinationModType() != null) {
            throw new OseeStateException("Source item marked as new but destination already has item: " + change);
         }
         if (change.getCurrentSourceModType() == ModificationType.INTRODUCED && change.getDestinationModType() != null) {
            throw new OseeStateException("Source item marked as introduced but destination already has item: " + change);
         }
      }
   }
}
