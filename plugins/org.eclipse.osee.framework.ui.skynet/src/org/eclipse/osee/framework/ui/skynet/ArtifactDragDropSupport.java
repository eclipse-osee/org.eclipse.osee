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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.ArtifactType;
import org.eclipse.osee.framework.core.model.AttributeType;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.RelationType;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactData;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.WorkspaceURL;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeSideSorter;
import org.eclipse.osee.framework.ui.plugin.util.AWorkspace;
import org.eclipse.osee.framework.ui.skynet.artifact.ArtifactTransfer;
import org.eclipse.osee.framework.ui.skynet.branch.BranchSelectionDialog;
import org.eclipse.osee.framework.ui.skynet.relation.explorer.RelationExplorerWindow;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Michael S. Rodgers
 */
public class ArtifactDragDropSupport {

   public static void performDragDrop(DropTargetEvent e, RelationExplorerWindow window, Shell shell) throws OseeCoreException {
      performDragDrop(e, null, window, shell);
   }

   private static void performDragDrop(DropTargetEvent e, Artifact[] artifacts, RelationExplorerWindow window, Shell shell) throws OseeCoreException {

      if (ArtifactTransfer.getInstance().isSupportedType(e.currentDataType)) {

         if (artifacts != null) {
            addArtifacts(artifacts, window);
         } else {
            ArtifactData artData = ArtifactTransfer.getInstance().nativeToJava(e.currentDataType);
            addArtifacts(artData.getArtifacts(), window);
         }
      } else if (e.data instanceof String[]) {
         addFiles((String[]) e.data, window, shell);

      } else if (e.data instanceof String) {
         addURL((String) e.data, window, shell);
      }
   }

   private static void ensureLinkValidity(RelationTypeSideSorter group, Artifact artifact) throws OseeCoreException {
      RelationType relationType = group.getRelationType();
      Artifact otherArtifact = group.getArtifact();

      Artifact artifactA = group.getSide() == RelationSide.SIDE_A ? artifact : otherArtifact;
      Artifact artifactB = group.getSide() == RelationSide.SIDE_A ? otherArtifact : artifact;
      RelationManager.ensureRelationCanBeAdded(relationType, artifactA, artifactB);
   }

   private static void addArtifacts(Artifact[] artifacts, RelationExplorerWindow window) throws OseeCoreException {
      RelationTypeSideSorter group = window.getRelationGroup();
      RelationSide relationSide = group.getSide();
      RelationType relationType = group.getRelationType();

      try {
         for (Artifact artifact : artifacts) {
            try {
               Artifact artA = null;
               Artifact artB = null;
               if (relationSide == RelationSide.SIDE_A) {
                  artA = artifact;
                  artB = group.getArtifact();
               } else {
                  artA = group.getArtifact();
                  artB = artifact;
               }
               RelationManager.ensureRelationCanBeAdded(relationType, artA, artB);

               window.addValid(artifact);
            } catch (IllegalArgumentException ex) {
               window.addInvalidArtifact(artifact, ex.getMessage());
            }
         }
      } catch (OseeArgumentException ex) {
         window.addInvalid("All", ex.getMessage());
      }
   }

   private static void addFiles(String[] fileNames, RelationExplorerWindow window, Shell shell) {
      RelationTypeSideSorter group = window.getRelationGroup();
      IFile iFile;
      Artifact artifact;

      for (int i = 0; i < fileNames.length; i++) {
         iFile = AWorkspace.getIFile(fileNames[i]);
         String location;

         if (iFile == null) {
            try {
               location = new File(fileNames[i]).toURI().toURL().toString();
            } catch (MalformedURLException e) {
               window.addInvalid(fileNames[i], "Malformed URL exception: " + e.getMessage());
               continue;
            }
         } else {
            location = WorkspaceURL.getURL(iFile);
         }

         try {
            artifact = getArtifactFromWorkspaceFile(location, shell);
         } catch (Exception ex) {
            window.addInvalid(location, "Runtime exception: " + ex.getMessage());
            continue;
         }

         if (artifact == null) {
            window.addInvalid(location, "Artifact has been cancelled");
         } else {
            try {
               ensureLinkValidity(group, artifact);
               window.addValid(artifact);
            } catch (OseeCoreException ex) {
               window.addInvalid(artifact.getName(), ex.getMessage());
            }

         }
      }
   }

   private static void addURL(String url, RelationExplorerWindow window, Shell shell) {
      RelationTypeSideSorter group = window.getRelationGroup();
      Artifact artifact;
      String location;

      try {
         location = new URL(url).toString();
      } catch (MalformedURLException e) {
         window.addInvalid(url, "Malformed Exception : " + e.getLocalizedMessage());
         return;
      }

      try {
         artifact = getArtifactFromWorkspaceFile(location, shell);
      } catch (Exception ex) {
         window.addInvalid(location, "Runtime exception: " + ex.getLocalizedMessage());
         return;
      }

      if (artifact == null) {
         window.addInvalid(location, "Artifact has been cancelled");
      }
      try {
         ensureLinkValidity(group, artifact);
         window.addValid(artifact);
      } catch (OseeCoreException ex) {
         window.addInvalid(artifact.getName(), ex.getMessage());
      }
   }

   public static Artifact getArtifactFromWorkspaceFile(String location, Shell shell) throws OseeCoreException {
      Artifact artifact = null;
      Branch branch = BranchSelectionDialog.getBranchFromUser();
      if (branch != null) {
         try {
            artifact = ArtifactQuery.getArtifactFromAttribute("Content URL", location, branch);
         } catch (ArtifactDoesNotExist ex) {
            AttributeType attributeType = AttributeTypeManager.getType("Content URL");
            Collection<ArtifactType> artifactTypes =
                  ArtifactTypeManager.getArtifactTypesFromAttributeType(attributeType, branch);
            ArtifactTypeDialog dialog =
                  new ArtifactTypeDialog(shell, "Artifact Types", null,
                        "No Artifact could be found for this file. To create one, please select an artfact type.",
                        MessageDialog.QUESTION, new String[] {"OK", "Cancel"}, 0, artifactTypes);

            if (dialog.open() == Window.OK) {
               artifact = ArtifactTypeManager.makeNewArtifact(dialog.getArtifactType(), branch);
               artifact.setSoleAttributeValue("Content URL", location);
               artifact.setSoleAttributeValue("Name", new File(location).getName());
            }
         }
      }

      return artifact;
   }
}
