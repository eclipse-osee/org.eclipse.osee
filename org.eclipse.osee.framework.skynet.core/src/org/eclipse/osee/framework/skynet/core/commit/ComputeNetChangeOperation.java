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
            if (change.isIgnoreCase()) {
               iterator.remove();
            } else {
               checkForInvalidStates(change);

               if (change.getNet().getModType() != ModificationType.MERGED) {
                  ModificationType netModType = getNetModType(change);
                  if (netModType == null) {
                     throw new OseeStateException("Net Mod Type was null");
                  }
                  change.getNet().setModType(netModType);
                  change.getNet().setGammaId(change.getCurrent().getGammaId());
               }
            }
            monitor.worked(calculateWork(workPercentage));
         }
      }
      System.out.println("Commit change size: " + changes.size());
   }

   private ModificationType getNetModType(CommitItem change) {
      ModificationType netModType = null;
      if (change.getDestination().exists() && (change.getBase().exists() || change.getFirst().exists())) {
         netModType = change.getCurrent().getModType();
      } else if (change.wasNewOnSource()) {
         netModType = ModificationType.NEW;
      } else if (change.wasIntroducedOnSource()) {
         netModType = ModificationType.INTRODUCED;
      } else if (!change.getDestination().exists()) {
         if (!change.getBase().exists()) {
            netModType = ModificationType.NEW;
         } else {
            // Case when committing into non-parent
            netModType = ModificationType.INTRODUCED;
         }
      }
      return netModType;
   }

   private void checkForInvalidStates(CommitItem change) throws OseeCoreException {
      // check for case where destination branch is missing an artifact that was modified (not new) on the source branch
      if (!change.getDestination().exists() && change.getBase().exists()) {
         throw new OseeStateException(
               "This should be supported in the future - destination branch is not the source's parent: " + change);
      }

      if (change.getDestination().exists() && change.getDestination().getModType().isDeleted()) {
         throw new OseeStateException("Destination was deleted - source should not modify: " + change);
      }

      if ((change.getCurrent().isIntroduced() || change.getCurrent().isNew()) && change.getDestination().exists()) {
         throw new OseeStateException(
               "Source item marked as new/introduced but destination already has item: " + change);
      }

   }
}
