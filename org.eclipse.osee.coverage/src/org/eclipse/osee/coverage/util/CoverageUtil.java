/*
 * Created on Oct 13, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
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
import org.eclipse.osee.framework.core.enums.BranchControlled;
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
import org.eclipse.osee.framework.ui.skynet.OseeImage;
import org.eclipse.osee.framework.ui.skynet.branch.BranchSelectionDialog;
import org.eclipse.swt.widgets.Listener;

/**
 * @author Donald G. Dunne
 */
public class CoverageUtil {

   private static Branch branch = null;
   private static List<Listener> branchChangeListeners = new ArrayList<Listener>();

   public static boolean getBranchFromUser(boolean force) throws OseeCoreException {
      if (force || CoverageUtil.getBranch() == null) {
         Collection<Branch> branches =
               BranchManager.getBranches(BranchArchivedState.UNARCHIVED, BranchControlled.ALL, BranchType.WORKING);
         if (isAdmin()) {
            branches.add(BranchManager.getCommonBranch());
         }
         BranchSelectionDialog dialog = new BranchSelectionDialog("Select Branch", branches);
         if (dialog.open() != 0) {
            return false;
         }
         CoverageUtil.setBranch(dialog.getSelected());
      }
      return true;
   }

   public static OseeImage getCoveragePackageBaseImage(CoveragePackageBase coveragePackageBase) {
      if (coveragePackageBase instanceof CoverageImport) {
         return CoverageImage.COVERAGE_IMPORT;
      }
      return CoverageImage.COVERAGE_PACKAGE;
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

   public static void setBranch(Branch branch) {
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

   public static void getFullPathRecurse(ICoverage coverage, StringBuffer sb) {
      if (coverage == null) return;
      sb.append("[" + coverage.getName() + "]");
      getFullPathRecurse(coverage.getParent(), sb);
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
      if (coverage == null) return;
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

   public static Pair<Integer, String> getPercent(int complete, int total, boolean showZero) {
      if (total == 0 || complete == 0) return new Pair<Integer, String>(0, getPercentString(0, complete, total,
            showZero));
      Double percent = new Double(complete);
      percent = percent / total;
      percent = percent * 100;
      return new Pair<Integer, String>(percent.intValue(), getPercentString(percent.intValue(), complete, total,
            showZero));
   }

   public static String getPercentString(int percent, int complete, int total, boolean showZero) {
      if (!showZero && percent == 0) {
         return "0%";
      }
      return String.format("%d%% %d/%d", percent, complete, total);
   }

}
