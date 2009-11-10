/*
 * Created on Oct 13, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.model.CoverageImport;
import org.eclipse.osee.coverage.model.CoveragePackageBase;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.coverage.model.ICoverage;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.utility.UsersByIds;
import org.eclipse.osee.framework.ui.skynet.OseeImage;
import org.eclipse.osee.framework.ui.skynet.branch.BranchSelectionDialog;
import org.eclipse.swt.widgets.Listener;

/**
 * @author Donald G. Dunne
 */
public class CoverageUtil {

   private static Branch branch = null;
   private static List<Listener> branchChangeListeners = new ArrayList<Listener>();

   public static String getCoverageItemUsersStr(ICoverage coverageItem) {
      return Collections.toString(getCoverageItemUsers(coverageItem), "; ");
   }

   public static boolean getBranchFromUser(boolean force) throws OseeCoreException {
      if (force || CoverageUtil.getBranch() == null) {
         BranchSelectionDialog dialog = new BranchSelectionDialog("Select Branch", BranchManager.getBaselineBranches());
         if (dialog.open() != 0) {
            return false;
         }
         CoverageUtil.setBranch(dialog.getSelected());
      }
      return true;
   }

   public static Collection<User> getCoverageItemUsers(ICoverage coverageItem) {
      try {
         if (coverageItem.isAssignable() && Strings.isValid(coverageItem.getAssignees())) {
            return UsersByIds.getUsers(coverageItem.getAssignees());
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE, ex);
      }
      return java.util.Collections.emptyList();
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

   public static void getParentCoverageUnits(ICoverage coverage, Set<CoverageUnit> parents) {
      if (coverage.getParent() == null) return;
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

}
