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

package org.eclipse.osee.ats.rest.internal.workitem.pr;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.pr.PrViewData;
import org.eclipse.osee.ats.rest.AtsApiServer;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactResultRow;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Donald G. Dunne
 */
public class AmsPrOperations {

   private final AtsApiServer atsApi;
   private final OrcsApi orcsApi;
   private String programName;

   public AmsPrOperations(AtsApiServer atsApi) {
      this.atsApi = atsApi;
      this.orcsApi = atsApi.getOrcsApi();
   }

   public PrViewData getPrView(PrViewData prViewData) {
      //      prViewData.getRd().error("This is my error");

      Collection<ArtifactReadable> prTeamWfArts = orcsApi.getQueryFactory().fromBranch(atsApi.getAtsBranch()) //
         .andIds(prViewData.getPrWfs()) //
         .follow(AtsRelationTypes.ProblemReportToBid_Bid) //
         // followFork because the next two follows both start at Bids
         // eg: follow Bid <--> Version and Bid <--> TeamWf
         .followFork(AtsRelationTypes.BuildImpactDataToVer_Version) //
         .followFork(AtsRelationTypes.BuildImpactDataToTeamWf_TeamWf) //
         .asArtifacts();

      IAtsTeamWorkflow prTeamWf = atsApi.getWorkItemService().getTeamWf(prTeamWfArts.iterator().next());
      IAtsProgram program = atsApi.getProgramService().getProgram(prTeamWf);
      if (program != null) {
         programName = program.getName();
      } else {
         prViewData.getRd().errorf("Error getting program from teamWf %s", prTeamWf.toStringWithAtsId());
         return prViewData;
      }

      // Headers may or may not be used for UI but are here if needed
      prViewData.getHeaders().addAll(
         Arrays.asList("Artifact Type", "ATS ID", "Program", "Title/Name", "State", "Build Impact Status", "ID"));

      for (ArtifactReadable prTeamWfArt : prTeamWfArts) {
         prTeamWf = atsApi.getWorkItemService().getTeamWf(prTeamWfArt);
         ArtifactResultRow row = new ArtifactResultRow( //
            // art id,
            prTeamWfArt.getToken(), //
            // branch token (need so ARRs can be passed around and loaded with art token and branch
            atsApi.getAtsBranchToken(),
            // art type
            prTeamWfArt.getArtifactType(), //
            // columns
            prTeamWfArt.getArtifactType().getName(), //
            prTeamWf.getAtsId(), //
            programName, //
            prTeamWf.getName(), //
            prTeamWf.getCurrentStateName(), //
            "", //
            prTeamWf.getIdString() //
         );
         prViewData.add(row);
         addBidsItems(prTeamWfArt, prTeamWf, row);
      }

      return prViewData;
   }

   private void addBidsItems(ArtifactReadable teamWfArt, IAtsTeamWorkflow teamWf, ArtifactResultRow parentRow) {
      for (ArtifactReadable bidArt : teamWfArt.getRelated(AtsRelationTypes.ProblemReportToBid_Bid)) {

         ArtifactResultRow bidRow = new ArtifactResultRow();
         bidRow.setArtifact(bidArt.getToken());
         bidRow.setArtType(bidArt.getArtifactType());
         bidRow.setBranch(atsApi.getAtsBranchToken());
         parentRow.getChildren().add(bidRow);
         String bidStatus = addTeamWfs(bidArt, bidRow);
         bidRow.addValues( //
            // columns
            bidArt.getArtifactType().getName(), //
            "", //
            programName, //
            bidArt.getName(), //
            atsApi.getAttributeResolver().getSoleAttributeValueAsString(bidArt, AtsAttributeTypes.BitState, ""), //
            bidStatus, //
            bidArt.getIdString() //
         );
      }
   }

   private String addTeamWfs(ArtifactReadable bidArt, ArtifactResultRow bidRow) {
      int count = 0, completed = 0;
      for (ArtifactReadable teamWfArt : bidArt.getRelated(AtsRelationTypes.BuildImpactDataToTeamWf_TeamWf)) {
         IAtsTeamWorkflow teamWf = atsApi.getWorkItemService().getTeamWf(teamWfArt);
         ArtifactResultRow teamWfRow = new ArtifactResultRow( //
            // art id,
            teamWfArt.getToken(), //
            // branch token (need so ARRs can be passed around and loaded with art token and branch
            atsApi.getAtsBranchToken(), //
            // art type
            teamWfArt.getArtifactType(), //
            // columns
            teamWfArt.getArtifactType().getName(), //
            teamWf.getAtsId(), //
            programName, //
            teamWfArt.getName(), //
            teamWf.getCurrentStateName(), //
            "", //
            teamWfArt.getIdString() //
         );
         count++;
         if (teamWf.isCompletedOrCancelled()) {
            completed++;
         }
         bidRow.getChildren().add(teamWfRow);
      }
      return String.format("%s of %s Completed", completed, count);
   }

}
