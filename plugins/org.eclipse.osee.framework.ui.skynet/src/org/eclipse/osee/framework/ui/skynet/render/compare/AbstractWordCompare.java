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
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.change.ArtifactDelta;
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

   protected IVbaDiffGenerator createGenerator(List<Artifact> artifacts, IOseeBranch branch, PresentationType presentationType) throws OseeCoreException {
      boolean show = !getRenderer().getBooleanOption(IRenderer.NO_DISPLAY);
      boolean executeVbScript = System.getProperty("os.name").contains("Windows");
      IVbaDiffGenerator diffGenerator =
         WordUiUtil.createScriptGenerator(presentationType == PresentationType.MERGE, show,
            presentationType == PresentationType.MERGE, executeVbScript);
      return diffGenerator;
   }

   @Override
   public void compare(IProgressMonitor monitor, CompareDataCollector collector, PresentationType presentationType, ArtifactDelta artifactDelta, String pathPrefix) throws OseeCoreException {
      Artifact artifact = artifactDelta.getStartArtifact();
      if (artifact == null) {
         artifact = artifactDelta.getEndArtifact();
      }
      IOseeBranch branch = artifact.getBranch();

      IVbaDiffGenerator diffGenerator = createGenerator(Collections.singletonList(artifact), branch, presentationType);

      String resultPath =
         getDiffPath(artifactDelta.getStartArtifact(), artifactDelta.getEndArtifact(), presentationType, pathPrefix);
      String vbsPath = RenderingUtil.getRenderPath(renderer, branch, presentationType, null, "compareDocs", ".vbs");

      CompareData data = new CompareData(resultPath, vbsPath);

      addToCompare(monitor, data, presentationType, artifactDelta);

      diffGenerator.generate(data);
      collector.onCompare(data);
   }

   @Override
   public void compare(CompareDataCollector collector, Artifact baseVersion, Artifact newerVersion, IFile baseFile, IFile newerFile, PresentationType presentationType, String pathPrefix) throws OseeCoreException {
      IOseeBranch branch = (baseVersion != null ? baseVersion : newerVersion).getBranch();

      String resultPath = getDiffPath(baseVersion, newerVersion, presentationType, pathPrefix);
      String vbsPath = RenderingUtil.getRenderPath(renderer, branch, presentationType, null, "compareDocs", ".vbs");
      CompareData data = new CompareData(resultPath, vbsPath);

      data.add(baseFile.getLocation().toOSString(), newerFile.getLocation().toOSString());

      IVbaDiffGenerator diffGenerator =
         createGenerator(Collections.singletonList(newerVersion), branch, presentationType);

      diffGenerator.generate(data);
      collector.onCompare(data);
   }

   protected String getDiffPath(Artifact baseVersion, Artifact newerVersion, PresentationType presentationType, String prefix) throws OseeCoreException {
      Artifact artifact = baseVersion != null ? baseVersion : newerVersion;
      List<Artifact> artifacts = Collections.singletonList(artifact);
      String diffPath =
         RenderingUtil.getRenderPath(renderer, artifacts, artifact.getBranch(), presentationType, prefix, ".xml");
      return diffPath;
   }

   protected void addToCompare(IProgressMonitor monitor, CompareData data, PresentationType presentationType, ArtifactDelta artifactDelta) throws OseeCoreException {
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
      data.add(compareFiles.getFirst().getLocation().toOSString(), compareFiles.getSecond().getLocation().toOSString());
   }

   private Attribute<String> getWordContent(Artifact artifact, IAttributeType attributeType) throws OseeCoreException {
      Attribute<String> toReturn = null;
      if (artifact != null && !artifact.isDeleted()) {
         toReturn = artifact.getSoleAttribute(attributeType);
      }
      return toReturn;
   }
}
