/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.render.compare;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.change.ArtifactDelta;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.preferences.MsWordPreferencePage;
import org.eclipse.osee.framework.ui.skynet.render.FileSystemRenderer;
import org.eclipse.osee.framework.ui.skynet.render.IRenderer;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.RenderingUtil;
import org.eclipse.osee.framework.ui.skynet.render.VbaWordDiffGenerator;
import org.eclipse.osee.framework.ui.skynet.render.WordImageChecker;
import org.eclipse.osee.framework.ui.skynet.util.WordUiUtil;

public class WordTemplateCompare implements IComparator {
   private static final IAttributeType ATTRIBUTE_TYPE = CoreAttributeTypes.WordTemplateContent;

   private final FileSystemRenderer renderer;

   public WordTemplateCompare(FileSystemRenderer renderer) {
      this.renderer = renderer;
   }

   @Override
   public String compare(IProgressMonitor monitor, PresentationType presentationType, ArtifactDelta artifactDelta) throws OseeCoreException {
      Pair<String, Boolean> originalValue = null;
      Pair<String, Boolean> newAnnotationValue = null;

      Artifact baseArtifact = artifactDelta.getStartArtifact();
      Artifact newerArtifact = artifactDelta.getEndArtifact();

      //Check for tracked changes
      Set<Artifact> artifacts = new HashSet<Artifact>();
      artifacts.addAll(RenderingUtil.checkForTrackedChangesOn(baseArtifact));
      artifacts.addAll(RenderingUtil.checkForTrackedChangesOn(newerArtifact));

      if (!artifacts.isEmpty()) {
         if (RenderingUtil.arePopupsAllowed()) {
            WordUiUtil.displayWarningMessageDialog("Diff Artifacts Warning",
               "Detected tracked changes for some artifacts. Please refer to the results HTML report.");
            WordUiUtil.displayTrackedChangesOnArtifacts(artifacts);
         } else {
            OseeLog.log(SkynetGuiPlugin.class, Level.INFO,
               String.format("Test - Skipping - Detected tracked changes for some artifacts for [%s]", artifacts));
         }
      } else {
         Attribute<String> baseContent = getWordContent(baseArtifact, ATTRIBUTE_TYPE);
         Attribute<String> newerContent = getWordContent(newerArtifact, ATTRIBUTE_TYPE);

         if (!UserManager.getBooleanSetting(MsWordPreferencePage.IDENTFY_IMAGE_CHANGES)) {
            originalValue = WordImageChecker.checkForImageDiffs(baseContent, newerContent);
         }

         Pair<IFile, IFile> compareFiles =
            new ArtifactDeltaToFileConverter(renderer).convertToFile(presentationType, artifactDelta);

         WordImageChecker.restoreOriginalValue(baseContent, originalValue);
         WordImageChecker.restoreOriginalValue(newerContent, newAnnotationValue);

         return compare(baseArtifact, newerArtifact, compareFiles.getFirst(), compareFiles.getSecond(),
            presentationType);
      }
      return "";
   }

   @Override
   public String compare(Artifact baseVersion, Artifact newerVersion, IFile baseFile, IFile newerFile, PresentationType presentationType) throws OseeCoreException {
      String diffPath;

      String fileName = renderer.getStringOption(IRenderer.FILE_NAME_OPTION);
      if (!Strings.isValid(fileName)) {
         if (baseVersion != null) {
            String baseFileStr = baseFile.getLocation().toOSString();
            diffPath =
               baseFileStr.substring(0, baseFileStr.lastIndexOf(')') + 1) + " to " + (newerVersion != null ? newerVersion.getTransactionNumber() : " deleted") + baseFileStr.substring(baseFileStr.lastIndexOf(')') + 1);
         } else {
            String baseFileStr = newerFile.getLocation().toOSString();
            diffPath =
               baseFileStr.substring(0, baseFileStr.lastIndexOf('(') + 1) + "new " + baseFileStr.substring(baseFileStr.lastIndexOf('(') + 1);
         }
      } else {
         IFolder folder = RenderingUtil.getRenderFolder(baseVersion.getBranch(), PresentationType.MERGE_EDIT);
         diffPath = folder.getLocation().toOSString() + '\\' + fileName;
      }

      VbaWordDiffGenerator diffGenerator = new VbaWordDiffGenerator();
      diffGenerator.initialize(presentationType == PresentationType.DIFF,
         presentationType == PresentationType.MERGE_EDIT);

      boolean show = !renderer.getBooleanOption(IRenderer.NO_DISPLAY);
      if (presentationType == PresentationType.MERGE_EDIT && baseVersion != null) {
         IFolder folder = RenderingUtil.getRenderFolder(baseVersion.getBranch(), PresentationType.MERGE_EDIT);
         renderer.addFileToWatcher(folder, diffPath.substring(diffPath.lastIndexOf('\\') + 1));
         diffGenerator.addComparison(baseFile, newerFile, diffPath, true);

         String vbsPath = diffPath.substring(0, diffPath.lastIndexOf('\\')) + "mergeDocs.vbs";
         if (RenderingUtil.arePopupsAllowed()) {
            diffGenerator.finish(vbsPath, show);
         } else {
            OseeLog.log(SkynetGuiPlugin.class, Level.INFO,
               String.format("Test - Skip launch of mergeDocs.vbs for [%s]", vbsPath));
         }
      } else {
         diffGenerator.addComparison(baseFile, newerFile, diffPath, false);

         String vbsPath = diffPath.substring(0, diffPath.lastIndexOf('\\')) + "/compareDocs.vbs";
         if (RenderingUtil.arePopupsAllowed()) {
            diffGenerator.finish(vbsPath, show);
         } else {
            OseeLog.log(SkynetGuiPlugin.class, Level.INFO,
               String.format("Test - Skip launch of compareDocs.vbs for [%s]", vbsPath));
         }
      }
      return diffPath;
   }

   private Attribute<String> getWordContent(Artifact artifact, IAttributeType attributeType) throws OseeCoreException {
      Attribute<String> toReturn = null;
      if (artifact != null && !artifact.isDeleted()) {
         toReturn = artifact.getSoleAttribute(attributeType);
      }
      return toReturn;
   }

   /**
    * Creates a difference report for each artifact between baseArtifact and newerArtifact. Then produces a single
    * report by combining each of the difference reports together for a single report.
    */
   @Override
   public void compareArtifacts(IProgressMonitor monitor, PresentationType presentationType, Collection<ArtifactDelta> artifactDeltas) throws OseeCoreException {
      IOperation operation = new WordChangeReportOperation(artifactDeltas, renderer);
      Operations.executeWorkAndCheckStatus(operation, monitor);
   }
}
