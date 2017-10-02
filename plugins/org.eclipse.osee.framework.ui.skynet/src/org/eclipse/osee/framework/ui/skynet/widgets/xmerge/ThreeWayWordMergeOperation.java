/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets.xmerge;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.model.change.CompareData;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.util.RendererOption;
import org.eclipse.osee.framework.jdk.core.text.change.ChangeSet;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.plugin.core.util.AIFile;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.change.ArtifactDelta;
import org.eclipse.osee.framework.skynet.core.conflict.AttributeConflict;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.render.UpdateArtifactOperation;
import org.eclipse.osee.framework.ui.skynet.render.compare.CompareDataCollector;

/**
 * @author Ryan D. Brooks
 */
public class ThreeWayWordMergeOperation extends AbstractOperation {
   private static final Pattern authorPattern =
      Pattern.compile("aml:author=\".*?\"", Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
   private static final Pattern rsidRootPattern =
      Pattern.compile("\\</wsp:rsids\\>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
   private static final Pattern findSetRsids =
      Pattern.compile("wsp:rsidR=\".*?\"", Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
   private static final Pattern findSetRsidRPR =
      Pattern.compile("wsp:rsidRPr=\".*?\"", Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
   private static final Pattern findSetRsidP =
      Pattern.compile("wsp:rsidP=\".*?\"", Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
   private static final Pattern findSetRsidRDefault =
      Pattern.compile("wsp:rsidRDefault=\".*?\"", Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
   private static final Pattern annotationTag =
      Pattern.compile("(<aml:annotation[^\\>]*?[^/]\\>)|(</aml:annotation\\>)",
         Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);

   private static final Pattern rsidPattern =
      Pattern.compile("wsp:rsid(RPr|P|R)=\"(.*?)\"", Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);

   private final AttributeConflict attributeConflict;

   public ThreeWayWordMergeOperation(AttributeConflict attributeConflict) {
      super("Generate 3 Way Merge", Activator.PLUGIN_ID);
      this.attributeConflict = attributeConflict;
   }

   @Override
   protected void doWork(final IProgressMonitor monitor) throws Exception {
      final Artifact mergeArtifact = attributeConflict.getArtifact();

      Artifact startArtifact = MergeUtility.getStartArtifact(attributeConflict);
      monitor.worked(5);

      List<IFile> outputFiles = new ArrayList<>();

      createMergeDiffFile(outputFiles, startArtifact, attributeConflict.getSourceArtifact());
      monitor.worked(15);
      createMergeDiffFile(outputFiles, startArtifact, attributeConflict.getDestArtifact());
      monitor.worked(15);

      Conditions.checkExpressionFailOnTrue(outputFiles.size() != 2, "No compare outputfiles found");

      IFile sourceChangeFile = outputFiles.get(0);
      IFile destChangeFile = outputFiles.get(1);

      changeAuthorinWord("Source", sourceChangeFile, 2, "12345678", "55555555");
      changeAuthorinWord("Destination", destChangeFile, 2, "56781234", "55555555");
      monitor.worked(15);

      CompareDataCollector colletor = new CompareDataCollector() {
         @Override
         public void onCompare(CompareData data)  {
            File mergedFile = new File(data.getOutputPath());

            monitor.worked(40);
            attributeConflict.markStatusToReflectEdit();

            IOperation op = new UpdateArtifactOperation(mergedFile, Collections.singletonList(mergeArtifact),
               mergeArtifact.getBranch(), true);
            Operations.executeWorkAndCheckStatus(op, monitor);

            monitor.done();
            RendererManager.openInJob(mergeArtifact, PresentationType.SPECIALIZED_EDIT);
         }
      };
      Map<RendererOption, Object> rendererOptions = new HashMap<>();
      rendererOptions.put(RendererOption.NO_DISPLAY, true);

      RendererManager.merge(colletor, mergeArtifact, null, sourceChangeFile, destChangeFile, "Source_Dest_Merge",
         rendererOptions);
   }

   private static void createMergeDiffFile(final Collection<IFile> outputFiles, Artifact baseVersion, Artifact newerVersion) throws Exception {
      ArtifactDelta artifactDelta = new ArtifactDelta(baseVersion, newerVersion);

      CompareDataCollector colletor = new CompareDataCollector() {
         @Override
         public void onCompare(CompareData data) {
            outputFiles.add(AIFile.constructIFile(data.getOutputPath()));
         }
      };
      Map<RendererOption, Object> rendererOptions = new HashMap<>();
      rendererOptions.put(RendererOption.NO_DISPLAY, true);
      rendererOptions.put(RendererOption.TEMPLATE_OPTION, RendererOption.THREE_WAY_MERGE.getKey());

      RendererManager.diff(colletor, artifactDelta, "", rendererOptions);
   }

   private static void changeAuthorinWord(String newAuthor, IFile iFile, int revisionNumber, String rsidNumber, String baselineRsid) throws Exception {
      File file = iFile.getLocation().toFile();
      String fileValue = Lib.fileToString(file);

      Matcher m = authorPattern.matcher(fileValue);
      while (m.find()) {
         String name = m.group();
         fileValue = fileValue.replace(name, "aml:author=\"" + newAuthor + "\"");
      }

      m = findSetRsids.matcher(fileValue);
      while (m.find()) {
         String rev = m.group();
         fileValue = fileValue.replace(rev, "wsp:rsidR=\"" + baselineRsid + "\"");
      }
      m = findSetRsidRPR.matcher(fileValue);
      while (m.find()) {
         String rev = m.group();
         fileValue = fileValue.replace(rev, "wsp:rsidRPr=\"" + baselineRsid + "\"");
      }
      m = findSetRsidP.matcher(fileValue);
      while (m.find()) {
         String rev = m.group();
         fileValue = fileValue.replace(rev, "wsp:rsidP=\"" + baselineRsid + "\"");
      }
      m = findSetRsidRDefault.matcher(fileValue);
      while (m.find()) {
         String rev = m.group();
         fileValue = fileValue.replace(rev, "wsp:rsidRDefault=\"" + baselineRsid + "\"");
      }

      resetRsidIds(fileValue, rsidNumber, baselineRsid, file);
   }

   private static void resetRsidIds(String fileValue, String rsidNumber, String baselineRsid, File file) throws IOException {
      ChangeSet changeSet = new ChangeSet(fileValue);
      Matcher matcher = annotationTag.matcher(fileValue);

      while (matcher.find()) {
         int startIndex = matcher.start();
         int level = 1;

         do {
            matcher.find();

            if (matcher.group().startsWith("<aml:annotation")) {
               level++;
            } else {
               level--;
            }
         } while (level != 0);

         Matcher rsidMatcher = rsidPattern.matcher(fileValue);

         while (rsidMatcher.find(startIndex) && rsidMatcher.end() <= matcher.end()) {
            changeSet.replace(rsidMatcher.start(2), rsidMatcher.end(2) - 1, rsidNumber);
            startIndex = rsidMatcher.end();
         }
      }

      Matcher m = rsidRootPattern.matcher(fileValue);
      while (m.find()) {
         changeSet.replace(m.start(), m.end() - 1, "<wsp:rsid wsp:val=\"" + baselineRsid + "\"/></wsp:rsids>");
      }

      changeSet.applyChanges(file);
   }
}