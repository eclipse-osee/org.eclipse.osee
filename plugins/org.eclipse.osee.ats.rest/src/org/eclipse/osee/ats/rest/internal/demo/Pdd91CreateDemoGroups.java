/*********************************************************************
 * Copyright (c) 2025 Boeing
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

package org.eclipse.osee.ats.rest.internal.demo;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.demo.DemoUtil;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public class Pdd91CreateDemoGroups extends AbstractPopulateDemoDatabase {

   public Pdd91CreateDemoGroups(XResultData rd, AtsApi atsApi) {
      super(rd, atsApi);
   }

   @Override
   public void run() {

      IAtsChangeSet changes = atsApi.createChangeSet(getClass().getSimpleName());

      // Create group of all resulting objects
      ArtifactToken groupArt =
         atsApi.getGroupService().addGroup(DemoArtifactToken.Test_Group, atsApi.getAtsBranch(), changes);

      List<IAtsTeamWorkflow> teamWfs = new ArrayList<>();
      teamWfs.add(DemoUtil.getSawCodeCommittedWf());
      teamWfs.add(DemoUtil.getSawCodeUnCommittedWf());

      for (IAtsTeamWorkflow codeArt : teamWfs) {
         // Add Action to Universal Group
         changes.relate(groupArt, CoreRelationTypes.UniversalGrouping_Members,
            codeArt.getParentAction().getStoreObject());

         // Add All Team Workflows to Universal Group
         for (IAtsTeamWorkflow teamWf : atsApi.getWorkItemService().getTeams(codeArt.getParentAction())) {
            changes.relate(groupArt, CoreRelationTypes.UniversalGrouping_Members,
               atsApi.getQueryService().getArtifact(teamWf));
         }
      }

      // Add all Tasks to Group
      for (ArtifactToken task : atsApi.getQueryService().getArtifacts(AtsArtifactTypes.Task)) {
         changes.relate(groupArt, CoreRelationTypes.UniversalGrouping_Members, task);
      }

      changes.execute();

   }
}
