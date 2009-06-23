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
package org.eclipse.osee.framework.ui.skynet.widgets.xBranch;

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
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchArchivedState;
import org.eclipse.osee.framework.skynet.core.artifact.BranchControlled;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;

/**
 * @author Jeff C. Phillips
 */
public class XBranchContentProvider implements ITreeContentProvider {

   /**
    * @param showOnlyWorkingBranches the showOnlyWorkingBranches to set
    */
   public void setShowOnlyWorkingBranches(boolean showOnlyWorkingBranches) {
      this.showOnlyWorkingBranches = showOnlyWorkingBranches;
   }

   private final BranchXViewer changeXViewer;
   private boolean showChildBranchesAtMainLevel;
   private boolean showMergeBranches;
   private boolean showArchivedBranches;
   private boolean showTransactions;
   private boolean showChildBranchesUnderParents;
   private boolean showOnlyWorkingBranches;

   private static Object[] EMPTY_ARRAY = new Object[0];

   public XBranchContentProvider(BranchXViewer commitXViewer) {
      super();

      this.changeXViewer = commitXViewer;
      this.showChildBranchesAtMainLevel = false;
      this.showMergeBranches = false;
      this.showTransactions = false;
      this.showChildBranchesUnderParents = false;
      this.showArchivedBranches = false;
   }

   public Object[] getChildren(Object parentElement) {
      if (parentElement instanceof BranchManager) {
         return getBranchManagerChildren((BranchManager) parentElement);

      } else if (parentElement instanceof Branch) {
         return getBranchChildren((Branch) parentElement);

      } else if (parentElement instanceof Collection) {
         return ((Collection<?>) parentElement).toArray();
      }
      return EMPTY_ARRAY;
   }

   private Object[] getBranchChildren(Branch branch) {
      try {
         if (showChildBranchesUnderParents) {
            List<Object> items = new LinkedList<Object>();
            Collection<Branch> childBrances =
                  showArchivedBranches ? branch.getDescendants() : branch.getChildBranches();

            items.addAll(childBrances);
            items.addAll(getTransactions(branch));

            return items.toArray();
         } else {
            return getTransactions(branch).toArray();
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(this.getClass(), Level.WARNING, ex);
      }
      return EMPTY_ARRAY;
   }

   private Object[] getBranchManagerChildren(BranchManager branchManager) {
      BranchArchivedState branchState = BranchArchivedState.UNARCHIVED;
      List<BranchType> branchTypes = new ArrayList<BranchType>(4);

      try {
         if (AccessControlManager.isOseeAdmin()) {
            branchTypes.add(BranchType.SYSTEM_ROOT);
         } else {
            branchTypes.add(BranchType.TOP_LEVEL);
         }

         if (AccessControlManager.isOseeAdmin() && showMergeBranches) {
            branchTypes.add(BranchType.MERGE);
         }
         if (AccessControlManager.isOseeAdmin() && showArchivedBranches) {
            branchState = BranchArchivedState.ALL;
         }
         if (showChildBranchesAtMainLevel) {
            branchTypes.add(BranchType.TOP_LEVEL);
            branchTypes.add(BranchType.BASELINE);
            branchTypes.add(BranchType.WORKING);
         }

         List<Branch> branchesToReturn = new ArrayList<Branch>();
         if (showOnlyWorkingBranches) {
            branchesToReturn.addAll(BranchManager.getBranches(BranchArchivedState.UNARCHIVED, BranchControlled.ALL,
                  BranchType.WORKING));
         } else {
            branchesToReturn.addAll(BranchManager.getBranches(branchState, BranchControlled.ALL,
                  branchTypes.toArray(new BranchType[branchTypes.size()])));
         }
         return branchesToReturn.toArray();
      } catch (OseeCoreException ex) {
         OseeLog.log(this.getClass(), Level.WARNING, ex);
      }
      return EMPTY_ARRAY;
   }

   public Object getParent(Object element) {
      return null;
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

   public boolean hasChildren(Object element) {
      if (element instanceof BranchManager) return true;
      if (element instanceof Branch) {
         boolean hasChildren = true;

         if (!showTransactions) {
            try {
               if (!showChildBranchesAtMainLevel) {
                  hasChildren =
                        showArchivedBranches ? !((Branch) element).getDescendants().isEmpty() : !((Branch) element).getChildBranches().isEmpty();
               } else {
                  hasChildren = false;
               }
            } catch (OseeCoreException ex) {
               OseeLog.log(this.getClass(), Level.WARNING, ex);
            }
         }
         return hasChildren;
      }
      if (element instanceof Collection) return true;
      return false;
   }

   public Object[] getElements(Object inputElement) {
      return getChildren(inputElement);
   }

   public Object[] getAllElements(Object inputElement) {
      ArrayList<Object> objects = new ArrayList<Object>();

      objects.addAll(recurseAllElements(inputElement));

      for (Object object : recurseAllElements(inputElement)) {
         objects.addAll(recurseAllElements(object));
      }

      return objects.toArray();
   }

   private ArrayList<Object> recurseAllElements(Object inputElement) {
      ArrayList<Object> objects = new ArrayList<Object>();

      for (Object object : getChildren(inputElement)) {
         objects.add(object);
      }

      return objects;
   }

   public void dispose() {
   }

   public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
   }

   /**
    * @return the changeXViewer
    */
   public BranchXViewer getChangeXViewer() {
      return changeXViewer;
   }

   /**
    * @param flat
    */
   public void setPresentation(boolean flat) {
      showChildBranchesAtMainLevel = flat;
      showChildBranchesUnderParents = !flat;
   }

   /**
    * @param showMergeBranches
    */
   public void setShowMergeBranches(boolean showMergeBranches) {
      this.showMergeBranches = showMergeBranches;
   }

   /**
    * @param showArchivedBranches
    */
   public void setShowArchivedBranches(boolean showArchivedBranches) {
      this.showArchivedBranches = showArchivedBranches;
   }

   /**
    * @param showTransactions2
    */
   public void setShowTransactions(boolean showTransactions) {
      this.showTransactions = showTransactions;
   }
}
