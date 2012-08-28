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
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.core.config.AtsConfigCache;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.Result;
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
      for (String guid : getActionableItemGuids()) {
         IAtsActionableItem aia = AtsConfigCache.instance.getSoleByGuid(guid, IAtsActionableItem.class);
         ais.add(aia);
      }
      return ais;
   }

   public String getActionableItemsStr() throws OseeCoreException {
      return AtsObjects.toString("; ", getActionableItems());
   }

   public List<String> getActionableItemGuids() throws OseeCoreException {
      return artifact.getAttributesToStringList(AtsAttributeTypes.ActionableItem);
   }

   public void addActionableItem(IAtsActionableItem aia) throws OseeCoreException {
      if (!getActionableItemGuids().contains(aia.getGuid())) {
         artifact.addAttribute(AtsAttributeTypes.ActionableItem, aia.getGuid());
      }
   }

   public void removeActionableItem(IAtsActionableItem aia) throws OseeCoreException {
      artifact.deleteAttribute(AtsAttributeTypes.ActionableItem, aia.getGuid());
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
