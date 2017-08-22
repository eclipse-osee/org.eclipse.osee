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
package org.eclipse.osee.ats.client.demo;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import javax.ws.rs.core.Response;
import org.eclipse.core.runtime.Assert;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.agile.AgileEndpointApi;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.agile.JaxAgileItem;
import org.eclipse.osee.ats.api.agile.JaxNewAgileBacklog;
import org.eclipse.osee.ats.api.agile.JaxNewAgileFeatureGroup;
import org.eclipse.osee.ats.api.agile.JaxNewAgileSprint;
import org.eclipse.osee.ats.api.agile.JaxNewAgileTeam;
import org.eclipse.osee.ats.api.config.JaxAtsObject;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.api.workflow.transition.IAtsTransitionManager;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.client.demo.internal.Activator;
import org.eclipse.osee.ats.client.demo.internal.AtsClientService;
import org.eclipse.osee.ats.core.client.config.AtsBulkLoad;
import org.eclipse.osee.ats.core.client.util.AtsUtilClient;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.core.workflow.transition.TransitionFactory;
import org.eclipse.osee.ats.core.workflow.transition.TransitionHelper;
import org.eclipse.osee.ats.demo.api.DemoArtifactToken;
import org.eclipse.osee.ats.demo.api.DemoArtifactTypes;
import org.eclipse.osee.ats.demo.api.DemoWorkflowTitles;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.support.test.util.TestUtil;

/**
 * Run from the ATS Navigator after the DB is configured for "OSEE Demo Database", this class will populate the database
 * with sample actions written against XYZ configured teams
 *
 * @author Donald G. Dunne
 */
public class PopulateDemoAgile {

   private static void validateArtifactCache() throws OseeStateException {
      final Collection<Artifact> list = ArtifactCache.getDirtyArtifacts();
      if (!list.isEmpty()) {
         for (Artifact artifact : list) {
            System.err.println(String.format("Artifact [%s] is dirty [%s]", artifact.toStringWithId(),
               Artifacts.getDirtyReport(artifact)));
         }
         throw new OseeStateException("[%d] Dirty Artifacts found after populate (see console for details)",
            list.size());
      }

   }

   public void run() throws Exception {
      AtsUtilClient.setEmailEnabled(false);
      if (AtsUtil.isProductionDb()) {
         throw new IllegalStateException("PopulateDemoAgile should not be run on production DB");
      }

      validateArtifactCache();

      OseeLog.log(Activator.class, Level.INFO, "Populate Demo Agile");

      AtsBulkLoad.reloadConfig(true);

      SevereLoggingMonitor monitorLog = TestUtil.severeLoggingStart();

      createSampleAgileTeam();

      validateArtifactCache();
      TestUtil.severeLoggingEnd(monitorLog);
      OseeLog.log(Activator.class, Level.INFO, "Populate Complete");

   }

   private void createSampleAgileTeam() {
      AgileEndpointApi agile = AtsClientService.getAgile();

      long teamUuid = Lib.generateArtifactIdAsInt();
      String teamGuid = GUID.create();

      // Create Team
      JaxNewAgileTeam newTeam = newJaxAgileTeam(teamUuid, teamGuid);
      Response response = agile.createTeam(newTeam);
      Assert.isTrue(Response.Status.CREATED.getStatusCode() == response.getStatus());

      // Assigne ATS Team to Agile Team
      Artifact sawCodeArt = AtsClientService.get().getArtifact(DemoArtifactToken.SAW_Code);
      Conditions.assertNotNull(sawCodeArt, "sawCodeArt");
      Artifact agileTeam = AtsClientService.get().getArtifact(newTeam.getUuid());
      agileTeam.addRelation(AtsRelationTypes.AgileTeamToAtsTeam_AtsTeam, sawCodeArt);
      agileTeam.persist("Assigne ATS Team to Agile Team");

      // Create Backlog
      JaxNewAgileBacklog backlog = newBacklog(teamUuid);
      response = agile.createBacklog(teamUuid, backlog);
      Assert.isTrue(Response.Status.CREATED.getStatusCode() == response.getStatus());

      // Add items to backlog
      Collection<IAtsWorkItem> items =
         AtsClientService.get().getQueryService().createQuery(WorkItemType.TeamWorkflow).isOfType(
            DemoArtifactTypes.DemoCodeTeamWorkflow, DemoArtifactTypes.DemoReqTeamWorkflow,
            DemoArtifactTypes.DemoTestTeamWorkflow).getItems();
      Assert.isTrue(items.size() > 0);

      JaxAgileItem item = new JaxAgileItem();
      item.setBacklogUuid(backlog.getUuid());
      item.setSetBacklog(true);
      for (IAtsWorkItem workItem : items) {
         item.getUuids().add(workItem.getId());
      }
      response = agile.updateItems(item);
      Assert.isTrue(Response.Status.CREATED.getStatusCode() == response.getStatus());

      // Create Sprints
      long firstSprintUuid = 0L;
      long secondSprintUuid = 0L;
      for (int x = 1; x < 3; x++) {
         JaxNewAgileSprint newSprint = newSprint(teamUuid, x);
         if (x == 1) {
            firstSprintUuid = newSprint.getUuid();
         } else {
            secondSprintUuid = newSprint.getUuid();
         }
         response = agile.createSprint(teamUuid, newSprint);
         Assert.isTrue(Response.Status.CREATED.getStatusCode() == response.getStatus());
      }

      // Add items to Sprint
      JaxAgileItem completedItems = new JaxAgileItem();
      completedItems.setSprintUuid(firstSprintUuid);
      completedItems.setSetSprint(true);

      JaxAgileItem inworkItems = new JaxAgileItem();
      inworkItems.setSprintUuid(secondSprintUuid);
      inworkItems.setSetSprint(true);

      for (IAtsWorkItem workItem : items) {
         if (workItem.getStateMgr().getStateType().isCompleted()) {
            completedItems.getUuids().add(workItem.getId());
         } else {
            inworkItems.getUuids().add(workItem.getId());
         }
      }
      response = agile.updateItems(inworkItems);
      Assert.isTrue(Response.Status.CREATED.getStatusCode() == response.getStatus());
      response = agile.updateItems(completedItems);
      Assert.isTrue(Response.Status.CREATED.getStatusCode() == response.getStatus());

      // Transition First Sprint to completed
      IAtsWorkItem sprint = AtsClientService.get().getQueryService().createQuery(WorkItemType.WorkItem).andUuids(
         firstSprintUuid).getItems().iterator().next();
      IAtsChangeSet changes = AtsClientService.get().createChangeSet("Transition Agile Sprint");
      TransitionHelper helper =
         new TransitionHelper("Transition Agile Stprint", Arrays.asList(sprint), TeamState.Completed.getName(), null,
            null, changes, AtsClientService.get().getServices(), TransitionOption.OverrideAssigneeCheck);
      IAtsTransitionManager transitionMgr = TransitionFactory.getTransitionManager(helper);
      TransitionResults results = transitionMgr.handleAll();
      if (results.isEmpty()) {
         changes.execute();
      } else {
         throw new OseeStateException("Can't transition sprint to completed");
      }

      // Create Feature Groups
      for (String name : Arrays.asList("Communications", "UI", "Documentation", "Framework")) {
         JaxNewAgileFeatureGroup group = newFeatureGroup(teamUuid, name);
         response = agile.createFeatureGroup(teamUuid, group);
         Assert.isTrue(Response.Status.CREATED.getStatusCode() == response.getStatus());
      }

      setupSprint2ForBurndown(secondSprintUuid);
   }

   private void setupSprint2ForBurndown(long secondSprintUuid) {

      // Transition First Sprint to completed
      IAtsWorkItem sprint = AtsClientService.get().getQueryService().createQuery(WorkItemType.WorkItem).andUuids(
         secondSprintUuid).getItems().iterator().next();
      IAtsChangeSet changes = AtsClientService.get().createChangeSet("Setup Sprint 2 for Burndown");

      Calendar cal = Calendar.getInstance();
      cal.add(Calendar.DAY_OF_YEAR, -5);
      Date holiday1 = null, holiday2 = null;
      // backup start date till hit weekday
      while (!DateUtil.isWeekDay(cal)) {
         cal.add(Calendar.DAY_OF_YEAR, -1);
      }
      changes.setSoleAttributeValue(sprint, AtsAttributeTypes.StartDate, cal.getTime());
      int x = 1;
      int holidayDayNum = 5;
      // count up 20 weekdays and set 2 weekday holidays
      for (x = 1; x <= 21; x++) {
         cal.add(Calendar.DAY_OF_YEAR, 1);
         while (!DateUtil.isWeekDay(cal)) {
            cal.add(Calendar.DAY_OF_YEAR, 1);
         }
         if (x == holidayDayNum) {
            holiday1 = cal.getTime();
         } else if (x == holidayDayNum + 1) {
            holiday2 = cal.getTime();
         }
      }
      changes.setSoleAttributeValue(sprint, AtsAttributeTypes.EndDate, cal.getTime());
      changes.setSoleAttributeValue(sprint, AtsAttributeTypes.UnPlannedPoints, 45);
      changes.setSoleAttributeValue(sprint, AtsAttributeTypes.PlannedPoints, 200);
      changes.addAttribute(sprint, AtsAttributeTypes.Holiday, holiday1);
      changes.addAttribute(sprint, AtsAttributeTypes.Holiday, holiday2);
      // set sprint data on sprint items
      Artifact agileTeamArt =
         ((Artifact) sprint.getStoreObject()).getRelatedArtifact(AtsRelationTypes.AgileTeamToSprint_AgileTeam);
      changes.execute();

      setSprintItemData(agileTeamArt.getUuid(), (IAgileSprint) sprint);
   }

   private void setSprintItemData(Long teamUuid, IAgileSprint sprint) {
      List<SprintItemData> datas = new LinkedList<>();
      datas.add(
         new SprintItemData("Sprint Order", "Title", "Points", "Unplanned Work", "Feature Group", "CreatedDate"));
      datas.add(
         new SprintItemData("1", "Button W doesn't work on Situation Page", "8", " ", "Communications", "10/03/2016"));
      datas.add(new SprintItemData("2", "Can't load Diagram Tree", "4", "Unplanned Work", "Framework", "10/03/2016"));
      datas.add(new SprintItemData("3", "Can't see the Graph View", "8", "Unplanned Work", "Framework", "10/03/2016"));
      datas.add(new SprintItemData("4", "Problem in Diagram Tree", "40", " ", "Framework", "10/03/2016"));
      datas.add(new SprintItemData("5", "Problem with the Graph View", "8", " ", "Communications", "10/03/2016"));
      datas.add(new SprintItemData("6", DemoWorkflowTitles.SAW_COMMITTED_REQT_CHANGES_FOR_DIAGRAM_VIEW, "2",
         "Unplanned Work", "Framework", "10/03/2016"));
      datas.add(new SprintItemData("7", DemoWorkflowTitles.SAW_COMMITTED_REQT_CHANGES_FOR_DIAGRAM_VIEW, "8", " ",
         "Framework", "10/03/2016"));
      datas.add(new SprintItemData("8", DemoWorkflowTitles.SAW_COMMITTED_REQT_CHANGES_FOR_DIAGRAM_VIEW, "16", " ", "UI",
         "10/03/2016"));
      datas.add(new SprintItemData("9", DemoWorkflowTitles.SAW_NO_BRANCH_REQT_CHANGES_FOR_DIAGRAM_VIEW, "32", " ",
         "Communications", "10/03/2016"));
      datas.add(new SprintItemData("10", DemoWorkflowTitles.SAW_NO_BRANCH_REQT_CHANGES_FOR_DIAGRAM_VIEW, "40", " ",
         "Documentation", "10/03/2016"));
      datas.add(new SprintItemData("11", DemoWorkflowTitles.SAW_NO_BRANCH_REQT_CHANGES_FOR_DIAGRAM_VIEW, "8", " ",
         "Documentation", "10/03/2016"));
      datas.add(new SprintItemData("12", DemoWorkflowTitles.SAW_UNCOMMITTED_REQT_CHANGES_FOR_DIAGRAM_VIEW, "1", " ",
         "Communications", "10/03/2016"));
      datas.add(new SprintItemData("13", DemoWorkflowTitles.SAW_UNCOMMITTED_REQT_CHANGES_FOR_DIAGRAM_VIEW, "6", " ",
         "Documentation", "10/03/2016"));
      datas.add(new SprintItemData("14", DemoWorkflowTitles.SAW_UNCOMMITTED_REQT_CHANGES_FOR_DIAGRAM_VIEW, "32", " ",
         "Communications", "10/03/2016"));
      datas.add(new SprintItemData("15", DemoArtifactToken.SAW_UnCommitedConflicted_Req_TeamWf.getName(), "1", " ",
         "Communications", "10/03/2016"));
      datas.add(new SprintItemData("16", "Workaround for Graph View for SAW_Bld_2", "1", "Unplanned Work",
         "Communications", "10/03/2016"));
      datas.add(new SprintItemData("17", "Workaround for Graph View for SAW_Bld_3", "2", "Unplanned Work",
         "Communications", "10/03/2016"));

      int x = 1;
      for (JaxAtsObject jaxWorkItem : AtsClientService.getAgile().getSprintItemsAsJax(teamUuid,
         sprint.getId()).getAtsObjects()) {
         SprintItemData data = getSprintData(datas, x++, jaxWorkItem);
         String featureGroupName = data.getFeature();
         if (Strings.isValid(featureGroupName)) {
            AtsClientService.getAgile().addFeatureGroup(jaxWorkItem.getUuid(), featureGroupName);
         }
         String unPlannedStr = data.getUnPlanned();
         boolean unPlanned = false;
         if (Strings.isValid(unPlannedStr)) {
            if (unPlannedStr.toLowerCase().contains("un")) {
               unPlanned = true;
            }
         }
         AtsClientService.getAgile().setUnPlanned(jaxWorkItem.getUuid(), unPlanned);
         String points = data.getPoints();
         if (Strings.isValid(points)) {
            AtsClientService.getAgile().setPoints(jaxWorkItem.getUuid(), points);
         }
      }
   }

   private SprintItemData getSprintData(List<SprintItemData> datas, int i, JaxAtsObject workItem) {
      for (SprintItemData data : datas) {
         if (data.getOrder().equals(String.valueOf(i)) && data.getTitle().equals(workItem.getName())) {
            return data;
         }
      }
      return null;
   }

   private JaxNewAgileBacklog newBacklog(long teamUuid) {
      JaxNewAgileBacklog backlog = new JaxNewAgileBacklog();
      backlog.setName("My Backlog");
      backlog.setTeamUuid(teamUuid);
      backlog.setUuid(Lib.generateArtifactIdAsInt());
      return backlog;
   }

   private JaxNewAgileFeatureGroup newFeatureGroup(long teamUuid, String name) {
      JaxNewAgileFeatureGroup group = new JaxNewAgileFeatureGroup();
      group.setName(name);
      group.setTeamUuid(teamUuid);
      group.setUuid(Lib.generateArtifactIdAsInt());
      return group;
   }

   private JaxNewAgileSprint newSprint(long teamUuid, int x) {
      JaxNewAgileSprint newSprint = new JaxNewAgileSprint();
      newSprint.setName("Sprint 0" + x);
      newSprint.setUuid(Lib.generateArtifactIdAsInt());
      newSprint.setTeamUuid(teamUuid);
      return newSprint;
   }

   private JaxNewAgileTeam newJaxAgileTeam(long teamUuid, String teamGuid) {
      JaxNewAgileTeam newTeam = new JaxNewAgileTeam();
      newTeam.setName("SAW Agile Team");
      newTeam.setUuid(teamUuid);
      return newTeam;
   }

}
