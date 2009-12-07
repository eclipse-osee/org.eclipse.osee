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
package org.eclipse.osee.framework.skynet.core.commit.actions;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.WordAttribute;
import org.eclipse.osee.framework.skynet.core.change.AttributeChange;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.revision.ChangeManager;
import org.eclipse.osee.framework.skynet.core.validation.IOseeValidator;
import org.eclipse.osee.framework.skynet.core.validation.OseeValidator;

/**
 * @author Theron Virgin
 */
public class CatchTrackedChanges implements CommitAction {

   /**
    * Check that none of the artifacts that will be commited contain tracked changes Use the change report to get
    * attributeChanges and check their content for trackedChanges
    */

   @Override
   public void runCommitAction(Branch branch) throws OseeCoreException {
      Set<Artifact> changedArtifacts = new HashSet<Artifact>();

      for (Change change : ChangeManager.getChangesPerBranch(branch, new NullProgressMonitor())) {
         if (!change.getModificationType().isDeleted()) {
            if (change instanceof AttributeChange) {
               Attribute<?> attribute = ((AttributeChange) change).getAttribute();
               if (attribute instanceof WordAttribute) {
                  if (((WordAttribute) attribute).containsWordAnnotations()) {
                     throw new OseeCoreException(
                           String.format(
                                 "Commit Branch Failed Artifact \"%s\" Art_ID \"%d\" contains Tracked Changes. Accept all revision changes and then recommit",
                                 attribute.getArtifact().getSafeName(), attribute.getArtifact().getArtId()));
                  }
               }
            }
            Artifact artifactChanged = change.getArtifact();
            if (artifactChanged != null) {
               changedArtifacts.add(artifactChanged);
            }
         }
      }

      int severityMask = IStatus.ERROR;
      OseeValidator validator = OseeValidator.getInstance();
      for (Artifact artifactChanged : changedArtifacts) {
         if (!artifactChanged.isDeleted()) {
            IStatus status = validator.validate(IOseeValidator.LONG, artifactChanged);
            try {
               Operations.checkForStatusSeverityMask(status, severityMask);
            } catch (Exception ex) {
               throw new OseeWrappedException(getArtifactErrorMessage(artifactChanged), ex);
            }
         }
      }
   }

   private String getArtifactErrorMessage(Artifact artifact) {
      return String.format("Error validating: [(%s)(%s) - %s] on branchId:[%s]", artifact.getArtId(),
            artifact.getGuid(), artifact.getName(), artifact.getBranch().getId());
   }
}
