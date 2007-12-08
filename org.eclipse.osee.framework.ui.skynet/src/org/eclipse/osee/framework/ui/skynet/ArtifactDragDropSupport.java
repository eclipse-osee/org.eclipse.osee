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
import org.eclipse.core.resources.IFile;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactData;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTransfer;
import org.eclipse.osee.framework.skynet.core.artifact.WorkspaceFileArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.WorkspaceURL;
import org.eclipse.osee.framework.skynet.core.relation.IRelationLinkDescriptor;
import org.eclipse.osee.framework.skynet.core.relation.LinkManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationLinkGroup;
import org.eclipse.osee.framework.ui.plugin.util.AWorkspace;
import org.eclipse.osee.framework.ui.skynet.relation.explorer.RelationExplorerWindow;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Michael S. Rodgers
 */
public class ArtifactDragDropSupport {

   public static void performDragDrop(DropTargetEvent e, RelationExplorerWindow window, Shell shell) {
      performDragDrop(e, null, window, shell);
   }

   public static void performDragDrop(DropTargetEvent e, Artifact[] artifacts, RelationExplorerWindow window, Shell shell) {

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

   private static void ensureLinkValidity(RelationLinkGroup group, Artifact artifact) {
      boolean sideA = group.isSideA();
      IRelationLinkDescriptor linkDescriptor = group.getDescriptor();
      LinkManager linkManager = group.getLinkManager();
      linkManager.ensureLinkValidity(linkDescriptor, sideA, artifact);
   }

   private static void addArtifacts(Artifact[] artifacts, RelationExplorerWindow window) {
      RelationLinkGroup group = window.getRelationGroup();
      boolean sideA = group.isSideA();
      IRelationLinkDescriptor linkDescriptor = group.getDescriptor();
      LinkManager linkManager = group.getLinkManager();

      try {
         linkManager.ensureHalfLinksValidity(linkDescriptor, !sideA, artifacts.length);

         for (Artifact artifact : artifacts) {
            try {
               linkManager.ensureLinkValidity(linkDescriptor, sideA, artifact);
               window.addValid(artifact);
            } catch (IllegalArgumentException ex) {
               window.addInvalidArtifact(artifact, ex.getMessage());
            }
         }
      } catch (IllegalArgumentException ex) {
         window.addInvalid("All", ex.getMessage());
      }
   }

   private static void addFiles(String[] fileNames, RelationExplorerWindow window, Shell shell) {
      RelationLinkGroup group = window.getRelationGroup();
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
         } catch (RuntimeException e) {
            window.addInvalid(location, "Runtime exception: " + e.getMessage());
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

   private static void addURL(String url, RelationExplorerWindow window, Shell shell) {
      RelationLinkGroup group = window.getRelationGroup();
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
      } catch (RuntimeException e) {
         window.addInvalid(location, "Runtime exception: " + e.getMessage());
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
