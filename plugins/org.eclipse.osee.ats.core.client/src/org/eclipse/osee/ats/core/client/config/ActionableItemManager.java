/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.client.config;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.core.client.internal.Activator;
import org.eclipse.osee.ats.core.client.internal.AtsClientService;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class ActionableItemManager {

   private final Artifact artifact;

   public ActionableItemManager(Artifact artifact) {
      this.artifact = artifact;
   }

   public Set<IAtsActionableItem> getActionableItems() throws OseeCoreException {
      Set<IAtsActionableItem> ais = new HashSet<IAtsActionableItem>();
      if (!artifact.isDeleted()) {
         for (Long uuid : getActionableItemUuids()) {
            IAtsActionableItem aia = AtsClientService.get().getConfig().getSoleByUuid(uuid, IAtsActionableItem.class);
            if (aia == null && !artifact.isDeleted()) {
               OseeLog.logf(Activator.class, Level.SEVERE,
                  "Actionable Item Uuid [%d] from [%s] doesn't match item in AtsConfigCache", uuid,
                  artifact.toStringWithId());
            } else {
               ais.add(aia);
            }
         }
      }
      return ais;
   }

   public String getActionableItemsStr() throws OseeCoreException {
      return AtsObjects.toString("; ", getActionableItems());
   }

   /**
    * Return cached guids stored in DB to uuids or query and fill cache. This cache will go away when ATS Team
    * Definitions and AIs are referenced by uuid instead of guid in DB Store.
    */
   public List<Long> getActionableItemUuids() throws OseeCoreException {
      List<Long> uuids = new LinkedList<Long>();
      for (String guid : artifact.getAttributesToStringList(AtsAttributeTypes.ActionableItem)) {
         Long uuid = AtsClientService.get().getStoreService().getUuidFromGuid(guid);
         if (uuid != null) {
            uuids.add(uuid);
         }
      }
      return uuids;
   }

   public void addActionableItem(IAtsActionableItem aia) throws OseeCoreException {
      if (!getActionableItemUuids().contains(aia.getUuid())) {
         String guid = null;
         if (aia.getStoreObject() instanceof Artifact) {
            guid = ((Artifact) aia.getStoreObject()).getGuid();
         } else {
            guid = AtsUtilCore.getGuid(aia);
         }
         artifact.addAttribute(AtsAttributeTypes.ActionableItem, guid);
      }
   }

   public void removeActionableItem(IAtsActionableItem aia) throws OseeCoreException {
      String guid = AtsUtilCore.getGuid(aia);
      artifact.deleteAttribute(AtsAttributeTypes.ActionableItem, guid);
   }

   public Result setActionableItems(Collection<IAtsActionableItem> newItems) throws OseeCoreException {
      Set<IAtsActionableItem> existingAias = getActionableItems();

      // Remove non-selected items
      for (IAtsActionableItem existingAia : existingAias) {
         if (!newItems.contains(existingAia)) {
            removeActionableItem(existingAia);
         }
      }

      // Add newly-selected items
      for (IAtsActionableItem newItem : newItems) {
         if (!existingAias.contains(newItem)) {
            addActionableItem(newItem);
         }
      }

      return Result.TrueResult;
   }

}
