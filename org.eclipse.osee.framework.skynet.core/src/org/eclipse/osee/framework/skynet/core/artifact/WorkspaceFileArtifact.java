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
import java.sql.SQLException;
import java.util.Collection;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;
import org.eclipse.osee.framework.skynet.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
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
    * @throws SQLException
    */
   public WorkspaceFileArtifact(ArtifactFactory parentFactory, String guid, String humanReadableId, Branch branch, ArtifactType artifactType) {
      super(parentFactory, guid, humanReadableId, branch, artifactType);
   }

   public static Artifact getArtifactFromWorkspaceFile(String location, Shell shell) throws SQLException, OseeCoreException {
      Artifact artifact = null;
      int descriptorSelected = -1;
      ArtifactType descriptor = null;
      ArtifactDescriptorDialog dialog = null;

      try {
         artifact =
               ArtifactQuery.getArtifactFromAttribute("Content URL", location,
                     BranchPersistenceManager.getDefaultBranch());
      } catch (ArtifactDoesNotExist ex) {
         Collection<ArtifactType> descriptors =
               ConfigurationPersistenceManager.getArtifactTypesFromAttributeType(AttributeTypeManager.getType("Content URL"));
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
            artifact = descriptor.makeNewArtifact(BranchPersistenceManager.getDefaultBranch());
            artifact.setSoleAttributeValue("Content URL", location);
            artifact.setSoleAttributeValue("Name", new File(location).getName());
            artifact.persistAttributes();
         }
      }

      return artifact;
   }
}
