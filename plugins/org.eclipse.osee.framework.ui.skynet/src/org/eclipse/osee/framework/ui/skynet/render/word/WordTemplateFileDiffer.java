/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.framework.ui.skynet.render.word;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionDelta;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.DeletionFlag;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.change.ArtifactDelta;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.render.IRenderer;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;

/**
 * @author Jeff C. Phillips
 */
public class WordTemplateFileDiffer {
   private String nextParagraphNumber;
   private String outlineType;

   public void generateFileDifferences(String fileName, VariableMap variableMap, String nextParagraphNumber, String outlineType) throws OseeArgumentException, OseeCoreException {
      this.nextParagraphNumber = nextParagraphNumber;
      this.outlineType = outlineType;

      List<Artifact> endArtifacts = variableMap.getArtifacts("artifacts");
      variableMap.setValue(IRenderer.FILE_NAME_OPTION, fileName);
      variableMap.setValue("diffReportFolderName", ".preview" + fileName);
      variableMap.setValue("Publish With Attributes", true);
      variableMap.setValue("Publish As Diff", true);
      variableMap.setValue("Diff Branch", variableMap.getValue("Branch"));
      variableMap.setValue("Use Artifact Names", true);
      variableMap.setValue("inPublishMode", true);
      variableMap.setValue("suppressWord", true);

      Branch endBranch = variableMap.getBranch("Branch");
      Branch startBranch = variableMap.getBranch("compareBranch");

      if (endBranch == null || startBranch == null && !variableMap.getBoolean("Diff from Baseline")) {
         throw new OseeCoreException(
            "Must Select a " + endBranch == null ? "Branch" : "Date" + " to diff against when publishing as Diff");
      }
      TransactionRecord startTransaction;
      boolean isDiffFromBaseline = false;//variableMap.getBoolean("Diff from Baseline");

      if (isDiffFromBaseline) {
         startTransaction = endBranch.getBaseTransaction();
         startBranch = endBranch;
      } else {
         startTransaction = TransactionManager.getHeadTransaction(startBranch);
      }

      TransactionRecord endTransaction = TransactionManager.getHeadTransaction(endBranch);
      TransactionDelta txDelta = new TransactionDelta(startTransaction, endTransaction);

      for (Artifact artifact : endArtifacts) {
         try {
            diff(isDiffFromBaseline, txDelta, startBranch,
               ArtifactCache.getActive(artifact.getArtId(), artifact.getBranch()), variableMap);
         } catch (Exception ex) {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
         }
      }
   }

   private void diff(boolean isDiffFromBaseline, TransactionDelta txDelta, Branch startBranch, Artifact artifact, VariableMap variableMap) throws OseeCoreException {
      List<Artifact> endArtifacts = Arrays.asList(artifact);
      List<Artifact> startArtifacts = getStartArtifacts(endArtifacts, startBranch);

      Collection<ArtifactDelta> compareItems = new ArrayList<ArtifactDelta>();
      for (int index = 0; index < startArtifacts.size() && index < endArtifacts.size(); index++) {
         Artifact start = startArtifacts.get(index);
         Artifact end = endArtifacts.get(index);
         if (isDeleted(start)) {
            start = null;
         }
         if (isDeleted(end)) {
            end = null;
         }
         if (start != null || end != null) {
            compareItems.add(new ArtifactDelta(txDelta, start, end));
         }
      }
      variableMap.setValue("paragraphNumber", nextParagraphNumber);
      variableMap.setValue("outlineType", outlineType);

      Job job = RendererManager.diffInJob(compareItems, variableMap);
      try {
         job.join();
      } catch (InterruptedException ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
   }

   private List<Artifact> getStartArtifacts(List<Artifact> artifacts, Branch startBranch) throws OseeCoreException {
      List<Artifact> startArtifacts = new ArrayList<Artifact>(artifacts.size());
      @SuppressWarnings("unused")
      Collection<Artifact> bulkLoadedArtifacts =
         ArtifactQuery.getArtifactListFromIds(Artifacts.toGuids(artifacts), startBranch, DeletionFlag.INCLUDE_DELETED);

      for (Artifact artifact : artifacts) {
         startArtifacts.add(ArtifactCache.getActive(artifact.getArtId(), startBranch));
      }
      return startArtifacts;
   }

   private boolean isDeleted(Artifact artifact) {
      return artifact != null && artifact.isDeleted();
   }
}
