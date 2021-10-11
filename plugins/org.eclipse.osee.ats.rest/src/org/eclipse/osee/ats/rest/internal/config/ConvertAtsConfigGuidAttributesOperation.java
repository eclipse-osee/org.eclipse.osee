/*********************************************************************
 * Copyright (c) 2017 Boeing
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

package org.eclipse.osee.ats.rest.internal.config;

import static org.eclipse.osee.ats.api.data.AtsTypeTokenProvider.ats;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.ev.IAtsWorkPackage;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeString;
import org.eclipse.osee.framework.core.data.IAttribute;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * See ConvertAtsConfigGuidAttributes for explanation of what this operation does.
 *
 * @author Donald G. Dunne
 */
public class ConvertAtsConfigGuidAttributesOperation {

   private final AtsApi atsApi;

   // Types would need to be moved back temporarily to AtsAttributeTypes to be loaded correctly
   AttributeTypeString WorkPackageGuid =
      ats.createString(1152921504606847876L, "ats.Work Package Guid", MediaType.TEXT_PLAIN, "");
   private static AttributeTypeString ActionableItem = ats.createString(1152921504606847200L, "ats.Actionable Item",
      MediaType.TEXT_PLAIN, "Actionable Items that are impacted by this change.");
   private static AttributeTypeString TeamDefinition =
      ats.createString(1152921504606847201L, "ats.Team Definition", MediaType.TEXT_PLAIN, "");

   public ConvertAtsConfigGuidAttributesOperation(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   public XResultData createUpdateConfig(XResultData rd) {

      atsApi.getLogger().error("starting");

      // Convert Team Def and AIs for Team Workflow
      List<ArtifactId> artIdList = new LinkedList<>();
      artIdList.addAll(atsApi.getQueryService().createQuery(WorkItemType.TeamWorkflow).andNotExists(
         AtsAttributeTypes.TeamDefinitionReference).getItemIds());
      artIdList.addAll(
         atsApi.getQueryService().createQuery(WorkItemType.TeamWorkflow).andNotExists(TeamDefinition).getItemIds());
      List<Collection<ArtifactId>> subDivide = Collections.subDivide(artIdList, 2000);
      int size = subDivide.size(), count = 1;
      for (Collection<ArtifactId> artIds : subDivide) {
         atsApi.getLogger().info(String.format("processing %s / %s", count++, size));
         List<Long> ids = new LinkedList<>();
         for (ArtifactId art : artIds) {
            ids.add(art.getId());
         }
         Collection<ArtifactToken> allArtifacts = atsApi.getQueryService().getArtifacts(ids);
         IAtsChangeSet changes = atsApi.createChangeSet("Update TeamDef, AI and WorkPkg TeamWf GUIDs");
         for (ArtifactToken art : allArtifacts) {
            convertTeamDefinitionIfNeeded(changes, art, atsApi);
            convertActionableItemsIfNeeded(changes, art, atsApi);
            atsApi.getLogger().error("Work Item - " + art.toStringWithId());
         }
         TransactionId transaction = changes.executeIfNeeded();
         if (transaction != null && transaction.isValid()) {
            atsApi.getLogger().info("================================== > executed");
         }
         try {
            Thread.sleep(5 * 1000);
         } catch (InterruptedException ex) {
            // do nothing
         }
      }

      // convert work packages
      List<ArtifactId> artIdList2 = new LinkedList<>();
      artIdList2.addAll(getWorkItemIdsMissingWorkPacakge());
      List<Collection<ArtifactId>> subDivide2 = Collections.subDivide(artIdList2, 2000);
      int size2 = subDivide2.size(), count2 = 1;
      for (Collection<ArtifactId> artIds : subDivide2) {
         atsApi.getLogger().info(String.format("processing %s / %s", count2++, size2));
         List<Long> ids = new LinkedList<>();
         for (ArtifactId art : artIds) {
            ids.add(art.getId());
         }
         Collection<ArtifactToken> allArtifacts = atsApi.getQueryService().getArtifacts(ids);
         IAtsChangeSet changes = atsApi.createChangeSet("Update Work Package GUIDs");
         for (ArtifactToken art : allArtifacts) {
            convertWorkPackageIfNeeded(changes, art);
         }
         TransactionId transaction = changes.executeIfNeeded();
         if (transaction != null && transaction.isValid()) {
            atsApi.getLogger().info("================================== > executed");
         }
         try {
            Thread.sleep(5 * 1000);
         } catch (InterruptedException ex) {
            // do nothing
         }
      }

      // Convert Program Team Definition
      IAtsChangeSet changes = atsApi.createChangeSet("Update Program Team Def GUID");
      for (IAtsProgram program : atsApi.getQueryService().createQuery(AtsArtifactTypes.Program).getItems(
         IAtsProgram.class)) {
         convertTeamDefinitionIfNeeded(changes, program.getStoreObject(), atsApi);
      }
      changes.executeIfNeeded();

      // Delete AIs for Action, they shouldn't have them
      changes = atsApi.createChangeSet("Remove Action AI and TeamDef GUIDs");
      for (ArtifactToken actionArt : atsApi.getQueryService().getArtifacts(atsApi.getAtsBranch(), false,
         AtsArtifactTypes.Action)) {
         changes.deleteAttributes(actionArt, TeamDefinition);
         changes.deleteAttributes(actionArt, ActionableItem);
      }
      changes.executeIfNeeded();

      // Convert AIs for ats.Review (stand-alone reviews)
      changes = atsApi.createChangeSet("Update AIs for ats.Review GUIDs");
      for (IAtsAbstractReview program : atsApi.getQueryService().createQuery(WorkItemType.Review).getItems(
         IAtsAbstractReview.class)) {
         convertActionableItemsIfNeeded(changes, program.getStoreObject(), atsApi);
      }
      changes.executeIfNeeded();
      atsApi.getLogger().error("complete");
      return rd;
   }

   private Set<ArtifactId> getWorkItemIdsMissingWorkPacakge() {
      Collection<ArtifactId> haveWorkPackageGuid = atsApi.getQueryService().getArtifactIdsFromQuery(
         "SELECT UNIQUE attr.art_id FROM OSEE_ATTRIBUTE attr, OSEE_TXS txs WHERE attr.GAMMA_ID = txs.GAMMA_ID AND " //
            + "txs.BRANCH_ID = 570 AND txs.TX_CURRENT = 1 AND attr.ATTR_TYPE_ID = 1152921504606847876");

      Collection<ArtifactId> haveWorkPackageId = atsApi.getQueryService().getArtifactIdsFromQuery(
         "SELECT UNIQUE attr.art_id FROM OSEE_ATTRIBUTE attr, OSEE_TXS txs WHERE attr.GAMMA_ID = txs.GAMMA_ID AND " //
            + "txs.BRANCH_ID = 570 AND txs.TX_CURRENT = 1 AND attr.ATTR_TYPE_ID = 473096133909456789");

      Set<ArtifactId> missingWorkPackage = new HashSet<>();
      missingWorkPackage.addAll(Collections.setComplement(haveWorkPackageGuid, haveWorkPackageId));
      missingWorkPackage.addAll(Collections.setComplement(haveWorkPackageId, haveWorkPackageGuid));
      return missingWorkPackage;
   }

   boolean workPackagesLoaded = false;

   private void convertWorkPackageIfNeeded(IAtsChangeSet changes, ArtifactToken workItemArt) {
      if (!workPackagesLoaded) {
         for (ArtifactToken workPackageArt : atsApi.getQueryService().getArtifacts(atsApi.getAtsBranch(), false,
            AtsArtifactTypes.WorkPackage)) {
            IAtsWorkPackage workPkg = atsApi.getEarnedValueService().getWorkPackage(workPackageArt);
            guidToWorkPackage.put(workPkg.getStoreObject().getGuid(), workPkg);
         }
         workPackagesLoaded = true;
      }
      // convert guid to id
      ArtifactId workPackageId = atsApi.getAttributeResolver().getSoleArtifactIdReference(workItemArt,
         AtsAttributeTypes.WorkPackageReference, ArtifactId.SENTINEL);
      if (workPackageId.isInvalid()) {
         String workPackageGuid = atsApi.getAttributeResolver().getSoleAttributeValue(workItemArt, WorkPackageGuid, "");
         if (Strings.isValid(workPackageGuid)) {
            IAtsWorkPackage workPackage = guidToWorkPackage.get(workPackageGuid);
            if (workPackage == null) {
               atsApi.getLogger().error(String.format("Work Package null for guid %s; deleting attribute for %s",
                  workPackageGuid, workItemArt.toStringWithId()));
               changes.deleteAttributes(workItemArt, WorkPackageGuid);
               return;
            } else {
               changes.setSoleAttributeValue(workItemArt, AtsAttributeTypes.WorkPackageReference,
                  workPackage.getStoreObject());
            }
         }
      }
      // convert id to guid
      String workPackageGuid = atsApi.getAttributeResolver().getSoleAttributeValue(workItemArt, WorkPackageGuid, "");
      if (Strings.isInValid(workPackageGuid)) {
         ArtifactId workPackageArt = atsApi.getAttributeResolver().getSoleArtifactIdReference(workItemArt,
            AtsAttributeTypes.WorkPackageReference, ArtifactId.SENTINEL);
         ArtifactReadable artifact = (ArtifactReadable) atsApi.getQueryService().getArtifact(workPackageArt);
         if (artifact != null) {
            changes.setSoleAttributeValue(workItemArt, WorkPackageGuid, artifact.getGuid());
         }
      }
   }

   java.util.Map<String, IAtsWorkPackage> guidToWorkPackage = new HashMap<>();

   public static void convertActionableItemsIfNeeded(IAtsChangeSet changes, ArtifactToken art, AtsApi atsApi) {
      // convert guids to id
      Collection<ArtifactId> currentAiRefIds =
         atsApi.getAttributeResolver().getAttributeValues(art, AtsAttributeTypes.ActionableItemReference);

      List<ArtifactId> neededAiRefIds = new LinkedList<>();
      for (IAttribute<?> attr : atsApi.getAttributeResolver().getAttributes(art, ActionableItem)) {
         String aiArtGuid = (String) attr.getValue();
         ArtifactToken ai = atsApi.getQueryService().getArtifactByGuidOrSentinel(aiArtGuid);
         if (ai.isInvalid()) {
            atsApi.getLogger().error("AI not found for aiArtGuid " + aiArtGuid + " for art " + art.toStringWithId());
         } else if (!currentAiRefIds.contains(ai)) {
            neededAiRefIds.add(ai);
         }
      }

      for (ArtifactId need : neededAiRefIds) {
         changes.addAttribute(art, AtsAttributeTypes.ActionableItemReference, need);
      }

      // convert id to guid
      List<String> currentAiGuidIds = atsApi.getAttributeResolver().getAttributesToStringList(art, ActionableItem);

      List<String> neededAiGuidIds = new LinkedList<>();
      Collection<ArtifactId> aiArts =
         atsApi.getAttributeResolver().getAttributeValues(art, AtsAttributeTypes.ActionableItemReference);
      for (ArtifactId id : aiArts) {
         IAtsActionableItem ai = atsApi.getConfigService().getConfigurations().getIdToAi().get(id.getId());
         if (ai == null) {
            ai = atsApi.getQueryService().getConfigItem(id);
         }
         if (ai == null) {
            atsApi.getLogger().error("AI not found for id " + id + " for art " + art.toStringWithId());
         } else if (!currentAiGuidIds.contains(ai.getStoreObject().getGuid())) {
            neededAiGuidIds.add(ai.getStoreObject().getGuid());
         }
      }

      for (String guid : neededAiGuidIds) {
         changes.addAttribute(art, ActionableItem, guid);
      }
   }

   public static void convertTeamDefinitionIfNeeded(IAtsChangeSet changes, ArtifactToken art, AtsApi atsApi) {
      // convert guid to id
      ArtifactId teamDefId = atsApi.getAttributeResolver().getSoleArtifactIdReference(art,
         AtsAttributeTypes.TeamDefinitionReference, ArtifactId.SENTINEL);
      if (teamDefId.isInvalid()) {
         String teamDefGuid = atsApi.getAttributeResolver().getSoleAttributeValue(art, TeamDefinition, "");
         if (Strings.isValid(teamDefGuid)) {
            ArtifactToken artifact = atsApi.getQueryService().getArtifactByGuidOrSentinel(teamDefGuid);
            IAtsTeamDefinition teamDef = atsApi.getTeamDefinitionService().getTeamDefinitionById(artifact);
            changes.setSoleAttributeValue(art, AtsAttributeTypes.TeamDefinitionReference, teamDef.getStoreObject());
         }
      }
      // convert id to guid
      String teamDefGuid = atsApi.getAttributeResolver().getSoleAttributeValue(art, TeamDefinition, "");
      if (!Strings.isValid(teamDefGuid)) {
         ArtifactId teamDefArt = atsApi.getAttributeResolver().getSoleArtifactIdReference(art,
            AtsAttributeTypes.TeamDefinitionReference, ArtifactId.SENTINEL);
         ArtifactToken artifact = atsApi.getQueryService().getArtifact(teamDefArt);
         if (artifact != null) {
            changes.setSoleAttributeValue(art, TeamDefinition, artifact.getGuid());
         }
      }
   }

}
