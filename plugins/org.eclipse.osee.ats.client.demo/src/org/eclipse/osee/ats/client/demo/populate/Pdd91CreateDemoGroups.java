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
package org.eclipse.osee.ats.client.demo.populate;

import java.util.Arrays;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.client.demo.DemoUtil;
import org.eclipse.osee.ats.client.demo.internal.AtsClientService;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.demo.api.DemoArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.UniversalGroup;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;

/**
 * @author Donald G. Dunne
 */
public class Pdd91CreateDemoGroups {

   public void run() {

      SkynetTransaction transaction =
         TransactionManager.createTransaction(AtsClientService.get().getAtsBranch(), getClass().getSimpleName());

      // Create group of all resulting objects
      Artifact groupArt =
         UniversalGroup.addGroup(DemoArtifactToken.Test_Group, AtsClientService.get().getAtsBranch(), transaction);
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

   }
}
