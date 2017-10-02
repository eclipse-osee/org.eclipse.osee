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

import static org.eclipse.osee.framework.core.enums.CoreBranches.SYSTEM_ROOT;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.cache.BranchFilter;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.skynet.change.ChangeUiUtil;

/**
 * @author Jeff C. Phillips
 */
public class XBranchContentProvider implements ITreeContentProvider {

   private final BranchXViewer changeXViewer;
   private boolean showChildBranchesAtMainLevel;
   private boolean showMergeBranches;
   private boolean showArchivedBranches;
   private boolean showTransactions;
   private boolean showChildBranchesUnderParents;
   private boolean showOnlyWorkingBranches;

   private static Object[] EMPTY_ARRAY = new Object[0];
   private int maxPerList = 100;

   public XBranchContentProvider(BranchXViewer commitXViewer) {
      super();

      changeXViewer = commitXViewer;
      showChildBranchesAtMainLevel = false;
      showMergeBranches = false;
      showTransactions = false;
      showChildBranchesUnderParents = false;
      showArchivedBranches = false;
   }

   @Override
   public Object[] getChildren(Object parentElement) {
      if (parentElement instanceof BranchId) {
         if (!ChangeUiUtil.permissionsDeniedWithDialog((BranchId) parentElement)) {
            return getBranchChildren((BranchId) parentElement);
         }
      } else if (parentElement instanceof Collection<?>) {
         return ((Collection<?>) parentElement).toArray();
      } else if (parentElement instanceof Object[]) {
         Object[] objects = (Object[]) parentElement;
         if (objects.length == 1) {
            return getBranchManagerChildren();
         } else {
            return (Object[]) parentElement;
         }
      }
      return EMPTY_ARRAY;
   }

   public Object[] getBranchChildren(BranchId branch) {
      try {
         if (showChildBranchesUnderParents) {
            List<Object> items = new LinkedList<>();
            Collection<? extends BranchId> childBrances = BranchManager.getChildBranches(branch, showArchivedBranches);
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

   protected Object[] getBranchManagerChildren() {
      BranchArchivedState branchState = BranchArchivedState.UNARCHIVED;
      List<BranchType> branchTypes = new ArrayList<>(4);

      try {
         boolean isAdmin = AccessControlManager.isOseeAdmin();
         if (isAdmin) {
            branchTypes.add(BranchType.SYSTEM_ROOT);
         }

         if (isAdmin && showMergeBranches) {
            branchTypes.add(BranchType.MERGE);
         }
         if (isAdmin && showArchivedBranches) {
            branchState = BranchArchivedState.ALL;
         }
         if (showChildBranchesAtMainLevel) {
            branchTypes.add(BranchType.BASELINE);
            branchTypes.add(BranchType.WORKING);
         }

         Set<BranchId> branchesToReturn = new HashSet<>();
         if (showOnlyWorkingBranches) {
            branchesToReturn.addAll(BranchManager.getBranches(BranchArchivedState.UNARCHIVED, BranchType.WORKING));
         }
         if (!showChildBranchesAtMainLevel) {
            if (AccessControlManager.isOseeAdmin()) {
               branchesToReturn.add(SYSTEM_ROOT);
            } else {
               branchTypes.add(BranchType.BASELINE);
               BranchFilter filter =
                  new BranchFilter(branchState, branchTypes.toArray(new BranchType[branchTypes.size()]));

               for (BranchId branch : BranchManager.getBranches(filter)) {
                  if (BranchManager.isParentSystemRoot(branch)) {
                     branchesToReturn.add(branch);
                  }
               }
            }
         } else {
            branchesToReturn.addAll(
               BranchManager.getBranches(branchState, branchTypes.toArray(new BranchType[branchTypes.size()])));
         }
         return branchesToReturn.toArray();
      } catch (OseeCoreException ex) {
         OseeLog.log(this.getClass(), Level.WARNING, ex);
      }
      return EMPTY_ARRAY;
   }

   @Override
   public Object getParent(Object element) {
      return null;
   }

   private Collection<Object> getTransactions(BranchId branch) {
      if (!showTransactions) {
         return Collections.emptyList();
      }
      List<TransactionRecord> transactions = TransactionManager.getTransactionsForBranch(branch);
      Collections.sort(transactions, new Comparator<TransactionRecord>() {
         @Override
         public int compare(TransactionRecord o1, TransactionRecord o2) {
            return (int) (o1.getId().longValue() - o2.getId().longValue());
         }
      });
      if (transactions != null) {
         return org.eclipse.osee.framework.jdk.core.util.Collections.getAggregateTree(
            new ArrayList<Object>(transactions), maxPerList);
      } else {
         return Collections.emptyList();
      }
   }

   @Override
   public boolean hasChildren(Object element) {
      if (element instanceof BranchManager) {
         return true;
      }
      if (element instanceof BranchId) {
         boolean hasChildren = true;
         try {
            if (!showTransactions) {
               if (!showChildBranchesAtMainLevel) {
                  hasChildren = BranchManager.hasChildren((BranchId) element);
               } else {
                  hasChildren = false;
               }
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(this.getClass(), Level.WARNING, ex);
         }
         return hasChildren;
      }
      if (element instanceof Collection<?>) {
         return true;
      }
      return false;
   }

   @Override
   public Object[] getElements(Object inputElement) {
      return getChildren(inputElement);
   }

   public Object[] getAllElements(Object inputElement) {
      ArrayList<Object> objects = new ArrayList<>();

      objects.addAll(recurseAllElements(inputElement));

      for (Object object : recurseAllElements(inputElement)) {
         objects.addAll(recurseAllElements(object));
      }

      return objects.toArray();
   }

   private ArrayList<Object> recurseAllElements(Object inputElement) {
      ArrayList<Object> objects = new ArrayList<>();

      for (Object object : getChildren(inputElement)) {
         objects.add(object);
      }

      return objects;
   }

   @Override
   public void dispose() {
      // do nothing
   }

   @Override
   public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
      // do nothing
   }

   public BranchXViewer getChangeXViewer() {
      return changeXViewer;
   }

   public void setPresentation(boolean flat) {
      showChildBranchesAtMainLevel = flat;
      showChildBranchesUnderParents = !flat;
   }

   //TODO: get rid of this and replace with hashset<branchoptenum, bool>
   /**
    * @param showOnlyWorkingBranches the showOnlyWorkingBranches to set
    */
   public void setShowOnlyWorkingBranches(boolean showOnlyWorkingBranches) {
      this.showOnlyWorkingBranches = showOnlyWorkingBranches;
   }

   public void setShowMergeBranches(boolean showMergeBranches) {
      this.showMergeBranches = showMergeBranches;
   }

   public void setShowArchivedBranches(boolean showArchivedBranches) {
      this.showArchivedBranches = showArchivedBranches;
   }

   public void setShowTransactions(boolean showTransactions) {
      this.showTransactions = showTransactions;
   }

   public boolean isShowTransactions() {
      return showTransactions;
   }

   public void setMaxPerList(int maxPerList) {
      this.maxPerList = maxPerList;
   }

}
