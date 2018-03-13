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
package org.eclipse.osee.framework.ui.skynet.commit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.MutableBoolean;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.commit.actions.CommitAction;
import org.eclipse.osee.framework.skynet.core.revision.ChangeManager;
import org.eclipse.osee.framework.skynet.core.validation.IOseeValidator;
import org.eclipse.osee.framework.skynet.core.validation.OseeValidator;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Megumi Telles
 */
public class CatchValidationChecks implements CommitAction {

   private static final String ATS_TEMP_ADMIN = "AtsTempAdmin";
   private static final String ATS_ADMIN = "AtsAdmin";

   @Override
   public void runCommitAction(BranchId sourceBranch, BranchId destinationBranch) throws OseeCoreException {
      Set<Artifact> changedArtifacts = new HashSet<>();
      Collection<Change> changes = new ArrayList<>();
      IOperation operation = ChangeManager.compareTwoBranchesHead(sourceBranch, destinationBranch, changes);
      Operations.executeWorkAndCheckStatus(operation);

      for (Change change : changes) {
         if (!change.getModificationType().isDeleted()) {
            Artifact artifactChanged = change.getChangeArtifact();
            if (artifactChanged != null) {
               changedArtifacts.add(artifactChanged);
            }
         }
      }

      final MutableBoolean adminOverride = new MutableBoolean(false);
      OseeValidator validator = OseeValidator.getInstance();
      for (Artifact artifactChanged : changedArtifacts) {
         if (!artifactChanged.isDeleted()) {
            IStatus status = validator.validate(IOseeValidator.LONG, artifactChanged);
            if (status.getSeverity() == IStatus.ERROR) {
               // Allow Admin to override state validation
               checkForOverride(adminOverride, status, artifactChanged);
               if (!adminOverride.getValue()) {
                  throw new OseeWrappedException(getArtifactErrorMessage(artifactChanged) + " " + status.getMessage(),
                     status.getException());
               }
            }
         }
      }

   }

   private void checkForOverride(final MutableBoolean adminOverride, IStatus status, Artifact artifactChanged) {
      List<Artifact> relatedArtifacts = UserManager.getUser().getRelatedArtifacts(CoreRelationTypes.Users_Artifact);
      for (Artifact related : relatedArtifacts) {
         if (related.getName().equals(ATS_ADMIN) || related.getName().equals(ATS_TEMP_ADMIN)) {
            Displays.pendInDisplayThread(new Runnable() {
               @Override
               public void run() {
                  if (MessageDialog.openConfirm(Displays.getActiveShell(), "Override State Validation",
                     status.getMessage() + " [" + artifactChanged.getName() + "(" + artifactChanged.getArtId() + ")]" // 
                        + "\n\nYou are set as Admin, OVERRIDE this?")) {
                     adminOverride.setValue(true);
                  } else {
                     adminOverride.setValue(false);
                  }
               }
            });
         }
      }
   }

   private String getArtifactErrorMessage(Artifact artifact) {
      return String.format("Error validating: [(%s)(%s) - %s] on branchUuid:[%s]", artifact.getArtId(),
         artifact.getGuid(), artifact.getName(), artifact.getBranch());
   }

}
