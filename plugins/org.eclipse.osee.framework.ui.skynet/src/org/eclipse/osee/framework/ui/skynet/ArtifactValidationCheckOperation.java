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
package org.eclipse.osee.framework.ui.skynet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.validation.IOseeValidator;
import org.eclipse.osee.framework.skynet.core.validation.OseeValidator;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactValidationCheckOperation extends AbstractOperation {
   private final List<Artifact> itemsToCheck;
   private final boolean stopOnFirstError;

   public ArtifactValidationCheckOperation(final Collection<Artifact> itemsToCheck, boolean stopOnFirstError) {
      super("Validate Artifact(s)", Activator.PLUGIN_ID);
      this.stopOnFirstError = stopOnFirstError;
      this.itemsToCheck = new ArrayList<>(itemsToCheck);
      if (itemsToCheck != null) {
         this.itemsToCheck.addAll(itemsToCheck);
      }
   }

   public boolean isStopOnFirstError() {
      return stopOnFirstError;
   }

   private void handleStatus(Artifact itemChecked, IStatus status) throws Exception {
      if (!status.isOK()) {
         String link = XResultDataUI.getHyperlink(
            String.format("%s:[%s]", itemChecked.getArtifactTypeName(), itemChecked.getName()), itemChecked,
            itemChecked.getBranch());
         String message = String.format("%s: %s", link, status.getMessage());
         status =
            new Status(status.getSeverity(), status.getPlugin(), status.getCode(), message, status.getException());
         setStatus(status);
      }
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      monitor.setTaskName(getName());
      if (!itemsToCheck.isEmpty()) {
         int totalArts = itemsToCheck.size();
         double workAmountPercentage = 1 / itemsToCheck.size();
         int workAmount = calculateWork(workAmountPercentage);
         for (int index = 0; index < totalArts; index++) {
            monitor.setTaskName(String.format("Validating Artifact(s): [%s of %s]", index + 1, totalArts));
            Artifact itemChecked = itemsToCheck.get(index);
            IStatus status = OseeValidator.getInstance().validate(IOseeValidator.LONG, itemChecked);
            handleStatus(itemChecked, status);
            monitor.worked(workAmount);
            if (isStopOnFirstError()) {
               break;
            }
            checkForCancelledStatus(monitor);
         }
      }
   }
}
