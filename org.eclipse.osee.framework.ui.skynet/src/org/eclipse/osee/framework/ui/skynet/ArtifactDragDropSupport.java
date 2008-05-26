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
import java.sql.SQLException;
import org.eclipse.core.resources.IFile;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactData;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTransfer;
import org.eclipse.osee.framework.skynet.core.artifact.WorkspaceFileArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.WorkspaceURL;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationSide;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeSide;
import org.eclipse.osee.framework.ui.plugin.util.AWorkspace;
import org.eclipse.osee.framework.ui.skynet.relation.explorer.RelationExplorerWindow;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Michael S. Rodgers
 */
public class ArtifactDragDropSupport {

   public static void performDragDrop(DropTargetEvent e, RelationExplorerWindow window, Shell shell) throws SQLException {
      performDragDrop(e, null, window, shell);
   }

   public static void performDragDrop(DropTargetEvent e, Artifact[] artifacts, RelationExplorerWindow window, Shell shell) throws SQLException {

      if (ArtifactTransfer.getInstance().isSupportedType(e.currentDataType)) {

         if (artifacts != null)
            addArtifacts(artifacts, window);
         else {
            ArtifactData artData = ArtifactTransfer.getInstance().nativeToJava(e.currentDataType);
            addArtifacts(artData.getArtifacts(), window);
         }
      } else if (e.data instanceof String[]) {
         addFiles((String[]) e.data, window, shell);

      } else if (e.data instanceof String) {
         addURL((String) e.data, window, shell);
      }
   }

   private static void ensureLinkValidity(RelationTypeSide group, Artifact artifact) throws SQLException {
      RelationType relationType = group.getRelationType();
      Artifact otherArtifact = group.getArtifact();

      Artifact artifactA = group.getSide() == RelationSide.SIDE_A ? artifact : otherArtifact;
      Artifact artifactB = group.getSide() == RelationSide.SIDE_A ? otherArtifact : artifact;
      RelationManager.ensureRelationCanBeAdded(relationType, artifactA, artifactB);
   }

   private static void addArtifacts(Artifact[] artifacts, RelationExplorerWindow window) throws SQLException {
      RelationTypeSide group = window.getRelationGroup();
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
      } catch (IllegalArgumentException ex) {
         window.addInvalid("All", ex.getMessage());
      }
   }

   private static void addFiles(String[] fileNames, RelationExplorerWindow window, Shell shell) throws SQLException {
      RelationTypeSide group = window.getRelationGroup();
      IFile iFile;
      Artifact artifact;

      for (int i = 0; i < fileNames.length; i++) {
         iFile = AWorkspace.getIFile(fileNames[i]);
         String location;

         if (iFile == null) {
            try {
               location = new File(fileNames[i]).toURL().toString();
            } catch (MalformedURLException e) {
               window.addInvalid(fileNames[i], "Malformed URL exception: " + e.getMessage());
               continue;
            }
         } else {
            location = WorkspaceURL.getURL(iFile);
         }

         try {
            artifact = WorkspaceFileArtifact.getArtifactFromWorkspaceFile(location, shell);
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
            } catch (IllegalArgumentException ex) {
               window.addInvalid(artifact.getDescriptiveName(), ex.getMessage());
            }

         }
      }
   }

   private static void addURL(String url, RelationExplorerWindow window, Shell shell) throws SQLException {
      RelationTypeSide group = window.getRelationGroup();
      Artifact artifact;
      String location;

      try {
         location = new URL(url).toString();
      } catch (MalformedURLException e) {
         window.addInvalid(url, "Malformed Exception : " + e.getMessage());
         return;
      }

      try {
         artifact = WorkspaceFileArtifact.getArtifactFromWorkspaceFile(location, shell);
      } catch (Exception ex) {
         window.addInvalid(location, "Runtime exception: " + ex.getMessage());
         return;
      }

      if (artifact == null) {
         window.addInvalid(location, "Artifact has been cancelled");
      }
      try {
         ensureLinkValidity(group, artifact);
         window.addValid(artifact);
      } catch (IllegalArgumentException ex) {
         window.addInvalid(artifact.getDescriptiveName(), ex.getMessage());
      }
   }
}
