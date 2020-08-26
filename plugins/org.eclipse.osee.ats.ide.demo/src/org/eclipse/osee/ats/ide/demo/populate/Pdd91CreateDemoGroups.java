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

package org.eclipse.osee.ats.ide.demo.populate;

import java.util.Arrays;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.demo.DemoUtil;
import org.eclipse.osee.ats.ide.demo.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
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
         TransactionManager.createTransaction(AtsApiService.get().getAtsBranch(), getClass().getSimpleName());

      // Create group of all resulting objects
      Artifact groupArt =
         UniversalGroup.addGroup(DemoArtifactToken.Test_Group, AtsApiService.get().getAtsBranch(), transaction);
      for (TeamWorkFlowArtifact codeArt : Arrays.asList(DemoUtil.getSawCodeCommittedWf(),
         DemoUtil.getSawCodeUnCommittedWf())) {

         // Add Action to Universal Group
         groupArt.addRelation(CoreRelationTypes.UniversalGrouping_Members,
            (Artifact) codeArt.getParentAction().getStoreObject());

         // Add All Team Workflows to Universal Group
         for (IAtsTeamWorkflow teamWf : AtsApiService.get().getWorkItemService().getTeams(
            codeArt.getParentAction().getStoreObject())) {
            groupArt.addRelation(CoreRelationTypes.UniversalGrouping_Members,
               AtsApiService.get().getQueryServiceIde().getArtifact(teamWf));
         }

         codeArt.persist(transaction);
      }

      // Add all Tasks to Group
      for (Artifact task : ArtifactQuery.getArtifactListFromType(AtsArtifactTypes.Task,
         AtsApiService.get().getAtsBranch())) {
         groupArt.addRelation(CoreRelationTypes.UniversalGrouping_Members, task);
      }
      groupArt.persist(transaction);
      transaction.execute();

   }
}
