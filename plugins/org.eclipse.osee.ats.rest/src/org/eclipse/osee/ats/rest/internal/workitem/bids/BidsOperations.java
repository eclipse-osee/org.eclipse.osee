/*********************************************************************
 * Copyright (c) 2021 Boeing
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
package org.eclipse.osee.ats.rest.internal.workitem.bids;

import java.util.Collection;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.config.JaxTeamWorkflow;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.program.JaxProgram;
import org.eclipse.osee.ats.api.task.JaxAttribute;
import org.eclipse.osee.ats.api.team.CreateOption;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.NewActionData;
import org.eclipse.osee.ats.api.workflow.NewActionDataMulti;
import org.eclipse.osee.ats.api.workflow.cr.bit.model.BuildImpactData;
import org.eclipse.osee.ats.api.workflow.cr.bit.model.BuildImpactDatas;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.ElapsedTime;
import org.eclipse.osee.framework.jdk.core.util.ElapsedTime.Units;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Donald G. Dunne
 */
public class BidsOperations {

   private final AtsApi atsApi;
   private final OrcsApi orcsApi;

   public BidsOperations(AtsApi atsApi, OrcsApi orcsApi) {
      this.atsApi = atsApi;
      this.orcsApi = orcsApi;
   }

   public BuildImpactDatas createBids(BuildImpactDatas bids) {
      if (bids.getTeamWf() == null) {
         bids.getResults().errorf("Must specify Team Workflow\n", bids.getTeamWf());
         return bids;
      }
      IAtsTeamWorkflow teamWf = atsApi.getWorkItemService().getTeamWf(bids.getTeamWf());
      if (teamWf == null) {
         bids.getResults().errorf("Invalid Team Workflow [%s]\n", bids.getTeamWf());
         return bids;
      }
      if (bids.getResults().isErrors()) {
         return bids;
      }
      try {
         AtsUser createdBy = atsApi.getUserService().getCurrentUser();
         String opName = "Create BIDs";
         IAtsChangeSet changes = atsApi.createChangeSet(opName, createdBy);

         for (BuildImpactData bid : bids.getBuildImpacts()) {
            ArtifactToken bidArt = null;
            // Get existing bid
            if (bid.getBidArt().getId() > 0) {
               bidArt = atsApi.getQueryService().getArtifact(bid.getBidArt().getId());
            }
            // Or create if missing and relate
            else {
               bidArt = changes.createArtifact(bids.getBidArtType(), bid.getBidArt().getName());
               bid.setBidArt(bidArt);
               changes.relate(teamWf, AtsRelationTypes.ProblemReportToBid_Bid, bidArt);
               // Relate version to bid
               ArtifactToken verArt = atsApi.getQueryService().getArtifact(bid.getBuild());
               if (verArt != null) {
                  changes.relate(bidArt, AtsRelationTypes.BuildImpactDataToVer_Version, verArt);
                  IAtsVersion version = atsApi.getVersionService().getVersionById(verArt);
                  IAtsTeamDefinition teamDef = atsApi.getVersionService().getTeamDefinition(version);
                  IAtsProgram program = atsApi.getProgramService().getProgram(teamDef);
                  if (program != null) {
                     bid.setProgram(program.getArtifactToken());
                  }
               }
            }

            // Create/update state
            if (Strings.isValid(bid.getState())) {
               changes.setSoleAttributeValue(bidArt, AtsAttributeTypes.BitState, bid.getState());
            }

            NewActionDataMulti datas = new NewActionDataMulti(opName, atsApi.user());
            datas.setChanges(changes);
            datas.setPersist(false);
            for (JaxTeamWorkflow jTeamWf : bid.getTeamWfs()) {
               if (jTeamWf.getNewAi().isValid()) {
                  IAtsActionableItem ai = atsApi.getActionableItemService().getActionableItemById(jTeamWf.getNewAi());
                  Conditions.assertNotNull(ai, "AI [%s] not found", jTeamWf.getNewAi());
                  IAtsTeamDefinition teamDef = atsApi.getTeamDefinitionService().getImpactedTeamDef(ai);
                  Conditions.assertNotNull(ai, "Team Def not found from AI %s", ai.toStringWithId());
                  ArtifactToken tarVer = jTeamWf.getTargetVersion();

                  NewActionData data = atsApi.getActionService() //
                     .createTeamWfData(opName, teamWf.getParentAction(), teamDef) //
                     .andAi(ai).andCreateOption(CreateOption.Duplicate_If_Exists) //
                     .andVersion(tarVer.isValid() ? tarVer : null) //
                     .andPriority(jTeamWf.getPriority()) //
                     .andRelation(AtsRelationTypes.BuildImpactDataToTeamWf_Bid, bidArt);

                  for (JaxAttribute jAttr : jTeamWf.getAttributes()) {
                     data.andAttr(jAttr.getAttrType(), Collections.castAll(jAttr.getValues()));
                  }
                  datas.add(data);
               }
            }
            NewActionDataMulti newDatas = atsApi.getActionService().createActions(datas);
            bids.getResults().merge(newDatas.getRd());
            if (newDatas.getRd().isErrors()) {
               return bids;
            }
         }
         TransactionToken tx = changes.execute();
         bids.setTransaction(tx);
      } catch (Exception ex) {
         bids.getResults().errorf("Exception adding bids %s", Lib.exceptionToString(ex));
      }
      return bids;
   }

   public BuildImpactDatas getBidParents(ArtifactId twId) {
      BuildImpactDatas bids = new BuildImpactDatas();
      IAtsTeamWorkflow teamWf = (IAtsTeamWorkflow) atsApi.getWorkItemService().getWorkItem(twId.getId());
      if (teamWf == null) {
         bids.getResults().errorf("Invalid TW Id [%s]", twId);
      }
      // TBD Convert this to fast performance follows with Artifact Readable when needed
      for (ArtifactToken bidArtTok : atsApi.getRelationResolver().getRelated(teamWf,
         AtsRelationTypes.BuildImpactDataToTeamWf_Bid)) {
         ArtifactReadable bidArt = (ArtifactReadable) bidArtTok;
         BuildImpactData bid = getBid(bidArt, null, bids.getResults(), false);
         bids.getBuildImpacts().add(bid);
      }
      return bids;
   }

   public BuildImpactDatas getBids(ArtifactId twId) {
      BuildImpactDatas bids = new BuildImpactDatas();
      return getBids(twId, bids);
   }

   public BuildImpactDatas getBids(ArtifactId prTeamWfId, BuildImpactDatas bids) {
      if (bids.getResults().isErrors()) {
         return bids;
      }
      if (prTeamWfId == null) {
         bids.getResults().error("teamWfId can not be null");
         return bids;
      }
      boolean debugOn = false;

      ElapsedTime all = new ElapsedTime("getBids - ALL", debugOn);

      ElapsedTime time5 = new ElapsedTime("Perform BIDs Query w/ Follows", debugOn);
      // Follows query returns what you searched for in andId, and loads all related from follows calls
      Collection<ArtifactToken> teamWfArts =
         Collections.castAll(orcsApi.getQueryFactory().fromBranch(atsApi.getAtsBranch()) //
            .andId(prTeamWfId) //
            .follow(AtsRelationTypes.ProblemReportToBid_Bid) //
            // followFork because the next two follows both start at Bids
            // eg: follow Bid <--> Version and Bid <--> TeamWf
            .followFork(AtsRelationTypes.BuildImpactDataToVer_Version) //
            .followFork(AtsRelationTypes.BuildImpactDataToTeamWf_TeamWf) //
            .asArtifacts());
      ArtifactReadable teamWfArt = (ArtifactReadable) teamWfArts.iterator().next();
      time5.end();
      if (!teamWfArt.isOfType(AtsArtifactTypes.ProblemReportTeamWorkflow)) {
         bids.getResults().errorf("TeamWfId must be PrTeamWorkflow, not [%s]", teamWfArt.toStringWithId());
         return bids;
      }

      IAtsTeamWorkflow teamWf = atsApi.getWorkItemService().getTeamWf(teamWfArt);
      bids.setTeamWf(teamWfArt.getToken());

      ElapsedTime time1 = new ElapsedTime("Get Program", debugOn);
      IAtsProgram program = atsApi.getProgramService().getProgram(teamWf);
      if (program == null || program.isInvalid()) {
         bids.getResults().errorf("No Program found for workflow %s", teamWf.toStringWithAtsId());
         return bids;
      }
      time1.end();

      ResultSet<ArtifactReadable> bidArtsNew = teamWfArt.getRelated(AtsRelationTypes.ProblemReportToBid_Bid);

      for (ArtifactReadable bidArt : bidArtsNew) {
         BuildImpactData bid = getBid(bidArt, prTeamWfId, bids.getResults(), debugOn);
         bids.addBuildImpactData(bid);
      }
      time5.end();

      all.end(Units.SEC);

      return bids;
   }

   public BuildImpactData getBid(ArtifactReadable bidArt, ArtifactId prTeamWfId, XResultData rd, boolean debugOn) {
      ElapsedTime time = new ElapsedTime("BidOperations.getBid", debugOn);
      BuildImpactData bid = new BuildImpactData();
      bid.setBidArt(bidArt.getToken());

      bid.setState(atsApi.getAttributeResolver().getSoleAttributeValueAsString(bidArt, AtsAttributeTypes.BitState, ""));

      // Populate related version
      ArtifactToken verArt =
         atsApi.getRelationResolver().getRelatedOrSentinel(bidArt, AtsRelationTypes.BuildImpactDataToVer_Version);
      if (verArt != null) {
         bid.setBuild(verArt.getToken());
         ArtifactId progId = atsApi.getAttributeResolver().getSoleAttributeValue(verArt, AtsAttributeTypes.ProgramId,
            ArtifactId.SENTINEL);
         if (progId.isInvalid()) {
            rd.errorf("Version artifact %s must have ProgramId set", verArt.toStringWithId());
         } else {
            JaxProgram program = atsApi.getConfigService().getConfigurations().getIdToProgram().get(progId.getId());
            bid.setProgram(ArtifactToken.valueOf(progId, program.getName()));
         }
      }

      // Populate related teamWf(s)
      for (ArtifactReadable bidTeamWfArt : bidArt.getRelated(AtsRelationTypes.BuildImpactDataToTeamWf_TeamWf)) {
         if (bidTeamWfArt.isValid()) {
            IAtsTeamWorkflow bidTeamWf = atsApi.getWorkItemService().getTeamWf(bidTeamWfArt);

            JaxTeamWorkflow jTeamWf = new JaxTeamWorkflow();
            bid.getTeamWfs().add(jTeamWf);
            ArtifactId teamDefId = bidTeamWfArt.getSoleAttributeValue(AtsAttributeTypes.TeamDefinitionReference);
            // Load TeamDef token from configurations so don't need another query
            if (teamDefId.isValid()) {
               IAtsTeamDefinition teamDef =
                  atsApi.getConfigService().getConfigurations().getIdToTeamDef().get(teamDefId.getId());
               if (teamDef != null) {
                  jTeamWf.setTeam(teamDef.getArtifactToken());
               }
            }
            atsApi.getWorkItemService().populateJaxTeamWf(jTeamWf, bidTeamWf);
         }
      }
      time.end(Units.MSEC);
      return bid;
   }

   public BuildImpactDatas deleteBids(BuildImpactDatas bids) {
      if (bids.getTeamWf().isInvalid()) {
         bids.getResults().errorf("Must specify Team Workflow\n", bids.getTeamWf());
         return bids;
      }
      IAtsTeamWorkflow teamWf = atsApi.getWorkItemService().getTeamWf(bids.getTeamWf());
      if (teamWf == null) {
         bids.getResults().errorf("Invalid Team Workflow [%s]\n", bids.getTeamWf());
      }
      if (bids.getResults().isErrors()) {
         return bids;
      }
      try {
         AtsUser currentUser = atsApi.getUserService().getCurrentUser();
         IAtsChangeSet changes = atsApi.createChangeSet("Create Build Impact Data", currentUser);

         for (BuildImpactData bid : bids.getBuildImpacts()) {
            if (bid.getBidArt().isInvalid()) {
               bids.getResults().errorf("Invalid BID Artifact Token");
            }
            changes.deleteArtifact(ArtifactId.valueOf(bid.getBidArt().getId()));
         }
         if (bids.getResults().isErrors()) {
            return bids;
         }

         TransactionToken transaction = changes.executeIfNeeded();

         bids.setTransaction(TransactionId.valueOf(transaction.getId()));
      } catch (Exception ex) {
         bids.getResults().errorf("Exception adding bids %s", Lib.exceptionToString(ex));
      }
      return bids;

   }

}
