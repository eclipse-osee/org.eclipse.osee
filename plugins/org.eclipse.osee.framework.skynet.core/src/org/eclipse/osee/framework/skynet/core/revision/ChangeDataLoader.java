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
package org.eclipse.osee.framework.skynet.core.revision;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.ArtifactChangeItem;
import org.eclipse.osee.framework.core.data.AttributeChangeItem;
import org.eclipse.osee.framework.core.data.ChangeItem;
import org.eclipse.osee.framework.core.data.ChangeReportRequest;
import org.eclipse.osee.framework.core.data.ChangeReportResponse;
import org.eclipse.osee.framework.core.data.ChangeVersion;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.core.data.RelationChangeItem;
import org.eclipse.osee.framework.core.data.TransactionDelta;
import org.eclipse.osee.framework.core.enums.CoreTranslatorId;
import org.eclipse.osee.framework.core.enums.Function;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.AttributeType;
import org.eclipse.osee.framework.core.model.RelationType;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.util.ChangeItemUtil;
import org.eclipse.osee.framework.database.core.SQL3DataType;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoad;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoader;
import org.eclipse.osee.framework.skynet.core.artifact.HttpClientMessage;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.change.ArtifactChange;
import org.eclipse.osee.framework.skynet.core.change.ArtifactDelta;
import org.eclipse.osee.framework.skynet.core.change.AttributeChange;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.change.ErrorChange;
import org.eclipse.osee.framework.skynet.core.change.RelationChange;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;

/**
 * @author Jeff C. Phillips
 */
public class ChangeDataLoader extends AbstractOperation {

   private final TransactionDelta txDelta;
   private final Collection<Change> changes;

   public ChangeDataLoader(Collection<Change> changes, TransactionDelta txDelta) {
      super("Compute Changes", Activator.PLUGIN_ID);
      this.changes = changes;
      this.txDelta = txDelta;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      ChangeReportResponse response = requestChanges(monitor, txDelta);
      Collection<ChangeItem> changeItems = response.getChangeItems();
      monitor.worked(calculateWork(0.20));

      if (changeItems.isEmpty()) {
         monitor.worked(calculateWork(0.80));
      } else {
         monitor.setTaskName("Bulk load changed artifacts");
         Collection<Artifact> bulkLoaded = new ArrayList<Artifact>();
         bulkLoadArtifactDeltas(monitor, bulkLoaded, changeItems);
         monitor.worked(calculateWork(0.20));

         monitor.setTaskName("Compute artifact deltas");
         double workAmount = 0.60 / changeItems.size();
         IOseeBranch startTxBranch = txDelta.getStartTx().getBranch();
         for (ChangeItem item : changeItems) {
            checkForCancelledStatus(monitor);
            Change change = computeChange(startTxBranch, item);
            changes.add(change);
            monitor.worked(calculateWork(workAmount));
         }
      }
   }

   private Artifact getArtifactAtTx(int artId, TransactionRecord transaction) throws OseeCoreException {
      Artifact artifactAtTransaction = null;
      if (txDelta.areOnTheSameBranch()) {
         artifactAtTransaction = ArtifactQuery.checkHistoricalArtifactFromId(artId, transaction, true);
      } else {
         artifactAtTransaction = ArtifactQuery.checkArtifactFromId(artId, transaction.getBranch(), true);
      }
      return artifactAtTransaction;
   }

   private Change computeChange(IOseeBranch startTxBranch, ChangeItem item) {
      Change change = null;
      try {
         int artId = item.getArtId();
         Artifact startTxArtifact;
         if (txDelta.areOnTheSameBranch()) {
            startTxArtifact = getArtifactAtTx(artId, txDelta.getStartTx());
         } else {
            startTxArtifact =
                  ArtifactQuery.checkHistoricalArtifactFromId(artId,
                        txDelta.getStartTx().getBranch().getBaseTransaction(), true);
         }
         Artifact endTxArtifact;
         if (txDelta.areOnTheSameBranch()) {
            endTxArtifact = getArtifactAtTx(artId, txDelta.getEndTx());
         } else {
            endTxArtifact = getArtifactAtTx(artId, txDelta.getStartTx());
         }

         ArtifactDelta artifactDelta = new ArtifactDelta(txDelta, startTxArtifact, endTxArtifact);
         change = createChangeObject(item, txDelta, startTxBranch, artifactDelta);

      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         change = new ErrorChange(startTxBranch, item.getArtId(), ex.toString());
      }
      return change;
   }

   private Change createChangeObject(ChangeItem item, TransactionDelta txDelta, IOseeBranch startTxBranch, ArtifactDelta artifactDelta) throws OseeCoreException {
      Change change = null;

      int itemId = item.getItemId();
      long itemGammaId = item.getNetChange().getGammaId();
      ModificationType netModType = item.getNetChange().getModType();
      int artId = item.getArtId();

      // The change artifact is the artifact that is displayed by the GUI.
      // When we are comparing two different branches, the displayed artifact should be the start artifact or the artifact from the
      // source branch. When we are comparing items from the same branch, the displayed artifact should be the artifact in the end transaction
      // since that is the resulting change artifact.
      Artifact changeArtifact = artifactDelta.getEndArtifact();
      boolean isHistorical = txDelta.areOnTheSameBranch();

      if (item instanceof ArtifactChangeItem) {
         change =
               new ArtifactChange(startTxBranch, itemGammaId, itemId, txDelta, netModType, isHistorical,
                     changeArtifact, artifactDelta);
      } else if (item instanceof AttributeChangeItem) {
         String isValue = item.getCurrentVersion().getValue();
         AttributeType attributeType = AttributeTypeManager.getType(item.getItemTypeId());

         String wasValue = "";
         if (!txDelta.areOnTheSameBranch()) {
            ChangeVersion netChange = item.getNetChange();
            if (!ChangeItemUtil.isNew(netChange) && !ChangeItemUtil.isIntroduced(netChange)) {
               ChangeVersion fromVersion = ChangeItemUtil.getStartingVersion(item);
               wasValue = fromVersion.getValue();
            }
         }
         change =
               new AttributeChange(startTxBranch, itemGammaId, artId, txDelta, netModType, isValue, wasValue, itemId,
                     attributeType, netModType, isHistorical, changeArtifact, artifactDelta);

      } else if (item instanceof RelationChangeItem) {
         RelationChangeItem relationItem = (RelationChangeItem) item;
         RelationType relationType = RelationTypeManager.getType(relationItem.getItemTypeId());

         TransactionRecord transaction = txDelta.getStartTx();
         if (txDelta.areOnTheSameBranch()) {
            transaction = txDelta.getEndTx();
         }
         Artifact endTxBArtifact = getArtifactAtTx(relationItem.getBArtId(), transaction);

         change =
               new RelationChange(startTxBranch, itemGammaId, artId, txDelta, netModType, endTxBArtifact.getArtId(),
                     itemId, relationItem.getRationale(), relationType, isHistorical, changeArtifact, artifactDelta,
                     endTxBArtifact);
      } else {
         throw new OseeCoreException("The change item must map to either an artifact, attribute or relation change");
      }
      return change;
   }

   private void bulkLoadArtifactDeltas(IProgressMonitor monitor, Collection<Artifact> bulkLoaded, Collection<ChangeItem> changeItems) throws OseeCoreException {
      checkForCancelledStatus(monitor);
      Set<Integer> artIds = asArtIds(changeItems);
      if (txDelta.areOnTheSameBranch()) {
         preloadArtifacts(bulkLoaded, artIds, txDelta.getStartTx(), false);
      } else {
         // Load current artifacts by id for each branch
         preloadArtifacts(bulkLoaded, artIds, txDelta.getStartTx(), false);
         preloadArtifacts(bulkLoaded, artIds, txDelta.getEndTx(), false);
      }
   }

   private static void preloadArtifacts(Collection<Artifact> bulkLoaded, Collection<Integer> artIds, TransactionRecord tx, boolean isHistorical) throws OseeCoreException {
      int queryId = ArtifactLoader.getNewQueryId();
      Timestamp insertTime = GlobalTime.GreenwichMeanTimestamp();

      Integer branchId = tx.getBranchId();
      Object txId = isHistorical ? tx.getId() : SQL3DataType.INTEGER;

      List<Object[]> insertParameters = new LinkedList<Object[]>();
      for (Integer artId : artIds) {
         insertParameters.add(new Object[] {queryId, insertTime, artId, branchId, txId});
      }
      ArtifactLoader.loadArtifacts(bulkLoaded, queryId, ArtifactLoad.ALL_CURRENT, null, insertParameters, true,
            isHistorical, true);
   }

   private static Set<Integer> asArtIds(Collection<ChangeItem> changeItems) {
      Set<Integer> artIds = new HashSet<Integer>();
      for (ChangeItem item : changeItems) {
         artIds.add(item.getArtId());
         if (item instanceof RelationChangeItem) {
            artIds.add(((RelationChangeItem) item).getBArtId());
         }
      }
      return artIds;
   }

   private static ChangeReportResponse requestChanges(IProgressMonitor monitor, TransactionDelta txDelta) throws OseeCoreException {
      Map<String, String> parameters = new HashMap<String, String>();
      parameters.put("function", Function.CHANGE_REPORT.name());

      ChangeReportRequest requestData =
            new ChangeReportRequest(txDelta.getStartTx().getId(), txDelta.getEndTx().getId());

      ChangeReportResponse response =
            HttpClientMessage.send(OseeServerContext.BRANCH_CONTEXT, parameters,
                  CoreTranslatorId.CHANGE_REPORT_REQUEST, requestData, CoreTranslatorId.CHANGE_REPORT_RESPONSE);
      if (response.wasSuccessful()) {
         // OseeEventManager.kickBranchEvent(HttpBranchCreation.class, ,
         // branch.getId());
      }
      return response;
   }
}
