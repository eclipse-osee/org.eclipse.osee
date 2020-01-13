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
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItemService;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.IAtsStoreService;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.core.config.ActionableItem;
import org.eclipse.osee.ats.core.config.ActionableItems;
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
         if (art.isOfType( AtsArtifactTypes.ActionableItem)) {
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
      for (IAtsTeamDefinition childTeamDef : teamDef.getChildrenTeamDefinitions()) {
         getActiveActionableItemsAndChildrenRecurse(childTeamDef, ais);
      }
   }

   @Override
   public Set<IAtsActionableItem> getActionableItems(IAtsObject atsObject) {
      Set<IAtsActionableItem> ais = new HashSet<>();
      if (!atsStoreService.isDeleted(atsObject)) {
         for (ArtifactId id : getActionableItemIds(atsObject)) {
            IAtsActionableItem aia = atsApi.getQueryService().getConfigItem(id);
            if (aia == null) {
               OseeLog.logf(ActionableItemServiceImpl.class, Level.SEVERE,
                  "Actionable Item id [%s] from [%s] doesn't match item in AtsConfigCache", id,
                  atsObject.toStringWithId());
            } else {
               ais.add(aia);
            }
         }
      }
      return ais;
   }

   @Override
   public String getActionableItemsStr(IAtsObject atsObject) {
      return AtsObjects.toString("; ", getActionableItems(atsObject));
   }

   @Override
   public Collection<ArtifactId> getActionableItemIds(IAtsObject atsObject) {
      return attrResolver.getAttributeValues(atsObject, AtsAttributeTypes.ActionableItemReference);
   }

   @Override
   public void addActionableItem(IAtsObject atsObject, IAtsActionableItem aia, IAtsChangeSet changes) {
      if (!getActionableItemIds(atsObject).contains(atsObject)) {
         changes.addAttribute(atsObject, AtsAttributeTypes.ActionableItemReference, aia.getStoreObject());
      }
   }

   @Override
   public void removeActionableItem(IAtsObject atsObject, IAtsActionableItem aia, IAtsChangeSet changes) {
      changes.deleteAttribute(atsObject, AtsAttributeTypes.ActionableItemReference, aia.getStoreObject());
   }

   @Override
   public Result setActionableItems(IAtsObject atsObject, Collection<IAtsActionableItem> newItems, IAtsChangeSet changes) {
      Set<IAtsActionableItem> existingAias = getActionableItems(atsObject);

      // Remove non-selected items
      for (IAtsActionableItem existingAia : existingAias) {
         if (!newItems.contains(existingAia)) {
            removeActionableItem(atsObject, existingAia, changes);
         }
      }

      // Add newly-selected items
      for (IAtsActionableItem newItem : newItems) {
         if (!existingAias.contains(newItem)) {
            addActionableItem(atsObject, newItem, changes);
         }
      }

      return Result.TrueResult;
   }

   @Override
   public boolean hasActionableItems(IAtsObject atsObject) {
      boolean hasAis = false;
      hasAis = attrResolver.getAttributeCount(atsObject, AtsAttributeTypes.ActionableItemReference) > 0;
      if (!hasAis && atsApi.getStoreService().getAttributeTypes().contains(AtsAttributeTypes.ActionableItem)) {
         hasAis = attrResolver.getAttributeCount(atsObject, AtsAttributeTypes.ActionableItem) > 0;
      }
      return hasAis;
   }

   @Override
   public Collection<IAtsTeamDefinition> getCorrespondingTeamDefinitions(IAtsObject atsObject) {
      Set<IAtsTeamDefinition> teamDefs = new HashSet<>();
      if (getActionableItems(atsObject).size() > 0) {
         teamDefs.addAll(ActionableItems.getImpactedTeamDefs(getActionableItems(atsObject)));
      }
      return teamDefs;
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

}
