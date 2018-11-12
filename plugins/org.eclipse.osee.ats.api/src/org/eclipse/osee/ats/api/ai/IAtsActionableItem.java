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
package org.eclipse.osee.ats.api.ai;

import java.util.Collection;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.rule.IAtsRules;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;

/**
 * @author Donald G. Dunne
 */
public interface IAtsActionableItem extends IAtsConfigObject, IAtsRules {

   /*****************************
    * Name, Full Name, Description
    ******************************/
   void setName(String name);

   IAtsActionableItem SENTINEL = createSentinel();

   void setDescription(String description);

   /*****************************
    * Parent and Children Team Definitions
    ******************************/
   Collection<IAtsActionableItem> getChildrenActionableItems();

   IAtsActionableItem getParentActionableItem();

   IAtsTeamDefinition getTeamDefinition();

   IAtsTeamDefinition getTeamDefinitionInherited();

   /*****************************
    * Misc
    ******************************/
   Collection<String> getStaticIds();

   public boolean isActionable();

   /*****************************************************
    * Team Leads, Members
    ******************************************************/
   Collection<IAtsUser> getLeads();

   Collection<IAtsUser> getSubscribed();

   boolean isAllowUserActionCreation();

   public static IAtsActionableItem createSentinel() {
      final class IAtsActionableItemSentinel extends NamedIdBase implements IAtsActionableItem {

         public IAtsActionableItemSentinel() {
            super(Id.SENTINEL, "SENTINEL");

         }

         @Override
         public boolean isActive() {
            return false;
         }

         @Override
         public ArtifactTypeId getArtifactType() {
            return null;
         }

         @Override
         public Collection<String> getRules() {
            return null;
         }

         @Override
         public boolean hasRule(String rule) {
            return false;
         }

         @Override
         public void setDescription(String description) {
         }

         @Override
         public Collection<IAtsActionableItem> getChildrenActionableItems() {
            return null;
         }

         @Override
         public IAtsActionableItem getParentActionableItem() {
            return null;
         }

         @Override
         public IAtsTeamDefinition getTeamDefinition() {
            return null;
         }

         @Override
         public IAtsTeamDefinition getTeamDefinitionInherited() {
            return null;
         }

         @Override
         public Collection<String> getStaticIds() {
            return null;
         }

         @Override
         public boolean isActionable() {
            return false;
         }

         @Override
         public Collection<IAtsUser> getLeads() {
            return null;
         }

         @Override
         public Collection<IAtsUser> getSubscribed() {
            return null;
         }

         @Override
         public boolean isAllowUserActionCreation() {
            return false;
         }

      }
      return new IAtsActionableItemSentinel();
   }

}
