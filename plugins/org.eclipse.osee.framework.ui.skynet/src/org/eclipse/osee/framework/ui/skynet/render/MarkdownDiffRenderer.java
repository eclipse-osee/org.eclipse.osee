/*********************************************************************
 * Copyright (c) 2023 Boeing
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
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.exception.MultipleAttributesExist;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.change.ArtifactDelta;
import org.eclipse.osee.framework.ui.skynet.compare.CompareHandler;
import org.eclipse.osee.framework.ui.skynet.render.compare.CompareDataCollector;
import org.eclipse.osee.framework.ui.skynet.render.compare.IComparator;
import org.eclipse.swt.widgets.Display;

/**
 * Class to display the differences between two ASCII Markdown text files in the default Eclipse diff view.
 *
 * @author David W. Miller
 */
public class MarkdownDiffRenderer implements IComparator {

   @Override
   public void compare(IProgressMonitor monitor, CompareDataCollector collector, PresentationType presentationType, ArtifactDelta artifactDelta, String pathPrefix) throws MultipleAttributesExist {
      Artifact startArtifact = artifactDelta.getStartArtifact();
      Artifact endArtifact = artifactDelta.getEndArtifact();

      String artifactName = "";
      String startAscii = "";
      if (startArtifact != null) {
         startAscii = startArtifact.getSoleAttributeValueAsString(CoreAttributeTypes.MarkdownContent, "");
         artifactName = startArtifact.getName();
      }
      String endAscii = "";
      if (endArtifact != null) {
         endAscii = endArtifact.getSoleAttributeValueAsString(CoreAttributeTypes.MarkdownContent, "");
         artifactName = endArtifact.getName();
      }

      final CompareHandler compareHandler = new CompareHandler(artifactName, startAscii, endAscii);
      Display.getDefault().syncExec(new Runnable() {
         @Override
         public void run() {
            compareHandler.compare();
         }
      });

   }

   @Override
   public void compare(CompareDataCollector collector, Artifact baseVersion, Artifact newerVersion, IFile baseFile, IFile newerFile, PresentationType presentationType, String pathPrefix) {
      throw new OseeCoreException("The Markdown renderer does not support the compare operation");
   }

   @Override
   public void compareArtifacts(IProgressMonitor monitor, CompareDataCollector collector, PresentationType presentationType, Collection<ArtifactDelta> artifactDeltas, String pathPrefix) {
      for (ArtifactDelta artifactDelta : artifactDeltas) {
         compare(monitor, collector, presentationType, artifactDelta, pathPrefix);
      }
   }

}
