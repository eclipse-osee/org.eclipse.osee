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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.ArtifactChangeItem;
import org.eclipse.osee.framework.core.data.AttributeChangeItem;
import org.eclipse.osee.framework.core.data.ChangeItem;
import org.eclipse.osee.framework.core.data.ChangeReportResponse;
import org.eclipse.osee.framework.core.data.ChangeVersion;
import org.eclipse.osee.framework.core.data.RelationChangeItem;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.util.ChangeItemUtil;
import org.eclipse.osee.framework.database.core.SQL3DataType;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoad;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoader;
import org.eclipse.osee.framework.skynet.core.artifact.HttpChangeDataRequester;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.change.ArtifactChange;
import org.eclipse.osee.framework.skynet.core.change.AttributeChange;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.change.ChangeType;
import org.eclipse.osee.framework.skynet.core.change.ErrorChange;
import org.eclipse.osee.framework.skynet.core.change.RelationChange;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;

/**
 * @author Jeff C. Phillips
 */
public class ChangeDataLoader {

   public Collection<Change> getChanges(Branch sourceBranch, TransactionRecord transactionId, IProgressMonitor monitor) throws OseeCoreException {
      boolean isHistorical = sourceBranch == null;
      ArrayList<Change> changes = new ArrayList<Change>();
      List<ChangeItem> changeItems = loadChangeItems(sourceBranch, transactionId, monitor, isHistorical);

      //This is to keep the weak reference from being collected before they can be used.
      Collection<Artifact> bulkLoadedArtifacts =
            preloadArtifacts(changeItems, sourceBranch, transactionId, isHistorical, monitor);

      for (ChangeItem item : changeItems) {
         Change change = null;
         Branch branch = null;
         Artifact artifact = null;

         try {
            branch = isHistorical ? transactionId.getBranch() : sourceBranch;
            TransactionRecord toTransactionId =
                  TransactionManager.getTransactionId(item.getCurrentVersion().getTransactionNumber().intValue());
            TransactionRecord fromTransactionId;
            String wasValue = "";

            if (isHistorical) {
               fromTransactionId = TransactionManager.getPriorTransaction(toTransactionId);
               artifact = ArtifactCache.getHistorical(item.getArtId(), transactionId.getId());
            } else {
               artifact = ArtifactQuery.getArtifactFromId(item.getArtId(), branch, true);
               ChangeVersion netChange = item.getNetChange();
               if (ChangeItemUtil.isNew(netChange) || ChangeItemUtil.isIntroduced(netChange)) {
                  fromTransactionId = toTransactionId;
               } else {
                  ChangeVersion fromVersion = ChangeItemUtil.getStartingVersion(item);
                  fromTransactionId = TransactionManager.getTransactionId(fromVersion.getTransactionNumber());
                  wasValue = fromVersion.getValue();
               }
            }

            monitor.subTask("Build Change Display Objects");
            //The artifacts have been previously bulk loaded for performance      
            change = asChange(item, artifact, branch, fromTransactionId, toTransactionId, wasValue, isHistorical);
         } catch (Exception ex) {
            change = new ErrorChange(branch, item.getArtId(), ex.toString());
         }
         changes.add(change);
      }
      monitor.done();
      bulkLoadedArtifacts.clear();
      return changes;
   }

   private Change asChange(ChangeItem item, Artifact artifact, Branch branch, TransactionRecord fromTransactionId, TransactionRecord toTransactionId, String wasValue, boolean isHistorical) throws OseeCoreException {
      Change change = null;
      if (item instanceof ArtifactChangeItem) {
         change =
               new ArtifactChange(branch, artifact.getArtifactType(),
               (int) item.getCurrentVersion().getGammaId().longValue(), item.getItemId(), toTransactionId,
               fromTransactionId, item.getNetChange().getModType(), ChangeType.OUTGOING, isHistorical, artifact);
      } else if (item instanceof AttributeChangeItem) {
         change =
               new AttributeChange(branch, artifact.getArtifactType(),
               (int) item.getCurrentVersion().getGammaId().longValue(), item.getArtId(), toTransactionId,
               fromTransactionId, item.getNetChange().getModType(), ChangeType.OUTGOING,
               item.getCurrentVersion().getValue(), wasValue, item.getItemId(), artifact.getAttributeById(
               item.getItemId(), true).getAttributeType().getId(), item.getNetChange().getModType(),
               isHistorical, artifact);
      } else if (item instanceof RelationChangeItem) {
         RelationChangeItem relationChangeItem = (RelationChangeItem) item;
         Artifact bArtifact;

         if (isHistorical) {
            bArtifact = ArtifactCache.getHistorical(relationChangeItem.getBArtId(), toTransactionId.getId());
         } else {
            bArtifact = ArtifactQuery.getArtifactFromId(relationChangeItem.getBArtId(), branch, true);
         }
         change =
               new RelationChange(branch, artifact.getArtifactType(),
               (int) relationChangeItem.getCurrentVersion().getGammaId().longValue(), item.getArtId(),
               toTransactionId, fromTransactionId, relationChangeItem.getNetChange().getModType(),
               ChangeType.OUTGOING, bArtifact.getArtId(), relationChangeItem.getItemId(),
               relationChangeItem.getRationale(), RelationTypeManager.getType(relationChangeItem.getRelTypeId()),
               isHistorical, artifact, bArtifact);
      } else {
         throw new OseeCoreException("The change item must map to either a artifact, attribute or relation change");
      }
      return change;
   }

   private Collection<Artifact> preloadArtifacts(List<ChangeItem> changeItems, Branch sourceBranch, TransactionRecord transactionId, boolean isHistorical, IProgressMonitor monitor) throws OseeCoreException {
      Collection<Artifact> artifacts = Collections.emptyList();
      if (!changeItems.isEmpty()) {
         monitor.subTask("Preload artifacts");
         int queryId = ArtifactLoader.getNewQueryId();
         Timestamp insertTime = GlobalTime.GreenwichMeanTimestamp();
         Branch branch = isHistorical ? transactionId.getBranch() : sourceBranch;

         Set<Integer> artIds = new HashSet<Integer>();
         for (ChangeItem item : changeItems) {
            artIds.add(item.getArtId());

            if (item instanceof RelationChangeItem) {
               artIds.add(((RelationChangeItem) item).getBArtId());
            }
         }

         List<Object[]> insertParameters = new LinkedList<Object[]>();
         for (Integer artId : artIds) {
            insertParameters.add(new Object[] {queryId, insertTime, artId, branch.getId(),
                  isHistorical ? transactionId.getId() : SQL3DataType.INTEGER});
         }

         artifacts =
               ArtifactLoader.loadArtifacts(queryId, ArtifactLoad.ALL_CURRENT, null, insertParameters, true,
               isHistorical, true);
      }
      return artifacts;
   }

   /**
    * @param sourceBranch
    * @param monitor
    * @return
    * @throws OseeCoreException
    * @throws OseeWrappedException
    */
   private List<ChangeItem> loadChangeItems(Branch sourceBranch, TransactionRecord transactionId, IProgressMonitor monitor, boolean isHistorical) throws OseeCoreException, OseeWrappedException {
      TransactionRecord destinationTransactionId;
      TransactionRecord sourceTransactionId;

      if (isHistorical) {
         destinationTransactionId = TransactionManager.getPriorTransaction(transactionId);
         sourceTransactionId = transactionId;
      } else {
         destinationTransactionId = TransactionManager.getLastTransaction(sourceBranch.getParentBranch());
         sourceTransactionId = TransactionManager.getLastTransaction(sourceBranch);
      }
      ChangeReportResponse response = HttpChangeDataRequester.getChanges(sourceTransactionId, destinationTransactionId, monitor, isHistorical);
      return response.getChangeItems();
   }
}
