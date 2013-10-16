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
package org.eclipse.osee.ats.client.integration.tests.util;

import static org.eclipse.osee.framework.core.enums.DeletionFlag.EXCLUDE_DELETED;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.client.demo.DemoActionableItems;
import org.eclipse.osee.ats.client.demo.DemoArtifactTypes;
import org.eclipse.osee.ats.client.demo.DemoSawBuilds;
import org.eclipse.osee.ats.client.demo.DemoTeam;
import org.eclipse.osee.ats.client.demo.DemoWorkType;
import org.eclipse.osee.ats.client.integration.tests.AtsClientService;
import org.eclipse.osee.ats.core.client.action.ActionManager;
import org.eclipse.osee.ats.core.client.task.TaskArtifact;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.workflow.ChangeType;
import org.eclipse.osee.ats.core.config.ActionableItems;
import org.eclipse.osee.ats.core.config.TeamDefinitions;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.data.IUserToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.exception.OseeAuthenticationException;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.artifact.search.QueryOptions;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;

/**
 * @author Donald G. Dunne
 */
public class DemoTestUtil {
   public static Map<DemoWorkType, Artifact> unCommittedWorkflows;
   public static Map<DemoWorkType, Artifact> committedWorkflows;
   public static TeamWorkFlowArtifact toolsTeamWorkflow;

   public static Result isDbPopulatedWithDemoData() throws Exception {
      Collection<Artifact> robotArtifacts =
         ArtifactQuery.getArtifactListFromTypeAndName(CoreArtifactTypes.SoftwareRequirement, "Robot",
            DemoSawBuilds.SAW_Bld_1, QueryOptions.CONTAINS_MATCH_OPTIONS);
      if (robotArtifacts.size() < 6) {
         return new Result(
            String.format(
               "Expected at least 6 Software Requirements with name \"Robot\" but found [%s].  Database is not be populated with demo data.",
               robotArtifacts.size()));
      }
      return Result.TrueResult;
   }

   public static Collection<String> getTaskTitles(boolean firstTaskWorkflow) {
      if (firstTaskWorkflow) {
         firstTaskWorkflow = false;
         return Arrays.asList("Look into Graph View.", "Redesign how view shows values.",
            "Discuss new design with Senior Engineer", "Develop prototype", "Show prototype to management",
            "Create development plan", "Create test plan", "Make changes");
      } else {
         return Arrays.asList("Document how Graph View works", "Update help contents", "Review new documentation",
            "Publish documentation to website", "Remove old viewer", "Deploy release");
      }
   }

   public static int getNumTasks() {
      return getTaskTitles(false).size() + getTaskTitles(true).size();
   }

   public static User getDemoUser(IUserToken demoUser) throws OseeCoreException {
      return UserManager.getUserByName(demoUser.getName());
   }

   /**
    * Creates an action with the name title and demo code workflow
    */
   public static TeamWorkFlowArtifact createSimpleAction(String title, SkynetTransaction transaction) throws OseeCoreException {
      Artifact actionArt =
         ActionManager.createAction(null, title, "Description", ChangeType.Improvement, "2", false, null,
            ActionableItems.getActionableItems(Arrays.asList(DemoActionableItems.SAW_Code.getName())), new Date(),
            AtsClientService.get().getUserAdmin().getCurrentUser(), null, transaction);

      TeamWorkFlowArtifact teamArt = null;
      for (TeamWorkFlowArtifact team : ActionManager.getTeams(actionArt)) {
         if (team.getTeamDefinition().getName().contains("Code")) {
            teamArt = team;
         }
      }
      return teamArt;
   }

   public static Set<IAtsActionableItem> getActionableItems(DemoActionableItems demoActionableItems) throws OseeCoreException {
      return ActionableItems.getActionableItems(Arrays.asList(demoActionableItems.getName()));
   }

   public static IAtsActionableItem getActionableItem(DemoActionableItems demoActionableItems) throws OseeCoreException {
      return getActionableItems(demoActionableItems).iterator().next();
   }

   public static TeamWorkFlowArtifact addTeamWorkflow(Artifact actionArt, String title, SkynetTransaction transaction) throws OseeCoreException {
      Set<IAtsActionableItem> actionableItems = getActionableItems(DemoActionableItems.SAW_Test);
      Collection<IAtsTeamDefinition> teamDefs = TeamDefinitions.getImpactedTeamDefs(actionableItems);

      ActionManager.createTeamWorkflow(actionArt, teamDefs.iterator().next(), actionableItems,
         Arrays.asList(AtsClientService.get().getUserAdmin().getCurrentUser()), transaction, new Date(),
         AtsClientService.get().getUserAdmin().getCurrentUser(), null);

      TeamWorkFlowArtifact teamArt = null;
      for (TeamWorkFlowArtifact team : ActionManager.getTeams(actionArt)) {
         if (team.getTeamDefinition().getName().contains("Test")) {
            teamArt = team;
         }
      }
      return teamArt;
   }

   /**
    * Create tasks named title + <num>
    */
   public static Collection<TaskArtifact> createSimpleTasks(TeamWorkFlowArtifact teamArt, String title, int numTasks, String relatedToState, SkynetTransaction transaction) throws Exception {
      List<String> names = new ArrayList<String>();
      for (int x = 1; x < numTasks + 1; x++) {
         names.add(title + " " + x);
      }
      return teamArt.createTasks(names, Arrays.asList(AtsClientService.get().getUserAdmin().getCurrentUser()),
         new Date(), AtsClientService.get().getUserAdmin().getCurrentUser(), relatedToState, transaction);
   }

   public static TeamWorkFlowArtifact getToolsTeamWorkflow() throws OseeCoreException {
      if (toolsTeamWorkflow == null) {
         for (Artifact art : ArtifactQuery.getArtifactListFromName("Button S doesn't work on help",
            AtsUtil.getAtsBranch(), EXCLUDE_DELETED)) {
            if (art.isOfType(AtsArtifactTypes.TeamWorkflow)) {
               toolsTeamWorkflow = (TeamWorkFlowArtifact) art;
            }
         }
      }
      return toolsTeamWorkflow;
   }

   public static Artifact getUncommittedActionWorkflow(DemoWorkType demoWorkType) throws OseeCoreException {
      if (unCommittedWorkflows == null) {
         unCommittedWorkflows = new HashMap<DemoWorkType, Artifact>();
         for (Artifact art : ArtifactQuery.getArtifactListFromName(
            "SAW (uncommitted) More Reqt Changes for Diagram View", AtsUtil.getAtsBranch(), EXCLUDE_DELETED)) {
            if (art.isOfType(DemoArtifactTypes.DemoCodeTeamWorkflow)) {
               unCommittedWorkflows.put(DemoWorkType.Code, art);
            } else if (art.isOfType(DemoArtifactTypes.DemoTestTeamWorkflow)) {
               unCommittedWorkflows.put(DemoWorkType.Test, art);
            } else if (art.isOfType(DemoArtifactTypes.DemoReqTeamWorkflow)) {
               unCommittedWorkflows.put(DemoWorkType.Requirements, art);
            } else if (art.isOfType(AtsArtifactTypes.TeamWorkflow)) {
               unCommittedWorkflows.put(DemoWorkType.SW_Design, art);
            }
         }
      }
      return unCommittedWorkflows.get(demoWorkType);
   }

   public static Artifact getCommittedActionWorkflow(DemoWorkType demoWorkType) throws OseeCoreException {
      if (committedWorkflows == null) {
         committedWorkflows = new HashMap<DemoWorkType, Artifact>();
         for (Artifact art : ArtifactQuery.getArtifactListFromName("SAW (committed) Reqt Changes for Diagram View",
            AtsUtil.getAtsBranch(), EXCLUDE_DELETED)) {
            if (art.isOfType(DemoArtifactTypes.DemoCodeTeamWorkflow)) {
               committedWorkflows.put(DemoWorkType.Code, art);
            } else if (art.isOfType(DemoArtifactTypes.DemoTestTeamWorkflow)) {
               committedWorkflows.put(DemoWorkType.Test, art);
            } else if (art.isOfType(DemoArtifactTypes.DemoReqTeamWorkflow)) {
               committedWorkflows.put(DemoWorkType.Requirements, art);
            } else if (art.isOfType(AtsArtifactTypes.TeamWorkflow)) {
               committedWorkflows.put(DemoWorkType.SW_Design, art);
            }
         }
      }
      return committedWorkflows.get(demoWorkType);
   }

   public static void setUpTest() throws Exception {
      try {
         // This test should only be run on test db
         assertFalse(AtsUtil.isProductionDb());
         // Confirm test setup with demo data
         Result result = isDbPopulatedWithDemoData();
         assertTrue(result.getText(), result.isTrue());
         // Confirm user is Joe Smith
         assertTrue("User \"Joe Smith\" does not exist in DB.  Run Demo DBInit prior to this test.",
            UserManager.getUserByUserId("Joe Smith") != null);
         // Confirm user is Joe Smith
         assertTrue(
            "Authenticated user should be \"Joe Smith\" and is not.  Check that Demo Application Server is being run.",
            AtsClientService.get().getUserAdmin().getCurrentUser().getUserId().equals("Joe Smith"));
      } catch (OseeAuthenticationException ex) {
         OseeLog.log(DemoTestUtil.class, Level.SEVERE, ex);
         fail("Can't authenticate, either Demo Application Server is not running or Demo DbInit has not been performed");
      }

   }

   public static IAtsTeamDefinition getTeamDef(DemoTeam team) throws OseeCoreException {
      IAtsTeamDefinition results = null;
      // Add check to keep exception from occurring for OSEE developers running against production
      if (!ClientSessionManager.isProductionDataStore()) {
         try {
            results =
               AtsClientService.get().getAtsConfig().getSoleByGuid(team.getTeamDefToken().getGuid(),
                  IAtsTeamDefinition.class);
         } catch (Exception ex) {
            OseeLog.log(DemoTestUtil.class, Level.SEVERE, ex);
         }
      }
      return results;
   }

}
