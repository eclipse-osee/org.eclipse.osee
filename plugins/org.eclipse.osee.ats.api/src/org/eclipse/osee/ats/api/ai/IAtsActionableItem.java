/*********************************************************************
 * Copyright (c) 2013 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.api.ai;

import java.util.Collection;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.config.TeamDefinition;
import org.eclipse.osee.ats.api.config.WorkType;
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

         @Override
         public Collection<WorkType> getWorkTypes() {
            return null;
         }

         @Override
         public boolean isWorkType(WorkType workType) {
            return false;
         }

         @Override
         public Collection<String> getTags() {
            return null;
         }

         @Override
         public boolean hasTag(String tag) {
            return false;
         }

         @Override
         public String getProgramId() {
            return null;
         }

         @Override
         public Collection<String> getCscis() {
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

   public String getProgramId();

   public Collection<String> getCscis();

}
