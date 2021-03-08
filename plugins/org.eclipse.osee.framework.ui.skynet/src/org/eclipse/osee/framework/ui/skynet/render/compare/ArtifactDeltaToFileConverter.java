/*********************************************************************
 * Copyright (c) 2010 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.skynet.render.compare;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.resources.IFile;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.model.TransactionDelta;
import org.eclipse.osee.framework.core.model.change.CompareData;
import org.eclipse.osee.framework.core.util.RendererOption;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.plugin.core.util.AIFile;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.change.ArtifactDelta;
import org.eclipse.osee.framework.ui.skynet.render.FileSystemRenderer;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;

public class ArtifactDeltaToFileConverter {
   private final FileSystemRenderer renderer;

   public ArtifactDeltaToFileConverter(FileSystemRenderer renderer) {
      this.renderer = renderer;
   }

   public FileSystemRenderer getRenderer() {
      return renderer;
   }

   public Pair<IFile, IFile> convertToFile(PresentationType presentationType, ArtifactDelta artifactDelta) {
      Artifact baseArtifact = artifactDelta.getStartArtifact();
      Artifact newerArtifact = artifactDelta.getEndArtifact();
      if (newerArtifact.getModType().isDeleted()) {
         newerArtifact = null;
      }
      BranchToken branch = artifactDelta.getBranch();

      IFile baseFile = renderer.renderToFile(baseArtifact, branch, presentationType);
      IFile newerFile = renderer.renderToFile(newerArtifact, branch, presentationType);
      return new Pair<>(baseFile, newerFile);
   }

   public Pair<IFile, IFile> convertToFileAndCopy(PresentationType presentationType, ArtifactDelta artifactDelta) {
      Artifact baseArtifact = artifactDelta.getStartArtifact();
      Artifact newerArtifact = artifactDelta.getEndArtifact();
      if (newerArtifact.getModType().isDeleted()) {
         newerArtifact = null;
      }
      BranchToken branch = artifactDelta.getBranch();

      IFile baseFile = renderer.renderToFile(baseArtifact, branch, presentationType);
      IFile copiedFile = renderer.copyToNewFile(newerArtifact, branch, presentationType, baseFile);

      return new Pair<>(baseFile, copiedFile);
   }

   public void convertToFileForMerge(final Collection<IFile> outputFiles, TransactionDelta txDelta, Artifact baseVersion, Artifact newerVersion) {
      ArtifactDelta artifactDelta = new ArtifactDelta(txDelta, baseVersion, newerVersion);

      CompareDataCollector colletor = new CompareDataCollector() {
         @Override
         public void onCompare(CompareData data) {
            outputFiles.add(AIFile.constructIFile(data.getOutputPath()));
         }
      };
      Map<RendererOption, Object> rendererOptions = new HashMap<>();
      rendererOptions.put(RendererOption.NO_DISPLAY, true);
      rendererOptions.put(RendererOption.TEMPLATE_OPTION, RendererOption.DIFF_NO_ATTRIBUTES_VALUE.getKey());
      rendererOptions.put(RendererOption.ADD_MERGE_TAG, true);
      // Set ADD MERGE TAG as an option so resulting document will indicate a merge section

      RendererManager.diff(colletor, artifactDelta, "", rendererOptions);
   }
}