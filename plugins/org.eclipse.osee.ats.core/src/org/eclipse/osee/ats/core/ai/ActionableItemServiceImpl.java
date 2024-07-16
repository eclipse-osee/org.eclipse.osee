/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.ats.core.ai;

import static org.eclipse.osee.ats.api.data.AtsAttributeTypes.Description;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.ai.ActionableItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItemService;
import org.eclipse.osee.ats.api.config.TeamDefinition;
import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.query.IAtsQueryService;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public class ActionableItemServiceImpl implements IAtsActionableItemService {

   private final IAttributeResolver attrResolver;
   private final AtsApi atsApi;

   public ActionableItemServiceImpl(IAttributeResolver attrResolver, AtsApi atsApi) {
      this.attrResolver = attrResolver;
      this.atsApi = atsApi;
   }

   @Override
   public ActionableItem getActionableItemById(ArtifactId aiId) {
      ActionableItem ai = null;
      if (aiId instanceof ActionableItem) {
         ai = (ActionableItem) aiId;
      }
      if (ai == null) {
         ai = atsApi.getConfigService().getConfigurations().getIdToAi().get(aiId.getId());
      }
      if (ai == null) {
         // Don't want to load artifacts on client.  Request from server.
         if (atsApi.isIde()) {
            ai = atsApi.getServerEndpoints().getConfigEndpoint().getActionableItem(ArtifactId.valueOf(aiId.getId()));
            ai.setAtsApi(atsApi);
         } else {
            ArtifactToken aiArt = atsApi.getQueryService().getArtifact(aiId);
            if (aiArt.isValid()) {
               ActionableItem ai2 = createActionableItem(aiArt);
               atsApi.getConfigService().getConfigurations().addAi(ai2);
               ai = ai2;
            }
         }
      }
      return ai;
   }

   @Override
   public ActionableItem createActionableItem(ArtifactToken aiArt) {
      ActionableItem ai = new ActionableItem(aiArt, atsApi);
      ai.setName(aiArt.getName());
      ai.setId(aiArt.getId());
      ai.setGuid(aiArt.getGuid());
      ArtifactId programId =
         atsApi.getAttributeResolver().getSoleAttributeValue(aiArt, AtsAttributeTypes.ProgramId, ArtifactId.SENTINEL);
      if (programId.isValid()) {
         ai.setProgramId(programId.getIdString());
      }
      for (String workTypeStr : atsApi.getAttributeResolver().getAttributesToStringList(aiArt,
         AtsAttributeTypes.WorkType)) {
         WorkType workType = WorkType.valueOfOrNone(workTypeStr);
         if (workType.isNotNone()) {
            ai.getWorkTypes().add(workType);
         }
      }
      ai.getTags().addAll(atsApi.getAttributeResolver().getAttributeValues(aiArt, CoreAttributeTypes.StaticId));
      for (String csci : atsApi.getAttributeResolver().getAttributesToStringList(aiArt, AtsAttributeTypes.CSCI)) {
         ai.getCscis().add(csci);
      }
      ai.setDescription(atsApi.getAttributeResolver().getSoleAttributeValue(aiArt, Description, ""));
      ai.setActive(atsApi.getAttributeResolver().getSoleAttributeValue(aiArt, AtsAttributeTypes.Active, true));
      ai.setActionable(atsApi.getAttributeResolver().getSoleAttributeValue(aiArt, AtsAttributeTypes.Actionable, true));
      ai.setAllowUserActionCreation(
         atsApi.getAttributeResolver().getSoleAttributeValue(aiArt, AtsAttributeTypes.AllowUserActionCreation, true));
      ArtifactToken teamDefArt =
         atsApi.getRelationResolver().getRelatedOrNull(aiArt, AtsRelationTypes.TeamActionableItem_TeamDefinition);
      if (teamDefArt != null && teamDefArt.isValid()) {
         ai.setTeamDefId(teamDefArt.getId());
      }
      ArtifactToken parent = atsApi.getRelationResolver().getParent(aiArt);
      if (parent != null) {
         ai.setParentId(parent.getId());
      }
      for (ArtifactToken child : atsApi.getRelationResolver().getChildren(aiArt)) {
         if (child.isOfType(AtsArtifactTypes.ActionableItem)) {
            ai.getChildren().add(child.getId());
         }
      }
      return ai;
   }

   @Override
   public List<IAtsActionableItem> getActiveActionableItemsAndChildren(IAtsTeamDefinition teamDef) {
      List<IAtsActionableItem> ais = new LinkedList<>();
      getActiveActionableItemsAndChildrenRecurse((TeamDefinition) teamDef, ais);
      return ais;
   }

   private void getActiveActionableItemsAndChildrenRecurse(TeamDefinition teamDef, List<IAtsActionableItem> ais) {
      for (ActionableItem ai : teamDef.getActionableItems()) {
         if (ai.isActive() && ai.isActionable()) {
            ais.add(ai);
         }
      }
      for (TeamDefinition childTeamDef : teamDef.getChildrenTeamDefs()) {
         getActiveActionableItemsAndChildrenRecurse(childTeamDef, ais);
      }
   }

   @Override
   public Set<IAtsActionableItem> getActionableItems(IAtsWorkItem workItem) {
      Set<IAtsActionableItem> ais = new HashSet<>();
      if (!atsApi.getStoreService().isDeleted(workItem)) {
         for (ArtifactId id : getActionableItemIds(workItem)) {
            IAtsActionableItem aia = getActionableItemById(id);
            if (aia == null) {
               OseeLog.logf(ActionableItemServiceImpl.class, Level.SEVERE,
                  "Actionable Item id [%s] from [%s] doesn't match item in AtsConfigCache", id,
                  workItem.toStringWithId());
            } else {
               ais.add(aia);
            }
         }
      }
      return ais;
   }

   @Override
   public String getActionableItemsStr(IAtsWorkItem workItem) {
      return AtsObjects.toString("; ", getActionableItems(workItem));
   }

   @Override
   public Collection<ArtifactId> getActionableItemIds(IAtsWorkItem workItem) {
      return attrResolver.getAttributeValues(workItem, AtsAttributeTypes.ActionableItemReference);
   }

   @Override
   public void addActionableItem(IAtsWorkItem workItem, IAtsActionableItem aia, IAtsChangeSet changes) {
      if (!getActionableItemIds(workItem).contains(aia.getArtifactId())) {
         changes.addAttribute(workItem, AtsAttributeTypes.ActionableItemReference, aia.getStoreObject());
      }
   }

   @Override
   public void removeActionableItem(IAtsWorkItem workItem, IAtsActionableItem aia, IAtsChangeSet changes) {
      changes.deleteAttribute(workItem, AtsAttributeTypes.ActionableItemReference, aia.getStoreObject());
   }

   @Override
   public Result setActionableItems(IAtsWorkItem workItem, Collection<IAtsActionableItem> newItems, IAtsChangeSet changes) {
      Set<IAtsActionableItem> existingAias = getActionableItems(workItem);

      // Remove non-selected items
      for (IAtsActionableItem existingAia : existingAias) {
         if (!newItems.contains(existingAia)) {
            removeActionableItem(workItem, existingAia, changes);
         }
      }

      // Add newly-selected items
      for (IAtsActionableItem newItem : newItems) {
         if (!existingAias.contains(newItem)) {
            addActionableItem(workItem, newItem, changes);
         }
      }

      return Result.TrueResult;
   }

   @Override
   public boolean hasActionableItems(IAtsWorkItem workItem) {
      return attrResolver.getAttributeCount(workItem, AtsAttributeTypes.ActionableItemReference) > 0;
   }

   @Override
   public Collection<ActionableItem> getActionableItems(IAtsTeamDefinition teamDef) {
      return teamDef.getActionableItems();
   }

   @Override
   public IAtsActionableItem getActionableItem(IAtsTeamDefinition teamDef) {
      ActionableItem ai = null;
      Collection<ActionableItem> related = teamDef.getActionableItems();
      if (related != null) {
         if (related.isEmpty()) {
            return null;
         } else if (related.size() > 1) {
            throw new OseeStateException("Multiple AIs related to teamDef; Invalid method for this");
         } else if (related.size() == 1) {
            ai = related.iterator().next();
         }
      }
      return ai;
   }

   @Override
   public Collection<IAtsActionableItem> getActionableItems(IAtsAction action) {
      Set<IAtsActionableItem> aias = new HashSet<>();
      for (IAtsTeamWorkflow team : atsApi.getWorkItemService().getTeams(action)) {
         aias.addAll(atsApi.getActionableItemService().getActionableItems(team));
      }
      return aias;
   }

   @Override
   public ActionableItem createActionableItem(String name, long id, IAtsChangeSet changes) {
      ArtifactToken artifact = changes.createArtifact(AtsArtifactTypes.ActionableItem, name, id);
      return createActionableItem(artifact);
   }

   @Override
   public ActionableItem createActionableItem(String name, IAtsChangeSet changes) {
      return createActionableItem(name, Lib.generateArtifactIdAsInt(), changes);
   }

   @Override
   public IAtsActionableItem getActionableItem(String value) {
      return getActionableItemById(ArtifactId.valueOf(value));
   }

   @Override
   public Collection<AtsUser> getSubscribed(IAtsActionableItem ai) {
      return atsApi.getUserService().getRelatedUsers(atsApi, ai.getArtifactToken(),
         AtsRelationTypes.SubscribedUser_User);
   }

   @Override
   public Collection<AtsUser> getLeads(IAtsActionableItem ai) {
      return atsApi.getUserService().getRelatedUsers(atsApi, ai.getStoreObject(), AtsRelationTypes.TeamLead_Lead);
   }

   @Override
   public IAtsTeamDefinition getTeamDefinitionInherited(IAtsActionableItem ai) {
      return atsApi.getTeamDefinitionService().getImpactedTeamDef(ai);
   }

   @Override
   public Set<IAtsActionableItem> getAIsFromItemAndChildren(IAtsActionableItem ai) {
      Set<IAtsActionableItem> ais = new HashSet<>();
      ais.add(ai);
      for (IAtsActionableItem art : ai.getChildrenActionableItems()) {
         ais.addAll(getAIsFromItemAndChildren(art));
      }
      return ais;
   }

   @Override
   public Set<IAtsActionableItem> getActionableItemsFromItemAndChildren(IAtsActionableItem ai) {
      Set<IAtsActionableItem> ais = new HashSet<>();
      getActionableItemsFromItemAndChildren(ai, ais);
      return ais;
   }

   @Override
   public void getActionableItemsFromItemAndChildren(IAtsActionableItem ai, Set<IAtsActionableItem> aias) {
      for (IAtsActionableItem art : ai.getChildrenActionableItems()) {
         aias.add(art);
         for (IAtsActionableItem childArt : ai.getChildrenActionableItems()) {
            getActionableItemsFromItemAndChildren(childArt, aias);
         }
      }
   }

   @Override
   public Set<IAtsActionableItem> getActionableItems(Collection<String> actionableItemNames) {
      Set<IAtsActionableItem> ais = new HashSet<>();
      for (ActionableItem ai : atsApi.getConfigService().getConfigurations().getIdToAi().values()) {
         if (actionableItemNames.contains(ai.getName())) {
            ais.add(ai);
         }
      }
      return ais;
   }

   @Override
   public Collection<IAtsTeamDefinition> getImpactedTeamDefs(Collection<IAtsActionableItem> ais) {
      return atsApi.getTeamDefinitionService().getImpactedTeamDefs(ais);
   }

   @Override
   public Collection<IAtsActionableItem> getActionableItems(Active active, IAtsQueryService queryService) {
      Collection<IAtsActionableItem> ais = new HashSet<>();
      for (ActionableItem ai : atsApi.getConfigService().getConfigurations().getIdToAi().values()) {
         if (active == Active.Both || ((active == Active.Active && ai.isActive()) || (active == Active.InActive && ai.isInActive()))) {
            ais.add(ai);
         }
      }
      return ais;
   }

   @Override
   public String getNotActionableItemError(IAtsConfigObject configObject) {
      return "Action can not be written against " + configObject.getName() + " \"" + configObject + "\" (" + configObject.getIdString() + ").\n\nChoose another item.";
   }

   @Override
   public ActionableItem getTopActionableItem(AtsApi atsApi) {
      return atsApi.getActionableItemService().getActionableItemById(
         atsApi.getConfigService().getConfigurations().getTopActionableItem());
   }

   @Override
   public Collection<IAtsActionableItem> getActionableItemsAll(IAtsQueryService queryService) {
      return getActionableItems(Active.Both, queryService);
   }

   @Override
   public List<IAtsActionableItem> getTopLevelActionableItems(Active active) {
      IAtsActionableItem topAi = getTopActionableItem(atsApi);
      if (topAi == null) {
         return java.util.Collections.emptyList();
      }
      return Collections.castAll(getActive(getChildren(topAi, false), active));
   }

   @Override
   public List<IAtsActionableItem> getActive(Collection<IAtsActionableItem> ais, Active active) {
      List<IAtsActionableItem> results = new ArrayList<>();
      for (IAtsActionableItem ai : ais) {
         if (active == Active.Both) {
            results.add(ai);
         } else {
            // assume active unless otherwise specified
            boolean attributeActive = ai.isActive();
            if (active == Active.Active && attributeActive) {
               results.add(ai);
            } else if (active == Active.InActive && !attributeActive) {
               results.add(ai);
            }
         }
      }
      return results;
   }

   @Override
   public Set<IAtsActionableItem> getChildren(IAtsActionableItem topActionableItem, boolean recurse) {
      Set<IAtsActionableItem> children = new HashSet<>();
      for (IAtsActionableItem child : topActionableItem.getChildrenActionableItems()) {
         children.add(child);
         if (recurse) {
            children.addAll(getChildren(child, recurse));
         }
      }
      return children;
   }

   @Override
   public Collection<IAtsActionableItem> getUserEditableActionableItems(Collection<IAtsActionableItem> actionableItems) {
      List<IAtsActionableItem> ais = new LinkedList<>();
      for (IAtsActionableItem ai : actionableItems) {
         if (ai.isAllowUserActionCreation()) {
            ais.add(ai);
         }
      }
      return ais;
   }

}
