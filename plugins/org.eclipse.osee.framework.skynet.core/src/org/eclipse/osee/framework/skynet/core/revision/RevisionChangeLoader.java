/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.skynet.core.revision;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.client.OseeClient;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.RelationId;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.model.TransactionDelta;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.core.model.change.ChangeItemUtil;
import org.eclipse.osee.framework.core.model.change.ChangeType;
import org.eclipse.osee.framework.core.model.change.ChangeVersion;
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.HashCollectionSet;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoader;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.change.ArtifactChange;
import org.eclipse.osee.framework.skynet.core.change.ArtifactDelta;
import org.eclipse.osee.framework.skynet.core.change.AttributeChange;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.change.RelationChange;
import org.eclipse.osee.framework.skynet.core.internal.ServiceUtil;
import org.eclipse.osee.orcs.rest.model.TransactionEndpoint;

/**
 * Acquires changes for a single given artifact. See LoadArtifactHistory for the server-side loading of this data.
 *
 * @author Jeff C. Phillips
 * @author Branden w. Phillips
 */
public final class RevisionChangeLoader {

   private final OrcsTokenService tokenService;
   private final List<ChangeType> loadChangeTypes = new ArrayList<>();

   protected RevisionChangeLoader() {
      this.tokenService = OsgiUtil.getService(ArtifactLoader.class, OrcsTokenService.class);
   }

   /**
    * @return Returns all given changes from a specified artifact
    */
   public Collection<Change> getChangesPerArtifact(Artifact artifact, IProgressMonitor monitor, ChangeType... loadChangeTypes) {
      this.loadChangeTypes.clear();
      for (ChangeType changeType : loadChangeTypes) {
         this.loadChangeTypes.add(changeType);
      }

      return getChangesPerArtifact(artifact, -1, monitor);
   }

   /**
    * @return Returns limited amount of changes from a specified artifact
    */
   public Collection<Change> getChangesPerArtifact(Artifact artifact, int numberTransactionsToShow, IProgressMonitor monitor, ChangeType... loadChangeTypes) {
      this.loadChangeTypes.clear();
      for (ChangeType changeType : loadChangeTypes) {
         this.loadChangeTypes.add(changeType);
      }

      return getChangesPerArtifact(artifact, numberTransactionsToShow, monitor);
   }

   /**
    * Gets the list of ChangeItems for the specified artifact on the branch, if necessary it will trim down the
    * transactions. Pre-Loads the artifacts from the found artifact ids and transactions, and then creates the
    * collection of changes to return
    */
   private Collection<Change> getChangesPerArtifact(Artifact artifact, int numberTransactionsToShow, IProgressMonitor monitor) {
      Collection<Change> changes = new ArrayList<>();

      OseeClient client = ServiceUtil.getOseeClient();
      TransactionEndpoint txEndpoint = client.getTransactionEndpoint();
      try {
         List<ChangeItem> changeItems = txEndpoint.getArtifactHistory(artifact, artifact.getBranch());
         if (!changeItems.isEmpty()) {
            if (numberTransactionsToShow > 0) {
               changeItems = trimTransactions(changeItems, numberTransactionsToShow);
            }
            CompositeKeyHashMap<TransactionToken, ArtifactId, Artifact> loadedMap = getBulkLoadedArtifacts(changeItems);
            changes = getChanges(changeItems, loadedMap, numberTransactionsToShow);
         }
      } catch (Exception ex) {
         throw OseeCoreException.wrap(ex);
      }

      return changes;
   }

   /**
    * This method loops through changes and only keeps those that are within the amount of transactions asked to be
    * loaded.
    */
   private List<ChangeItem> trimTransactions(List<ChangeItem> changeItems, int numberTransactionsToShow) {
      List<ChangeItem> changeItemsToReturn = new ArrayList<>();
      Collections.reverse(changeItems);
      Set<TransactionToken> txSet = new HashSet<>();

      for (ChangeItem item : changeItems) {
         TransactionToken txToken = item.getCurrentVersion().getTransactionToken();
         if (!txSet.contains(txToken)) {
            if (txSet.size() == numberTransactionsToShow) {
               break;
            }
            txSet.add(txToken);
         }
         changeItemsToReturn.add(0, item);
      }
      return changeItemsToReturn;
   }

   /**
    * Loads the artifacts for the given change items.
    */
   private CompositeKeyHashMap<TransactionToken, ArtifactId, Artifact> getBulkLoadedArtifacts(List<ChangeItem> changeItems) {
      CompositeKeyHashMap<TransactionToken, ArtifactId, Artifact> loadedMap = new CompositeKeyHashMap<>();
      HashCollectionSet<TransactionToken, ArtifactId> txToArtIdMap = new HashCollectionSet<>();

      /**
       * Loops through each change item and extracts the transaction tokens and artifact ids that will need to be loaded
       */
      for (ChangeItem item : changeItems) {
         txToArtIdMap.put(item.getCurrentVersion().getTransactionToken(), item.getArtId());
         if (item.getChangeType().isRelationChange()) {
            txToArtIdMap.put(item.getCurrentVersion().getTransactionToken(), item.getArtIdB());
         }
      }

      /**
       * First we grab the most recent transaction token because the top entry should be the most current version of the
       * artifact. Then it loops through each transaction/artifactid combination and either loads the current version,
       * or historical versions. Finally loading those found artifacts into the map
       */
      TransactionToken lastTx = changeItems.get(changeItems.size() - 1).getCurrentVersion().getTransactionToken();
      for (Entry<TransactionToken, Set<ArtifactId>> entry : txToArtIdMap.entrySet()) {
         TransactionToken txToken = entry.getKey();
         List<Artifact> artifacts;
         if (txToken.equals(lastTx)) {
            artifacts =
               ArtifactQuery.getArtifactListFrom(entry.getValue(), txToken.getBranch(), DeletionFlag.INCLUDE_DELETED);
         } else {
            artifacts =
               ArtifactQuery.getHistoricalArtifactListFromIds(entry.getValue(), txToken, DeletionFlag.INCLUDE_DELETED);
         }
         for (Artifact art : artifacts) {
            loadedMap.put(txToken, ArtifactId.valueOf(art.getId()), art);
         }
      }

      return loadedMap;
   }

   /**
    * This method is what loops through the given ChangeItems and creates a collection of changes. This will only
    * process/add in changes that were previously set in the loadChangeTypes array.
    */
   private Collection<Change> getChanges(Collection<ChangeItem> changeItems, CompositeKeyHashMap<TransactionToken, ArtifactId, Artifact> loadedMap, int numberTransactionsToShow) {
      Collection<Change> changes = new ArrayList<>();
      List<TransactionToken> modifiedTxs = new ArrayList<>();

      for (ChangeItem changeItem : changeItems) {
         ChangeVersion currentVersion = changeItem.getCurrentVersion();
         TransactionToken currentTx = currentVersion.getTransactionToken();
         ArtifactId changeArtId = changeItem.getArtId();
         BranchToken currentBranch = BranchManager.getBranchToken(currentTx.getBranch());
         GammaId currentGammaId = currentVersion.getGammaId();
         ModificationType modType = currentVersion.getModType();
         ChangeVersion startVersion = changeItem.getBaselineVersion();
         Artifact baselineArtifact;
         TransactionToken startTx;
         if (startVersion.isValid()) {
            startTx = startVersion.getTransactionToken();
            baselineArtifact = loadedMap.get(startTx, changeArtId);
         } else {
            startTx = currentTx;
            baselineArtifact = Artifact.SENTINEL;
         }
         TransactionDelta txDelta = new TransactionDelta(startTx, currentTx);
         Artifact changeArtifact = loadedMap.get(currentVersion.getTransactionToken(), changeArtId);
         if (changeArtifact != null) {
            ArtifactDelta artDelta = new ArtifactDelta(txDelta, baselineArtifact, changeArtifact);
            boolean isHistorical = changeArtifact.isHistorical();

            ChangeType changeType = changeItem.getChangeType();
            if (changeType.isArtifactChange() && loadChangeTypes.contains(changeType)) {
               ArtifactChange artChange = new ArtifactChange(currentBranch, currentGammaId, changeArtId, txDelta,
                  modType, "", "", isHistorical, changeArtifact, artDelta);
               changes.add(artChange);

               /**
                * This if statement calculates if the artifact ChangeItem contains an applicability change<br/>
                * 1. isApplicabilityCopy<br/>
                * 2. The start/current version applicabilites aren't the same, AND either the start version is valid or
                * it is a modification type of new. This handles modified applicabilites, or the artifact being brought
                * onto a branch<br/>
                * 3. The modification type is a deleted type and the start version is valid. This handles when the
                * artifact is being deleted from a branch where it previously existed<br/>
                */
               if (changeItem.isApplicabilityCopy() || ((startVersion.isValid() || modType.equals(
                  ModificationType.NEW)) && !ChangeItemUtil.areApplicabilitiesEqual(startVersion,
                     currentVersion)) || (ChangeItemUtil.isDeleted(currentVersion) && startVersion.isValid())) {
                  ArtifactChange applicChange = getApplicabilityChange(startVersion, currentVersion, currentBranch,
                     currentGammaId, changeArtId, txDelta, changeArtifact, artDelta);

                  changes.add(applicChange);
               }
               if (!modifiedTxs.contains(currentTx)) {
                  modifiedTxs.add(currentTx);
               }
            } else if (changeType.isAttributeChange() && loadChangeTypes.contains(changeType)) {
               if (!modifiedTxs.contains(currentTx) && loadChangeTypes.contains(ChangeType.Artifact)) {
                  /**
                   * For each transaction, this adds a modified artifact change along with the attribute changes
                   */
                  ArtifactChange artChange = new ArtifactChange(currentBranch, GammaId.SENTINEL, changeArtId, txDelta,
                     ModificationType.MODIFIED, "", "", isHistorical, changeArtifact, artDelta);
                  changes.add(artChange);
                  modifiedTxs.add(currentTx);
               }

               AttributeChange attrChange = getAttributeChange(changeItem, startVersion, currentVersion, currentBranch,
                  currentGammaId, changeArtId, txDelta, modType, changeArtifact, artDelta);

               changes.add(attrChange);
            } else if (changeType.isRelationChange() && loadChangeTypes.contains(changeType)) {
               if (!modifiedTxs.contains(currentTx) && loadChangeTypes.contains(ChangeType.Artifact)) {
                  /**
                   * For each transaction, this adds a modified artifact change along with the relation changes
                   */
                  ArtifactChange artChange = new ArtifactChange(currentBranch, GammaId.SENTINEL, changeArtId, txDelta,
                     ModificationType.MODIFIED, "", "", true, changeArtifact, artDelta);
                  changes.add(artChange);
                  modifiedTxs.add(currentTx);
               }
               RelationChange relChange = getRelationChange(changeItem, loadedMap, startVersion, currentVersion,
                  currentBranch, currentGammaId, changeArtId, txDelta, modType, changeArtifact, artDelta);

               changes.add(relChange);
            }
         } else {
            /**
             * If there was an issue finding an artifact when loading, this adds in a basic artifact change
             */
            ArtifactChange change = new ArtifactChange(currentBranch, currentGammaId, changeArtId, txDelta, modType, "",
               "", true, Artifact.SENTINEL, null);
            changes.add(change);
         }
      }

      return changes;
   }

   /**
    * This method is used to get the applicability change, which is a type ArtifactChange. Uses the applicability token
    * name for the was/is values. If the modtype is deleted, we do not include the isValue name, it becomes blank since
    * it should no longer exist
    */
   private ArtifactChange getApplicabilityChange(ChangeVersion startVersion, ChangeVersion currentVersion, BranchToken branch, GammaId gammaId, ArtifactId artId, TransactionDelta txDelta, Artifact artifact, ArtifactDelta artDelta) {
      String wasValue = "";
      String isValue = "";
      if (startVersion.isValid()) {
         wasValue = startVersion.getApplicabilityToken().getName();
      }
      if (currentVersion.isValid() && !ChangeItemUtil.isDeleted(currentVersion)) {
         isValue = currentVersion.getApplicabilityToken().getName();
      }
      ArtifactChange applicChange = new ArtifactChange(branch, gammaId, artId, txDelta, ModificationType.APPLICABILITY,
         isValue, wasValue, artifact.isHistorical(), artifact, artDelta);

      return applicChange;
   }

   private AttributeChange getAttributeChange(ChangeItem changeItem, ChangeVersion startVersion, ChangeVersion currentVersion, BranchToken branch, GammaId gammaId, ArtifactId artId, TransactionDelta txDelta, ModificationType modType, Artifact artifact, ArtifactDelta artDelta) {
      String isValue = currentVersion.getValue();
      String isUri = currentVersion.getUri();
      String wasValue = startVersion.getValue();
      String wasUri = startVersion.getUri();
      AttributeId attrId = AttributeId.valueOf(changeItem.getItemId().getId());
      AttributeTypeToken typeToken = tokenService.getAttributeType(changeItem.getItemTypeId().getId());

      AttributeChange attrChange = new AttributeChange(branch, gammaId, artId, txDelta, modType, isValue, isUri,
         wasValue, wasUri, attrId, typeToken, modType, artifact.isHistorical(), artifact, artDelta);

      return attrChange;
   }

   private RelationChange getRelationChange(ChangeItem changeItem, CompositeKeyHashMap<TransactionToken, ArtifactId, Artifact> loadedMap, ChangeVersion startVersion, ChangeVersion currentVersion, BranchToken branch, GammaId gammaId, ArtifactId artId, TransactionDelta txDelta, ModificationType modType, Artifact artifact, ArtifactDelta artDelta) {
      RelationId relationId = RelationId.valueOf(changeItem.getItemId().getId());
      String value = currentVersion.getValue();
      RelationTypeToken relationType = tokenService.getRelationType(changeItem.getItemTypeId().getId());
      Artifact artifactB = loadedMap.get(currentVersion.getTransactionToken(), changeItem.getArtIdB());

      RelationChange relChange = new RelationChange(branch, gammaId, artId, txDelta, modType, changeItem.getArtIdB(),
         relationId, value, "", relationType, artifact.isHistorical(), artifact, artDelta, artifactB);

      return relChange;
   }
}