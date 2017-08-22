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
package org.eclipse.osee.ats.client.demo.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.client.demo.DemoUtil;
import org.eclipse.osee.ats.client.demo.internal.Activator;
import org.eclipse.osee.ats.client.demo.internal.AtsClientService;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.demo.api.DemoArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.UniversalGroup;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;

/**
 * @author Donald G. Dunne
 */
public class DemoDbGroups {
   private static String TEST_GROUP_NAME = "Test Group";

   public static List<TeamWorkFlowArtifact> createGroups(boolean DEBUG) throws Exception {

      SkynetTransaction transaction = TransactionManager.createTransaction(AtsClientService.get().getAtsBranch(),
         "Populate Demo DB - Create Groups");

      // Create group of all resulting objects
      List<TeamWorkFlowArtifact> codeWorkflows = new ArrayList<>();
      if (DEBUG) {
         OseeLog.log(Activator.class, Level.INFO, "Create Groups and add objects");
      }
      Artifact groupArt = UniversalGroup.addGroup(TEST_GROUP_NAME, AtsClientService.get().getAtsBranch(), transaction);
      for (TeamWorkFlowArtifact codeArt : Arrays.asList(DemoUtil.getSawCodeCommittedWf(),
         DemoUtil.getSawCodeUnCommittedWf())) {

         // Add Action to Universal Group
         groupArt.addRelation(CoreRelationTypes.Universal_Grouping__Members, codeArt.getParentActionArtifact());

         // Add All Team Workflows to Universal Group
         for (IAtsTeamWorkflow teamWf : AtsClientService.get().getWorkItemService().getTeams(
            codeArt.getParentActionArtifact())) {
            groupArt.addRelation(CoreRelationTypes.Universal_Grouping__Members, (Artifact) teamWf.getStoreObject());
         }

         codeArt.persist(transaction);
      }

      // Add all Tasks to Group
      for (Artifact task : ArtifactQuery.getArtifactListFromType(AtsArtifactTypes.Task,
         AtsClientService.get().getAtsBranch())) {
         groupArt.addRelation(CoreRelationTypes.Universal_Grouping__Members, task);
      }
      groupArt.persist(transaction);
      transaction.execute();

      Collection<Artifact> members = groupArt.getRelatedArtifacts(CoreRelationTypes.Universal_Grouping__Members);
      Conditions.assertEquals(23, members.size(), "Group Members count not expected");

      Conditions.assertEquals(2, Artifacts.getOfType(AtsArtifactTypes.Action, members).size());
      Conditions.assertEquals(14, Artifacts.getOfType(AtsArtifactTypes.Task, members).size());
      Conditions.assertEquals(2, Artifacts.getOfType(DemoArtifactTypes.DemoCodeTeamWorkflow, members).size());
      Conditions.assertEquals(2, Artifacts.getOfType(DemoArtifactTypes.DemoTestTeamWorkflow, members).size());
      Conditions.assertEquals(2, Artifacts.getOfType(DemoArtifactTypes.DemoReqTeamWorkflow, members).size());
      Conditions.assertEquals(7, Artifacts.getOfType(AtsArtifactTypes.TeamWorkflow, members).size());

      return codeWorkflows;
   }
}
