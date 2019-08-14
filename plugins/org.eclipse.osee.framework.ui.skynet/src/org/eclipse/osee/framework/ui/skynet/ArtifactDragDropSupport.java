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
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactData;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeSideSorter;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.skynet.artifact.ArtifactTransfer;
import org.eclipse.osee.framework.ui.skynet.branch.BranchSelectionDialog;
import org.eclipse.osee.framework.ui.skynet.relation.explorer.RelationExplorerWindow;
import org.eclipse.osee.framework.ui.skynet.util.ArtifactTypeLabelProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredTreeDialog;
import org.eclipse.osee.framework.ui.ws.AWorkspace;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Michael S. Rodgers
 */
public class ArtifactDragDropSupport {

   public static void performDragDrop(DropTargetEvent e, RelationExplorerWindow window, Shell shell) {
      performDragDrop(e, null, window, shell);
   }

   private static void performDragDrop(DropTargetEvent e, Artifact[] artifacts, RelationExplorerWindow window, Shell shell) {

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

   private static void ensureLinkValidity(RelationTypeSideSorter group, Artifact artifact) {
      IRelationType relationType = group.getRelationType();
      Artifact otherArtifact = group.getArtifact();

      Artifact artifactA = group.getSide() == RelationSide.SIDE_A ? artifact : otherArtifact;
      Artifact artifactB = group.getSide() == RelationSide.SIDE_A ? otherArtifact : artifact;
      RelationManager.ensureRelationCanBeAdded(relationType, artifactA, artifactB);
   }

   private static void addArtifacts(Artifact[] artifacts, RelationExplorerWindow window) {
      RelationTypeSideSorter group = window.getRelationGroup();
      RelationSide relationSide = group.getSide();
      IRelationType relationType = group.getRelationType();

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
            location = getURL(iFile);
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
         return;
      }
      try {
         ensureLinkValidity(group, artifact);
         window.addValid(artifact);
      } catch (OseeCoreException ex) {
         window.addInvalid(artifact.getName(), ex.getMessage());
      }
   }

   public static Artifact getArtifactFromWorkspaceFile(IResource resource, Shell shell) {
      String path = getURL(resource);
      return getArtifactFromWorkspaceFile(path, shell);
   }

   private static String getURL(IResource resource) {
      // Add only 1 "/" due to the path for the file having a preceding "/"
      return "ws:/" + resource.getFullPath().toString();
   }

   public static Artifact getArtifactFromWorkspaceFile(String location, Shell shell) {
      Artifact artifact = null;
      BranchId branch = BranchSelectionDialog.getBranchFromUser();
      if (branch != null) {
         try {
            artifact = ArtifactQuery.getArtifactFromAttribute(CoreAttributeTypes.ContentUrl, location, branch);
         } catch (ArtifactDoesNotExist ex) {
            Collection<? extends ArtifactTypeToken> artifactTypes =
               ArtifactTypeManager.getArtifactTypesFromAttributeType(CoreAttributeTypes.ContentUrl, branch);
            FilteredTreeDialog dialog = new FilteredTreeDialog("Artifact Types",
               "No Artifact could be found for this file. To create one, please select an artfact type.",
               new ArrayTreeContentProvider(), new ArtifactTypeLabelProvider());
            dialog.setInput(artifactTypes);

            if (dialog.open() == Window.OK) {
               artifact = ArtifactTypeManager.addArtifact((ArtifactTypeToken) dialog.getSelectedFirst(), branch);
               artifact.setSoleAttributeValue(CoreAttributeTypes.ContentUrl, location);
               artifact.setName(new File(location).getName());
            }
         }
      }

      return artifact;
   }
}
