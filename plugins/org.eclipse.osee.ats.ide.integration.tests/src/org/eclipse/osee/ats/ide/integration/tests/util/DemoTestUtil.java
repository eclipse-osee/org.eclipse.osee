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
package org.eclipse.osee.ats.ide.integration.tests.util;

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
import org.eclipse.osee.ats.api.demo.AtsDemoOseeTypes;
import org.eclipse.osee.ats.api.demo.DemoActionableItems;
import org.eclipse.osee.ats.api.demo.DemoWorkType;
import org.eclipse.osee.ats.api.team.ChangeType;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.ActionResult;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.config.TeamDefinitions;
import org.eclipse.osee.ats.ide.demo.DemoUtil;
import org.eclipse.osee.ats.ide.integration.tests.AtsClientService;
import org.eclipse.osee.ats.ide.workflow.task.TaskArtifact;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Donald G. Dunne
 */
public class DemoTestUtil {
   public static Map<DemoWorkType, Artifact> unCommittedWorkflows;
   public static Map<DemoWorkType, Artifact> committedWorkflows;
   public static TeamWorkFlowArtifact toolsTeamWorkflow;

   public static User getDemoUser(UserToken demoUser) {
      return UserManager.getUserByName(demoUser.getName());
   }

   /**
    * Creates an action with the name title and demo code workflow
    */
   public static IAtsTeamWorkflow createSimpleAction(String title, IAtsChangeSet changes) {
      ActionResult result = AtsClientService.get().getActionFactory().createAction(null, title, "Description",
         ChangeType.Improvement, "2", false, null,
         AtsClientService.get().getActionableItemService().getActionableItems(
            Arrays.asList(DemoActionableItems.SAW_Code.getName())),
         new Date(), AtsClientService.get().getUserService().getCurrentUser(), null, changes);

      IAtsTeamWorkflow teamWf = null;
      for (IAtsTeamWorkflow team : AtsClientService.get().getWorkItemService().getTeams(result)) {
         if (team.getTeamDefinition().getName().contains("Code")) {
            teamWf = team;
         }
      }
      return teamWf;
   }

   public static Set<IAtsActionableItem> getActionableItems(DemoActionableItems demoActionableItems) {
      return AtsClientService.get().getActionableItemService().getActionableItems(
         Arrays.asList(demoActionableItems.getName()));
   }

   public static IAtsActionableItem getActionableItem(DemoActionableItems demoActionableItems) {
      return getActionableItems(demoActionableItems).iterator().next();
   }

   public static IAtsTeamWorkflow addTeamWorkflow(IAtsAction action, String title, IAtsChangeSet changes) {
      Set<IAtsActionableItem> actionableItems = getActionableItems(DemoActionableItems.SAW_Test);
      Collection<IAtsTeamDefinition> teamDefs = TeamDefinitions.getImpactedTeamDefs(actionableItems);

      AtsClientService.get().getActionFactory().createTeamWorkflow(action, teamDefs.iterator().next(), actionableItems,
         Arrays.asList(AtsClientService.get().getUserService().getCurrentUser()), changes, new Date(),
         AtsClientService.get().getUserService().getCurrentUser(), null);

      IAtsTeamWorkflow teamArt = null;
      for (IAtsTeamWorkflow team : AtsClientService.get().getWorkItemService().getTeams(action)) {
         if (team.getTeamDefinition().getName().contains("Test")) {
            teamArt = team;
         }
      }
      return teamArt;
   }

   /**
    * Create tasks named title + <num>
    */
   public static Collection<TaskArtifact> createSimpleTasks(TeamWorkFlowArtifact teamArt, String title, int numTasks, String relatedToState) throws Exception {
      List<String> names = new ArrayList<>();
      for (int x = 1; x < numTasks + 1; x++) {
         names.add(title + " " + x);
      }
      Collection<IAtsTask> createTasks = AtsClientService.get().getTaskService().createTasks(teamArt, names,
         Arrays.asList(AtsClientService.get().getUserService().getCurrentUser()), new Date(),
         AtsClientService.get().getUserService().getCurrentUser(), relatedToState, null, null,
         "DemoTestUtil.creatSimpleTasks");
      return Collections.castAll(createTasks);
   }

   public static TeamWorkFlowArtifact getToolsTeamWorkflow() {
      if (toolsTeamWorkflow == null) {
         for (Artifact art : ArtifactQuery.getArtifactListFromName("Button S doesn't work on help",
            AtsClientService.get().getAtsBranch())) {
            if (art.isOfType(AtsArtifactTypes.TeamWorkflow)) {
               toolsTeamWorkflow = (TeamWorkFlowArtifact) art;
            }
         }
      }
      return toolsTeamWorkflow;
   }

   public static Artifact getUncommittedActionWorkflow(DemoWorkType demoWorkType) {
      if (unCommittedWorkflows == null) {
         unCommittedWorkflows = new HashMap<>();
         for (Artifact art : DemoUtil.getSawUnCommittedTeamWfs()) {
            if (art.isOfType(AtsDemoOseeTypes.DemoCodeTeamWorkflow)) {
               unCommittedWorkflows.put(DemoWorkType.Code, art);
            } else if (art.isOfType(AtsDemoOseeTypes.DemoTestTeamWorkflow)) {
               unCommittedWorkflows.put(DemoWorkType.Test, art);
            } else if (art.isOfType(AtsDemoOseeTypes.DemoReqTeamWorkflow)) {
               unCommittedWorkflows.put(DemoWorkType.Requirements, art);
            } else if (art.isOfType(AtsArtifactTypes.TeamWorkflow)) {
               unCommittedWorkflows.put(DemoWorkType.SW_Design, art);
            }
         }
      }
      return unCommittedWorkflows.get(demoWorkType);
   }

   public static Artifact getCommittedActionWorkflow(DemoWorkType demoWorkType) {
      if (committedWorkflows == null) {
         committedWorkflows = new HashMap<>();
         for (Artifact art : DemoUtil.getSawCommittedTeamWfs()) {
            if (art.isOfType(AtsDemoOseeTypes.DemoCodeTeamWorkflow)) {
               committedWorkflows.put(DemoWorkType.Code, art);
            } else if (art.isOfType(AtsDemoOseeTypes.DemoTestTeamWorkflow)) {
               committedWorkflows.put(DemoWorkType.Test, art);
            } else if (art.isOfType(AtsDemoOseeTypes.DemoReqTeamWorkflow)) {
               committedWorkflows.put(DemoWorkType.Requirements, art);
            } else if (art.isOfType(AtsArtifactTypes.TeamWorkflow)) {
               committedWorkflows.put(DemoWorkType.SW_Design, art);
            }
         }
      }
      return committedWorkflows.get(demoWorkType);
   }

   public static IAtsTeamDefinition getTeamDef(ArtifactId artifactId) {
      IAtsTeamDefinition teamDef = null;
      // Add check to keep exception from occurring for OSEE developers running against production
      if (!ClientSessionManager.isProductionDataStore()) {
         try {
            teamDef = AtsClientService.get().getTeamDefinitionService().getTeamDefinitionById(artifactId);
         } catch (Exception ex) {
            OseeLog.log(DemoTestUtil.class, Level.SEVERE, ex);
         }
      }
      return teamDef;
   }

   public static void assertTypes(Collection<? extends Object> objects, int count, Class<?> clazz) {
      assertTypes(objects, count, clazz, "Expected %d; Found %d", count, numOfType(objects, clazz));
   }

   public static void assertTypes(Collection<? extends Object> objects, int count, Class<?> clazz, String message, Object... data) {
      int found = numOfType(objects, clazz);
      if (count != found) {
         throw new OseeStateException(message, data);
      }
   }

   public static int numOfType(Collection<? extends Object> objects, Class<?> clazz) {
      int num = 0;
      for (Object obj : objects) {
         if (clazz.isInstance(obj)) {
            num++;
         }
      }
      return num;
   }

}
