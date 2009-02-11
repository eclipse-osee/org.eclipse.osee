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
package org.eclipse.osee.ats.test.testDb;

import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Level;
import junit.framework.TestCase;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.framework.core.exception.OseeAuthenticationException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.utility.Requirements;
import org.eclipse.osee.framework.ui.plugin.util.Result;

/**
 * @author Donald G. Dunne
 */
public class DemoTestUtil {

   public static Result isDbPopulatedWithDemoData() throws Exception {
      setDefaultBranch(BranchManager.getKeyedBranch("SAW_Bld_1"));

      if (ArtifactQuery.getArtifactsFromTypeAndName(Requirements.SOFTWARE_REQUIREMENT, "%Robot%",
            BranchManager.getDefaultBranch()).size() != 6) return new Result(
            "Expected at least 6 Software Requirements with word \"Robot\".  Database is not be populated with demo data.");
      return Result.TrueResult;
   }

   public static Collection<String> getTaskTitles(boolean firstTaskWorkflow) {
      if (firstTaskWorkflow) {
         firstTaskWorkflow = false;
         return Arrays.asList("Look into Graph View.", "Redesign how view shows values.",
               "Discuss new design with Senior Engineer", "Develop prototype", "Show prototype to management",
               "Create development plan", "Create test plan", "Make changes");
      } else
         return Arrays.asList("Document how Graph View works", "Update help contents", "Review new documentation",
               "Publish documentation to website", "Remove old viewer", "Deploy release");
   }

   public static int getNumTasks() {
      return getTaskTitles(false).size() + getTaskTitles(true).size();
   }

   public static void setDefaultBranch(Branch branch) throws Exception {
      BranchManager.setDefaultBranch(branch);
      Thread.sleep(2000L);
      Branch defaultBranch = BranchManager.getDefaultBranch();
      if (!branch.equals(defaultBranch)) {
         throw new IllegalStateException("Default Branch did not change on setDefaultBranch.");
      }
   }

   public static void setUpTest() throws Exception {
      try {
         // This test should only be run on test db
         TestCase.assertFalse(AtsPlugin.isProductionDb());
         // Confirm test setup with demo data
         TestCase.assertTrue(isDbPopulatedWithDemoData().isTrue());
         // Confirm user is Joe Smith
         TestCase.assertTrue("User \"Joe Smith\" does not exist in DB.  Run Demo DBInit prior to this test.",
               UserManager.getUserByUserId("Joe Smith") != null);
         // Confirm user is Joe Smith
         TestCase.assertTrue(
               "Authenticated user should be \"Joe Smith\" and is not.  Check that Demo Application Server is being run.",
               UserManager.getUser().getUserId().equals("Joe Smith"));
      } catch (OseeAuthenticationException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         TestCase.fail("Can't authenticate, either Demo Application Server is not running or Demo DbInit has not been performed");
      }

   }

}
