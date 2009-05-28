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
package org.eclipse.osee.ats.test.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import junit.framework.TestCase;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.util.ActionManager;
import org.eclipse.osee.ats.util.AtsPriority.PriorityType;
import org.eclipse.osee.ats.util.widgets.TaskManager;
import org.eclipse.osee.framework.core.exception.OseeAuthenticationException;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.utility.Requirements;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.util.ChangeType;
import org.eclipse.osee.support.test.util.AtsUserCommunity;
import org.eclipse.osee.support.test.util.DemoActionableItems;
import org.eclipse.osee.support.test.util.DemoUsers;
import org.eclipse.osee.support.test.util.TestUtil;

/**
 * @author Donald G. Dunne
 */
public class DemoTestUtil {

   public static Result isDbPopulatedWithDemoData() throws Exception {
      Branch branch = BranchManager.getKeyedBranch("SAW_Bld_1");

      if (ArtifactQuery.getArtifactsFromTypeAndName(Requirements.SOFTWARE_REQUIREMENT, "%Robot%", branch).size() != 6) return new Result(
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

   public static User getDemoUser(DemoUsers demoUser) throws OseeCoreException {
      return UserManager.getUserByName(demoUser.getName());
   }

   /**
    * Creates an action with the name title and demo code workflow
    * 
    * @param title
    * @param transaction
    * @return
    * @throws OseeCoreException
    */
   public static TeamWorkFlowArtifact createSimpleAction(String title, SkynetTransaction transaction) throws OseeCoreException {
      ActionArtifact actionArt =
            ActionManager.createAction(null, title, "Description", ChangeType.Improvement, PriorityType.Priority_2,
                  Arrays.asList(AtsUserCommunity.Other.name()), false, null,
                  ActionableItemArtifact.getActionableItems(Arrays.asList(DemoActionableItems.SAW_Code.getName())),
                  transaction);

      TeamWorkFlowArtifact teamArt = null;
      for (TeamWorkFlowArtifact team : actionArt.getTeamWorkFlowArtifacts()) {
         if (team.getTeamDefinition().getDescriptiveName().contains("Code")) {
            teamArt = team;
         }
      }
      return teamArt;
   }

   /**
    * Create tasks named title + <num>
    * 
    * @param teamArt
    * @param title
    * @param numTasks number to create
    * @param transaction
    * @throws Exception
    */
   public static Collection<TaskArtifact> createSimpleTasks(TeamWorkFlowArtifact teamArt, String title, int numTasks, SkynetTransaction transaction) throws Exception {
      List<String> names = new ArrayList<String>();
      for (int x = 1; x < numTasks + 1; x++) {
         names.add(title + " " + x);
      }
      return TaskManager.createTasks(teamArt, names, Arrays.asList(UserManager.getUser()), transaction);
   }

   /**
    * Deletes all artifacts with names that start with any title given
    * 
    * @param titles
    * @throws Exception
    */
   public static void cleanupSimpleTest(Collection<String> titles) throws Exception {
      List<Artifact> artifacts = new ArrayList<Artifact>();
      for (String title : titles) {
         artifacts.addAll(ArtifactQuery.getArtifactsFromName(title + "%", AtsPlugin.getAtsBranch(), false));
      }
      ArtifactPersistenceManager.purgeArtifacts(artifacts);
      TestUtil.sleep(4000);
   }

   /**
    * Deletes any artifact with name that starts with title
    * 
    * @param title
    * @throws Exception
    */
   public static void cleanupSimpleTest(String title) throws Exception {
      cleanupSimpleTest(Arrays.asList(title));
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
