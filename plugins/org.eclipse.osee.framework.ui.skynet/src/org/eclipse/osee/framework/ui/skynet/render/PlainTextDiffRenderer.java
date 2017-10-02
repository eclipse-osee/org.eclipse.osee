/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
 * Class to display the differences between two ASCII plain text files in the default Eclipse diff view.
 *
 * @author Shawn F. Cook
 */
public class PlainTextDiffRenderer implements IComparator {

   @Override
   public void compare(IProgressMonitor monitor, CompareDataCollector collector, PresentationType presentationType, ArtifactDelta artifactDelta, String pathPrefix) throws MultipleAttributesExist {
      Artifact startArtifact = artifactDelta.getStartArtifact();
      Artifact endArtifact = artifactDelta.getEndArtifact();

      String artifactName = "";
      String startAscii = "";
      if (startArtifact != null) {
         startAscii = startArtifact.getSoleAttributeValueAsString(CoreAttributeTypes.PlainTextContent, "");
         artifactName = startArtifact.getName();
      }
      String endAscii = "";
      if (endArtifact != null) {
         endAscii = endArtifact.getSoleAttributeValueAsString(CoreAttributeTypes.PlainTextContent, "");
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
   public void compare(CompareDataCollector collector, Artifact baseVersion, Artifact newerVersion, IFile baseFile, IFile newerFile, PresentationType presentationType, String pathPrefix)  {
      throw new OseeCoreException("The Plain Text renderer does not support the compare operation");
   }

   @Override
   public void compareArtifacts(IProgressMonitor monitor, CompareDataCollector collector, PresentationType presentationType, Collection<ArtifactDelta> artifactDeltas, String pathPrefix)  {
      for (ArtifactDelta artifactDelta : artifactDeltas) {
         compare(monitor, collector, presentationType, artifactDelta, pathPrefix);
      }
   }

}
