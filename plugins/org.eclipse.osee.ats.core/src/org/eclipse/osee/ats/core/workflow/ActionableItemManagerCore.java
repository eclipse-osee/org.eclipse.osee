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
package org.eclipse.osee.ats.core.workflow;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.core.config.ActionableItemArtifact;
import org.eclipse.osee.ats.core.config.AtsArtifactToken;
import org.eclipse.osee.ats.core.config.TeamDefinitionArtifact;
import org.eclipse.osee.ats.core.config.TeamDefinitionManager;
import org.eclipse.osee.ats.core.config.TeamDefinitionManagerCore;
import org.eclipse.osee.ats.core.internal.Activator;
import org.eclipse.osee.ats.core.type.AtsArtifactTypes;
import org.eclipse.osee.ats.core.type.AtsAttributeTypes;
import org.eclipse.osee.ats.core.util.AtsCacheManager;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;

/**
 * @author Donald G. Dunne
 */
public class ActionableItemManagerCore {

   private final Artifact artifact;

   public ActionableItemManagerCore(Artifact artifact) {
      this.artifact = artifact;
   }

   public Set<ActionableItemArtifact> getActionableItems() throws OseeCoreException {
      Set<ActionableItemArtifact> ais = new HashSet<ActionableItemArtifact>();
      for (String guid : getActionableItemGuids()) {
         try {
            ActionableItemArtifact aia =
               (ActionableItemArtifact) ArtifactCache.getActive(guid, AtsUtilCore.getAtsBranch());
            if (aia == null) {
               OseeLog.log(Activator.class, Level.SEVERE, "Can't find Actionable Item for guid " + guid);
            } else {
               ais.add(aia);
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, "Error getting actionable item for guid " + guid, ex);
         }
      }
      return ais;
   }

   public String getActionableItemsStr() throws OseeCoreException {
      return Artifacts.toString("; ", getActionableItems());
   }

   public List<String> getActionableItemGuids() throws OseeCoreException {
      return artifact.getAttributesToStringList(AtsAttributeTypes.ActionableItem);
   }

   public void addActionableItem(ActionableItemArtifact aia) throws OseeCoreException {
      if (!getActionableItemGuids().contains(aia.getGuid())) {
         artifact.addAttribute(AtsAttributeTypes.ActionableItem, aia.getGuid());
      }
   }

   public void removeActionableItem(ActionableItemArtifact aia) throws OseeCoreException {
      artifact.deleteAttribute(AtsAttributeTypes.ActionableItem, aia.getGuid());
   }

   public Result setActionableItems(Collection<ActionableItemArtifact> newItems) throws OseeCoreException {
      Set<ActionableItemArtifact> existingAias = getActionableItems();

      // Remove non-selected items
      for (ActionableItemArtifact existingAia : existingAias) {
         if (!newItems.contains(existingAia)) {
            removeActionableItem(existingAia);
         }
      }

      // Add newly-selected items
      for (ActionableItemArtifact newItem : newItems) {
         if (!existingAias.contains(newItem)) {
            addActionableItem(newItem);
         }
      }

      return Result.TrueResult;
   }

   public static Set<TeamDefinitionArtifact> getTeamsFromItemAndChildren(ActionableItemArtifact aia) throws OseeCoreException {
      return TeamDefinitionManager.getTeamsFromItemAndChildren(aia);
   }

   public static Set<ActionableItemArtifact> getActionableItemsFromItemAndChildren(ActionableItemArtifact aia) throws OseeCoreException {
      Set<ActionableItemArtifact> aias = new HashSet<ActionableItemArtifact>();
      getActionableItemsFromItemAndChildren(aia, aias);
      return aias;
   }

   public static void getActionableItemsFromItemAndChildren(ActionableItemArtifact aia, Set<ActionableItemArtifact> aiaTeams) throws OseeCoreException {
      for (Artifact art : aia.getChildren()) {
         if (art instanceof ActionableItemArtifact) {
            aiaTeams.add((ActionableItemArtifact) art);
            for (Artifact childArt : aia.getChildren()) {
               if (childArt instanceof ActionableItemArtifact) {
                  getActionableItemsFromItemAndChildren((ActionableItemArtifact) childArt, aiaTeams);
               }
            }
         }
      }
   }

   public static Set<ActionableItemArtifact> getActionableItems(Collection<String> actionableItemNames) {
      Set<ActionableItemArtifact> aias = new HashSet<ActionableItemArtifact>();
      for (String actionableItemName : actionableItemNames) {
         for (Artifact artifact : AtsCacheManager.getArtifactsByName(AtsArtifactTypes.ActionableItem,
            actionableItemName)) {
            aias.add((ActionableItemArtifact) artifact);
         }
      }
      return aias;
   }

   public static Collection<TeamDefinitionArtifact> getImpactedTeamDefs(Collection<ActionableItemArtifact> aias) throws OseeCoreException {
      return TeamDefinitionManagerCore.getImpactedTeamDefs(aias);
   }

   public static List<ActionableItemArtifact> getActionableItems(Active active) throws OseeCoreException {
      return Collections.castAll(AtsCacheManager.getArtifactsByActive(AtsArtifactTypes.ActionableItem, active));
   }

   public static String getNotActionableItemError(Artifact aia) {
      return "Action can not be written against " + aia.getArtifactTypeName() + " \"" + aia + "\" (" + aia.getHumanReadableId() + ").\n\nChoose another item.";
   }

   public static ActionableItemArtifact getTopActionableItem() {
      return (ActionableItemArtifact) AtsUtilCore.getFromToken(AtsArtifactToken.TopActionableItem);
   }

   public static List<ActionableItemArtifact> getActionableItemsAll() throws OseeCoreException {
      return Collections.castAll(AtsCacheManager.getArtifactsByActive(AtsArtifactTypes.ActionableItem, Active.Both));
   }

   public static List<ActionableItemArtifact> getTopLevelActionableItems(Active active) throws OseeCoreException {
      ActionableItemArtifact topAi = getTopActionableItem();
      if (topAi == null) {
         return java.util.Collections.emptyList();
      }
      return Collections.castAll(AtsUtilCore.getActive(
         Artifacts.getChildrenOfTypeSet(topAi, ActionableItemArtifact.class, false), active,
         ActionableItemArtifact.class));
   }

}
