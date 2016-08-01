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
import java.util.Collection;
import java.util.logging.Level;
import javax.ws.rs.core.Response;
import org.eclipse.core.runtime.Assert;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.agile.AgileEndpointApi;
import org.eclipse.osee.ats.api.agile.JaxAgileItem;
import org.eclipse.osee.ats.api.agile.JaxNewAgileBacklog;
import org.eclipse.osee.ats.api.agile.JaxNewAgileFeatureGroup;
import org.eclipse.osee.ats.api.agile.JaxNewAgileSprint;
import org.eclipse.osee.ats.api.agile.JaxNewAgileTeam;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.api.workflow.transition.IAtsTransitionManager;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.client.demo.internal.Activator;
import org.eclipse.osee.ats.client.demo.internal.AtsClientService;
import org.eclipse.osee.ats.core.client.config.AtsBulkLoad;
import org.eclipse.osee.ats.core.client.util.AtsChangeSet;
import org.eclipse.osee.ats.core.client.util.AtsUtilClient;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.core.workflow.transition.TransitionFactory;
import org.eclipse.osee.ats.core.workflow.transition.TransitionHelper;
import org.eclipse.osee.ats.demo.api.DemoArtifactTypes;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Lib;
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
      AtsChangeSet changes = new AtsChangeSet("Transition Agile Sprint");
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
