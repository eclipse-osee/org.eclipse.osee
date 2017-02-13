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

import static org.eclipse.osee.framework.core.enums.DeletionFlag.INCLUDE_DELETED;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.model.TransactionDelta;
import org.eclipse.osee.framework.core.model.change.ChangeIgnoreType;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.core.model.change.ChangeItemUtil;
import org.eclipse.osee.framework.core.model.change.ChangeVersion;
import org.eclipse.osee.framework.core.model.change.CompareResults;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.change.ArtifactChange;
import org.eclipse.osee.framework.skynet.core.change.ArtifactDelta;
import org.eclipse.osee.framework.skynet.core.change.AttributeChange;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.change.ErrorChange;
import org.eclipse.osee.framework.skynet.core.change.RelationChange;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.internal.ServiceUtil;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;
import org.eclipse.osee.jaxrs.client.JaxRsExceptions;
import org.eclipse.osee.orcs.rest.client.OseeClient;
import org.eclipse.osee.orcs.rest.model.TransactionEndpoint;

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
      List<ChangeItem> changeItems = requestChanges(txDelta);

      monitor.worked(calculateWork(0.20));

      if (changeItems.isEmpty()) {
         monitor.worked(calculateWork(0.80));
      } else {
         monitor.setTaskName("Bulk load changed artifacts");

         checkForCancelledStatus(monitor);
         CompositeKeyHashMap<TransactionId, Integer, Artifact> bulkLoaded = new CompositeKeyHashMap<>();

         bulkLoadArtifactDeltas(bulkLoaded, changeItems);
         monitor.worked(calculateWork(0.20));

         monitor.setTaskName("Compute artifact deltas");
         double workAmount = 0.60 / changeItems.size();
         BranchId startTxBranch = txDelta.getStartTx().getBranch();
         for (ChangeItem item : changeItems) {
            checkForCancelledStatus(monitor);
            if (ChangeItemUtil.hasValueChange(item) && ChangeItemUtil.hasApplicabilityChange(item)) {
               ChangeItem splitItem = ChangeItemUtil.splitForApplicability(item);
               Change splitChange = computeChange(bulkLoaded, startTxBranch, splitItem);
               changes.add(splitChange);
            }
            Change change = computeChange(bulkLoaded, startTxBranch, item);
            changes.add(change);
            monitor.worked(calculateWork(workAmount));
         }
      }
   }

   public void determineChanges(IProgressMonitor monitor) throws OseeCoreException {
      monitor.setTaskName("Retrieve Change Items");
      List<ChangeItem> changeItems = requestChanges(txDelta);

      checkForCancelledStatus(monitor);
      monitor.setTaskName("Bulk load changed artifacts");
      CompositeKeyHashMap<TransactionId, Integer, Artifact> bulkLoaded = new CompositeKeyHashMap<>();

      bulkLoadArtifactDeltas(bulkLoaded, changeItems);

      monitor.setTaskName("Compute artifact deltas");
      double workAmount = 0.30 / changeItems.size();
      BranchId startTxBranch = txDelta.getStartTx().getBranch();
      for (ChangeItem item : changeItems) {
         checkForCancelledStatus(monitor);
         if (ChangeItemUtil.hasValueChange(item) && ChangeItemUtil.hasApplicabilityChange(item)) {
            ChangeItem splitItem = ChangeItemUtil.splitForApplicability(item);
            Change splitChange = computeChangeFromGamma(bulkLoaded, startTxBranch, splitItem);
            changes.add(splitChange);
         }
         Change change = computeChangeFromGamma(bulkLoaded, startTxBranch, item);
         changes.add(change);
         monitor.worked(calculateWork(workAmount));
      }
   }

   private Change computeChangeFromGamma(CompositeKeyHashMap<TransactionId, Integer, Artifact> bulkLoaded, BranchId startTxBranch, ChangeItem item) {
      Change change = null;
      try {
         int artId = item.getArtId();
         Long destGamma = item.getDestinationVersion().getGammaId();
         Long baseGamma = item.getBaselineVersion().getGammaId();
         Artifact startTxArtifact;
         Artifact endTxArtifact;
         // When start and end transactions are on same branch set them to start and end respectfully
         // When they are on different branches, they are switched so the difference between them
         // will be detected and represented appropriately.
         if (txDelta.areOnTheSameBranch()) {
            startTxArtifact = bulkLoaded.get(txDelta.getStartTx(), artId);
            endTxArtifact = bulkLoaded.get(txDelta.getEndTx(), artId);
         } else {
            startTxArtifact = bulkLoaded.get(txDelta.getEndTx(), artId);
            endTxArtifact = bulkLoaded.get(txDelta.getStartTx(), artId);
         }
         Artifact baseTxArtifact;
         if (baseGamma != null && baseGamma.equals(destGamma)) {
            // change must be only on IS branch
            baseTxArtifact = startTxArtifact;
         } else {
            // if basGamma is null then this must be a new artifact
            // Otherwise, change must be on IS branch and WAS branch.
            // In either case, set the baseTxArtifact to the base of the start branch
            baseTxArtifact = bulkLoaded.get(BranchManager.getBaseTransaction(txDelta.getStartTx().getBranch()), artId);
         }

         ArtifactDelta artifactDelta = new ArtifactDelta(txDelta, startTxArtifact, endTxArtifact, baseTxArtifact);
         change = createChangeObject(bulkLoaded, item, txDelta, startTxBranch, artifactDelta);
         change.setChangeItem(item);

      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         change = new ErrorChange(startTxBranch, item.getArtId(), ex.toString());
      }
      return change;
   }

   private Change computeChange(CompositeKeyHashMap<TransactionId, Integer, Artifact> bulkLoaded, BranchId startTxBranch, ChangeItem item) {
      Change change = null;
      try {
         int artId = item.getArtId();
         Artifact startTxArtifact;
         Artifact endTxArtifact;
         if (txDelta.areOnTheSameBranch()) {
            startTxArtifact = bulkLoaded.get(txDelta.getStartTx(), artId);
            endTxArtifact = bulkLoaded.get(txDelta.getEndTx(), artId);
         } else {
            startTxArtifact = bulkLoaded.get(BranchManager.getBaseTransaction(txDelta.getStartTx().getBranch()), artId);
            endTxArtifact = bulkLoaded.get(txDelta.getStartTx(), artId);
         }

         ArtifactDelta artifactDelta = new ArtifactDelta(txDelta, startTxArtifact, endTxArtifact);
         change = createChangeObject(bulkLoaded, item, txDelta, startTxBranch, artifactDelta);
         change.setChangeItem(item);

      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         change = new ErrorChange(startTxBranch, item.getArtId(), ex.toString());
      }
      return change;
   }

   private Change createChangeObject(CompositeKeyHashMap<TransactionId, Integer, Artifact> bulkLoaded, ChangeItem item, TransactionDelta txDelta, BranchId startTxBranch, ArtifactDelta artifactDelta) throws OseeCoreException {
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

      switch (item.getChangeType()) {
         case ARTIFACT_CHANGE:

            if (item.isApplicabilityCopy() || ChangeItemUtil.hasApplicabilityOnlyChange(item)) {
               netModType = ModificationType.APPLICABILITY;
               change = new ArtifactChange(startTxBranch, itemGammaId, itemId, txDelta, netModType,
                  item.getCurrentVersion().getApplicabilityToken().getName(),
                  item.getDestinationVersion().getApplicabilityToken().getName(), isHistorical, changeArtifact,
                  artifactDelta);
            } else {
               change = new ArtifactChange(startTxBranch, itemGammaId, itemId, txDelta, netModType, "", "",
                  isHistorical, changeArtifact, artifactDelta);
            }
            break;
         case ATTRIBUTE_CHANGE:
            AttributeType attributeType = AttributeTypeManager.getTypeByGuid(item.getItemTypeId());
            if (item.isApplicabilityCopy() || ChangeItemUtil.hasApplicabilityOnlyChange(item)) {
               netModType = ModificationType.APPLICABILITY;
               change = new AttributeChange(startTxBranch, itemGammaId, artId, txDelta, netModType,
                  item.getCurrentVersion().getApplicabilityToken().getName(),
                  item.getDestinationVersion().getApplicabilityToken().getName(), itemId, attributeType, netModType,
                  isHistorical, changeArtifact, artifactDelta);
            } else {
               String isValue = item.getCurrentVersion().getValue();

               String wasValue = "";
               if (!txDelta.areOnTheSameBranch()) {
                  ChangeVersion netChange = item.getNetChange();
                  if (!ChangeItemUtil.isNew(netChange) && !ChangeItemUtil.isIntroduced(netChange)) {
                     ChangeVersion fromVersion = ChangeItemUtil.getStartingVersion(item);
                     wasValue = fromVersion.getValue();
                  }
               } else {
                  Artifact startArtifact = artifactDelta.getBaseArtifact();
                  if (startArtifact == null) {
                     startArtifact = artifactDelta.getStartArtifact();
                  }
                  if (startArtifact != null) {
                     wasValue = startArtifact.getAttributesToString(attributeType);
                     if (wasValue == null) {
                        wasValue = "";
                     }
                  }
               }

               change = new AttributeChange(startTxBranch, itemGammaId, artId, txDelta, netModType, isValue, wasValue,
                  itemId, attributeType, netModType, isHistorical, changeArtifact, artifactDelta);
            }
            break;
         case RELATION_CHANGE:
            RelationType relationType = RelationTypeManager.getTypeByGuid(item.getItemTypeId());

            TransactionId transaction = txDelta.getStartTx();
            if (txDelta.areOnTheSameBranch()) {
               transaction = txDelta.getEndTx();
            }
            Artifact endTxBArtifact = bulkLoaded.get(transaction, item.getArtIdB());

            String rationale = item.getCurrentVersion().getValue();

            if (item.isApplicabilityCopy() || ChangeItemUtil.hasApplicabilityOnlyChange(item)) {
               netModType = ModificationType.APPLICABILITY;
               change = new RelationChange(startTxBranch, itemGammaId, artId, txDelta, netModType,
                  endTxBArtifact.getArtId(), itemId, item.getCurrentVersion().getApplicabilityToken().getName(),
                  item.getDestinationVersion().getApplicabilityToken().getName(), relationType, isHistorical,
                  changeArtifact, artifactDelta, endTxBArtifact);
            } else {
               change =
                  new RelationChange(startTxBranch, itemGammaId, artId, txDelta, netModType, endTxBArtifact.getArtId(),
                     itemId, rationale, "", relationType, isHistorical, changeArtifact, artifactDelta, endTxBArtifact);
            }
            break;
         default:
            throw new OseeCoreException("The change item must map to either an artifact, attribute or relation change");
      }
      return change;
   }

   private void bulkLoadArtifactDeltas(CompositeKeyHashMap<TransactionId, Integer, Artifact> bulkLoaded, Collection<ChangeItem> changeItems) throws OseeCoreException {
      Set<Integer> artIds = asArtIds(changeItems);

      preloadArtifacts(bulkLoaded, artIds, txDelta.getStartTx(), txDelta.areOnTheSameBranch());
      if (!txDelta.getStartTx().equals(txDelta.getEndTx())) {
         preloadArtifacts(bulkLoaded, artIds, txDelta.getEndTx(), txDelta.areOnTheSameBranch());
      }

      if (!txDelta.areOnTheSameBranch()) {
         preloadArtifacts(bulkLoaded, artIds, BranchManager.getBaseTransaction(txDelta.getStartTx().getBranch()), true);
      }
   }

   private static void preloadArtifacts(CompositeKeyHashMap<TransactionId, Integer, Artifact> bulkLoaded, Collection<Integer> artIds, TransactionToken tx, boolean isHistorical) throws OseeCoreException {
      List<Artifact> artifacts;

      if (isHistorical) {
         artifacts = ArtifactQuery.getHistoricalArtifactListFromIds(artIds, tx, INCLUDE_DELETED);
      } else {
         artifacts = ArtifactQuery.getArtifactListFromIds(artIds, tx.getBranch(), INCLUDE_DELETED);
      }
      for (Artifact artifact : artifacts) {
         bulkLoaded.put(tx, artifact.getArtId(), artifact);
      }
   }

   private static Set<Integer> asArtIds(Collection<ChangeItem> changeItems) {
      Set<Integer> artIds = new HashSet<>();
      for (ChangeItem item : changeItems) {
         artIds.add(item.getArtId());
         if (item.getChangeType().isRelationChange()) {
            artIds.add(item.getArtIdB());
         }
      }
      return artIds;
   }

   private static List<ChangeItem> requestChanges(TransactionDelta txDelta) throws OseeCoreException {
      OseeClient client = ServiceUtil.getOseeClient();
      TransactionEndpoint proxy = client.getTransactionEndpoint();

      try {
         CompareResults results = proxy.compareTxs(txDelta.getStartTx(), txDelta.getEndTx());
         List<ChangeItem> changes = new ArrayList<>();
         for (ChangeItem item : results.getChanges()) {
            if (isAllowableChange(item.getIgnoreType())) {
               changes.add(item);
            }
         }
         return changes;

      } catch (Exception ex) {
         throw JaxRsExceptions.asOseeException(ex);
      }
   }

   private static boolean isAllowableChange(ChangeIgnoreType ignoreType) {
      return //
      ignoreType.isNone() || //
         ignoreType.isResurrected() || //
         ignoreType.isDeletedOnDestAndNotResurrected() || //
         ignoreType.isDeletedOnDestination();
   }
}
