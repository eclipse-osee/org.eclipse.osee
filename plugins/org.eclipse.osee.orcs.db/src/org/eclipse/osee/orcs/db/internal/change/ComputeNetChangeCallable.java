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
package org.eclipse.osee.orcs.db.internal.change;

import java.util.Iterator;
import java.util.List;
import org.eclipse.osee.executor.admin.CancellableCallable;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.core.model.change.ChangeItemUtil;

/**
 * @author Roberto E. Escobar
 */
public class ComputeNetChangeCallable extends CancellableCallable<List<ChangeItem>> {
   private final List<ChangeItem> changes;

   public ComputeNetChangeCallable(List<ChangeItem> changes) {
      super();
      this.changes = changes;
   }

   @Override
   public List<ChangeItem> call() throws Exception {
      if (!changes.isEmpty()) {
         Iterator<ChangeItem> iterator = changes.iterator();
         while (iterator.hasNext()) {
            checkForCancelled();
            ChangeItem change = iterator.next();
            if (ChangeItemUtil.isIgnoreCase(change)) {
               iterator.remove();
            } else {
               checkForInvalidStates(change);

               if (!ChangeItemUtil.isModType(change.getNetChange(), ModificationType.MERGED)) {
                  ModificationType netModType = getNetModType(change);
                  if (netModType == null) {
                     throw new OseeStateException("Net Mod Type was null");
                  }
                  change.getNetChange().copy(change.getCurrentVersion());
                  change.getNetChange().setModType(netModType);
               } else {
                  if (ChangeItemUtil.isDeleted(change.getCurrentVersion())) {
                     change.getNetChange().copy(change.getCurrentVersion());
                  }
               }
            }
         }
      }
      return changes;
   }

   private ModificationType getNetModType(ChangeItem change) {
      ModificationType modificationType;
      modificationType = calculateNetWithDestinationBranch(change);
      return modificationType;
   }

   private ModificationType calculateNetWithDestinationBranch(ChangeItem change) {
      ModificationType netModType = change.getCurrentVersion().getModType();
      if (change.getDestinationVersion().isValid() && (change.getBaselineVersion().isValid() || change.getFirstNonCurrentChange().isValid())) {
         netModType = change.getCurrentVersion().getModType();
      } else if (ChangeItemUtil.wasNewOnSource(change)) {
         netModType = ModificationType.NEW;
      } else if (ChangeItemUtil.wasIntroducedOnSource(change)) {
         netModType = ModificationType.INTRODUCED;
      } else if (!change.getDestinationVersion().isValid()) {
         if (!change.getBaselineVersion().isValid()) {
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
      if (!change.getDestinationVersion().isValid() && change.getBaselineVersion().isValid()) {
         throw new OseeStateException(
            "Attemping to change an artifact that is not on the destination.  Check for an uncommitted branch in the source hierarchy that is not in the destination hierarchy. [%s]",
            change);
      }

      if (change.getDestinationVersion().isValid() && ChangeItemUtil.isDeleted(change.getDestinationVersion())) {
         throw new OseeStateException("Destination was deleted - source should not modify [%s] ", change);
      }

      if ((ChangeItemUtil.isIntroduced(change.getCurrentVersion()) || ChangeItemUtil.isNew(change.getCurrentVersion())) //
         && change.getDestinationVersion().isValid()) {
         throw new OseeStateException("Source item marked as new/introduced but destination already has item [%s]",
            change);
      }
   }
}