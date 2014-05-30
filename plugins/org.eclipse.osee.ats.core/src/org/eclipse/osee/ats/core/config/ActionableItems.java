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
import org.eclipse.osee.ats.core.internal.AtsCoreService;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;

/**
 * @author Donald G. Dunne
 */
public class ActionableItems {

   private static String TopActionableItemGuid = "AAABER+37QEA8O7WSQaqJQ";

   public static Collection<String> getNames(Collection<? extends IAtsActionableItem> ais) {
      ArrayList<String> names = new ArrayList<String>();
      for (IAtsActionableItem ai : ais) {
         names.add(ai.getName());
      }
      return names;
   }

   public static List<String> toGuids(Collection<? extends IAtsActionableItem> ais) {
      List<String> guids = new ArrayList<String>(ais.size());
      for (IAtsActionableItem ai : ais) {
         guids.add(ai.getGuid());
      }
      return guids;
   }

   /**
    * Recurses default hierarchy and collections children of parentAI that are of type class
    */
   @SuppressWarnings("unchecked")
   public static <A extends IAtsActionableItem> void getChildrenOfType(IAtsActionableItem parentAi, Collection<A> children, Class<A> clazz, boolean recurse) throws OseeCoreException {
      for (IAtsActionableItem child : parentAi.getChildrenActionableItems()) {
         if (clazz.isInstance(child)) {
            children.add((A) child);
            if (recurse) {
               getChildrenOfType(child, children, clazz, recurse);
            }
         }
      }
   }

   public static Set<IAtsActionableItem> getAIsFromItemAndChildren(IAtsActionableItem ai) throws OseeCoreException {
      Set<IAtsActionableItem> ais = new HashSet<IAtsActionableItem>();
      ais.add(ai);
      for (IAtsActionableItem art : ai.getChildrenActionableItems()) {
         ais.addAll(getAIsFromItemAndChildren(art));
      }
      return ais;
   }

   public static Set<IAtsTeamDefinition> getTeamsFromItemAndChildren(IAtsActionableItem ai) throws OseeCoreException {
      return TeamDefinitions.getTeamsFromItemAndChildren(ai);
   }

   public static Set<IAtsActionableItem> getActionableItemsFromItemAndChildren(IAtsActionableItem ai) throws OseeCoreException {
      Set<IAtsActionableItem> ais = new HashSet<IAtsActionableItem>();
      getActionableItemsFromItemAndChildren(ai, ais);
      return ais;
   }

   public static void getActionableItemsFromItemAndChildren(IAtsActionableItem ai, Set<IAtsActionableItem> aiTeams) throws OseeCoreException {
      for (IAtsActionableItem art : ai.getChildrenActionableItems()) {
         aiTeams.add(art);
         for (IAtsActionableItem childArt : ai.getChildrenActionableItems()) {
            getActionableItemsFromItemAndChildren(childArt, aiTeams);
         }
      }
   }

   public static Set<IAtsActionableItem> getActionableItems(Collection<String> actionableItemNames) throws OseeCoreException {
      Set<IAtsActionableItem> ais = new HashSet<IAtsActionableItem>();
      for (String actionableItemName : actionableItemNames) {
         for (IAtsActionableItem ai : AtsCoreService.getConfig().get(IAtsActionableItem.class)) {
            if (ai.getName().equals(actionableItemName)) {
               ais.add(ai);
            }
         }
      }
      return ais;
   }

   public static Collection<IAtsTeamDefinition> getImpactedTeamDefs(Collection<IAtsActionableItem> ais) throws OseeCoreException {
      return TeamDefinitions.getImpactedTeamDefs(ais);
   }

   public static List<IAtsActionableItem> getActionableItems(Active active) throws OseeCoreException {
      return Collections.castAll(getActive(AtsCoreService.getConfig().get(IAtsActionableItem.class), active));
   }

   public static String getNotActionableItemError(IAtsConfigObject configObject) {
      return "Action can not be written against " + configObject.getName() + " \"" + configObject + "\" (" + configObject.getGuid() + ").\n\nChoose another item.";
   }

   public static IAtsActionableItem getTopActionableItem() throws OseeCoreException {
      return AtsCoreService.getConfig().getSoleByGuid(TopActionableItemGuid, IAtsActionableItem.class);
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

   public static List<IAtsActionableItem> getActive(Collection<IAtsActionableItem> ais, Active active) {
      List<IAtsActionableItem> results = new ArrayList<IAtsActionableItem>();
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
