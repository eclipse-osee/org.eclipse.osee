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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.exception.OperationTimedoutException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.AIFile;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.change.ArtifactDelta;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.preferences.MsWordPreferencePage;
import org.eclipse.osee.framework.ui.skynet.render.FileSystemRenderer;
import org.eclipse.osee.framework.ui.skynet.render.IRenderer;
import org.eclipse.osee.framework.ui.skynet.render.RenderingUtil;
import org.eclipse.osee.framework.ui.skynet.render.WordImageChecker;
import org.eclipse.osee.framework.ui.skynet.util.IVbaDiffGenerator;
import org.eclipse.osee.framework.ui.skynet.util.WordUiUtil;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractWordCompare implements IComparator {
   private final FileSystemRenderer renderer;
   private final ArtifactDeltaToFileConverter converter;
   private final List<AttributeTypeId> wordAttributeType = new ArrayList<>();
   protected boolean skipDialogs;
   private static final Pattern authorPattern =
      Pattern.compile("aml:author=\".*?\"", Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);

   public AbstractWordCompare(FileSystemRenderer renderer, AttributeTypeId... wordAttributeType) {
      this.renderer = renderer;
      this.wordAttributeType.addAll(Arrays.asList(wordAttributeType));
      converter = new ArtifactDeltaToFileConverter(renderer);
   }

   protected FileSystemRenderer getRenderer() {
      return renderer;
   }

   protected IVbaDiffGenerator createGenerator(List<Artifact> artifacts, BranchId branch, PresentationType presentationType) throws OseeCoreException {
      boolean show = !getRenderer().getBooleanOption(IRenderer.NO_DISPLAY);
      boolean executeVbScript = System.getProperty("os.name").contains("Windows");
      boolean skipErrors = !getRenderer().getBooleanOption(IRenderer.SKIP_ERRORS);
      skipDialogs = getRenderer().getBooleanOption(IRenderer.SKIP_DIALOGS);
      boolean diffFieldCodes = !UserManager.getBooleanSetting(MsWordPreferencePage.IGNORE_FIELD_CODE_CHANGES);

      IVbaDiffGenerator diffGenerator = WordUiUtil.createScriptGenerator(presentationType == PresentationType.MERGE,
         show, presentationType == PresentationType.MERGE, executeVbScript, skipErrors, diffFieldCodes);
      return diffGenerator;
   }

   @Override
   public void compare(IProgressMonitor monitor, CompareDataCollector collector, PresentationType presentationType, ArtifactDelta artifactDelta, String pathPrefix) throws OseeCoreException {
      boolean invalid = false;
      Artifact startArt = artifactDelta.getStartArtifact();
      Artifact endArt = artifactDelta.getEndArtifact();
      String paraNumStart = null;
      String paraNumEnd = null;
      if (startArt != null) {
         paraNumStart = startArt.getSoleAttributeValueAsString(CoreAttributeTypes.ParagraphNumber, null);
      }
      if (endArt != null) {
         paraNumEnd =
            artifactDelta.getEndArtifact().getSoleAttributeValueAsString(CoreAttributeTypes.ParagraphNumber, null);
      }

      if (paraNumStart != null && paraNumStart.matches(".*[a-zA-Z].*")) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP,
            String.format("Invalid Paragraph Number on Artifact [%s], cannot contain letters",
               artifactDelta.getEndArtifact().getArtId()));
         invalid = true;
      }
      if (paraNumEnd != null && paraNumEnd.matches(".*[a-zA-Z].*")) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP,
            String.format("Invalid Paragraph Number on Artifact [%s], cannot contain letters",
               artifactDelta.getEndArtifact().getArtId()));
         invalid = true;
      }

      if (!invalid) {
         Artifact artifact = artifactDelta.getStartArtifact();
         if (artifact == null) {
            artifact = artifactDelta.getEndArtifact();
         }
         IOseeBranch branch = artifact.getBranchToken();

         IVbaDiffGenerator diffGenerator =
            createGenerator(Collections.singletonList(artifact), branch, presentationType);

         String resultPath =
            getDiffPath(artifactDelta.getStartArtifact(), artifactDelta.getEndArtifact(), presentationType, pathPrefix);
         String vbsPath = RenderingUtil.getRenderPath(renderer, branch, presentationType, null, "compareDocs", ".vbs");

         CompareData data = new CompareData(resultPath, vbsPath);

         addToCompare(monitor, data, presentationType, artifactDelta);
         try {
            diffGenerator.generate(monitor, data);
         } catch (OperationTimedoutException ex) {
            if (!skipDialogs) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, String.format(
                  "The View Word Change Report Timed-out for Artifact(s) [%s] on Branch [%s]", artifact, branch));
            }
         }

         collector.onCompare(data);
      }

   }

   @Override
   public void compare(CompareDataCollector collector, Artifact baseVersion, Artifact newerVersion, IFile baseFile, IFile newerFile, PresentationType presentationType, String pathPrefix) throws OseeCoreException {
      IOseeBranch branch = (baseVersion != null ? baseVersion : newerVersion).getBranchToken();

      String resultPath = getDiffPath(baseVersion, newerVersion, presentationType, pathPrefix);
      String vbsPath = RenderingUtil.getRenderPath(renderer, branch, presentationType, null, "compareDocs", ".vbs");
      CompareData data = new CompareData(resultPath, vbsPath);

      data.add(baseFile.getLocation().toOSString(), newerFile.getLocation().toOSString());

      IVbaDiffGenerator diffGenerator =
         createGenerator(Collections.singletonList(newerVersion), branch, presentationType);

      IProgressMonitor monitor = new NullProgressMonitor();
      try {
         diffGenerator.generate(monitor, data);
      } catch (OperationTimedoutException ex) {
         if (!skipDialogs) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, String.format(
               "The View Word Change Report Timed-out for Artifact(s) [%s] on Branch [%s]", newerVersion, branch));
         }
      }
      collector.onCompare(data);
   }

   protected String getDiffPath(Artifact baseVersion, Artifact newerVersion, PresentationType presentationType, String prefix) throws OseeCoreException {
      Artifact artifact = baseVersion != null ? baseVersion : newerVersion;
      List<Artifact> artifacts = Collections.singletonList(artifact);
      String diffPath =
         RenderingUtil.getRenderPath(renderer, artifacts, artifact.getBranchToken(), presentationType, prefix, ".xml");
      return diffPath;
   }

   protected void addToCompare(IProgressMonitor monitor, CompareData data, PresentationType presentationType, ArtifactDelta artifactDelta) throws OseeCoreException {
      Pair<String, Boolean> originalValue = null;

      Artifact baseArtifact = artifactDelta.getStartArtifact();
      Artifact newerArtifact = artifactDelta.getEndArtifact();

      Attribute<String> baseContent = getWordContent(baseArtifact);
      Attribute<String> newerContent = getWordContent(newerArtifact);

      if (!UserManager.getBooleanSetting(MsWordPreferencePage.IDENTFY_IMAGE_CHANGES)) {
         originalValue = WordImageChecker.checkForImageDiffs(baseContent, newerContent);
      }
      monitor.setTaskName(
         "Preparing comparison for: " + (newerArtifact == null ? baseArtifact.getName() : newerArtifact.getName()));

      Pair<IFile, IFile> compareFiles;
      if (artifactDelta.getStartArtifact() == artifactDelta.getBaseArtifact()) {
         compareFiles = converter.convertToFile(presentationType, artifactDelta);
      } else {
         // The artifactDelta is a 3 Way Merge
         List<IFile> outputFiles = new ArrayList<>();
         converter.convertToFileForMerge(outputFiles, artifactDelta.getBaseArtifact(),
            artifactDelta.getStartArtifact());
         converter.convertToFileForMerge(outputFiles, artifactDelta.getBaseArtifact(), artifactDelta.getEndArtifact());
         // this is where we are getting the exception that the length of outputFiles is 1
         // This happens because the artifact did not exist on the previous
         // branch or was removed on the current branch
         if (outputFiles.size() == 1) {
            String outputFileName = outputFiles.get(0).getRawLocation().toOSString();
            try {
               String tempFileName = Lib.removeExtension(outputFileName);
               File tempFile = new File(tempFileName + ".temp.xml");
               Lib.writeStringToFile("", tempFile);
               IFile constructIFile = AIFile.constructIFile(tempFile.getPath());
               outputFiles.add(constructIFile);
            } catch (IOException ex) {
               throw new OseeCoreException(ex, "Empty file for comparison could not be created, [%s]", outputFileName);
            }
         }
         compareFiles = new Pair<>(outputFiles.get(0), outputFiles.get(1));
         data.addMerge(outputFiles.get(0).getLocation().toOSString());
      }
      WordImageChecker.restoreOriginalValue(baseContent, originalValue);
      data.add(compareFiles.getFirst().getLocation().toOSString(), compareFiles.getSecond().getLocation().toOSString());
   }

   private Attribute<String> getWordContent(Artifact artifact) throws OseeCoreException {
      Attribute<String> toReturn = null;
      if (artifact != null && !artifact.isDeleted()) {
         for (AttributeTypeId wordAttr : wordAttributeType) {
            toReturn = artifact.getSoleAttribute(wordAttr);
            if (toReturn != null) {
               break;
            }
         }
      }
      return toReturn;
   }
}
