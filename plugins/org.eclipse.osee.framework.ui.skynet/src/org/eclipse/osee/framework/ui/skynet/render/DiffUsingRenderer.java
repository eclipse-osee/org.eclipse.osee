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

package org.eclipse.osee.framework.ui.skynet.render;

import java.util.Collection;
import java.util.Collections;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.publishing.RendererMap;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.change.ArtifactDelta;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.render.compare.CompareDataCollector;
import org.eclipse.osee.framework.ui.skynet.render.compare.IComparator;

public final class DiffUsingRenderer extends AbstractOperation {

   private final RendererMap rendererOptions;
   private final Collection<ArtifactDelta> artifactDeltas;
   private final String pathPrefix;
   private final CompareDataCollector collector;
   private final IRenderer preferedRenderer;
   private final PresentationType presentationType;

   public DiffUsingRenderer(CompareDataCollector collector, Collection<ArtifactDelta> artifactDeltas, String pathPrefix, IRenderer preferedRenderer, RendererMap rendererOptions, PresentationType presentationType) {
      super(generateOperationName(artifactDeltas), Activator.PLUGIN_ID);
      this.artifactDeltas = artifactDeltas;
      this.pathPrefix = pathPrefix;
      this.rendererOptions = rendererOptions;
      this.collector = collector;
      this.preferedRenderer = preferedRenderer;
      this.presentationType = presentationType;
   }

   public DiffUsingRenderer(CompareDataCollector collector, Collection<ArtifactDelta> artifactDeltas, String pathPrefix, RendererMap rendererOptions, PresentationType presentationType) {
      this(collector, artifactDeltas, pathPrefix, null, rendererOptions, presentationType);
   }

   public DiffUsingRenderer(CompareDataCollector collector, ArtifactDelta artifactDelta, String diffPrefix, RendererMap rendererOptions, PresentationType presentationType) {
      this(collector, Collections.singletonList(artifactDelta), diffPrefix, rendererOptions, presentationType);
   }

   private static String generateOperationName(Collection<ArtifactDelta> artifactDeltas) {
      ArtifactDelta firstDelta = artifactDeltas.iterator().next();
      if (artifactDeltas.size() == 1) {
         Artifact startVersion = firstDelta.getStartArtifact();
         Artifact endVersion = firstDelta.getEndArtifact();
         return String.format("Compare %s to %s", startVersion == null ? " new " : startVersion.getName(),
            endVersion == null ? " delete " : endVersion.getName());
      } else {
         return "Combined Diff";
      }
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {

      ArtifactDelta firstDelta = artifactDeltas.iterator().next();
      Artifact sampleArtifact =
         firstDelta.getStartArtifact() != null ? firstDelta.getStartArtifact() : firstDelta.getEndArtifact();

      IRenderer renderer = preferedRenderer;
      if (preferedRenderer == null) {
         renderer = RendererManager.getBestRenderer(presentationType, sampleArtifact, rendererOptions);
      }

      IComparator comparator = renderer.getComparator();
      if (artifactDeltas.size() == 1) {
         comparator.compare(monitor, collector, presentationType, firstDelta, pathPrefix);
      } else {
         comparator.compareArtifacts(monitor, collector, presentationType, artifactDeltas, pathPrefix);
      }
   }
}
