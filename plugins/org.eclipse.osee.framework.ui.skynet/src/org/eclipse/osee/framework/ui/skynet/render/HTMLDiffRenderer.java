/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.render;

/*
 * @author Marc Potter
 */

import java.util.Collection;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.change.ArtifactDelta;
import org.eclipse.osee.framework.ui.skynet.compare.CompareHandler;
import org.eclipse.osee.framework.ui.skynet.compare.CompareItem;
import org.eclipse.osee.framework.ui.skynet.render.compare.CompareDataCollector;
import org.eclipse.osee.framework.ui.skynet.render.compare.IComparator;
import org.eclipse.swt.widgets.Display;

public class HTMLDiffRenderer implements IComparator {

   @Override
   public void compare(IProgressMonitor monitor, CompareDataCollector collector, PresentationType presentationType, ArtifactDelta artifactDelta, String pathPrefix) {
      String was = " ", is = " ", artifactName = null;
      Artifact startArtifact = artifactDelta.getStartArtifact();
      if (startArtifact != null) {
         was = startArtifact.getSoleAttributeValueAsString(CoreAttributeTypes.HtmlContent, "");
         artifactName = startArtifact.getName();
      }
      Artifact endArtifact = artifactDelta.getEndArtifact();
      if (endArtifact != null) {
         is = endArtifact.getSoleAttributeValueAsString(CoreAttributeTypes.HtmlContent, "");
      }
      if (Strings.isValid(is) && Strings.isValid(was)) {
         final CompareHandler compareHandler =
            new CompareHandler(artifactName, new CompareItem("Was", was, System.currentTimeMillis(), null),
               new CompareItem("Is", is, System.currentTimeMillis(), null), null);
         Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
               compareHandler.compare();
            }
         });
      }
   }

   @Override
   public void compare(CompareDataCollector collector, Artifact baseVersion, Artifact newerVersion, IFile baseFile, IFile newerFile, PresentationType presentationType, String pathPrefix) {
      throw new OseeCoreException("The HTML Content renderer does not support the compare operation");
   }

   @Override
   public void compareArtifacts(IProgressMonitor monitor, CompareDataCollector collector, PresentationType presentationType, Collection<ArtifactDelta> artifactDeltas, String pathPrefix) {
      for (ArtifactDelta artifactDelta : artifactDeltas) {
         compare(monitor, collector, presentationType, artifactDelta, pathPrefix);
      }
   }

}
