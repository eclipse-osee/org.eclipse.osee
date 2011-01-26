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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.change.ArtifactDelta;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.render.FileSystemRenderer;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.util.IVbaDiffGenerator;

public class WordTemplateCompare extends AbstractWordCompare {

   public WordTemplateCompare(FileSystemRenderer renderer) {
      super(renderer, CoreAttributeTypes.WordTemplateContent);
   }

   /**
    * Creates a difference report for each artifact between baseArtifact and newerArtifact. Then produces a single
    * report by combining each of the difference reports together for a single report.
    */
   @Override
   public void compareArtifacts(IProgressMonitor monitor, PresentationType presentationType, Collection<ArtifactDelta> artifactDeltas) throws OseeCoreException {
      IVbaDiffGenerator diffGenerator = createGenerator(presentationType);

      for (ArtifactDelta artifactDelta : artifactDeltas) {
         if (monitor.isCanceled()) {
            throw new OperationCanceledException();
         }

         try {
            addTocompare(monitor, diffGenerator, PresentationType.DIFF, artifactDelta);
         } catch (OseeCoreException ex) {
            OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         } finally {
            monitor.worked(1);
         }
      }

      if (monitor.isCanceled()) {
         throw new OperationCanceledException();
      }

      if (!artifactDeltas.isEmpty()) {
         ArtifactDelta artifactDelta1 = artifactDeltas.iterator().next();
         Artifact testArtifact = artifactDelta1.getStartArtifact();
         if (testArtifact == null) {
            testArtifact = artifactDelta1.getEndArtifact();
         }
         finish(diffGenerator, testArtifact.getBranch(), presentationType);
      }
   }
}