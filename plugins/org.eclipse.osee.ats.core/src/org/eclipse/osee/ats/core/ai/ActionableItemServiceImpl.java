/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.ai;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItemService;
import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.IAtsStoreService;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.core.config.ActionableItem;
import org.eclipse.osee.ats.core.internal.AtsApiService;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public class ActionableItemServiceImpl implements IAtsActionableItemService {

   private final IAttributeResolver attrResolver;
   private final IAtsStoreService atsStoreService;
   private final AtsApi atsApi;

   public ActionableItemServiceImpl(IAttributeResolver attrResolver, IAtsStoreService atsStoreService, AtsApi atsApi) {
      this.attrResolver = attrResolver;
      this.atsStoreService = atsStoreService;
      this.atsApi = atsApi;
   }

   @Override
   public IAtsActionableItem getActionableItemById(ArtifactId aiId) {
      IAtsActionableItem ai = null;
      if (aiId instanceof IAtsActionableItem) {
         ai = (IAtsActionableItem) aiId;
      } else {
         ArtifactToken art = atsApi.getQueryService().getArtifact(aiId);
         if (art.isOfType(AtsArtifactTypes.ActionableItem)) {
            ai = new ActionableItem(atsApi.getLogger(), atsApi, art);
         }
      }
      return ai;
   }

   @Override
   public List<IAtsActionableItem> getActiveActionableItemsAndChildren(IAtsTeamDefinition teamDef) {
      List<IAtsActionableItem> ais = new LinkedList<>();
      getActiveActionableItemsAndChildrenRecurse(teamDef, ais);
      return ais;
   }

   private void getActiveActionableItemsAndChildrenRecurse(IAtsTeamDefinition teamDef, List<IAtsActionableItem> ais) {
      for (ArtifactId aiArt : atsApi.getRelationResolver().getRelated(teamDef,
         AtsRelationTypes.TeamActionableItem_ActionableItem)) {
         IAtsActionableItem ai = atsApi.getQueryService().getConfigItem(aiArt);
         if (ai.isActionable()) {
            ais.add(ai);
         }
      }
      for (IAtsTeamDefinition childTeamDef : atsApi.getTeamDefinitionService().getChildrenTeamDefinitions(teamDef)) {
         getActiveActionableItemsAndChildrenRecurse(childTeamDef, ais);
      }
   }

   @Override
   public Set<IAtsActionableItem> getActionableItems(IAtsWorkItem workItem) {
      Set<IAtsActionableItem> ais = new HashSet<>();
      if (!atsStoreService.isDeleted(workItem)) {
         for (ArtifactId id : getActionableItemIds(workItem)) {
            IAtsActionableItem aia = atsApi.getQueryService().getConfigItem(id);
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
      boolean hasAis = false;
      hasAis = attrResolver.getAttributeCount(workItem, AtsAttributeTypes.ActionableItemReference) > 0;
      if (!hasAis && atsApi.getStoreService().getAttributeTypes().contains(AtsAttributeTypes.ActionableItem)) {
         hasAis = attrResolver.getAttributeCount(workItem, AtsAttributeTypes.ActionableItem) > 0;
      }
      return hasAis;
   }

   @Override
   public Collection<IAtsActionableItem> getActionableItems(IAtsTeamDefinition teamDef) {
      Set<IAtsActionableItem> aias = new HashSet<>();
      for (ArtifactToken ai : atsApi.getRelationResolver().getRelated(teamDef,
         AtsRelationTypes.TeamActionableItem_ActionableItem)) {
         aias.add(atsApi.getActionableItemService().getActionableItemById(ai));
      }
      return aias;
   }

   @Override
   public IAtsActionableItem getActionableItem(IAtsTeamDefinition teamDef) {
      IAtsActionableItem ai = null;
      Collection<ArtifactToken> related =
         atsApi.getRelationResolver().getRelated(teamDef, AtsRelationTypes.TeamActionableItem_ActionableItem);
      if (related.isEmpty()) {
         return null;
      } else if (related.size() > 1) {
         throw new OseeStateException("Multiple AIs related to teamDef; Invalid method for this");
      } else if (related.size() == 1) {
         ai = atsApi.getActionableItemService().getActionableItemById(related.iterator().next());
      }
      return ai;
   }

   @Override
   public IAtsActionableItem createActionableItem(String name, long id, IAtsChangeSet changes, AtsApi atsApi) {
      ArtifactToken artifact = changes.createArtifact(AtsArtifactTypes.ActionableItem, name, id);
      return new ActionableItem(atsApi.getLogger(), atsApi, artifact);
   }

   @Override
   public IAtsActionableItem createActionableItem(String name, IAtsChangeSet changes, AtsApi atsApi) {
      return createActionableItem(name, Lib.generateArtifactIdAsInt(), changes, atsApi);
   }

   @Override
   public IAtsActionableItem getActionableItem(String value) {
      return getActionableItemById(ArtifactId.valueOf(value));
   }

   @Override
   public Collection<WorkType> getWorkTypes(IAtsWorkItem workItem) {
      Collection<WorkType> workTypes = new HashSet<>();
      for (IAtsActionableItem ai : atsApi.getActionableItemService().getActionableItems(workItem)) {
         Collection<String> workTypeStrs =
            atsApi.getAttributeResolver().getAttributeValues(ai, AtsAttributeTypes.WorkType);
         for (String workTypeStr : workTypeStrs) {
            try {
               WorkType workType = WorkType.valueOf(workTypeStr);
               workTypes.add(workType);
            } catch (Exception ex) {
               // do nothing
            }
         }
      }
      return workTypes;
   }

   @Override
   public boolean isWorkType(IAtsWorkItem workItem, WorkType workType) {
      return getWorkTypes(workItem).contains(workType);
   }

   @Override
   public Collection<IAtsUser> getSubscribed(IAtsActionableItem ai) {
      return AtsApiService.get().getUserService().getRelatedUsers(atsApi, ai.getArtifactToken(),
         AtsRelationTypes.SubscribedUser_User);
   }

   @Override
   public Collection<IAtsUser> getLeads(IAtsActionableItem ai) {
      return AtsApiService.get().getUserService().getRelatedUsers(atsApi, ai.getStoreObject(),
         AtsRelationTypes.TeamLead_Lead);
   }

}
