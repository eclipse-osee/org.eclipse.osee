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
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItemService;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.IAtsStoreService;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.core.config.ActionableItems;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public class ActionableItemManager implements IAtsActionableItemService {

   private final IAttributeResolver attrResolver;
   private final IAtsStoreService atsStoreService;
   private final IAtsServices services;

   public ActionableItemManager(IAttributeResolver attrResolver, IAtsStoreService atsStoreService, IAtsServices services) {
      this.attrResolver = attrResolver;
      this.atsStoreService = atsStoreService;
      this.services = services;
   }

   @Override
   public List<IAtsActionableItem> getActiveActionableItemsAndChildren(IAtsTeamDefinition teamDef) {
      List<IAtsActionableItem> ais = new LinkedList<>();
      getActiveActionableItemsAndChildrenRecurse(teamDef, ais);
      return ais;
   }

   private void getActiveActionableItemsAndChildrenRecurse(IAtsTeamDefinition teamDef, List<IAtsActionableItem> ais) {
      for (ArtifactId aiArt : services.getRelationResolver().getRelated(teamDef,
         AtsRelationTypes.TeamActionableItem_ActionableItem)) {
         IAtsActionableItem ai = services.getConfigItem(aiArt);
         if (ai.isActionable()) {
            ais.add(ai);
         }
      }
      for (IAtsTeamDefinition childTeamDef : teamDef.getChildrenTeamDefinitions()) {
         getActiveActionableItemsAndChildrenRecurse(childTeamDef, ais);
      }
   }

   @Override
   public Set<IAtsActionableItem> getActionableItems(IAtsObject atsObject) throws OseeCoreException {
      Set<IAtsActionableItem> ais = new HashSet<>();
      if (!atsStoreService.isDeleted(atsObject)) {
         for (ArtifactId id : getActionableItemIds(atsObject)) {
            IAtsActionableItem aia = services.getConfigItem(id);
            if (aia == null) {
               OseeLog.logf(ActionableItemManager.class, Level.SEVERE,
                  "Actionable Item Guid [%s] from [%s] doesn't match item in AtsConfigCache", id,
                  atsObject.toStringWithId());
            } else {
               ais.add(aia);
            }
         }
      }
      return ais;
   }

   @Override
   public String getActionableItemsStr(IAtsObject atsObject) throws OseeCoreException {
      return AtsObjects.toString("; ", getActionableItems(atsObject));
   }

   @Override
   public Collection<ArtifactId> getActionableItemIds(IAtsObject atsObject) {
      return attrResolver.getAttributeValues(atsObject, AtsAttributeTypes.ActionableItemReference);
   }

   @Override
   public void addActionableItem(IAtsObject atsObject, IAtsActionableItem aia, IAtsChangeSet changes) throws OseeCoreException {
      if (!getActionableItemIds(atsObject).contains(atsObject)) {
         changes.addAttribute(atsObject, AtsAttributeTypes.ActionableItemReference, aia.getStoreObject());
      }
   }

   @Override
   public void removeActionableItem(IAtsObject atsObject, IAtsActionableItem aia, IAtsChangeSet changes) throws OseeCoreException {
      changes.deleteAttribute(atsObject, AtsAttributeTypes.ActionableItemReference, aia.getStoreObject());
   }

   @Override
   public Result setActionableItems(IAtsObject atsObject, Collection<IAtsActionableItem> newItems, IAtsChangeSet changes) throws OseeCoreException {
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
      return attrResolver.getAttributeCount(atsObject, AtsAttributeTypes.ActionableItemReference) > 0;
   }

   @Override
   public Collection<IAtsTeamDefinition> getCorrespondingTeamDefinitions(IAtsObject atsObject) {
      Set<IAtsTeamDefinition> teamDefs = new HashSet<>();
      if (getActionableItems(atsObject).size() > 0) {
         teamDefs.addAll(ActionableItems.getImpactedTeamDefs(getActionableItems(atsObject)));
      }
      return teamDefs;
   }

}
