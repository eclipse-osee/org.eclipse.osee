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
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author Roberto E. Escobar
 */
public class ComputeNetChangeOperation extends AbstractOperation {
   private final List<OseeChange> changes;

   public ComputeNetChangeOperation(List<OseeChange> changes) {
      super("Compute Net Change", Activator.PLUGIN_ID);
      this.changes = changes;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      if (!changes.isEmpty()) {
         double workPercentage = 1.0 / changes.size();
         Iterator<OseeChange> iterator = changes.iterator();
         while (iterator.hasNext()) {
            checkForCancelledStatus(monitor);
            OseeChange change = iterator.next();

            if (change.wasNewOrIntroducedOnSource() && change.getCurrentSourceModType().isDeleted() || change.isAlreadyOnDestination()) {
               iterator.remove();
            } else {
               // check for case where destination branch is missing an artifact that was modified (not new) on the source branch
               if (change.getDestinationModType() == null && !change.wasNewOrIntroducedOnSource()) {
                  throw new OseeStateException(
                        "This should be supported in the future - destination branch is not the source's parent: " + change);
               }
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
}
