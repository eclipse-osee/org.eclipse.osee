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
package org.eclipse.osee.orcs.db.internal.conflict;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import org.eclipse.osee.framework.core.enums.ConflictStatus;
import org.eclipse.osee.framework.core.executor.CancellableCallable;
import org.eclipse.osee.framework.core.model.change.ChangeItem;

/**
 * @author Roberto E. Escobar
 */
public class ComputeConflictsCallable extends CancellableCallable<Object> {
   private final Collection<ChangeItem> changes;
   private final Map<ChangeItem, ConflictStatus> conflicts;

   public ComputeConflictsCallable(Collection<ChangeItem> changes, Map<ChangeItem, ConflictStatus> conflicts) {
      super();
      this.changes = changes;
      this.conflicts = conflicts;
   }

   @Override
   public Object call() throws Exception {
      if (!changes.isEmpty()) {
         Iterator<ChangeItem> iterator = changes.iterator();
         while (iterator.hasNext()) {
            checkForCancelled();
            ChangeItem change = iterator.next();
            ConflictStatus conflictStatus = getConflictStatus(change);
            if (conflictStatus.isConflict()) {
               conflicts.put(change, conflictStatus);
            }
         }
      }
      return null;
   }

   private ConflictStatus getConflictStatus(ChangeItem change) {
      ConflictStatus conflictStatus = ConflictStatus.NOT_CONFLICTED;
      if (change.getDestinationVersion().isValid()) {

         // item changed on source and destination
         if (change.getCurrentVersion().getGammaId() != change.getDestinationVersion().getGammaId()) {
            //
         }

         // destination current gamma != source baseline gamma
         if (change.getDestinationVersion().getGammaId() != change.getBaselineVersion().getGammaId()) {
            //
         }

      }
      return conflictStatus;
   }

   /**
    * change Source Destination Type Any mod deleted informational deleted deleted informational deleted changed
    * informational new none select * from osee_merge where merge_branch_id = 2957; select * from osee_txs_archived
    * where branch_id in (2140, 784) and gamma_id in (4903469, 8710061) order by branch_id; select
    * count(merge_branch_id), count(conflict_id) from osee_conflict group by merge_branch_id, conflict_id; select * from
    * osee_artifact_version where art_id = 2083 and gamma_id = 8710061; -- 8710061 select * from osee_attribute where
    * art_id = 2083; mod mod
    */
   // source baseline version is not equal to the destinations current version;
   // source item changed - destination item deleted

   // if (ChangeItemUtil.isIgnoreCase(change)) {
   //               iterator.remove();
   //            } else {
   //               checkForInvalidStates(change);
   //
   //               if (!ChangeItemUtil.isModType(change.getNetChange(), ModificationType.MERGED)) {
   //                  ModificationType netModType = getNetModType(change);
   //                  if (netModType == null) {
   //                     throw new OseeStateException("Net Mod Type was null");
   //                  }
   //                  change.getNetChange().copy(change.getCurrentVersion());
   //                  change.getNetChange().setModType(netModType);
   //               } else {
   //                  if (ChangeItemUtil.isDeleted(change.getCurrentVersion())) {
   //                     change.getNetChange().copy(change.getCurrentVersion());
   //                  }
   //               }
   //            }

   //   private ModificationType getNetModType(ChangeItem change) {
   //      ModificationType modificationType;
   //      modificationType = calculateNetWithDestinationBranch(change);
   //      return modificationType;
   //   }
   //
   //   private ModificationType calculateNetWithDestinationBranch(ChangeItem change) {
   //      ModificationType netModType = change.getCurrentVersion().getModType();
   //      if (change.getDestinationVersion().isValid() && (change.getBaselineVersion().isValid() || change.getFirstNonCurrentChange().isValid())) {
   //         netModType = change.getCurrentVersion().getModType();
   //      } else if (ChangeItemUtil.wasNewOnSource(change)) {
   //         netModType = ModificationType.NEW;
   //      } else if (ChangeItemUtil.wasIntroducedOnSource(change)) {
   //         netModType = ModificationType.INTRODUCED;
   //      } else if (!change.getDestinationVersion().isValid()) {
   //         if (!change.getBaselineVersion().isValid()) {
   //            netModType = ModificationType.NEW;
   //         } else {
   //            // Case when committing into non-parent
   //            netModType = ModificationType.INTRODUCED;
   //         }
   //      }
   //      return netModType;
   //   }
   //
   //   private void checkForInvalidStates(ChangeItem change)  {
   //      // check for case where destination branch is missing an artifact that was modified (not new) on the source branch
   //      if (!change.getDestinationVersion().isValid() && change.getBaselineVersion().isValid()) {
   //         throw new OseeStateException(
   //               "This should be supported in the future - destination branch is not the source's parent: " + change);
   //      }
   //
   //      if (change.getDestinationVersion().isValid() && ChangeItemUtil.isDeleted(change.getDestinationVersion())) {
   //         throw new OseeStateException("Destination was deleted - source should not modify: " + change);
   //      }
   //
   //      if ((ChangeItemUtil.isIntroduced(change.getCurrentVersion()) || ChangeItemUtil.isNew(change.getCurrentVersion())) //
   //            && change.getDestinationVersion().isValid()) {
   //         throw new OseeStateException(
   //               "Source item marked as new/introduced but destination already has item: " + change);
   //      }
   //
   //   }
}
