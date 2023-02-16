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

import java.util.Arrays;
import java.util.Date;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.config.JaxTeamWorkflow;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.team.CreateTeamOption;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.cr.bit.model.BuildImpactData;
import org.eclipse.osee.ats.api.workflow.cr.bit.model.BuildImpactDatas;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.jdk.core.util.Collections;
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
         bids.getResults().errorf("Invalid ATS Id [%s]\n", bids.getTeamWf());
      }
      if (bids.getResults().isErrors()) {
         return bids;
      }
      try {
         AtsUser currentUser = atsApi.getUserService().getCurrentUser();
         IAtsChangeSet changes = atsApi.createChangeSet("Create Build Impact Data", currentUser);

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
               changes.relate(teamWf, AtsRelationTypes.BuildImpactTableToData_Bid, bidArt);
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

            // Create/Update configs
            changes.setAttributeValues(bidArt, AtsAttributeTypes.BitConfig, Collections.castAll(bid.getConfigs()));

            // Create/update state
            if (Strings.isValid(bid.getState())) {
               changes.setSoleAttributeValue(bidArt, AtsAttributeTypes.BitState, bid.getState());
            }

            for (JaxTeamWorkflow jTeamWf : bid.getTeamWfs()) {
               if (jTeamWf.getNewAi().isValid()) {
                  IAtsActionableItem ai = atsApi.getActionableItemService().getActionableItemById(jTeamWf.getNewAi());
                  IAtsTeamDefinition teamDef = atsApi.getTeamDefinitionService().getImpactedTeamDef(ai);
                  Date createdDate = new Date();
                  IAtsTeamWorkflow newTeamWf =
                     atsApi.getActionService().createTeamWorkflow(teamWf.getParentAction(), teamDef, Arrays.asList(ai),
                        null, changes, createdDate, currentUser, null, CreateTeamOption.Duplicate_If_Exists);
                  ArtifactToken tarVer = jTeamWf.getTargetVersion();
                  if (tarVer.isValid()) {
                     IAtsVersion version = atsApi.getVersionService().getVersionById(tarVer);
                     atsApi.getVersionService().setTargetedVersion(newTeamWf, version, changes);
                  }
                  String pts = jTeamWf.getPriority();
                  if (Strings.isValid(pts)) {
                     changes.setSoleAttributeValue(newTeamWf, AtsAttributeTypes.Priority, pts);
                  }
                  populateJaxTeamWf(jTeamWf, newTeamWf);
                  changes.relate(bidArt, AtsRelationTypes.BuildImpactDataToTeamWf_TeamWf, newTeamWf);
               }
            }
         }

         TransactionId transaction = changes.execute();
         // Reload latest work item
         teamWf = atsApi.getWorkItemService().getTeamWf(atsApi.getQueryService().getArtifact(teamWf.getId()));

         bids.setTransaction(TransactionId.valueOf(transaction.getId()));
      } catch (Exception ex) {
         bids.getResults().errorf("Exception adding bids %s", Lib.exceptionToString(ex));
      }
      return bids;
   }

   private void populateJaxTeamWf(JaxTeamWorkflow jTeamWf, IAtsTeamWorkflow newTeamWf) {
      jTeamWf.setTeam(newTeamWf.getTeamDefinition().getStoreObject());
      jTeamWf.setAtsId(newTeamWf.getAtsId());
      jTeamWf.setName(newTeamWf.getName());
      jTeamWf.setId(newTeamWf.getId());
      jTeamWf.setStateType(newTeamWf.getCurrentStateType());
      jTeamWf.setCurrentState(newTeamWf.getCurrentStateName());
   }

   public BuildImpactDatas getBids(String atsId) {
      BuildImpactDatas bids = new BuildImpactDatas();
      IAtsTeamWorkflow teamWf = (IAtsTeamWorkflow) atsApi.getWorkItemService().getWorkItemByAtsId(atsId);
      if (teamWf == null) {
         bids.getResults().errorf("Invalid ATS Id [%s]", bids.getTeamWf());
      }
      if (bids.getResults().isErrors()) {
         return bids;
      }
      if (teamWf != null) {
         bids.setTeamWf(teamWf.getStoreObject());
      }

      if (teamWf == null) {
         throw new RuntimeException("teamWf is null");
      }
      IAtsProgram program = atsApi.getProgramService().getProgram(teamWf);
      if (program == null || program.isInvalid()) {
         bids.getResults().errorf("No Program found for workflow %s", teamWf.toStringWithAtsId());
         return bids;
      }
      BranchToken branch = atsApi.getProgramService().getProductLineBranch(program);
      if (branch.isInvalid()) {
         bids.getResults().errorf("No PL Branch found for program %s", program.toStringWithId());
         return bids;
      }
      for (ArtifactToken view : orcsApi.getQueryFactory().applicabilityQuery().getViewsForBranch(branch)) {
         bids.addConfig(view);
      }

      for (ArtifactToken bidArt : atsApi.getRelationResolver().getRelated(teamWf,
         AtsRelationTypes.BuildImpactTableToData_Bid)) {
         BuildImpactData bid = new BuildImpactData();
         bid.setBidArt(bidArt);

         for (Object obj : atsApi.getAttributeResolver().getAttributeValues(bidArt, AtsAttributeTypes.BitConfig)) {
            ArtifactId configArt = (ArtifactId) obj;
            ArtifactToken config = bids.getIdToConfig().get(configArt.getId());
            bid.getConfigs().add(config);
         }

         bid.setState(atsApi.getAttributeResolver().getSoleAttributeValue(bidArt, AtsAttributeTypes.BitState, ""));

         // Populate related version
         ArtifactToken verArt =
            atsApi.getRelationResolver().getRelatedOrSentinel(bidArt, AtsRelationTypes.BuildImpactDataToVer_Version);
         bid.setBuild(verArt);
         bids.addBuildImpactData(bid);
         if (verArt != null) {
            IAtsVersion version = atsApi.getVersionService().getVersionById(verArt);
            IAtsTeamDefinition teamDef = atsApi.getVersionService().getTeamDefinition(version);
            IAtsProgram prog = atsApi.getProgramService().getProgram(teamDef);
            if (prog != null) {
               bid.setProgram(prog.getArtifactToken());
            }
         }

         // Populate related teamWf(s)
         for (ArtifactToken bidTeamWfArt : atsApi.getRelationResolver().getRelated(bidArt,
            AtsRelationTypes.BuildImpactDataToTeamWf_TeamWf)) {
            if (bidTeamWfArt.isValid()) {
               IAtsTeamWorkflow bidTeamWf = atsApi.getWorkItemService().getTeamWf(bidTeamWfArt);

               JaxTeamWorkflow jTeamWf = new JaxTeamWorkflow();
               bid.getTeamWfs().add(jTeamWf);
               populateJaxTeamWf(jTeamWf, bidTeamWf);
            }
         }
      }

      return bids;
   }

   public BuildImpactDatas deleteBids(BuildImpactDatas bids) {
      if (bids.getTeamWf() == null) {
         bids.getResults().errorf("Must specify Team Workflow\n", bids.getTeamWf());
         return bids;
      }
      IAtsTeamWorkflow teamWf = atsApi.getWorkItemService().getTeamWf(bids.getTeamWf());
      if (teamWf == null) {
         bids.getResults().errorf("Invalid ATS Id [%s]\n", bids.getTeamWf());
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
