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
import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.resources.IFile;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.PresentationType;
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

   public Pair<IFile, IFile> convertToFile(PresentationType presentationType, ArtifactDelta artifactDelta)  {
      Artifact baseArtifact = artifactDelta.getStartArtifact();
      Artifact newerArtifact = artifactDelta.getEndArtifact();
      if (newerArtifact.getModType().isDeleted()) {
         newerArtifact = null;
      }
      IOseeBranch branch = artifactDelta.getBranch();

      IFile baseFile = renderer.renderToFile(baseArtifact, branch, presentationType);
      IFile newerFile = renderer.renderToFile(newerArtifact, branch, presentationType);
      return new Pair<IFile, IFile>(baseFile, newerFile);
   }

   public void convertToFileForMerge(final Collection<IFile> outputFiles, Artifact baseVersion, Artifact newerVersion)  {
      ArtifactDelta artifactDelta = new ArtifactDelta(baseVersion, newerVersion);

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