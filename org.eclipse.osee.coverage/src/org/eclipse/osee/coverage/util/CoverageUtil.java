/*
 * Created on Oct 13, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.util;

import java.util.Collection;
import org.eclipse.osee.coverage.editor.ICoverageEditorItem;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.utility.UsersByIds;

/**
 * @author Donald G. Dunne
 */
public class CoverageUtil {

   public static String getCoverageItemUsersStr(ICoverageEditorItem coverageItem) {
      return Collections.toString(getCoverageItemUsers(coverageItem), "; ");
   }

   public static Branch getBranch() throws OseeCoreException {
      return BranchManager.getCommonBranch();
   }

   public static Collection<User> getCoverageItemUsers(ICoverageEditorItem coverageItem) {
      try {
         if (coverageItem.isAssignable() && Strings.isValid(coverageItem.getAssignees())) {
            return UsersByIds.getUsers(coverageItem.getAssignees());
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE, ex);
      }
      return java.util.Collections.emptyList();
   }

}
