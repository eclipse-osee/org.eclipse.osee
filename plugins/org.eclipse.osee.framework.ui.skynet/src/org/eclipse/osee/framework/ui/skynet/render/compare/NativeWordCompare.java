/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.render.compare;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.exception.OperationTimedoutException;
import org.eclipse.osee.framework.core.model.change.CompareData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.AIFile;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.change.ArtifactDelta;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.render.FileSystemRenderer;
import org.eclipse.osee.framework.ui.skynet.render.RenderingUtil;
import org.eclipse.osee.framework.ui.skynet.util.IVbaDiffGenerator;

/**
 * @author David W. Miller
 */
public class NativeWordCompare extends AbstractWordCompare {

   public NativeWordCompare(FileSystemRenderer renderer) {
      super(renderer, CoreAttributeTypes.NativeContent);
   }

   @Override
   public void compare(IProgressMonitor monitor, CompareDataCollector collector, PresentationType presentationType, ArtifactDelta artifactDelta, String pathPrefix) {
      Artifact artifact = artifactDelta.getStartArtifact();
      if (artifact == null) {
         artifact = artifactDelta.getEndArtifact();
      }
      IOseeBranch branch = artifact.getBranchToken();

      IVbaDiffGenerator diffGenerator = createGenerator(Collections.singletonList(artifact), branch, presentationType);

      String resultPath =
         getDiffPath(artifactDelta.getStartArtifact(), artifactDelta.getEndArtifact(), presentationType, pathPrefix);
      String vbsPath = RenderingUtil.getRenderPath(renderer, branch, presentationType, null, "compareDocs", ".vbs");

      CompareData data = new CompareData(resultPath, vbsPath);

      addToCompare(monitor, data, presentationType, artifactDelta);
      try {
         diffGenerator.generate(monitor, data);
      } catch (OperationTimedoutException ex) {
         if (!skipDialogs) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, String.format(
               "The View Word Change Report Timed-out for Artifact(s) [%s] on Branch [%s]", artifact, branch));
         }
      }
      collector.onCompare(data);
   }

   @Override
   protected void addToCompare(IProgressMonitor monitor, CompareData data, PresentationType presentationType, ArtifactDelta artifactDelta) {
      Artifact baseArtifact = artifactDelta.getStartArtifact();
      Artifact newerArtifact = artifactDelta.getEndArtifact();

      monitor.setTaskName(
         "Preparing comparison for: " + (newerArtifact == null ? baseArtifact.getName() : newerArtifact.getName()));

      Pair<IFile, IFile> compareFiles;
      if (artifactDelta.getStartArtifact() == artifactDelta.getBaseArtifact()) {
         compareFiles = converter.convertToFile(presentationType, artifactDelta);
      } else {
         // The artifactDelta is a 3 Way Merge
         List<IFile> outputFiles = new ArrayList<>();
         converter.convertToFileForMerge(outputFiles, artifactDelta.getBaseArtifact(),
            artifactDelta.getStartArtifact());
         converter.convertToFileForMerge(outputFiles, artifactDelta.getBaseArtifact(), artifactDelta.getEndArtifact());
         /*
          * this is where we are getting the exception that the length of outputFiles is 1 This happens because the
          * artifact did not exist on the previous branch or was removed on the current branch
          */
         if (outputFiles.size() == 1) {
            String outputFileName = outputFiles.get(0).getRawLocation().toOSString();
            try {
               String tempFileName = Lib.removeExtension(outputFileName);
               File tempFile = new File(tempFileName + ".temp.doc");
               Lib.writeStringToFile("", tempFile);
               IFile constructIFile = AIFile.constructIFile(tempFile.getPath());
               outputFiles.add(constructIFile);
            } catch (IOException ex) {
               throw new OseeCoreException(ex, "Empty file for comparison could not be created, [%s]", outputFileName);
            }
         }
         compareFiles = new Pair<>(outputFiles.get(0), outputFiles.get(1));
         data.addMerge(outputFiles.get(0).getLocation().toOSString());
      }
      data.add(compareFiles.getFirst().getLocation().toOSString(), compareFiles.getSecond().getLocation().toOSString());
   }

   @Override
   public void compareArtifacts(IProgressMonitor monitor, CompareDataCollector collector, PresentationType presentationType, Collection<ArtifactDelta> artifactDeltas, String diffPrefix) {
      for (ArtifactDelta entry : artifactDeltas) {
         compare(monitor, collector, presentationType, entry, diffPrefix);
      }
   }
}