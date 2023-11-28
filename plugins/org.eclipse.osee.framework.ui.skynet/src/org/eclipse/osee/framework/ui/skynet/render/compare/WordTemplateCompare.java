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
import java.util.Collections;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.exception.OperationTimedoutException;
import org.eclipse.osee.framework.core.model.change.CompareData;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.publishing.RendererOption;
import org.eclipse.osee.framework.core.publishing.RendererUtil;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.change.ArtifactDelta;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.render.FileSystemRenderer;
import org.eclipse.osee.framework.ui.skynet.render.RenderingUtil;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.util.IVbaDiffGenerator;
import org.eclipse.osee.framework.ui.swt.Displays;

public class WordTemplateCompare extends AbstractWordCompare {

   public WordTemplateCompare(FileSystemRenderer renderer) {
      super(renderer, CoreAttributeTypes.WordTemplateContent, CoreAttributeTypes.PlainTextContent);
   }

   /**
    * Creates a difference report for each artifact between baseArtifact and newerArtifact. Then produces a single
    * report by combining each of the difference reports together for a single report.
    */
   @Override
   public void compareArtifacts(IProgressMonitor monitor, CompareDataCollector collector, PresentationType presentationType, Collection<ArtifactDelta> artifactDeltas, String pathPrefix) {
      if (artifactDeltas.isEmpty()) {
         throw new OseeArgumentException("The artifactDelts must not be empty");
      }

      ArtifactDelta artifactDelta1 = artifactDeltas.iterator().next();
      Artifact artifact = artifactDelta1.getStartArtifact();
      if (artifact == null) {
         artifact = artifactDelta1.getEndArtifact();
      }
      BranchToken branch = artifact.getBranchToken();
      List<Artifact> artifacts = Collections.emptyList();

      IVbaDiffGenerator diffGenerator = createGenerator(artifacts, branch, presentationType);

      //@formatter:off
      var resultPath =
         getDiffPath
            (
               artifactDelta1.getStartArtifact(),
               artifactDelta1.getEndArtifact(),
               presentationType,
               pathPrefix
            );

      var vbsPath =
         RenderingUtil
            .getRenderFile
               (
                  getRenderer(),
                  presentationType,
                  RendererUtil.makeRenderPath(pathPrefix),
                  "vbs",
                  branch.getShortName(),
                  "compareDocs"
               )
            .flatMap( RenderingUtil::getOsString )
            .orElseThrow
               (
                  () -> new OseeCoreException
                               (
                                  new Message()
                                         .title( "WordTemplateCompare::compareArtifacts, Failed to locate renderer file." )
                                         .indentInc()
                                         .segment( "Renderer",          renderer.getName()      )
                                         .segment( "Presentation Type", presentationType.name() )
                                         .segment( "Branch Identifier", branch.getIdString()    )
                                         .segment( "Path Prefix",       pathPrefix              )
                                         .toString()
                               )
               );

      var data = new CompareData( presentationType, pathPrefix, resultPath, vbsPath );
      //@formatter:on

      addArtifactDeltas(monitor, artifactDeltas, data);

      try {
         diffGenerator.generate(monitor, data);
      } catch (OperationTimedoutException ex) {
         if (!skipDialogs) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, String.format(
               "The View Word Change Report Timed-out for Artifact(s) [%s] on Branch [%s]", artifact, branch));
         }
      }

      getRenderer().setRendererOption(RendererOption.RESULT_PATH_RETURN, resultPath);
      collector.onCompare(data);
   }

   private void addArtifactDeltas(IProgressMonitor monitor, Collection<ArtifactDelta> artifactDeltas, CompareData data) {
      double workAmount = 0.70 / artifactDeltas.size();

      for (ArtifactDelta artifactDelta : artifactDeltas) {
         if (monitor.isCanceled()) {
            throw new OperationCanceledException();
         }

         try {
            addToCompare(monitor, data, artifactDelta);
         } catch (OseeCoreException ex) {
            notifyArtifactDeltaError(ex, artifactDelta);
         } catch (Exception ex) {
            notifyArtifactDeltaError(ex, artifactDelta);
         } finally {
            monitor.worked(Operations.calculateWork(Operations.TASK_WORK_RESOLUTION, workAmount));
         }
      }

      if (monitor.isCanceled()) {
         throw new OperationCanceledException();
      }
   }

   private void notifyArtifactDeltaError(Exception e, ArtifactDelta artifactDelta) {

      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {

            /*
             * Both start artifact and end artifact cannot be null.
             */

            var artifactEnd = "End";

            Artifact artifact = artifactDelta.getEndArtifact();

            if (artifact == null) {
               artifactEnd = "Start";
               artifact = artifactDelta.getStartArtifact();
            }

            //@formatter:off
            var message = new Message()
                                 .title( "Unable to process artifact for comparison." )
                                 .indentInc()
                                 .segment( "Artifact Name",       artifact.getName()     )
                                 .segment( "Artifact Identifier", artifact.getIdString() )
                                 .segment( "Artifact Delta End",  artifactEnd            )
                                 .reasonFollowsWithTrace( e )
                                 .toString();
            //@formatter:on

            if (RenderingUtil.arePopupsAllowed()) {

               var xResultData = new XResultData(false /* Don't log */ );

               xResultData.addRaw(message);

               XResultDataUI.report(xResultData, "Add to Compare Error");

            } else {

               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, message);

            }
         }
      });

   }
}