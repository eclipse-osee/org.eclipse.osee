/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.core.AtsCore;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;

/**
 * @author Donald G. Dunne
 */
public class ActionableItems {

   private static String TopActionableItemGuid = "AAABER+37QEA8O7WSQaqJQ";

   public static Collection<String> getNames(Collection<? extends IAtsActionableItem> aias) {
      ArrayList<String> names = new ArrayList<String>();
      for (IAtsActionableItem named : aias) {
         names.add(named.getName());
      }
      return names;
   }

   public static List<String> toGuids(Collection<? extends IAtsActionableItem> aias) {
      List<String> guids = new ArrayList<String>(aias.size());
      for (IAtsActionableItem artifact : aias) {
         guids.add(artifact.getGuid());
      }
      return guids;
   }

   /**
    * Recurses default hierarchy and collections children of parentArtifact that are of type class
    */
   @SuppressWarnings("unchecked")
   public static <A extends IAtsActionableItem> void getChildrenOfType(IAtsActionableItem parentArtifact, Collection<A> children, Class<A> clazz, boolean recurse) throws OseeCoreException {
      for (IAtsActionableItem child : parentArtifact.getChildrenActionableItems()) {
         if (clazz.isInstance(child)) {
            children.add((A) child);
            if (recurse) {
               getChildrenOfType(child, children, clazz, recurse);
            }
         }
      }
   }

   public static Set<IAtsActionableItem> getAIsFromItemAndChildren(IAtsActionableItem aia) throws OseeCoreException {
      Set<IAtsActionableItem> aias = new HashSet<IAtsActionableItem>();
      aias.add(aia);
      for (IAtsActionableItem art : aia.getChildrenActionableItems()) {
         aias.addAll(getAIsFromItemAndChildren(art));
      }
      return aias;
   }

   public static Set<IAtsTeamDefinition> getTeamsFromItemAndChildren(IAtsActionableItem aia) throws OseeCoreException {
      return TeamDefinitions.getTeamsFromItemAndChildren(aia);
   }

   public static Set<IAtsActionableItem> getActionableItemsFromItemAndChildren(IAtsActionableItem aia) throws OseeCoreException {
      Set<IAtsActionableItem> aias = new HashSet<IAtsActionableItem>();
      getActionableItemsFromItemAndChildren(aia, aias);
      return aias;
   }

   public static void getActionableItemsFromItemAndChildren(IAtsActionableItem aia, Set<IAtsActionableItem> aiaTeams) throws OseeCoreException {
      for (IAtsActionableItem art : aia.getChildrenActionableItems()) {
         aiaTeams.add(art);
         for (IAtsActionableItem childArt : aia.getChildrenActionableItems()) {
            getActionableItemsFromItemAndChildren(childArt, aiaTeams);
         }
      }
   }

   public static Set<IAtsActionableItem> getActionableItems(Collection<String> actionableItemNames) throws OseeCoreException {
      Set<IAtsActionableItem> aias = new HashSet<IAtsActionableItem>();
      for (String actionableItemName : actionableItemNames) {
         for (IAtsActionableItem aia : AtsCore.getAtsConfig().get(IAtsActionableItem.class)) {
            if (aia.getName().equals(actionableItemName)) {
               aias.add(aia);
            }
         }
      }
      return aias;
   }

   public static Collection<IAtsTeamDefinition> getImpactedTeamDefs(Collection<IAtsActionableItem> aias) throws OseeCoreException {
      return TeamDefinitions.getImpactedTeamDefs(aias);
   }

   public static List<IAtsActionableItem> getActionableItems(Active active) throws OseeCoreException {
      return Collections.castAll(getActive(AtsCore.getAtsConfig().get(IAtsActionableItem.class), active));
   }

   public static String getNotActionableItemError(IAtsConfigObject configObject) {
      return "Action can not be written against " + configObject.getName() + " \"" + configObject + "\" (" + configObject.getHumanReadableId() + ").\n\nChoose another item.";
   }

   public static IAtsActionableItem getTopActionableItem() throws OseeCoreException {
      return AtsCore.getAtsConfig().getSoleByGuid(TopActionableItemGuid, IAtsActionableItem.class);
   }

   public static List<IAtsActionableItem> getActionableItemsAll() throws OseeCoreException {
      return getActionableItems(Active.Both);
   }

   public static List<IAtsActionableItem> getTopLevelActionableItems(Active active) throws OseeCoreException {
      IAtsActionableItem topAi = getTopActionableItem();
      if (topAi == null) {
         return java.util.Collections.emptyList();
      }
      return Collections.castAll(getActive(getChildren(topAi, false), active));
   }

   public static List<IAtsActionableItem> getActive(Collection<IAtsActionableItem> teamDefs, Active active) {
      List<IAtsActionableItem> results = new ArrayList<IAtsActionableItem>();
      for (IAtsActionableItem aia : teamDefs) {
         if (active == Active.Both) {
            results.add(aia);
         } else {
            // assume active unless otherwise specified
            boolean attributeActive = aia.isActive();
            if (active == Active.Active && attributeActive) {
               results.add(aia);
            } else if (active == Active.InActive && !attributeActive) {
               results.add(aia);
            }
         }
      }
      return results;
   }

   public static Set<IAtsActionableItem> getChildren(IAtsActionableItem topActionableItem, boolean recurse) throws OseeCoreException {
      Set<IAtsActionableItem> children = new HashSet<IAtsActionableItem>();
      for (IAtsActionableItem child : topActionableItem.getChildrenActionableItems()) {
         children.add(child);
         if (recurse) {
            children.addAll(getChildren(child, recurse));
         }
      }
      return children;
   }

}
