/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.client.integration.tests.ats.agile;

import java.util.List;
import javax.ws.rs.core.Response;
import org.eclipse.osee.ats.api.agile.AgileEndpointApi;
import org.eclipse.osee.ats.api.agile.AgileSprintData;
import org.eclipse.osee.ats.api.agile.JaxAgileBacklog;
import org.eclipse.osee.ats.api.agile.JaxAgileFeatureGroup;
import org.eclipse.osee.ats.api.agile.JaxAgileSprint;
import org.eclipse.osee.ats.api.agile.JaxAgileTeam;
import org.eclipse.osee.ats.api.agile.JaxNewAgileBacklog;
import org.eclipse.osee.ats.api.agile.JaxNewAgileFeatureGroup;
import org.eclipse.osee.ats.api.agile.JaxNewAgileSprint;
import org.eclipse.osee.ats.api.agile.JaxNewAgileTeam;
import org.eclipse.osee.ats.client.integration.tests.AtsClientService;
import org.eclipse.osee.ats.demo.api.DemoArtifactToken;
import org.eclipse.osee.framework.core.util.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test unit for AgileEndpointImpl
 *
 * @author Donald G. Dunne
 */
public class AgileEndpointTest {

   private AgileEndpointApi agile;
   private long teamUuid;

   @Before
   public void setup() {
      agile = AtsClientService.getAgile();
      teamUuid = Lib.generateArtifactIdAsInt();
   }

   @After
   public void cleanup() {
      Artifact agileTeam = AtsClientService.get().getArtifact(teamUuid);
      if (agileTeam != null) {
         agile.deleteTeam(teamUuid);
      }
   }

   @Test
   public void testTeamCRUD() {
      // Test Create
      JaxNewAgileTeam newTeam = newJaxAgileTeam();
      Response response = agile.createTeam(newTeam);
      Assert.assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());

      // Test Get
      JaxAgileTeam team = agile.getTeam(teamUuid);
      Assert.assertNotNull(team);
      Assert.assertEquals("My Agile Team", team.getName());
      Assert.assertEquals(teamUuid, team.getUuid().longValue());
      Assert.assertEquals("", team.getDescription());
      Assert.assertEquals(true, team.isActive());

      // Test Update
      team.setDescription("description");
      team.setActive(false);
      team.setName("New Name");
      agile.updateTeam(team);
      JaxAgileTeam updatedTeam = agile.getTeam(teamUuid);
      Assert.assertNotNull(updatedTeam);
      Assert.assertEquals("New Name", updatedTeam.getName());
      Assert.assertEquals("description", updatedTeam.getDescription());
      Assert.assertEquals(false, updatedTeam.isActive());

      // Test Delete
      agile.deleteTeam(teamUuid);
      Assert.assertNull(AtsClientService.get().getArtifact(teamUuid));
   }

   private JaxNewAgileTeam newJaxAgileTeam() {
      JaxNewAgileTeam newTeam = new JaxNewAgileTeam();
      newTeam.setName("My Agile Team");
      newTeam.setUuid(teamUuid);
      return newTeam;
   }

   @Test
   public void testSprintCRUD() {
      JaxNewAgileTeam newTeam = newJaxAgileTeam();
      Response response = agile.createTeam(newTeam);
      Assert.assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());

      // Test Create
      JaxNewAgileSprint newSprint = new JaxNewAgileSprint();
      newSprint.setName("My Sprint");
      Long uuid = Lib.generateArtifactIdAsInt();
      newSprint.setUuid(uuid);
      newSprint.setTeamUuid(teamUuid);
      Response response2 = agile.createSprint(teamUuid, newSprint);
      Assert.assertEquals(Response.Status.CREATED.getStatusCode(), response2.getStatus());

      // Test Get
      List<JaxAgileSprint> sprints = agile.getSprints(teamUuid);
      Assert.assertEquals(1, sprints.size());
      JaxAgileSprint sprint = sprints.iterator().next();
      Assert.assertEquals("My Sprint", sprint.getName());
      Assert.assertEquals(teamUuid, sprint.getTeamUuid());
      Assert.assertEquals(uuid.longValue(), sprint.getUuid().longValue());

      // Test Delete
      agile.deleteSprint(teamUuid, sprint.getUuid());
      sprints = agile.getSprints(teamUuid);
      Assert.assertNull(AtsClientService.get().getArtifact(sprint.getUuid()));
   }

   @Test
   public void testFeatureGroupCRUD() {
      JaxNewAgileTeam newTeam = newJaxAgileTeam();
      Response response = agile.createTeam(newTeam);
      Assert.assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());

      // Test Create
      JaxNewAgileFeatureGroup group = new JaxNewAgileFeatureGroup();
      group.setName("Communications");
      group.setTeamUuid(teamUuid);
      Long uuid = Lib.generateArtifactIdAsInt();
      group.setUuid(uuid);

      Response response2 = agile.createFeatureGroup(teamUuid, group);
      Assert.assertEquals(Response.Status.CREATED.getStatusCode(), response2.getStatus());

      // Test Get
      List<JaxAgileFeatureGroup> groups = agile.getFeatureGroups(teamUuid);
      Assert.assertEquals(1, groups.size());
      JaxAgileFeatureGroup newGroup = groups.iterator().next();
      Assert.assertEquals("Communications", newGroup.getName());
      Assert.assertEquals(teamUuid, newGroup.getTeamUuid());
      Assert.assertEquals(uuid.longValue(), newGroup.getUuid().longValue());

      // Test Delete
      agile.deleteFeatureGroup(teamUuid, newGroup.getUuid());
      groups = agile.getFeatureGroups(teamUuid);
      Assert.assertTrue(groups.isEmpty());
      Assert.assertNull(AtsClientService.get().getArtifact(newGroup.getUuid()));
   }

   @Test
   public void testBacklogCR() {
      JaxNewAgileTeam newTeam = newJaxAgileTeam();
      Response response = agile.createTeam(newTeam);
      Assert.assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());

      JaxNewAgileBacklog backlog = new JaxNewAgileBacklog();
      backlog.setName("My Backlog");
      backlog.setTeamUuid(teamUuid);
      Long uuid = Lib.generateArtifactIdAsInt();
      backlog.setUuid(uuid);

      // Test Create
      Response response2 = agile.createBacklog(teamUuid, backlog);
      Assert.assertEquals(Response.Status.CREATED.getStatusCode(), response2.getStatus());

      // Test Get
      JaxAgileBacklog newBacklog = agile.getBacklog(teamUuid);
      Assert.assertEquals("My Backlog", newBacklog.getName());
      Assert.assertEquals(teamUuid, newBacklog.getTeamUuid());
      Assert.assertTrue(newBacklog.isActive());
      Assert.assertEquals(uuid.longValue(), newBacklog.getUuid().longValue());
   }

   @Test
   public void testGetSprintData() {
      AgileSprintData data =
         agile.getSprintData(DemoArtifactToken.SAW_Agile_Team.getId(), DemoArtifactToken.SAW_Sprint_2.getId());

      Assert.assertNotNull(data);
      Assert.assertEquals("SAW Agile Team", data.getAgileTeamName());
      Assert.assertEquals("SAW Sprint 2", data.getSprintName());
      Assert.assertEquals(2, data.getHolidays().size());
      Assert.assertEquals("ats.Points", data.getPointsAttrTypeName());
      Assert.assertEquals(new Integer(200), data.getPlannedPoints());
      Assert.assertEquals(new Integer(45), data.getUnPlannedPoints());
      Assert.assertEquals(20, data.getDates().size());
   }

   @Test
   public void testGetSprintDataTable() {
      String html =
         agile.getSprintDataTable(DemoArtifactToken.SAW_Agile_Team.getId(), DemoArtifactToken.SAW_Sprint_2.getId());
      Assert.assertNotNull(html);
      Assert.assertTrue(html.contains("SAW Sprint 2"));
      Assert.assertTrue(html.contains("200"));
      Assert.assertTrue(html.contains("45"));
      Assert.assertTrue(html.contains("Planned Complete"));
      Assert.assertTrue(html.contains("Total Remaining"));
   }

   @Test
   public void testGetSprintBurnDownChartUi() {
      String html = agile.getSprintBurndownChartUi(DemoArtifactToken.SAW_Agile_Team.getId(),
         DemoArtifactToken.SAW_Sprint_2.getId());
      Assert.assertTrue(html.contains("SAW Sprint 2 - Burndown"));
   }

   @Test
   public void testGetSprintBurnUpChartUi() {
      String html =
         agile.getSprintBurnupChartUi(DemoArtifactToken.SAW_Agile_Team.getId(), DemoArtifactToken.SAW_Sprint_2.getId());
      Assert.assertTrue(html.contains("SAW Sprint 2"));
   }

   @Test
   public void testGetSprintSummary() {
      String html =
         agile.getSprintSummary(DemoArtifactToken.SAW_Agile_Team.getId(), DemoArtifactToken.SAW_Sprint_2.getId());
      Assert.assertTrue(html.contains("SAW Sprint 2 - Summary"));
   }

   @Test
   public void testGetSprintReportStore() {
      XResultData results =
         agile.storeSprintReports(DemoArtifactToken.SAW_Agile_Team.getId(), DemoArtifactToken.SAW_Sprint_2.getId());
      Assert.assertFalse(results.toString(), results.isErrors());
      AtsClientService.get().getArtifact(DemoArtifactToken.SAW_Sprint_2).reloadAttributesAndRelations();
      Assert.assertEquals(4,
         AtsClientService.get().getRelationResolver().getChildren(DemoArtifactToken.SAW_Sprint_2).size());
   }

}
