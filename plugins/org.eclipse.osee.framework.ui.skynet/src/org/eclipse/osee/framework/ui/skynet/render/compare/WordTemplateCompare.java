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
import java.util.Collections;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.exception.OperationTimedoutException;
import org.eclipse.osee.framework.core.model.change.CompareData;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.util.RendererOption;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.change.ArtifactDelta;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.render.FileSystemRenderer;
import org.eclipse.osee.framework.ui.skynet.render.RenderingUtil;
import org.eclipse.osee.framework.ui.skynet.util.IVbaDiffGenerator;

public class WordTemplateCompare extends AbstractWordCompare {

   public WordTemplateCompare(FileSystemRenderer renderer) {
      super(renderer, CoreAttributeTypes.WordTemplateContent, CoreAttributeTypes.PlainTextContent);
   }

   /**
    * Creates a difference report for each artifact between baseArtifact and newerArtifact. Then produces a single
    * report by combining each of the difference reports together for a single report.
    */
   @Override
   public void compareArtifacts(IProgressMonitor monitor, CompareDataCollector collector, PresentationType presentationType, Collection<ArtifactDelta> artifactDeltas, String pathPrefix) throws OseeCoreException {
      if (artifactDeltas.isEmpty()) {
         throw new OseeArgumentException("The artifactDelts must not be empty");
      }

      ArtifactDelta artifactDelta1 = artifactDeltas.iterator().next();
      Artifact artifact = artifactDelta1.getStartArtifact();
      if (artifact == null) {
         artifact = artifactDelta1.getEndArtifact();
      }
      IOseeBranch branch = artifact.getBranchToken();
      List<Artifact> artifacts = Collections.emptyList();

      IVbaDiffGenerator diffGenerator = createGenerator(artifacts, branch, presentationType);

      String resultPath =
         getDiffPath(artifactDelta1.getStartArtifact(), artifactDelta1.getEndArtifact(), presentationType, pathPrefix);
      String vbsPath =
         RenderingUtil.getRenderPath(getRenderer(), branch, presentationType, null, "compareDocs", ".vbs");

      CompareData data = new CompareData(resultPath, vbsPath);

      addArtifactDeltas(monitor, artifactDeltas, data);
      try {
         diffGenerator.generate(monitor, data);
      } catch (OperationTimedoutException ex) {
         if (!skipDialogs) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, String.format(
               "The View Word Change Report Timed-out for Artifact(s) [%s] on Branch [%s]", artifact, branch));
         }
      }

      getRenderer().updateOption(RendererOption.RESULT_PATH_RETURN, resultPath);
      collector.onCompare(data);
   }

   private void addArtifactDeltas(IProgressMonitor monitor, Collection<ArtifactDelta> artifactDeltas, CompareData data) {
      double workAmount = 0.70 / artifactDeltas.size();

      for (ArtifactDelta artifactDelta : artifactDeltas) {
         if (monitor.isCanceled()) {
            throw new OperationCanceledException();
         }

         try {
            addToCompare(monitor, data, PresentationType.DIFF, artifactDelta);
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         } catch (Exception ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         } finally {
            monitor.worked(Operations.calculateWork(Operations.TASK_WORK_RESOLUTION, workAmount));
         }
      }

      if (monitor.isCanceled()) {
         throw new OperationCanceledException();
      }
   }
}