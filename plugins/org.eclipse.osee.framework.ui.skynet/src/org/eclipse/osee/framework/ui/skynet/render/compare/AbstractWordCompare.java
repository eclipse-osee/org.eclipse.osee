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

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
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
import org.eclipse.osee.framework.ui.skynet.render.WordImageChecker;
import org.eclipse.osee.framework.ui.skynet.util.IVbaDiffGenerator;
import org.eclipse.osee.framework.ui.skynet.util.WordUiUtil;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractWordCompare implements IComparator {
   private final FileSystemRenderer renderer;
   private final ArtifactDeltaToFileConverter converter;
   private final IAttributeType wordAttributeType;

   public AbstractWordCompare(FileSystemRenderer renderer, IAttributeType wordAttributeType) {
      this.renderer = renderer;
      this.wordAttributeType = wordAttributeType;
      converter = new ArtifactDeltaToFileConverter(renderer);
   }

   protected FileSystemRenderer getRenderer() {
      return renderer;
   }

   protected IVbaDiffGenerator createGenerator(List<Artifact> artifacts, IOseeBranch branch, PresentationType presentationType, String pathPrefix) throws OseeCoreException {
      boolean show = !getRenderer().getBooleanOption(IRenderer.NO_DISPLAY);
      String resultPath =
         RenderingUtil.getRenderPath(renderer, artifacts, branch, presentationType, pathPrefix, ".xml");
      IVbaDiffGenerator diffGenerator =
         WordUiUtil.createScriptGenerator(show, presentationType == PresentationType.MERGE, resultPath);
      return diffGenerator;
   }

   @Override
   public String compare(IProgressMonitor monitor, PresentationType presentationType, ArtifactDelta artifactDelta, String pathPrefix) throws OseeCoreException {
      Artifact artifact = artifactDelta.getStartArtifact();
      if (artifact == null) {
         artifact = artifactDelta.getEndArtifact();
      }
      IOseeBranch branch = artifact.getBranch();

      IVbaDiffGenerator diffGenerator =
         createGenerator(Collections.singletonList(artifact), branch, presentationType, pathPrefix);
      String diffPath = addToCompare(monitor, diffGenerator, presentationType, artifactDelta);

      finish(diffGenerator, artifact.getBranch(), presentationType);
      return diffPath;
   }

   @Override
   public String compare(Artifact baseVersion, Artifact newerVersion, IFile baseFile, IFile newerFile, PresentationType presentationType, String pathPrefix) throws OseeCoreException {
      IOseeBranch branch = (baseVersion != null ? baseVersion : newerVersion).getBranch();

      IVbaDiffGenerator diffGenerator =
         createGenerator(Collections.singletonList(newerVersion), branch, presentationType, pathPrefix);
      String diffPath = addToCompare(diffGenerator, baseVersion, newerVersion, baseFile, newerFile, presentationType);

      finish(diffGenerator, branch, presentationType);
      return diffPath;
   }

   protected String addToCompare(IVbaDiffGenerator diffGenerator, Artifact baseVersion, Artifact newerVersion, IFile baseFile, IFile newerFile, PresentationType presentationType) throws OseeCoreException {
      Artifact artifact = baseVersion != null ? baseVersion : newerVersion;
      List<Artifact> artifacts = Collections.singletonList(artifact);
      String diffPath =
         RenderingUtil.getRenderPath(renderer, artifacts, artifact.getBranch(), presentationType, null, ".xml");

      diffGenerator.addComparison(baseFile, newerFile, diffPath, presentationType == PresentationType.MERGE);
      return diffPath;
   }

   protected String addToCompare(IProgressMonitor monitor, IVbaDiffGenerator diffGenerator, PresentationType presentationType, ArtifactDelta artifactDelta) throws OseeCoreException {
      Pair<String, Boolean> originalValue = null;

      Artifact baseArtifact = artifactDelta.getStartArtifact();
      Artifact newerArtifact = artifactDelta.getEndArtifact();

      Attribute<String> baseContent = getWordContent(baseArtifact, wordAttributeType);
      Attribute<String> newerContent = getWordContent(newerArtifact, wordAttributeType);

      if (!UserManager.getBooleanSetting(MsWordPreferencePage.IDENTFY_IMAGE_CHANGES)) {
         originalValue = WordImageChecker.checkForImageDiffs(baseContent, newerContent);
      }

      Pair<IFile, IFile> compareFiles = converter.convertToFile(presentationType, artifactDelta);

      WordImageChecker.restoreOriginalValue(baseContent, originalValue);

      monitor.setTaskName("Adding to Diff Script: " + (newerArtifact == null ? baseArtifact.getName() : newerArtifact.getName()));

      return addToCompare(diffGenerator, baseArtifact, newerArtifact, compareFiles.getFirst(),
         compareFiles.getSecond(), presentationType);
   }

   private Attribute<String> getWordContent(Artifact artifact, IAttributeType attributeType) throws OseeCoreException {
      Attribute<String> toReturn = null;
      if (artifact != null && !artifact.isDeleted()) {
         toReturn = artifact.getSoleAttribute(attributeType);
      }
      return toReturn;
   }

   protected void finish(IVbaDiffGenerator diffGenerator, IOseeBranch branch, PresentationType presentationType) throws OseeCoreException {
      String vbsPath = RenderingUtil.getRenderPath(renderer, branch, presentationType, null, "compareDocs", ".vbs");
      if (RenderingUtil.arePopupsAllowed()) {
         diffGenerator.finish(vbsPath);
      } else {
         OseeLog.log(SkynetGuiPlugin.class, Level.INFO, String.format("Test - Skip launch of [%s]", vbsPath));
      }
   }
}
