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
import java.util.StringTokenizer;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.RelationId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.data.TupleTypeId;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.model.TransactionDelta;
import org.eclipse.osee.framework.core.model.change.ChangeIgnoreType;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.core.model.change.ChangeItemUtil;
import org.eclipse.osee.framework.core.model.change.ChangeVersion;
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
import org.eclipse.osee.framework.skynet.core.change.ArtifactWasIsLazyProvider;
import org.eclipse.osee.framework.skynet.core.change.AttributeChange;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.change.ErrorChange;
import org.eclipse.osee.framework.skynet.core.change.RelationChange;
import org.eclipse.osee.framework.skynet.core.change.TupleChange;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.internal.ServiceUtil;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;
import org.eclipse.osee.framework.skynet.core.utility.OseeInfo;
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
         CompositeKeyHashMap<TransactionId, ArtifactId, Artifact> bulkLoaded = new CompositeKeyHashMap<>();

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

   public void determineChanges(IProgressMonitor monitor) {
      monitor.setTaskName("Retrieve Change Items");
      List<ChangeItem> changeItems = requestChanges(txDelta);

      checkForCancelledStatus(monitor);
      monitor.setTaskName("Bulk load changed artifacts");
      CompositeKeyHashMap<TransactionId, ArtifactId, Artifact> bulkLoaded = new CompositeKeyHashMap<>();

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

   private Change computeChangeFromGamma(CompositeKeyHashMap<TransactionId, ArtifactId, Artifact> bulkLoaded, BranchId startTxBranch, ChangeItem item) {
      Change change = null;
      try {
         ArtifactId artId = item.getArtId();
         GammaId destGamma = item.getDestinationVersion().getGammaId();
         GammaId baseGamma = item.getBaselineVersion().getGammaId();
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

   private Change computeChange(CompositeKeyHashMap<TransactionId, ArtifactId, Artifact> bulkLoaded, BranchId startTxBranch, ChangeItem item) {
      Change change = null;
      try {
         ArtifactId artId = item.getArtId();
         ArtifactDelta artifactDelta = null;
         if (artId.isValid()) {
            Artifact startTxArtifact;
            Artifact endTxArtifact;
            if (txDelta.areOnTheSameBranch()) {
               startTxArtifact = bulkLoaded.get(txDelta.getStartTx(), artId);
               endTxArtifact = bulkLoaded.get(txDelta.getEndTx(), artId);
            } else {
               startTxArtifact =
                  bulkLoaded.get(BranchManager.getBaseTransaction(txDelta.getStartTx().getBranch()), artId);
               endTxArtifact = bulkLoaded.get(txDelta.getStartTx(), artId);
            }
            artifactDelta = new ArtifactDelta(txDelta, startTxArtifact, endTxArtifact);
         }

         change = createChangeObject(bulkLoaded, item, txDelta, startTxBranch, artifactDelta);
         change.setChangeItem(item);

      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         change = new ErrorChange(startTxBranch, item.getArtId(), ex.toString());
      }
      return change;
   }

   private Change createChangeObject(CompositeKeyHashMap<TransactionId, ArtifactId, Artifact> bulkLoaded, ChangeItem item, TransactionDelta txDelta, BranchId startTxBranch, ArtifactDelta artifactDelta) {
      Change change = null;

      Long itemId = item.getItemId().getId();
      GammaId itemGammaId = item.getNetChange().getGammaId();
      ModificationType netModType = item.getNetChange().getModType();
      ArtifactId artId = item.getArtId();

      // The change artifact is the artifact that is displayed by the GUI.
      // When we are comparing two different branches, the displayed artifact should be the start artifact or the artifact from the
      // source branch. When we are comparing items from the same branch, the displayed artifact should be the artifact in the end transaction
      // since that is the resulting change artifact.
      Artifact changeArtifact = Artifact.SENTINEL;
      if (artifactDelta != null) {
         changeArtifact = artifactDelta.getEndArtifact();
      }
      boolean isHistorical = txDelta.areOnTheSameBranch();

      switch (item.getChangeType()) {
         case ARTIFACT_CHANGE:

            if (item.isApplicabilityCopy() || ChangeItemUtil.hasApplicabilityOnlyChange(item)) {
               netModType = ModificationType.APPLICABILITY;
               change = new ArtifactChange(startTxBranch, itemGammaId, ArtifactId.valueOf(itemId), txDelta, netModType,
                  item.getCurrentVersion().getApplicabilityToken().getName(),
                  item.getDestinationVersion().getApplicabilityToken().getName(), isHistorical, changeArtifact,
                  artifactDelta);
            } else {
               change = new ArtifactChange(startTxBranch, itemGammaId, ArtifactId.valueOf(itemId), txDelta, netModType,
                  "", "", isHistorical, changeArtifact, artifactDelta);
            }
            break;
         case ATTRIBUTE_CHANGE:
            AttributeTypeToken attributeType = AttributeTypeManager.getTypeById(item.getItemTypeId().getId());
            if (item.isApplicabilityCopy() || ChangeItemUtil.hasApplicabilityOnlyChange(item)) {
               netModType = ModificationType.APPLICABILITY;
               change = new AttributeChange(startTxBranch, itemGammaId, artId, txDelta, netModType,
                  item.getCurrentVersion().getApplicabilityToken().getName(), null,
                  item.getDestinationVersion().getApplicabilityToken().getName(), null, AttributeId.valueOf(itemId),
                  attributeType, netModType, isHistorical, changeArtifact, artifactDelta);
            } else {

               // Remove after 26.0 release; only set in OseeInfo if want to turn off
               String value = OseeInfo.getValue("UseWasIsLazyProvider");
               boolean useWasIsLazyProvider = !"false".equals(value);
               ChangeDateLoaderWasIsLazyProvider wasIsProvider =
                  new ChangeDateLoaderWasIsLazyProvider(txDelta, item, attributeType, artifactDelta);
               if (useWasIsLazyProvider) {
                  change = new AttributeChange(startTxBranch, itemGammaId, artId, txDelta, netModType, wasIsProvider,
                     AttributeId.valueOf(itemId), attributeType, netModType, isHistorical, changeArtifact,
                     artifactDelta);
               } else {
                  change = new AttributeChange(startTxBranch, itemGammaId, artId, txDelta, netModType,
                     wasIsProvider.getIsValue(), wasIsProvider.getIsUri(), wasIsProvider.getWasValue(),
                     wasIsProvider.getWasUri(), AttributeId.valueOf(itemId), attributeType, netModType, isHistorical,
                     changeArtifact, artifactDelta);
               }
            }
            break;
         case RELATION_CHANGE:
            RelationType relationType = RelationTypeManager.getTypeByGuid(item.getItemTypeId().getId());

            TransactionId transaction = txDelta.getStartTx();
            if (txDelta.areOnTheSameBranch()) {
               transaction = txDelta.getEndTx();
            }
            Artifact endTxBArtifact = bulkLoaded.get(transaction, item.getArtIdB());

            String rationale = item.getCurrentVersion().getValue();

            if (item.isApplicabilityCopy() || ChangeItemUtil.hasApplicabilityOnlyChange(item)) {
               netModType = ModificationType.APPLICABILITY;
               change = new RelationChange(startTxBranch, itemGammaId, artId, txDelta, netModType, endTxBArtifact,
                  RelationId.valueOf(itemId), item.getCurrentVersion().getApplicabilityToken().getName(),
                  item.getDestinationVersion().getApplicabilityToken().getName(), relationType, isHistorical,
                  changeArtifact, artifactDelta, endTxBArtifact);
            } else {
               change = new RelationChange(startTxBranch, itemGammaId, artId, txDelta, netModType, endTxBArtifact,
                  RelationId.valueOf(itemId), rationale, "", relationType, isHistorical, changeArtifact, artifactDelta,
                  endTxBArtifact);
            }
            break;
         case TUPLE_CHANGE:
            TupleTypeId tupleTypeId = TupleTypeId.valueOf(item.getItemTypeId().getId());
            String value = item.getCurrentVersion().getValue();
            StringTokenizer tok = new StringTokenizer(value, "|");

            String itemKind = "";
            String tupleIsValue = "";

            if (tok.hasMoreTokens()) {
               itemKind = tok.nextToken();
            }
            if (tok.hasMoreElements()) {
               tupleIsValue = tok.nextToken();
            }
            change = new TupleChange(startTxBranch, itemGammaId, txDelta, netModType, tupleTypeId, tupleIsValue, "?",
               itemKind, isHistorical);
            break;
         default:
            throw new OseeCoreException("The change item must map to either an artifact, attribute or relation change");
      }
      return change;
   }

   private static class ChangeDateLoaderWasIsLazyProvider implements ArtifactWasIsLazyProvider {

      private final TransactionDelta txDelta;
      private final ChangeItem item;
      private final AttributeTypeToken attributeType;
      private final ArtifactDelta artifactDelta;

      public ChangeDateLoaderWasIsLazyProvider(TransactionDelta txDelta, ChangeItem item, AttributeTypeToken attributeType, ArtifactDelta artifactDelta) {
         this.txDelta = txDelta;
         this.item = item;
         this.attributeType = attributeType;
         this.artifactDelta = artifactDelta;

      }

      @Override
      public String getWasValue() {
         String wasValue = "";
         if (!txDelta.areOnTheSameBranch()) {
            ChangeVersion netChange = item.getNetChange();
            if (!ChangeItemUtil.isNew(netChange) && !ChangeItemUtil.isIntroduced(netChange)) {
               ChangeVersion fromVersion = ChangeItemUtil.getStartingVersion(item);
               wasValue = fromVersion.getValue();
            }
         } else {
            Artifact startArtifact = null;

            if (artifactDelta != null) {
               startArtifact = artifactDelta.getBaseArtifact();
               if (startArtifact == null) {
                  startArtifact = artifactDelta.getStartArtifact();
               }
            }

            if (startArtifact != null) {
               wasValue = startArtifact.getAttributesToString(attributeType);
               if (wasValue == null) {
                  wasValue = "";
               }
            }
         }

         return wasValue;
      }

      @Override
      public String getIsValue() {
         return item.getCurrentVersion().getValue();
      }

      @Override
      public String getWasUri() {
         return null;
      }

      @Override
      public String getIsUri() {
         return null;
      }

   }

   private void bulkLoadArtifactDeltas(CompositeKeyHashMap<TransactionId, ArtifactId, Artifact> bulkLoaded, Collection<ChangeItem> changeItems) {
      Set<ArtifactId> artIds = asArtIds(changeItems);

      preloadArtifacts(bulkLoaded, artIds, txDelta.getStartTx(), txDelta.areOnTheSameBranch());
      if (!txDelta.getStartTx().equals(txDelta.getEndTx())) {
         preloadArtifacts(bulkLoaded, artIds, txDelta.getEndTx(), txDelta.areOnTheSameBranch());
      }

      if (!txDelta.areOnTheSameBranch()) {
         preloadArtifacts(bulkLoaded, artIds, BranchManager.getBaseTransaction(txDelta.getStartTx().getBranch()), true);
      }
   }

   private static void preloadArtifacts(CompositeKeyHashMap<TransactionId, ArtifactId, Artifact> bulkLoaded, Collection<ArtifactId> artIds, TransactionToken tx, boolean isHistorical) {
      List<Artifact> artifacts;

      if (isHistorical) {
         artifacts = ArtifactQuery.getHistoricalArtifactListFromIds(artIds, tx, INCLUDE_DELETED);
      } else {
         artifacts = ArtifactQuery.getArtifactListFrom(artIds, tx.getBranch());
      }
      for (Artifact artifact : artifacts) {
         bulkLoaded.put(tx, ArtifactId.valueOf(artifact.getId()), artifact);
      }
   }

   private static Set<ArtifactId> asArtIds(Collection<ChangeItem> changeItems) {
      Set<ArtifactId> artIds = new HashSet<>();
      for (ChangeItem item : changeItems) {
         artIds.add(item.getArtId());
         if (item.getChangeType().isRelationChange()) {
            artIds.add(item.getArtIdB());
         }
      }
      return artIds;
   }

   private static List<ChangeItem> requestChanges(TransactionDelta txDelta) {
      OseeClient client = ServiceUtil.getOseeClient();
      TransactionEndpoint proxy = client.getTransactionEndpoint();

      try {
         List<ChangeItem> results = proxy.compareTxs(txDelta.getStartTx(), txDelta.getEndTx());
         List<ChangeItem> changes = new ArrayList<>();
         for (ChangeItem item : results) {
            if (isAllowableChange(item.getIgnoreType())) {
               changes.add(item);
            }
         }
         return changes;

      } catch (Exception ex) {
         throw OseeCoreException.wrap(ex);
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
