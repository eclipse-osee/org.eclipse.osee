/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.config;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.IAttribute;
import org.eclipse.osee.framework.core.util.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class ConvertAtsConfigGuidAttributesOperation {

   private final IAtsServices services;
   private final AttributeTypeToken TeamDefinition =
      AtsAttributeTypes.createType(1152921504606847201L, "Team Definition");

   public ConvertAtsConfigGuidAttributesOperation(IAtsServices services) {
      this.services = services;
   }

   public XResultData createUpdateConfig(XResultData rd) {

      // Convert Team Def and AIs for Team Workflow
      List<ArtifactId> artIdList = new LinkedList<>();
      artIdList.addAll(services.getQueryService().createQuery(WorkItemType.TeamWorkflow).getItemIds());
      List<Collection<ArtifactId>> subDivide = Collections.subDivide(artIdList, 4000);
      int size = subDivide.size(), count = 1;
      for (Collection<ArtifactId> artIds : subDivide) {
         services.getLogger().error(String.format("processing %s / %s", count++, size));
         List<Long> ids = new LinkedList<>();
         for (ArtifactId art : artIds) {
            ids.add(art.getId());
         }
         Collection<ArtifactToken> allArtifacts = services.getArtifacts(ids);
         IAtsChangeSet changes = services.createChangeSet("Update Team Def and AI TeamWf GUIDs");
         for (ArtifactToken art : allArtifacts) {
            convertTeamDefinitionIfNeeded(changes, art);
            convertActionableItemsIfNeeded(changes, art);
         }
         changes.executeIfNeeded();
      }

      // Convert Program Team Definition
      IAtsChangeSet changes = services.createChangeSet("Update Program Team Def GUID");
      for (IAtsProgram program : services.getQueryService().createQuery(AtsArtifactTypes.Program).getItems(
         IAtsProgram.class)) {
         convertTeamDefinitionIfNeeded(changes, program.getStoreObject());
      }
      changes.executeIfNeeded();

      // Convert AIs for Action
      changes = services.createChangeSet("Update Action AI GUIDs");
      for (ArtifactToken actionArt : services.getQueryService().getArtifacts(AtsArtifactTypes.Action,
         services.getAtsBranch())) {
         convertTeamDefinitionIfNeeded(changes, actionArt);
      }
      changes.executeIfNeeded();

      // Convert AIs for ats.Review (stand-alone reviews)
      changes = services.createChangeSet("Update AIs for ats.Review GUIDs");
      for (IAtsAbstractReview program : services.getQueryService().createQuery(WorkItemType.Review).getItems(
         IAtsAbstractReview.class)) {
         convertActionableItemsIfNeeded(changes, program.getStoreObject());
      }
      changes.executeIfNeeded();
      services.getLogger().error("complete");
      return rd;
   }

   private void convertActionableItemsIfNeeded(IAtsChangeSet changes, ArtifactToken art) {
      Collection<IAttribute<Object>> aiRefAttrs =
         services.getAttributeResolver().getAttributes(art, AtsAttributeTypes.ActionableItemReference);
      List<Long> currentAiRefIds = new LinkedList<>();
      for (IAttribute<Object> aiRefAttr : aiRefAttrs) {
         currentAiRefIds.add(aiRefAttr.getId());
      }

      List<Long> neededAiRefIds = new LinkedList<>();
      for (IAttribute<?> attr : services.getAttributeResolver().getAttributes(art, AtsAttributeTypes.ActionableItem)) {
         String aiArtGuid = (String) attr.getValue();
         IAtsActionableItem ai = services.getConfigItem(aiArtGuid);
         neededAiRefIds.add(ai.getId());
      }

      if (!Collections.isEqual(currentAiRefIds, neededAiRefIds)) {
         List<Object> newSet = new LinkedList<>();
         for (Long id : neededAiRefIds) {
            newSet.add(id.toString());
         }
         changes.setAttributeValues(art, AtsAttributeTypes.ActionableItemReference, newSet);
      }
   }

   private void convertTeamDefinitionIfNeeded(IAtsChangeSet changes, ArtifactToken art) {
      ArtifactId teamDefId = services.getAttributeResolver().getSoleArtifactIdReference(art,
         AtsAttributeTypes.TeamDefinitionReference, ArtifactId.SENTINEL);
      if (teamDefId.isInvalid()) {
         String teamDefGuid = services.getAttributeResolver().getSoleAttributeValue(art, TeamDefinition, "");
         if (Strings.isValid(teamDefGuid)) {
            IAtsTeamDefinition teamDef = services.getConfigItem(teamDefGuid);
            changes.setSoleAttributeValue(art, AtsAttributeTypes.TeamDefinitionReference,
               teamDef.getStoreObject().getId().toString());
         }
      }
   }

}
