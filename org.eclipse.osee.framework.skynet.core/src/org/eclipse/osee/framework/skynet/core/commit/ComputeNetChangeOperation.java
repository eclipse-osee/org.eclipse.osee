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
   private final Collection<ChangeItem> changes;
   private final boolean isCommitCase;

   public ComputeNetChangeOperation(Collection<ChangeItem> changes) {
      this(changes, true);
   }

   public ComputeNetChangeOperation(Collection<ChangeItem> changes, boolean hasDestinationBranch) {
      super("Compute Net Change", Activator.PLUGIN_ID);
      this.changes = changes;
      this.isCommitCase = hasDestinationBranch;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      if (!changes.isEmpty()) {
         double workPercentage = 1.0 / changes.size();

         Iterator<ChangeItem> iterator = changes.iterator();
         while (iterator.hasNext()) {
            checkForCancelledStatus(monitor);
            ChangeItem change = iterator.next();
            if (ChangeItemUtil.isIgnoreCase(isCommitCase, change)) {
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
               } else {
                  if (change.getCurrent().isDeleted()) {
                     change.getNet().setGammaId(change.getCurrent().getGammaId());
                     change.getNet().setModType(change.getCurrent().getModType());
                  }
               }
            }
            monitor.worked(calculateWork(workPercentage));
         }
      } else {
         monitor.worked(calculateWork(1.0));
      }
      System.out.println("Commit change size: " + changes.size());
   }

   private ModificationType getNetModType(ChangeItem change) {
      ModificationType modificationType;

      if (isCommitCase) {
         modificationType = calculateNetWithDestinationBranch(change);
      } else {
         modificationType = calculateNetWithoutDestinationBranch(change);
      }
      return modificationType;
   }

   private ModificationType calculateNetWithoutDestinationBranch(ChangeItem change) {
      ModificationType netModType = change.getCurrent().getModType();

      if (ChangeItemUtil.wasNewOnSource(change)) {
         netModType = ModificationType.NEW;
      } else if (ChangeItemUtil.wasIntroducedOnSource(change)) {
         netModType = ModificationType.INTRODUCED;
      } else if (!change.getBase().exists() && !change.getFirst().exists() && change.getCurrent().getModType() == ModificationType.MODIFIED) {
         netModType = ModificationType.NEW;
      }
      return netModType;
   }

   private ModificationType calculateNetWithDestinationBranch(ChangeItem change) {
      ModificationType netModType = null;
      if (change.getDestination().exists() && (change.getBase().exists() || change.getFirst().exists())) {
         netModType = change.getCurrent().getModType();
      } else if (ChangeItemUtil.wasNewOnSource(change)) {
         netModType = ModificationType.NEW;
      } else if (ChangeItemUtil.wasIntroducedOnSource(change)) {
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

   private void checkForInvalidStates(ChangeItem change) throws OseeCoreException {
      // check for case where destination branch is missing an artifact that was modified (not new) on the source branch
      if (isCommitCase) {
         if (!change.getDestination().exists() && change.getBase().exists()) {
            throw new OseeStateException(
                  "This should be supported in the future - destination branch is not the source's parent: " + change);
         }
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
