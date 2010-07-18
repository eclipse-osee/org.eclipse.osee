/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.coverage.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.merge.MatchItem;
import org.eclipse.osee.coverage.merge.MergeManager;
import org.eclipse.osee.coverage.model.CoverageImport;
import org.eclipse.osee.coverage.model.CoverageItem;
import org.eclipse.osee.coverage.model.CoverageOption;
import org.eclipse.osee.coverage.model.CoveragePackageBase;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.coverage.model.ICoverage;
import org.eclipse.osee.coverage.model.ICoverageItemProvider;
import org.eclipse.osee.coverage.model.ICoverageUnitProvider;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.SystemGroup;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.skynet.branch.BranchSelectionDialog;
import org.eclipse.osee.framework.ui.swt.KeyedImage;
import org.eclipse.swt.widgets.Listener;

/**
 * @author Donald G. Dunne
 */
public class CoverageUtil {

   private static Branch branch = null;
   private static List<Listener> branchChangeListeners = new ArrayList<Listener>();

   public static boolean getBranchFromUser(boolean force) throws OseeCoreException {
      if (force || CoverageUtil.getBranch() == null) {
         Collection<Branch> branches = BranchManager.getBranches(BranchArchivedState.UNARCHIVED, BranchType.WORKING);
         if (isAdmin()) {
            branches.add(BranchManager.getCommonBranch());
         }
         BranchSelectionDialog dialog = new BranchSelectionDialog("Select Branch", branches);
         if (dialog.open() != 0) {
            return false;
         }
         CoverageUtil.setNavigatorSelectedBranch(dialog.getSelected());
      }
      return true;
   }

   public static CoverageItem getCoverageItemMatchingOrder(Collection<? extends ICoverage> items, CoverageItem coverageItem) {
      for (ICoverage coverage : items) {
         if (coverage instanceof CoverageItem && ((CoverageItem) coverage).getOrderNumber().equals(
               coverageItem.getOrderNumber())) {
            return (CoverageItem) coverage;
         }
      }
      return null;
   }

   public static KeyedImage getCoveragePackageBaseImage(CoveragePackageBase coveragePackageBase) {
      if (coveragePackageBase instanceof CoverageImport) {
         return CoverageImage.COVERAGE_IMPORT;
      }
      return CoverageImage.COVERAGE_PACKAGE;
   }

   public static boolean isInTest() {
      return Boolean.valueOf(System.getProperty("osee.isInTest"));
   }

   public static void setIsInTest(boolean isInTest) {
      System.setProperty("osee.isInTest", String.valueOf(isInTest));
   }

   public static CoveragePackageBase getParentCoveragePackageBase(ICoverage coverage) {
      if (coverage.getParent() instanceof CoveragePackageBase) {
         return (CoveragePackageBase) coverage.getParent();
      }
      return getParentCoveragePackageBase(coverage.getParent());
   }

   public static CoverageUnit getTopLevelCoverageUnit(ICoverage coverage) {
      if (coverage instanceof CoverageUnit && coverage.getParent() instanceof CoveragePackageBase) {
         return (CoverageUnit) coverage;
      }
      return getTopLevelCoverageUnit(coverage.getParent());
   }

   public static Collection<ICoverage> getFirstNonFolderCoverageUnits(Collection<ICoverage> coverages) {
      Set<ICoverage> firstNonFolderCoverageUnits = new HashSet<ICoverage>();
      for (ICoverage coverage : coverages) {
         firstNonFolderCoverageUnits.add(CoverageUtil.getFirstNonFolderCoverageUnit(coverage));
      }
      return firstNonFolderCoverageUnits;
   }

   public static CoverageUnit getFirstNonFolderCoverageUnit(ICoverage coverage) {
      if (coverage instanceof CoverageUnit) {
         if (coverage.getParent() instanceof CoveragePackageBase) {
            return (CoverageUnit) coverage;
         }
         if (((CoverageUnit) coverage.getParent()).isFolder()) {
            return (CoverageUnit) coverage;
         }
      }
      ICoverage parentCovergeUnit = getFirstNonFolderCoverageUnit(coverage.getParent());
      if (parentCovergeUnit != null) {
         return (CoverageUnit) parentCovergeUnit;
      } else {
         return (CoverageUnit) coverage;
      }
   }

   public static void getParentCoverageUnits(ICoverage coverage, Set<CoverageUnit> parents) {
      if (coverage.getParent() == null) {
         return;
      }
      if (coverage.getParent() instanceof CoverageUnit) {
         parents.add((CoverageUnit) coverage.getParent());
         getParentCoverageUnits(coverage.getParent(), parents);
      }
   }

   public static Branch getBranch() {
      return branch;
   }

   public static void setNavigatorSelectedBranch(Branch branch) {
      CoverageUtil.branch = branch;
      for (Listener listener : branchChangeListeners) {
         listener.handleEvent(null);
      }
   }

   public static void addBranchChangeListener(Listener listener) {
      branchChangeListeners.add(listener);
   }

   public static boolean isAdmin() {
      try {
         return SystemGroup.OseeAdmin.isCurrentUserMember();
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         return false;
      }
   }

   /**
    * Returns string of all parent ICoverage items up the tree
    */
   public static String getFullPath(ICoverage coverage) {
      StringBuffer sb = new StringBuffer();
      getFullPathRecurse(coverage.getParent(), sb);
      return sb.toString();
   }

   public static String getFullPathWithName(ICoverage coverage) {
      StringBuffer sb = new StringBuffer();
      getFullPathRecurse(coverage.getParent(), sb);
      sb.append("[" + coverage.getName() + "]");
      return sb.toString();
   }

   public static void getFullPathRecurse(ICoverage coverage, StringBuffer sb) {
      if (coverage == null) {
         return;
      }
      getFullPathRecurse(coverage.getParent(), sb);
      if (coverage instanceof CoverageImport) {
         sb.append("[Import]");
      } else {
         sb.append("[" + coverage.getName() + "]");
      }
   }

   public static void printCoverageItemDiffs(CoveragePackageBase coveragePackage, CoveragePackageBase coverageImport) throws OseeStateException {
      for (CoverageItem importItem : coverageImport.getCoverageItems()) {
         MatchItem item = MergeManager.getPackageCoverageItem(coveragePackage, importItem);
         if (!item.isMatch()) {
            System.out.println(String.format("No Match for item [%s] path [%s]", importItem,
                  CoverageUtil.getFullPath(importItem)));
         }
      }
   }

   public static String printTree(ICoverage coverage) throws OseeStateException {
      StringBuffer sb = new StringBuffer();
      printTreeRecurse(coverage, sb, 0);
      return sb.toString();
   }

   public static void printTreeRecurse(ICoverage coverage, StringBuffer sb, int pad) throws OseeStateException {
      if (coverage == null) {
         return;
      }
      sb.append(Lib.getSpace(pad * 3) + coverage.toString() + "\n");
      if (coverage instanceof ICoverageUnitProvider) {
         for (CoverageUnit childCoverageUnit : ((ICoverageUnitProvider) coverage).getCoverageUnits()) {
            printTreeRecurse(childCoverageUnit, sb, pad + 1);
         }
      }
      if (coverage instanceof ICoverageItemProvider) {
         for (CoverageItem childCoverageItem : ((ICoverageItemProvider) coverage).getCoverageItems()) {
            printTreeRecurse(childCoverageItem, sb, pad + 1);
         }
      }
   }

   public static List<CoverageItem> getCoverageItemsCovered(List<CoverageItem> coverageItems, CoverageOption... CoverageOption) {
      List<CoverageOption> coverageMethods = Collections.getAggregate(CoverageOption);
      List<CoverageItem> items = new ArrayList<CoverageItem>();
      for (CoverageItem coverageItem : coverageItems) {
         if (coverageMethods.contains(coverageItem.getCoverageMethod())) {
            items.add(coverageItem);
         }
      }
      return items;
   }

   public static Pair<Double, String> getPercent(int complete, int total, boolean showZero) {
      if (total == 0 || complete == 0) {
         return new Pair<Double, String>(0.0, getPercentString(0, complete, total, showZero));
      }
      Double percent = new Double(complete);
      percent = percent / total;
      percent = percent * 100;
      return new Pair<Double, String>(percent, getPercentString(percent, complete, total, showZero));
   }

   public static String getPercentString(double percent, int complete, int total, boolean showZero) {
      if (!showZero && percent == 0.0 && complete == 0) {
         return "0%";
      }
      if (percent == 100.0) {
         return String.format("100%% - %d / %d", complete, total);
      }
      if (percent == 0.0) {
         return String.format("0%% - %d / %d", complete, total);
      }
      return String.format("%2.2f%% - %d / %d", percent, complete, total);
   }

   public static boolean isAllCoverageItems(Collection<? extends ICoverage> coverages) throws OseeCoreException {
      boolean coverageItemFound = false;
      boolean nonCoverageItemFound = false;
      for (ICoverage coverage : coverages) {
         if (coverage instanceof CoverageItem) {
            coverageItemFound = true;
            if (nonCoverageItemFound) {
               throw new OseeStateException("Coverages can only be all CoverageItem or all !CoverageItem");
            }
         } else {
            nonCoverageItemFound = true;
            if (coverageItemFound) {
               throw new OseeStateException("Coverages can only be all CoverageItem or all !CoverageItem");
            }
            return false;
         }
      }
      return true;
   }
}
