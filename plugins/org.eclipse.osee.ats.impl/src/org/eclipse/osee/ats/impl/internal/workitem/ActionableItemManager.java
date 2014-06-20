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
package org.eclipse.osee.ats.impl.internal.workitem;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.core.config.IAtsConfig;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Donald G. Dunne
 */
public class ActionableItemManager {

   private final IAtsConfig atsConfig;

   public ActionableItemManager(IAtsConfig atsConfig) {
      this.atsConfig = atsConfig;
   }

   public Set<IAtsActionableItem> getActionableItems(IAtsObject atsObject) throws OseeCoreException {
      Set<IAtsActionableItem> ais = new HashSet<IAtsActionableItem>();
      if (!((ArtifactReadable) atsObject.getStoreObject()).isDeleted()) {
         for (String guid : getActionableItemGuids(atsObject)) {
            IAtsActionableItem aia = atsConfig.getSoleByGuid(guid, IAtsActionableItem.class);
            if (aia == null && !((ArtifactReadable) atsObject.getStoreObject()).isDeleted()) {
               OseeLog.logf(ActionableItemManager.class, Level.SEVERE,
                  "Actionable Item Guid [%s] from [%s] doesn't match item in AtsConfigCache", guid,
                  AtsUtilCore.toStringWithId(atsObject));
            } else {
               ais.add(aia);
            }
         }
      }
      return ais;
   }

   public String getActionableItemsStr(IAtsObject atsObject) throws OseeCoreException {
      return AtsObjects.toString("; ", getActionableItems(atsObject));
   }

   public List<String> getActionableItemGuids(IAtsObject atsObject) throws OseeCoreException {
      return ((ArtifactReadable) atsObject.getStoreObject()).getAttributeValues(AtsAttributeTypes.ActionableItem);
   }

   public void addActionableItem(IAtsObject atsObject, IAtsActionableItem aia, IAtsChangeSet changes) throws OseeCoreException {
      if (!getActionableItemGuids(atsObject).contains(aia.getGuid())) {
         changes.addAttribute(atsObject, AtsAttributeTypes.ActionableItem, aia.getGuid());
      }
   }

   public void removeActionableItem(IAtsObject atsObject, IAtsActionableItem aia, IAtsChangeSet changes) throws OseeCoreException {
      changes.deleteAttribute(atsObject, AtsAttributeTypes.ActionableItem, aia.getGuid());
   }

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

}
