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
package org.eclipse.osee.framework.ui.skynet.render;

/**
 * @author Ryan D. Brooks
 */
import java.util.Collection;
import java.util.Collections;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.change.ArtifactDelta;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.render.compare.IComparator;

public final class DiffUsingRenderer extends AbstractOperation {
   private final VariableMap options;
   private final Collection<ArtifactDelta> itemsToCompare;
   private final boolean show;

   public DiffUsingRenderer(Collection<ArtifactDelta> itemsToCompare, VariableMap options, boolean show) {
      super(generateOperationName(itemsToCompare), SkynetGuiPlugin.PLUGIN_ID);
      this.itemsToCompare = itemsToCompare;
      this.options = options;
      this.show = show;
   }

   public DiffUsingRenderer(ArtifactDelta itemToCompare, VariableMap options, boolean show) {
      this(Collections.singletonList(itemToCompare), options, show);
   }

   private static String generateOperationName(Collection<ArtifactDelta> itemsToCompare) {
      ArtifactDelta firstDelta = itemsToCompare.iterator().next();
      if (itemsToCompare.size() == 1) {
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
      ArtifactDelta firstDelta = itemsToCompare.iterator().next();
      Artifact sampleArtifact =
         firstDelta.getStartArtifact() != null ? firstDelta.getStartArtifact() : firstDelta.getEndArtifact();

      IRenderer renderer = RendererManager.getBestRenderer(PresentationType.DIFF, sampleArtifact, options);
      IComparator comparator = renderer.getComparator();
      if (itemsToCompare.size() == 1) {
         comparator.compare(monitor, PresentationType.DIFF, firstDelta, show);
      } else {
         comparator.compareArtifacts(monitor, PresentationType.DIFF, itemsToCompare);
      }
   }
}