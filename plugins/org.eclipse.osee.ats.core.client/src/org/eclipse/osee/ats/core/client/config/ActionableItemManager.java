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
import java.util.Set;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.core.client.internal.AtsClientService;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
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
      Set<IAtsActionableItem> ais = new HashSet<>();
      if (!artifact.isDeleted()) {
         for (String guid : artifact.getAttributesToStringList(AtsAttributeTypes.ActionableItem)) {
            IAtsActionableItem ai = AtsClientService.get().getConfigItem(guid);
            if (ai != null) {
               ais.add(ai);
            }
         }
      }
      return ais;
   }

   public String getActionableItemsStr() throws OseeCoreException {
      return AtsObjects.toString("; ", getActionableItems());
   }

   public void addActionableItem(IAtsActionableItem aia) throws OseeCoreException {
      if (!getActionableItems().contains(aia)) {
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

   public boolean hasActionableItems() {
      return artifact.getAttributeCount(AtsAttributeTypes.ActionableItem) > 0;
   }

}
