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
package org.eclipse.osee.framework.skynet.core.artifact;

import java.io.File;
import java.util.Collection;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.db.connection.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.attribute.TypeValidityManager;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Ryan D. Brooks
 */
public class WorkspaceFileArtifact extends Artifact {
   public static final String ARTIFACT_NAME = "Workspace File";

   /**
    * @param parentFactory
    * @param guid
    * @param branch
    */
   public WorkspaceFileArtifact(ArtifactFactory parentFactory, String guid, String humanReadableId, Branch branch, ArtifactType artifactType) {
      super(parentFactory, guid, humanReadableId, branch, artifactType);
   }

   public static Artifact getArtifactFromWorkspaceFile(String location, Shell shell) throws OseeCoreException {
      Artifact artifact = null;
      int descriptorSelected = -1;
      ArtifactType descriptor = null;
      ArtifactDescriptorDialog dialog = null;

      try {
         artifact =
               ArtifactQuery.getArtifactFromAttribute("Content URL", location,
                     BranchManager.getDefaultBranch());
      } catch (ArtifactDoesNotExist ex) {
         Collection<ArtifactType> descriptors =
               TypeValidityManager.getArtifactTypesFromAttributeType("Content URL",
                     BranchManager.getDefaultBranch());
         dialog =
               new ArtifactDescriptorDialog(
                     shell,
                     "Artifact Descriptor",
                     null,
                     "No Artifact could be found for this file. To create a new artifact please" + " select an artfact descriptor.",
                     MessageDialog.QUESTION, new String[] {"OK", "Cancel"}, 0, descriptors);
         descriptorSelected = dialog.open();
         if (descriptorSelected == 0) {
            descriptor = dialog.getEntry();
            artifact = descriptor.makeNewArtifact(BranchManager.getDefaultBranch());
            artifact.setSoleAttributeValue("Content URL", location);
            artifact.setSoleAttributeValue("Name", new File(location).getName());
            artifact.persistAttributes();
         }
      }

      return artifact;
   }
}
