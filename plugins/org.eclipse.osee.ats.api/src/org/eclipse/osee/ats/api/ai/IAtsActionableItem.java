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
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.config.TeamDefinition;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;

/**
 * @author Donald G. Dunne
 */
public interface IAtsActionableItem extends IAtsConfigObject {

   IAtsActionableItem SENTINEL = createSentinel();

   public static IAtsActionableItem createSentinel() {
      final class IAtsActionableItemSentinel extends NamedIdBase implements IAtsActionableItem {

         @Override
         public AtsApi getAtsApi() {
            return null;
         }

         @Override
         public boolean isActive() {
            return false;
         }

         @Override
         public ArtifactTypeToken getArtifactType() {
            return AtsArtifactTypes.ActionableItem;
         }

         @Override
         public boolean isActionable() {
            return false;
         }

         @Override
         public IAtsActionableItem getParentActionableItem() {
            return null;
         }

         @Override
         public Collection<IAtsActionableItem> getChildrenActionableItems() {
            return null;
         }

         @Override
         public boolean isAllowUserActionCreation() {
            return false;
         }

         @Override
         public TeamDefinition getTeamDefinition() {
            return null;
         }

      }
      return new IAtsActionableItemSentinel();
   }

   public boolean isActionable();

   public IAtsActionableItem getParentActionableItem();

   public Collection<IAtsActionableItem> getChildrenActionableItems();

   public boolean isAllowUserActionCreation();

   public IAtsTeamDefinition getTeamDefinition();

}
