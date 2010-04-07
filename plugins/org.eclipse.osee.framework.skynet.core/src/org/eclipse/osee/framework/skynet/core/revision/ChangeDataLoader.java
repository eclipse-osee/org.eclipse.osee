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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.ArtifactChangeItem;
import org.eclipse.osee.framework.core.data.AttributeChangeItem;
import org.eclipse.osee.framework.core.data.ChangeItem;
import org.eclipse.osee.framework.core.data.ChangeReportResponse;
import org.eclipse.osee.framework.core.data.ChangeVersion;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.RelationChangeItem;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.util.ChangeItemUtil;
import org.eclipse.osee.framework.database.core.SQL3DataType;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoad;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoader;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.change.ArtifactChange;
import org.eclipse.osee.framework.skynet.core.change.AttributeChange;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.change.ErrorChange;
import org.eclipse.osee.framework.skynet.core.change.RelationChange;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;

/**
 * @author Jeff C. Phillips
 */
public class ChangeDataLoader {

   public Collection<Change> getChanges(IOseeBranch sourceBranch, TransactionRecord transactionId, IProgressMonitor monitor) throws OseeCoreException {
      boolean isHistorical = sourceBranch == null;
      IOseeBranch branch = isHistorical ? transactionId.getBranch() : sourceBranch;

      ArrayList<Change> changes = new ArrayList<Change>();
      Pair<TransactionRecord, TransactionRecord> toTransactionFromTransactionPair =
            getTransactionRecords(sourceBranch, transactionId, monitor, isHistorical);
      ChangeReportResponse response =
            HttpChangeDataRequester.getChanges(toTransactionFromTransactionPair.getFirst(),
                  toTransactionFromTransactionPair.getSecond(), monitor, isHistorical);
      List<ChangeItem> changeItems = response.getChangeItems();

      //This is to keep the weak reference from being collected before they can be used.
      Collection<Artifact> bulkLoadedToArtifacts =
            preloadArtifacts(changeItems, sourceBranch, transactionId, isHistorical, monitor);
      Collection<Artifact> bulkLoadedFromArtifacts =
            preloadArtifacts(changeItems, sourceBranch, toTransactionFromTransactionPair.getSecond(), true, monitor);

      Map<Integer, ArtifactChangeItem> artifactChanges = new HashMap<Integer, ArtifactChangeItem>();
      for (ChangeItem item : changeItems) {
         if (item instanceof ArtifactChangeItem) {
            ArtifactChangeItem artItem = (ArtifactChangeItem) item;
            artifactChanges.put(artItem.getArtId(), artItem);
         }
      }

      for (ChangeItem item : changeItems) {
         Change change = null;
         try {
            Artifact toArtifact = null;
            if (isHistorical) {
               toArtifact = ArtifactCache.getHistorical(item.getArtId(), transactionId.getId());
            } else {
               toArtifact = ArtifactQuery.getArtifactFromId(item.getArtId(), branch, true);
            }

            Artifact fromArtifact = null;

            ArtifactChangeItem artifactChangeItem = artifactChanges.get(item.getArtId());
            ModificationType artifactModType = artifactChangeItem.getBaselineVersion().getModType();

            if (artifactModType != ModificationType.NEW && artifactModType != ModificationType.INTRODUCED) {
               fromArtifact =
                     ArtifactCache.getHistorical(item.getArtId(), toTransactionFromTransactionPair.getSecond().getId());
            }

            String wasValue = "";

            if (!isHistorical) {
               ChangeVersion netChange = item.getNetChange();
               if (!ChangeItemUtil.isNew(netChange) && !ChangeItemUtil.isIntroduced(netChange)) {
                  ChangeVersion fromVersion = ChangeItemUtil.getStartingVersion(item);
                  wasValue = fromVersion.getValue();
               }
            }
            monitor.subTask("Build Change Display Objects");
            //The artifacts have been previously bulk loaded for performance
            change =
                  asChange(item, toArtifact, fromArtifact, branch, toTransactionFromTransactionPair.getSecond(),
                        toTransactionFromTransactionPair.getFirst(), wasValue, isHistorical);
         } catch (Exception ex) {
            change = new ErrorChange(branch, item.getArtId(), ex.toString());
         }
         changes.add(change);
      }
      monitor.done();
      bulkLoadedToArtifacts.clear();
      bulkLoadedFromArtifacts.clear();
      return changes;
   }

   private Pair<TransactionRecord, TransactionRecord> getTransactionRecords(IOseeBranch sourceBranch, TransactionRecord transactionId,
         IProgressMonitor monitor, boolean isHistorical) throws OseeCoreException {
      TransactionRecord destinationTransactionId;
      TransactionRecord sourceTransactionId;

      if (isHistorical) {
         destinationTransactionId = TransactionManager.getPriorTransaction(transactionId);
         sourceTransactionId = transactionId;
      } else {
         destinationTransactionId =
               TransactionManager.getHeadTransaction(BranchManager.getBranch(sourceBranch).getParentBranch());
         sourceTransactionId = TransactionManager.getHeadTransaction(sourceBranch);
      }
      return new Pair<TransactionRecord, TransactionRecord>(sourceTransactionId, destinationTransactionId);
   }

   private Change asChange(ChangeItem item, Artifact toArtifact, Artifact fromArtifact, IOseeBranch branch, TransactionRecord fromTransactionId, TransactionRecord toTransactionId, String wasValue, boolean isHistorical) throws OseeCoreException {
      Change change = null;
      // TODO create artifact item
      // HERE
      if (item instanceof ArtifactChangeItem) {
         change =
               new ArtifactChange(branch, toArtifact.getArtifactType(),
                     (int) item.getCurrentVersion().getGammaId().longValue(), item.getItemId(), toTransactionId,
                     fromTransactionId, item.getNetChange().getModType(), isHistorical, toArtifact, fromArtifact);
      } else if (item instanceof AttributeChangeItem) {
         change =
               new AttributeChange(branch, toArtifact.getArtifactType(),
                     (int) item.getCurrentVersion().getGammaId().longValue(), item.getArtId(), toTransactionId,
                     fromTransactionId, item.getNetChange().getModType(), item.getCurrentVersion().getValue(),
                     wasValue, item.getItemId(),
                     toArtifact.getAttributeById(item.getItemId(), true).getAttributeType().getId(),
                     item.getNetChange().getModType(), isHistorical, toArtifact, fromArtifact);
      } else if (item instanceof RelationChangeItem) {
         RelationChangeItem relationChangeItem = (RelationChangeItem) item;
         Artifact bArtifact;

         if (isHistorical) {
            bArtifact = ArtifactCache.getHistorical(relationChangeItem.getBArtId(), toTransactionId.getId());
         } else {
            bArtifact = ArtifactQuery.getArtifactFromId(relationChangeItem.getBArtId(), branch, true);
         }
         change =
               new RelationChange(branch, toArtifact.getArtifactType(),
                     (int) relationChangeItem.getCurrentVersion().getGammaId().longValue(), item.getArtId(),
                     toTransactionId, fromTransactionId, relationChangeItem.getNetChange().getModType(),
                     bArtifact.getArtId(), relationChangeItem.getItemId(), relationChangeItem.getRationale(),
                     RelationTypeManager.getType(relationChangeItem.getRelTypeId()), isHistorical, toArtifact,
                     bArtifact, fromArtifact);
      } else {
         throw new OseeCoreException("The change item must map to either a artifact, attribute or relation change");
      }
      return change;
   }

   private Collection<Artifact> preloadArtifacts(List<ChangeItem> changeItems, IOseeBranch sourceBranch, TransactionRecord transactionId, boolean isHistorical, IProgressMonitor monitor) throws OseeCoreException {
      Collection<Artifact> artifacts = Collections.emptyList();
      if (!changeItems.isEmpty()) {
         monitor.subTask("Preload artifacts");
         int queryId = ArtifactLoader.getNewQueryId();
         Timestamp insertTime = GlobalTime.GreenwichMeanTimestamp();
         IOseeBranch branch = isHistorical ? transactionId.getBranch() : sourceBranch;

         Set<Integer> artIds = new HashSet<Integer>();
         for (ChangeItem item : changeItems) {
            artIds.add(item.getArtId());

            if (item instanceof RelationChangeItem) {
               artIds.add(((RelationChangeItem) item).getBArtId());
            }
         }
         List<Object[]> insertParameters = new LinkedList<Object[]>();
         for (Integer artId : artIds) {
            insertParameters.add(new Object[] {queryId, insertTime, artId, BranchManager.getBranch(branch).getId(),
                  isHistorical ? transactionId.getId() : SQL3DataType.INTEGER});
         }
         artifacts =
               ArtifactLoader.loadArtifacts(queryId, ArtifactLoad.ALL_CURRENT, null, insertParameters, true,
                     isHistorical, true);
      }
      return artifacts;
   }
}
