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
package org.eclipse.osee.framework.ui.skynet.render;

import java.util.Date;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.plugin.core.util.OseeData;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.change.ArtifactDelta;
import org.eclipse.osee.framework.skynet.core.word.WordAnnotationHandler;
import org.eclipse.osee.framework.ui.skynet.preferences.MsWordPreferencePage;

public final class RenderingUtil {
   private static final Random generator = new Random();

   private static IFolder workingFolder;
   private static IFolder diffFolder;
   private static IFolder previewFolder;
   private static IFolder mergeEditFolder;
   private static boolean arePopupsAllowed = true;

   private RenderingUtil() {
   }

   public static void setPopupsAllowed(boolean popupsAllowed) {
      arePopupsAllowed = popupsAllowed;
   }

   public static boolean arePopupsAllowed() {
      return arePopupsAllowed;
   }

   public static Pair<Artifact, Artifact> asRenderInput(ArtifactDelta delta) {
      Artifact artFile1;
      Artifact artFile2;

      //    if (delta.getTxDelta() == null
      //          || !delta.getTxDelta().areOnTheSameBranch()) {
      //       // Assumptions - when comparing data between transactions on
      //       // different branches, the start artifact will never be null;
      //       if (delta.getStartArtifact().getModType().isDeleted()) {
      //          artFile1 = delta.getStartArtifact();
      //          artFile2 = null;
      //       } else if (delta.getEndArtifact() == null) {
      //          artFile1 = null;
      //          artFile2 = delta.getStartArtifact();
      //       } else { // case where there are new, modified, or deleted attributes (are artifact is not new or deleted)
      //          // also could be introduced in both branches
      //          artFile1 = delta.getEndArtifact();
      //          artFile2 = delta.getStartArtifact();
      //       }
      //    } else {
      // Assumptions - when comparing data between transactions on the
      // same branch, the end artifact will never be null;
      if (delta.getEndArtifact().getModType().isDeleted()) {
         artFile1 = delta.getStartArtifact();
         artFile2 = null;
      } else if (delta.getStartArtifact() == null) {
         artFile1 = null;
         artFile2 = delta.getEndArtifact();
      } else {
         artFile1 = delta.getStartArtifact();
         artFile2 = delta.getEndArtifact();
      }
      //    }
      return new Pair<Artifact, Artifact>(artFile1, artFile2);
   }

   public static String getFilenameFromArtifact(FileSystemRenderer renderer, Artifact artifact, PresentationType presentationType) throws OseeCoreException {
      StringBuilder name = new StringBuilder(100);

      if (artifact != null) {
         name.append(artifact.getSafeName());
         name.append("(");
         name.append(artifact.getGuid());
         name.append(")");

         if (artifact.isHistorical() || presentationType == PresentationType.DIFF) {
            name.append("(");
            name.append(artifact.getTransactionNumber());
            name.append(")");
         }

         name.append(" ");
         name.append(new Date().toString().replaceAll(":", ";"));
         name.append("-");
         name.append(generator.nextInt(99) + 1);
         name.append(".");
         name.append(renderer.getAssociatedExtension(artifact));
      } else {
         name.append(GUID.create());
         name.append(".xml");
      }
      return name.toString();
   }

   public static IFolder getRenderFolder(Branch branch, PresentationType presentationType) throws OseeCoreException {
      try {
         IFolder baseFolder = ensureRenderFolderExists(presentationType);
         IFolder renderFolder = baseFolder.getFolder(BranchManager.toFileName(branch));
         if (!renderFolder.exists()) {
            renderFolder.create(true, true, null);
         }
         return renderFolder;
      } catch (CoreException ex) {
         throw new OseeCoreException(ex);
      }
   }

   public static IFolder ensureRenderFolderExists(PresentationType presentationType) throws OseeCoreException {
      IFolder toReturn = null;
      switch (presentationType) {
         case DIFF:
            diffFolder = getOrCreateFolder(diffFolder, ".diff");
            toReturn = diffFolder;
            break;
         case SPECIALIZED_EDIT:
            workingFolder = getOrCreateFolder(workingFolder, ".working");
            toReturn = workingFolder;
            break;
         case PREVIEW:
            previewFolder = getOrCreateFolder(previewFolder, ".preview");
            toReturn = previewFolder;
            break;
         case MERGE_EDIT:
            mergeEditFolder = getOrCreateFolder(mergeEditFolder, ".mergeEdit");
            toReturn = mergeEditFolder;
            break;
         default:
            throw new OseeArgumentException("Unexpected presentation type");
      }
      return toReturn;
   }

   private static IFolder getOrCreateFolder(IFolder folder, String name) throws OseeCoreException {
      IFolder toCheck = folder;
      if (toCheck == null || !toCheck.exists()) {
         toCheck = OseeData.getFolder(name);
      }
      return toCheck;
   }

   public static Set<Artifact> checkForTrackedChangesOn(Artifact artifact) throws OseeCoreException {
      Set<Artifact> artifacts = new HashSet<Artifact>();

      if (!UserManager.getUser().getBooleanSetting(MsWordPreferencePage.REMOVE_TRACKED_CHANGES)) {
         if (artifact != null) {
            Attribute<?> attribute = artifact.getSoleAttribute(CoreAttributeTypes.WORD_TEMPLATE_CONTENT);
            if (attribute != null) {
               String value = attribute.getValue().toString();
               // check for track changes
               if (WordAnnotationHandler.containsWordAnnotations(value)) {
                  // capture those artifacts that have tracked changes on
                  artifacts.add(artifact);
               }
            }
         }
      }
      return artifacts;
   }
}
