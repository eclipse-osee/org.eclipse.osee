/*
 * Created on Nov 9, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
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
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.ChangeItem;
import org.eclipse.osee.framework.core.data.ChangeVersion;
import org.eclipse.osee.framework.core.data.TransactionRecord;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.core.operation.CompositeOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.database.core.SQL3DataType;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoad;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoader;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.change.ArtifactChange;
import org.eclipse.osee.framework.skynet.core.change.AttributeChange;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.change.ChangeType;
import org.eclipse.osee.framework.skynet.core.change.ErrorChange;
import org.eclipse.osee.framework.skynet.core.change.RelationChange;
import org.eclipse.osee.framework.skynet.core.commit.ArtifactChangeItem;
import org.eclipse.osee.framework.skynet.core.commit.AttributeChangeItem;
import org.eclipse.osee.framework.skynet.core.commit.ChangeItemUtil;
import org.eclipse.osee.framework.skynet.core.commit.ComputeNetChangeOperation;
import org.eclipse.osee.framework.skynet.core.commit.LoadChangeDataOperation;
import org.eclipse.osee.framework.skynet.core.commit.RelationChangeItem;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;

/**
 * @author Jeff C. Phillips
 */
public class ChangeReportDataLoader {

   public Collection<Change> getChanges(Branch sourceBranch, TransactionRecord transactionId, IProgressMonitor monitor) throws OseeCoreException {
      boolean isHistorical = sourceBranch == null;
      ArrayList<Change> changes = new ArrayList<Change>();
      List<ChangeItem> changeItems = loadChangeItems(sourceBranch, transactionId, monitor, isHistorical);
      @SuppressWarnings("unused")
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
      List<ChangeItem> changeItems = new ArrayList<ChangeItem>();
      List<IOperation> ops = new ArrayList<IOperation>();
      TransactionRecord destinationTransactionId;
      TransactionRecord sourceTransactionId;

      if (isHistorical) {
         destinationTransactionId = TransactionManager.getPriorTransaction(transactionId);
         sourceTransactionId = transactionId;
         ops.add(new LoadChangeDataOperation(sourceTransactionId.getId(), destinationTransactionId, changeItems));
      } else {
         destinationTransactionId = TransactionManager.getLastTransaction(sourceBranch.getParentBranch());
         sourceTransactionId = TransactionManager.getLastTransaction(sourceBranch);
         ops.add(new LoadChangeDataOperation(sourceTransactionId, destinationTransactionId, null, changeItems));
      }

      ops.add(new ComputeNetChangeOperation(changeItems));

      String opName =
            String.format("Gathering changes for %s",
                  sourceBranch != null ? sourceBranch.getShortName() : transactionId);
      IOperation op = new CompositeOperation(opName, Activator.PLUGIN_ID, ops);
      Operations.executeWork(op, monitor, -1);
      try {
         Operations.checkForErrorStatus(op.getStatus());
      } catch (Exception ex) {
         if (ex instanceof OseeCoreException) {
            throw (OseeCoreException) ex;
         } else {
            throw new OseeWrappedException(ex);
         }
      }
      return changeItems;
   }
}
