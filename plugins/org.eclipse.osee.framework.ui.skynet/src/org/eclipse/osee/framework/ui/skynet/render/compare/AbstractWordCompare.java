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

   @Override
   public String compare(IProgressMonitor monitor, PresentationType presentationType, ArtifactDelta artifactDelta) throws OseeCoreException {
      boolean show = !getRenderer().getBooleanOption(IRenderer.NO_DISPLAY);
      IVbaDiffGenerator diffGenerator = WordUiUtil.createScriptGenerator();
      diffGenerator.initialize(show, presentationType == PresentationType.MERGE);
      String diffPath = addTocompare(monitor, diffGenerator, presentationType, artifactDelta);
      finish(diffGenerator, artifactDelta.getStartArtifact().getBranch(), presentationType);
      return diffPath;
   }

   @Override
   public String compare(Artifact baseVersion, Artifact newerVersion, IFile baseFile, IFile newerFile, PresentationType presentationType) throws OseeCoreException {
      boolean show = !renderer.getBooleanOption(IRenderer.NO_DISPLAY);
      IVbaDiffGenerator diffGenerator = WordUiUtil.createScriptGenerator();
      diffGenerator.initialize(show, presentationType == PresentationType.MERGE);
      String diffPath = addTocompare(diffGenerator, baseVersion, newerVersion, baseFile, newerFile, presentationType);
      finish(diffGenerator, baseVersion.getBranch(), presentationType);
      return diffPath;
   }

   protected String addTocompare(IVbaDiffGenerator diffGenerator, Artifact baseVersion, Artifact newerVersion, IFile baseFile, IFile newerFile, PresentationType presentationType) throws OseeCoreException {
      String diffPath =
         RenderingUtil.getRenderFile(renderer, Collections.singletonList(baseVersion), baseVersion.getBranch(),
            presentationType).getLocation().toOSString();

      diffGenerator.addComparison(baseFile, newerFile, diffPath, presentationType == PresentationType.MERGE);
      return diffPath;
   }

   protected String addTocompare(IProgressMonitor monitor, IVbaDiffGenerator diffGenerator, PresentationType presentationType, ArtifactDelta artifactDelta) throws OseeCoreException {
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

      return addTocompare(diffGenerator, baseArtifact, newerArtifact, compareFiles.getFirst(),
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
      boolean show = !renderer.getBooleanOption(IRenderer.NO_DISPLAY);
      String vbsPath = RenderingUtil.getRenderPath("compareDocs.vbs", branch, presentationType);
      if (RenderingUtil.arePopupsAllowed()) {
         diffGenerator.finish(vbsPath, show);
      } else {
         OseeLog.log(SkynetGuiPlugin.class, Level.INFO, String.format("Test - Skip launch of [%s]", vbsPath));
      }
   }
}