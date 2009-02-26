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

import static org.eclipse.osee.framework.core.enums.ModificationType.DELETED;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactChangeListener;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchControlled;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchState;
import org.eclipse.osee.framework.skynet.core.revision.ArtifactChange;
import org.eclipse.osee.framework.skynet.core.revision.ArtifactNameDescriptorCache;
import org.eclipse.osee.framework.skynet.core.revision.AttributeChange;
import org.eclipse.osee.framework.skynet.core.revision.AttributeSummary;
import org.eclipse.osee.framework.skynet.core.revision.ChangeSummary;
import org.eclipse.osee.framework.skynet.core.revision.RelationLinkChange;
import org.eclipse.osee.framework.skynet.core.revision.RelationLinkSummary;
import org.eclipse.osee.framework.skynet.core.revision.RevisionChange;
import org.eclipse.osee.framework.skynet.core.revision.TransactionData;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;
import org.eclipse.osee.framework.ui.plugin.util.JobbedNode;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.swt.IContentProviderRunnable;
import org.eclipse.osee.framework.ui.swt.ITreeNode;

/**
 * @author Jeff C. Phillips
 * @author Robert A. Fisher
 */
public class BranchContentProvider implements ITreeContentProvider, ArtifactChangeListener {
   private static final Object[] EMPTY_ARRAY = new Object[0];
   private static final String EMPTY_REPORT = "No changes";
   private static ArtifactNameDescriptorCache artifactNameDescriptorCache = new ArtifactNameDescriptorCache();
   private JobbedNode root;
   private IContentProviderRunnable providerRunnable;
   private boolean showChildBranchesAtMainLevel;
   private boolean showChildBranchesUnderParents;
   private boolean showTransactions;
   private boolean showMergeBranches;

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

   public void setShowMergeBranches(boolean showMergeBranches) {
      this.showMergeBranches = showMergeBranches;
   }

   private final class ChildrenRunnable implements IContentProviderRunnable {

      @SuppressWarnings("unchecked")
      public Object[] run(Object parentElement) throws Exception {
         if (parentElement instanceof BranchManager) {
            List<BranchType> branchTypes = new ArrayList<BranchType>(4);
            branchTypes.add(BranchType.TOP_LEVEL);
            if (AccessControlManager.isOseeAdmin() && showMergeBranches) {
               branchTypes.add(BranchType.MERGE);
            }

            if (showChildBranchesAtMainLevel) {
               branchTypes.add(BranchType.BASELINE);
               branchTypes.add(BranchType.WORKING);
            }

            List<Branch> branches =
                  BranchManager.getBranches(BranchState.ACTIVE, BranchControlled.ALL,
                        branchTypes.toArray(new BranchType[branchTypes.size()]));
            return branches.toArray();
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
         } else if (parentElement instanceof Collection) {
            Collection collection = (Collection) parentElement;
            return collection.toArray();
         }
         return EMPTY_ARRAY;
      }

      private Collection<Object> getTransactions(Branch branch) throws OseeCoreException {
         if (!showTransactions) return Collections.emptyList();
         List<TransactionId> transactions = TransactionIdManager.getTransactionsForBranch(branch);
         Collections.sort(transactions, new Comparator<TransactionId>() {
            public int compare(TransactionId o1, TransactionId o2) {
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

         if (data.getComment() != null && data.getComment().contains(BranchManager.NEW_BRANCH_COMMENT)) return false;
      }
      if (element instanceof Branch) {
         Branch branch = (Branch) element;

         try {
            boolean readable = AccessControlManager.checkObjectPermission(branch, PermissionEnum.READ);
            return readable && (showTransactions || (!branch.getChildBranches().isEmpty() && showChildBranchesUnderParents));
         } catch (OseeCoreException ex) {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
            return false;
         }
      }

      return (element instanceof TransactionData || element instanceof Pair || element instanceof ChangeSummary || element instanceof Collection || (element instanceof ArtifactChange && ((ArtifactChange) element).getModType() != DELETED));
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

   public static Collection<Object> summarize(Collection<RevisionChange> changes) throws IllegalArgumentException {
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
