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
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.executor.CancellableCallable;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.core.model.change.ChangeItemUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;

/**
 * @author Roberto E. Escobar
 */
public class ComputeNetChangeCallable extends CancellableCallable<List<ChangeItem>> {
   private final List<ChangeItem> changes;

   public ComputeNetChangeCallable(List<ChangeItem> changes) {
      this.changes = changes;
   }

   @Override
   public List<ChangeItem> call() throws Exception {
      if (changes != null) {
         Iterator<ChangeItem> iterator = changes.iterator();
         while (iterator.hasNext()) {
            checkForCancelled();
            ChangeItem change = iterator.next();
            ChangeItemUtil.checkAndSetIgnoreCase(change);
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

}