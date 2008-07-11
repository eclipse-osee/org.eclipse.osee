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

package org.eclipse.osee.framework.ui.skynet.branch;

import static org.eclipse.osee.framework.skynet.core.change.ModificationType.DELETED;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.SnapshotPersistenceManager;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactChangeListener;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ConflictingArtifactSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ISearchPrimitive;
import org.eclipse.osee.framework.skynet.core.change.ChangeType;
import org.eclipse.osee.framework.skynet.core.change.ModificationType;
import org.eclipse.osee.framework.skynet.core.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.exception.TransactionDoesNotExist;
import org.eclipse.osee.framework.skynet.core.revision.ArtifactChange;
import org.eclipse.osee.framework.skynet.core.revision.ArtifactNameDescriptorCache;
import org.eclipse.osee.framework.skynet.core.revision.AttributeChange;
import org.eclipse.osee.framework.skynet.core.revision.AttributeSummary;
import org.eclipse.osee.framework.skynet.core.revision.ChangeReportInput;
import org.eclipse.osee.framework.skynet.core.revision.ChangeSummary;
import org.eclipse.osee.framework.skynet.core.revision.RelationLinkChange;
import org.eclipse.osee.framework.skynet.core.revision.RelationLinkSummary;
import org.eclipse.osee.framework.skynet.core.revision.RevisionChange;
import org.eclipse.osee.framework.skynet.core.revision.RevisionManager;
import org.eclipse.osee.framework.skynet.core.revision.TransactionData;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;
import org.eclipse.osee.framework.ui.plugin.util.JobbedNode;
import org.eclipse.osee.framework.ui.swt.IContentProviderRunnable;
import org.eclipse.osee.framework.ui.swt.ITreeNode;
import org.eclipse.osee.framework.ui.swt.TreeNode;

/**
 * @author Jeff C. Phillips
 * @author Robert A. Fisher
 */
public class BranchContentProvider implements ITreeContentProvider, ArtifactChangeListener {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(BranchContentProvider.class);
   private static final ArtifactPersistenceManager artifactManager = ArtifactPersistenceManager.getInstance();
   private static final SnapshotPersistenceManager snapshotManager = SnapshotPersistenceManager.getInstance();
   private static final RevisionManager revisionManager = RevisionManager.getInstance();
   private static final TransactionIdManager transactionIdManager = TransactionIdManager.getInstance();
   private static final Object[] EMPTY_ARRAY = new Object[0];
   private static final String EMPTY_REPORT = "No changes";
   private static final Object[] EMPTY_REPORT_CHILDREN = new Object[] {EMPTY_REPORT};
   private static final String NAMESPACE = "Branch Change Reports";

   private static ArtifactNameDescriptorCache artifactNameDescriptorCache = new ArtifactNameDescriptorCache();
   private JobbedNode root;
   private IContentProviderRunnable providerRunnable;
   private boolean showChildBranchesAtMainLevel;
   private boolean showChildBranchesUnderParents;
   private boolean showTransactions;

   //   private boolean myHideTransactions = false;

   public BranchContentProvider() {
      this.providerRunnable = new ChildrenRunnable();
      this.root = null;
      this.showChildBranchesAtMainLevel = false;
      this.showChildBranchesUnderParents = true;
   }

   /**
    * @return Returns the children of the parent element or null if there are none.
    */
   public Object[] getChildren(Object parentElement) {
      if (parentElement instanceof ITreeNode)
         return ((ITreeNode) parentElement).getChildren();
      else
         return EMPTY_ARRAY;
   }

   /**
    * @return Returns the elements.
    */
   public Object[] getElements(Object inputElement) {
      return root.getChildren();
   }

   public void setShowTransactions(boolean showTransactions) {
      this.showTransactions = showTransactions;
   }

   private final class ChildrenRunnable implements IContentProviderRunnable {

      @SuppressWarnings("unchecked")
      public Object[] run(Object parentElement) throws Exception {
         if (parentElement instanceof BranchPersistenceManager) {
            try {
               Collection<Branch> branches = BranchPersistenceManager.getBranches();
               Iterator<Branch> iter = branches.iterator();

               while (iter.hasNext()) {
                  Branch branch = iter.next();

                  if ((!showChildBranchesAtMainLevel && branch.getParentBranch() != null) || (!OseeProperties.isDeveloper() && branch.isMergeBranch())) {
                     iter.remove();
                  }
               }
               return branches.toArray();
            } catch (SQLException ex) {
               logger.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
            }
         } else if (parentElement instanceof Branch) {
            Branch branch = (Branch) parentElement;
            if (showChildBranchesUnderParents) {
               List<Object> items = new LinkedList<Object>();
               items.addAll(branch.getChildBranches());
               items.addAll(getTransactions(branch));
               return items.toArray();
            } else {
               return getTransactions(branch).toArray();
            }
         } else if (parentElement instanceof TransactionData) {
            TransactionId tranId = ((TransactionData) parentElement).getTransactionId();
            return getArtifactChanges(tranId);
         } else if (parentElement instanceof Pair) {
            Pair pair = (Pair) parentElement;
            if (pair.getKey() instanceof TransactionId && pair.getValue() instanceof TransactionId) {
               return getArtifactChanges(null, (TransactionId) pair.getKey(), (TransactionId) pair.getValue());
            }
         } else if (parentElement instanceof ChangeReportInput) {
            ChangeReportInput input = (ChangeReportInput) parentElement;
            return handleBranchChangeReportRequest(input);
         } else if (parentElement instanceof ArtifactChange) {
            ArtifactChange change = (ArtifactChange) parentElement;
            if (change.getModType() != DELETED) {
               return summarize(revisionManager.getTransactionChanges(change, artifactNameDescriptorCache)).toArray();
            }
         } else if (parentElement instanceof ChangeSummary) {
            ChangeSummary change = (ChangeSummary) parentElement;
            return change.getChanges().toArray();
         } else if (parentElement instanceof Collection) {
            Collection collection = (Collection) parentElement;
            return collection.toArray();
         }
         return EMPTY_ARRAY;
      }

      private Collection<Object> getTransactions(Branch branch) throws OseeCoreException, SQLException {
         if (!showTransactions) return Collections.emptyList();
         List<TransactionData> transactions = revisionManager.getTransactionsPerBranch(branch);
         Collections.sort(transactions, new Comparator<TransactionData>() {
            public int compare(TransactionData o1, TransactionData o2) {
               return o1.getTransactionNumber() - o2.getTransactionNumber();
            }
         });
         if (transactions != null) {
            return org.eclipse.osee.framework.jdk.core.util.Collections.getAggregateTree(new ArrayList<Object>(
                  transactions), 100);
         } else {
            return Collections.emptyList();
         }
      }
   }

   // This is synchronized so that successive calls to refresh will result in just one refresh. This
   // will happen since
   // the first job will shut off the forceRefresh flag on the input before the other jobs come in,
   // so they will just
   // use the snapshot that the first concurrently running job produced.
   @SuppressWarnings("unchecked")
   private synchronized Object[] handleBranchChangeReportRequest(ChangeReportInput input) throws OseeCoreException, SQLException {
      String key = calculateKey(input);
      Object[] changeReport = null;
      Date changeTime = null;
      Pair<Object, Date> snapshotData = null;
      Pair<ChangeReportInput, Object[]> reportData;
      ChangeReportInput oldInput = null;

      try {
         if (!input.isForceRefresh()) {
            snapshotData = snapshotManager.getSnapshot(NAMESPACE, key);
         }

         // Check that the snapshot is the correct format, inherently performs null checking via
         // instanceof
         if (!(snapshotData instanceof Pair && snapshotData.getKey() instanceof Pair && ((Pair) snapshotData.getKey()).getKey() instanceof ChangeReportInput && ((Pair) snapshotData.getKey()).getValue() instanceof Object[])) {
            changeReport = computeChangeReport(input);

            snapshotManager.persistSnapshot(NAMESPACE, key, new Pair<ChangeReportInput, Object[]>(input, changeReport));

            changeTime = new Date();
            input.setForceRefresh(false);
            oldInput = input;
         } else {
            reportData = (Pair<ChangeReportInput, Object[]>) snapshotData.getKey();
            changeReport = reportData.getValue();
            changeTime = snapshotData.getValue();
            oldInput = reportData.getKey();
         }

         ITreeNode node = new TreeNode(new SnapshotDescription(oldInput, input, changeTime));
         node.setChildren(changeReport);
         changeReport = new Object[] {node};
      } catch (BranchDoesNotExist ex) {
         logger.log(Level.SEVERE, ex.toString(), ex);
      }

      return changeReport;
   }

   private String calculateKey(ChangeReportInput input) {
      if (input.getBaseParentTransactionId() == null)
         return "Transaction " + input.getBaseTransaction() + " to " + input.getToTransaction();
      else
         return "Branch " + input.getBaseTransaction().getBranch().getBranchId();
   }

   @SuppressWarnings("unchecked")
   private Object[] computeChangeReport(ChangeReportInput input) throws OseeCoreException, SQLException {
      Object[] items;
      if (input.isEmptyChange()) {
         items = EMPTY_REPORT_CHILDREN;
      } else {

         // Get a new cache with a backup mechanism to the parent if it is available
         if (input.getBaseParentTransactionId() != null) {
            artifactNameDescriptorCache =
                  new ArtifactNameDescriptorCache(input.getBaseParentTransactionId().getBranch());
         }

         items = getArtifactChanges(input);

         Object childData;
         for (int index = 0; index < items.length; index++) {
            ITreeNode node = new TreeNode(items[index]);
            TreeNode.fillNode(node, providerRunnable);
            items[index] = node;

            for (Object childObj : node.getChildren()) {
               childData = ((TreeNode) childObj).getBackingData();
               if (childData instanceof ChangeSummary && ((ChangeSummary) childData).isConflicted()) {
                  ((ArtifactChange) node.getBackingData()).setChangeType(ChangeType.CONFLICTING);
                  break;
               }
            }
         }
      }

      return items;
   }

   public static Object[] getArtifactChanges(ChangeReportInput input) throws OseeCoreException, SQLException {
      return getArtifactChanges(input.getBaseParentTransactionId(), input.getBaseTransaction(),
            input.getToTransaction());
   }

   private static Object[] getArtifactChanges(TransactionId toTransaction) throws OseeCoreException, SQLException {
      TransactionId priorTransaction;
      try {
         priorTransaction = transactionIdManager.getPriorTransaction(toTransaction);
      } catch (TransactionDoesNotExist ex) {
         priorTransaction = null;
      }
      return getArtifactChanges(null, priorTransaction, toTransaction);
   }

   private static Object[] getArtifactChanges(TransactionId baseParentTransaction, TransactionId baseTransaction, TransactionId toTransaction) throws OseeCoreException, SQLException {
      TransactionId headParentTransaction =
            baseParentTransaction == null ? null : transactionIdManager.getStartEndPoint(
                  baseParentTransaction.getBranch()).getValue();

      Collection<ArtifactChange> deletedArtChanges =
            revisionManager.getDeletedArtifactChanges(baseParentTransaction, headParentTransaction, baseTransaction,
                  toTransaction, artifactNameDescriptorCache);
      Collection<ArtifactChange> newAndModArtChanges =
            revisionManager.getNewAndModArtifactChanges(baseParentTransaction, headParentTransaction, baseTransaction,
                  toTransaction, artifactNameDescriptorCache);

      // Combine both the collections into one of them to return one continuous data set
      newAndModArtChanges.addAll(deletedArtChanges);

      // Perform conflict detection if a baseParentTransaction is available.
      if (baseParentTransaction != null) {

         // If the baseline hasn't moved, then we don't care to check for conflicts.
         // Note that attempting to do conflict detection will be inaccurate as the search
         // API will include the lower bound when the lower and upper bound match, which
         // is not what we want.
         if (baseParentTransaction != headParentTransaction) {
            Map<Integer, Artifact> parentBranchModConflicts = new HashMap<Integer, Artifact>();
            Collection<Integer> parentBranchDelConflicts = new HashSet<Integer>();

            ISearchPrimitive conflictCriteria =
                  new ConflictingArtifactSearch(baseParentTransaction.getBranch().getBranchId(),
                        baseParentTransaction.getTransactionNumber(), headParentTransaction.getTransactionNumber(),
                        baseTransaction.getBranch().getBranchId(), baseTransaction.getTransactionNumber(),
                        toTransaction.getTransactionNumber());

            Collection<Artifact> artModConflicts =
                  artifactManager.getArtifacts(conflictCriteria, headParentTransaction.getBranch());
            for (Artifact artifact : artModConflicts)
               parentBranchModConflicts.put(artifact.getArtId(), artifact);

            Collection<ArtifactChange> artDelConflicts =
                  revisionManager.getDeletedArtifactChanges(null, null, baseParentTransaction, headParentTransaction,
                        null);
            for (ArtifactChange change : artDelConflicts) {
               parentBranchDelConflicts.add(change.getArtifact().getArtId());
            }

            for (ArtifactChange change : newAndModArtChanges) {
               if (parentBranchDelConflicts.contains(change.getArtifact().getArtId())) {
                  change.setChangeType(ChangeType.CONFLICTING);
                  change.setConflictingModArtifact(change.getArtifact());
               } else if (parentBranchModConflicts.containsKey(change.getArtifact().getArtId())) {
                  change.setConflictingModArtifact(parentBranchModConflicts.get(change.getArtifact().getArtId()));
               }
            }
         }
      }

      return newAndModArtChanges.toArray();
   }

   /**
    * @return Returns the parent element.
    */
   public Object getParent(Object element) {
      return null;
   }

   public boolean hasChildren(Object element) {
      if (element instanceof ITreeNode) element = ((ITreeNode) element).getBackingData();
      if (element == EMPTY_REPORT) return false;

      if (element instanceof TransactionData) {
         TransactionData data = (TransactionData) element;

         if (data.getComment() != null && data.getComment().contains(BranchPersistenceManager.NEW_BRANCH_COMMENT)) return false;
      }

      return ((element instanceof Branch && AccessControlManager.checkObjectPermission(element, PermissionEnum.READ)) || element instanceof TransactionData || element instanceof Pair || element instanceof SnapshotDescription || element instanceof ChangeSummary || element instanceof Collection || (element instanceof ArtifactChange && ((ArtifactChange) element).getModType() != DELETED));
   }

   public void dispose() {

   }

   public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
      root = new JobbedNode(newInput, viewer, providerRunnable);
   }

   public void refresh() {
      if (root != null) {
         root.refresh(false);
      }
   }

   public void refresh(boolean expand) {
      if (root != null) {
         root.refresh(expand);
      }
   }

   public static Collection<Object> summarize(Collection<RevisionChange> changes) throws IllegalArgumentException, SQLException {
      Collection<Object> summary = new LinkedList<Object>();
      HashCollection<Integer, AttributeChange> attrChanges = new HashCollection<Integer, AttributeChange>();
      HashCollection<Integer, RelationLinkChange> linkChanges = new HashCollection<Integer, RelationLinkChange>();

      // Aggregate all of the data
      for (RevisionChange change : changes) {
         if (change instanceof AttributeChange) {
            attrChanges.put(((AttributeChange) change).getAttrId(), (AttributeChange) change);
         } else if (change instanceof RelationLinkChange) {
            linkChanges.put(((RelationLinkChange) change).getRelLinkId(), (RelationLinkChange) change);
         } else if (change instanceof ArtifactChange) {
            ArtifactChange artifactChange = (ArtifactChange) change;

            if (artifactChange.getModType() == ModificationType.DELETED) {
               summary.add(artifactChange);
            }
         } else {
            throw new IllegalArgumentException(
                  "changes must be of type AttributeChange or RelationLinkChange, not " + change.getClass().getSimpleName());
         }
      }

      for (Integer attrId : attrChanges.keySet()) {
         Collection<AttributeChange> attrChangeSet = attrChanges.getValues(attrId);
         if (attrChangeSet.size() == 1)
            summary.add(attrChangeSet.iterator().next());
         else
            summary.add(new AttributeSummary(attrChangeSet));
      }

      for (Integer linkId : linkChanges.keySet()) {
         Collection<RelationLinkChange> linkChangeSet = linkChanges.getValues(linkId);
         if (linkChangeSet.size() == 1)
            summary.add(linkChangeSet.iterator().next());
         else
            summary.add(new RelationLinkSummary(linkChangeSet));
      }

      return summary;
   }

   /**
    * @return the showChildBranchesAtMainLevel
    */
   public boolean isShowChildBranchesAtMainLevel() {
      return showChildBranchesAtMainLevel;
   }

   /**
    * @param showChildBranchesAtMainLevel the showChildBranchesAtMainLevel to set
    */
   public void setShowChildBranchesAtMainLevel(boolean showChildBranchesAtMainLevel) {
      this.showChildBranchesAtMainLevel = showChildBranchesAtMainLevel;
   }

   /**
    * @return the showChildBranchesUnderParents
    */
   public boolean isShowChildBranchesUnderParents() {
      return showChildBranchesUnderParents;
   }

   /**
    * @param showChildBranchesUnderParents the showChildBranchesUnderParents to set
    */
   public void setShowChildBranchesUnderParents(boolean showChildBranchesUnderParents) {
      this.showChildBranchesUnderParents = showChildBranchesUnderParents;
   }

}
