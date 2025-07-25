/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.integration.tests.ats.util;

import static org.junit.Assert.assertFalse;
import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.agile.IAgileBacklog;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.AtsTestUtil;
import org.eclipse.osee.ats.ide.util.Import.ImportAgileActionsViaSpreadsheetBlam;
import org.eclipse.osee.ats.ide.util.Import.action.ImportActionsViaSpreadsheetBlam;
import org.eclipse.osee.ats.ide.util.Import.action.ImportActionsViaSpreadsheetBlam.ImportOption;
import org.eclipse.osee.ats.ide.workflow.goal.GoalArtifact;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

/**
 * This test is intended to be run against a demo database.
 *
 * @author Donald G. Dunne
 */
public class ImportActionsViaSpreadsheetTest {

   private static final String FIRST_ACTION_TITLE = "Fix the SAW Editor";
   private static final String SECOND_ACTION_TITLE = "Add the new feature";
   private static final String THIRD_ACTION_TITLE = "Help the users";
   private static List<String> ActionTitles =
      Arrays.asList(FIRST_ACTION_TITLE, SECOND_ACTION_TITLE, THIRD_ACTION_TITLE, "ImportActionsViaSpreadsheetTest");
   private AtsApi atsApi;

   @Before
   @After
   public void cleanUp() throws Exception {
      atsApi = AtsApiService.get();
      AtsTestUtil.cleanupSimpleTest(ActionTitles);
   }

   @Before
   public void setUp() throws Exception {
      // This test should only be run on test db
      assertFalse(atsApi.getStoreService().isProductionDb());

      for (String title : ActionTitles) {
         List<Artifact> arts = ArtifactQuery.getArtifactListFromName(title, atsApi.getAtsBranch());
         Assert.assertEquals(String.format("Action [%s] should have been purged before test start.", title), 0,
            arts.size());
      }
   }

   @org.junit.Test
   public void testImport() throws Exception {
      ImportActionsViaSpreadsheetBlam blam = new ImportActionsViaSpreadsheetBlam();

      File file = blam.getSampleSpreadsheetFile();
      Assert.assertNotNull(file);

      XResultData rd = blam.importActions(file, null, ImportOption.PERSIST);
      Assert.assertTrue(rd.toString(), rd.isSuccess());

      List<Artifact> arts = ArtifactQuery.getArtifactListFromName(FIRST_ACTION_TITLE, atsApi.getAtsBranch());
      Assert.assertEquals("3 Action and 3 Team Workflows should be created", 6, arts.size());
      TeamWorkFlowArtifact testWf = null, code1Wf = null, code2Wf = null;
      for (Artifact art : arts) {
         if (art.isOfType(AtsArtifactTypes.TeamWorkflow)) {
            TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) art;
            if (teamArt.getDescription().startsWith("Phase 1")) {
               code1Wf = teamArt;
            } else if (teamArt.getDescription().startsWith("Phase 2")) {
               code2Wf = teamArt;
            } else if (teamArt.getTeamDefinition().getName().contains("Test")) {
               testWf = teamArt;
            }
         }
      }

      Assert.assertNotNull(testWf);
      Assert.assertEquals("Fix the SAW Editor", testWf.getName());
      Assert.assertEquals("What needs to be done by Test team", testWf.getDescription());
      Assert.assertEquals("5", testWf.getSoleAttributeValue(AtsAttributeTypes.Priority, ""));
      Assert.assertEquals("Category", testWf.getSoleAttributeValue(AtsAttributeTypes.Category1, ""));
      Assert.assertEquals("3", testWf.getSoleAttributeValue(AtsAttributeTypes.Points, ""));
      Assert.assertTrue(testWf.getSoleAttributeValue(AtsAttributeTypes.EstimatedHours, 0.0) == 4.0);
      Assert.assertEquals("Improvement", testWf.getSoleAttributeValue(AtsAttributeTypes.ChangeType, null));
      Assert.assertEquals("SAW_Bld_3", atsApi.getVersionService().getTargetedVersion(testWf).getName());
      Assert.assertEquals(DemoUsers.Joe_Smith, testWf.getAssignees().iterator().next());
      Assert.assertEquals(DemoUsers.Kay_Jones.getUserId(),
         testWf.getSoleAttributeValue(AtsAttributeTypes.CreatedBy, ""));

      Assert.assertNotNull(code1Wf);
      Assert.assertEquals("Fix the SAW Editor", code1Wf.getName());
      Assert.assertEquals("Phase 1 of what needs to be fixed", code1Wf.getDescription());
      Assert.assertEquals("2", code1Wf.getSoleAttributeValue(AtsAttributeTypes.Priority, ""));
      Assert.assertEquals("", code1Wf.getSoleAttributeValue(AtsAttributeTypes.Category1, ""));
      Assert.assertEquals("3", code1Wf.getSoleAttributeValue(AtsAttributeTypes.Points, ""));
      Assert.assertTrue(code1Wf.getSoleAttributeValue(AtsAttributeTypes.EstimatedHours, 0.0) == 23.0);
      Assert.assertEquals("Improvement", code1Wf.getSoleAttributeValue(AtsAttributeTypes.ChangeType, null));
      Assert.assertEquals("SAW_Bld_2", atsApi.getVersionService().getTargetedVersion(code1Wf).getName());
      Assert.assertEquals(DemoUsers.Joe_Smith, code1Wf.getAssignees().iterator().next());
      Assert.assertEquals(DemoUsers.Kay_Jones.getUserId(),
         code1Wf.getSoleAttributeValue(AtsAttributeTypes.CreatedBy, ""));

      Assert.assertNotNull(code2Wf);
      Assert.assertEquals("Fix the SAW Editor", code2Wf.getName());
      Assert.assertEquals("Phase 2 targeted for different version", code2Wf.getDescription());
      Assert.assertEquals("3", code2Wf.getSoleAttributeValue(AtsAttributeTypes.Priority, ""));
      Assert.assertEquals("", code2Wf.getSoleAttributeValue(AtsAttributeTypes.Category1, ""));
      Assert.assertEquals("3", code2Wf.getSoleAttributeValue(AtsAttributeTypes.Points, ""));
      Assert.assertTrue(code2Wf.getSoleAttributeValue(AtsAttributeTypes.EstimatedHours, 0.0) == 3.0);
      Assert.assertEquals("Improvement", code2Wf.getSoleAttributeValue(AtsAttributeTypes.ChangeType, null));
      Assert.assertEquals("SAW_Bld_2", atsApi.getVersionService().getTargetedVersion(code2Wf).getName());
      Assert.assertEquals(DemoUsers.Joe_Smith, code2Wf.getAssignees().iterator().next());
      Assert.assertEquals(DemoUsers.Joe_Smith.getUserId(),
         code2Wf.getSoleAttributeValue(AtsAttributeTypes.CreatedBy, ""));
   }

   @org.junit.Test
   public void testImport_withGoal() throws Exception {
      IAtsChangeSet changes = atsApi.createChangeSet(getClass().getSimpleName());
      GoalArtifact goal =
         (GoalArtifact) atsApi.getActionService().createGoal(getClass().getSimpleName() + " - testImport_withGoal",
            changes);
      changes.execute();

      ImportActionsViaSpreadsheetBlam blam = new ImportActionsViaSpreadsheetBlam();

      File file = blam.getSampleSpreadsheetFile();
      Assert.assertNotNull(file);

      XResultData rd = blam.importActions(file, goal, ImportOption.PERSIST);
      Assert.assertTrue(rd.toString(), rd.isSuccess());

      Artifact goalArt = (Artifact) goal.getStoreObject();
      goalArt.reloadAttributesAndRelations();

      List<Artifact> members = goalArt.getRelatedArtifacts(AtsRelationTypes.Goal_Member);
      Assert.assertEquals("Should be 5 members", 5, members.size());
   }

   @org.junit.Test
   public void testImportAgileActions() throws Exception {
      ImportAgileActionsViaSpreadsheetBlam blam = new ImportAgileActionsViaSpreadsheetBlam();

      File file = blam.getSampleSpreadsheetFile();
      Assert.assertNotNull(file);

      // Import actions to backlog and sprint
      XResultData rd = blam.importActions(file, null, ImportOption.PERSIST);
      Assert.assertTrue(rd.toString(), rd.isSuccess());

      // Two should be added to backlog
      IAgileBacklog backlog = atsApi.getAgileService().getAgileBacklog(DemoArtifactToken.SAW_Backlog);
      Assert.assertNotNull(backlog);

      Artifact backlogArt = (Artifact) backlog.getStoreObject();
      backlogArt.reloadAttributesAndRelations();
      backlog = atsApi.getAgileService().getAgileBacklog(backlogArt);

      // Validate backlog
      Collection<ArtifactToken> members =
         atsApi.getRelationResolver().getRelated(backlog, AtsRelationTypes.Goal_Member);
      Assert.assertEquals("Should be 20 members", 20, members.size());
      String description = ((IAtsObject) members.toArray()[18]).getDescription();
      Assert.assertTrue("members should be in order", description.startsWith("Phase 1"));
      String description2 = ((IAtsObject) members.toArray()[19]).getDescription();
      Assert.assertTrue("members should be in order", description2.startsWith("Phase 2"));

      // Two should be added to sprint and points set
      IAgileSprint sprint2 = atsApi.getAgileService().getAgileSprint(DemoArtifactToken.SAW_Sprint_2);
      Assert.assertNotNull(sprint2);

      Artifact sprintArt = (Artifact) sprint2.getStoreObject();
      sprintArt.reloadAttributesAndRelations();
      sprint2 = atsApi.getAgileService().getAgileSprint(sprintArt);

      // Validate sprint
      members = atsApi.getRelationResolver().getRelated(sprint2, AtsRelationTypes.AgileSprintToItem_AtsItem);
      Assert.assertEquals("Should be 19 members", 19, members.size());

      Assert.assertTrue("members should be in order",
         ((IAtsObject) members.toArray()[17]).getDescription().startsWith("Phase 1"));
      Assert.assertEquals("2", atsApi.getAttributeResolver().getSoleAttributeValue((IAtsObject) members.toArray()[17],
         AtsAttributeTypes.Points, ""));

      Assert.assertTrue("members should be in order",
         ((IAtsObject) members.toArray()[18]).getDescription().startsWith("Phase 2"));
      Assert.assertEquals("3", atsApi.getAttributeResolver().getSoleAttributeValue((IAtsObject) members.toArray()[18],
         AtsAttributeTypes.Points, ""));

   }

}
