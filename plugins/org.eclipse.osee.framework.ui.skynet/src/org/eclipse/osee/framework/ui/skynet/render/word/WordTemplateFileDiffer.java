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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.core.data.TransactionDelta;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoad;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoader;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.change.ArtifactDelta;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;

/**
 * @author Jeff C. Phillips
 */
public class WordTemplateFileDiffer {

   public void generateFileDifferences(String fileName, VariableMap variableMap) throws OseeArgumentException, OseeCoreException {
      List<Artifact> newArtifacts = variableMap.getArtifacts("artifacts");
      variableMap.setValue("fileName", fileName);
      variableMap.setValue("diffReportFolderName", ".preview" + fileName);
      variableMap.setValue("inPublishMode", true);
      variableMap.setValue("suppressWord", true);
      variableMap.setValue("Publish With Attributes", true);
      variableMap.setValue("Publish As Diff", true);
      variableMap.setValue("Diff Branch", variableMap.getValue("Branch"));
      variableMap.setValue("Use Artifact Names", true);
      variableMap.setValue("inPublishMode", true);
      variableMap.setValue("suppressWord", true);

      Branch endBranch = variableMap.getBranch("Branch");
      Branch startBranch = variableMap.getBranch("compareBranch");

      if (endBranch == null || (startBranch == null && !variableMap.getBoolean("Diff from Baseline"))) {
         throw new OseeCoreException(
               "Must Select a " + endBranch == null ? "Branch" : "Date" + " to diff against when publishing as Diff");
      }
      TransactionRecord startTransaction;

      boolean isDiffFromBaseline = variableMap.getBoolean("Diff from Baseline");
      if (variableMap.getBoolean("Diff from Baseline")) {
         startTransaction = endBranch.getBaseTransaction();
         startBranch = endBranch;
      } else {
         startTransaction = TransactionManager.getHeadTransaction(startBranch);
         startBranch = variableMap.getBranch("compareBranch");
      }

      TransactionDelta txDelta =
            new TransactionDelta(startTransaction, TransactionManager.getHeadTransaction(endBranch));

      for (Artifact artifact : newArtifacts) {
         try {
            diff(isDiffFromBaseline, txDelta, startBranch, artifact, variableMap);
         } catch (Exception ex) {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
         }
      }
   }

   private void diff(boolean isDiffFromBaseline, TransactionDelta txDelta, Branch startBranch, Artifact artifact, VariableMap variableMap) throws OseeCoreException {
      List<Artifact> endArtifacts = new ArrayList<Artifact>();
      endArtifacts.add(artifact);

      int transactionId;
      if (isDiffFromBaseline) {
         transactionId = txDelta.getStartTx().getId();
      } else {
         transactionId = txDelta.getEndTx().getId();
      }
      List<Artifact> startArtifacts = getStartArtifacts(endArtifacts, transactionId, startBranch.getId());

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
      Job job = RendererManager.diffInJob(compareItems, variableMap);
      try {
         job.join();
      } catch (InterruptedException ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
   }

   private List<Artifact> getStartArtifacts(List<Artifact> artifacts, int transactionId, int branchId) throws OseeCoreException {
      List<Artifact> historicArtifacts = new ArrayList<Artifact>(artifacts.size());
      int queryId = ArtifactLoader.getNewQueryId();
      Timestamp insertTime = GlobalTime.GreenwichMeanTimestamp();

      Set<Artifact> artifactSet = new HashSet<Artifact>(artifacts);
      List<Object[]> insertParameters = new LinkedList<Object[]>();
      for (Artifact artifact : artifactSet) {
         insertParameters.add(new Object[] {queryId, insertTime, artifact.getArtId(), branchId, transactionId});
      }

      @SuppressWarnings("unused")
      Collection<Artifact> bulkLoadedArtifacts =
      //            ArtifactLoader.loadArtifacts(queryId, ArtifactLoad.FULL, null, insertParameters, false, true, true);
            ArtifactLoader.loadArtifacts(queryId, ArtifactLoad.FULL, null, insertParameters, false, false, true);

      for (Artifact artifact : artifacts) {
         //         historicArtifacts.add(ArtifactCache.getHistorical(artifact.getArtId(), transactionId));
         historicArtifacts.add(ArtifactCache.getActive(artifact.getArtId(), branchId));
      }
      return historicArtifacts;
   }

   private boolean isDeleted(Artifact artifact) {
      return artifact != null && artifact.isDeleted();
   }

   public void populateVariableMap(VariableMap variableMap) throws OseeCoreException {
      if (variableMap == null) {
         throw new IllegalArgumentException("variableMap must not be null");
      }

      String name = variableMap.getString("Name");
      Branch branch = variableMap.getBranch("Branch");

      List<Artifact> artifacts = ArtifactQuery.getArtifactListFromName(name, branch, false);
      variableMap.setValue("srsProducer.objects", artifacts);
   }
}
