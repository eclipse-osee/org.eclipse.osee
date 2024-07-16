/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.ats.api.team;

import java.util.Collection;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.ai.ActionableItem;
import org.eclipse.osee.ats.api.config.TeamDefinition;
import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;

/**
 * @author Donald G. Dunne
 */
public interface IAtsTeamDefinition extends IAtsConfigObject {

   IAtsTeamDefinition SENTINEL = createSentinel();

   public static IAtsTeamDefinition createSentinel() {
      final class IAtsTeamDefinitionSentinel extends NamedIdBase implements IAtsTeamDefinition {

         @Override
         public boolean isActive() {
            return false;
         }

         @Override
         public ArtifactTypeToken getArtifactType() {
            return AtsArtifactTypes.TeamDefinition;
         }

         @Override
         public AtsApi getAtsApi() {
            return null;
         }

         @Override
         public Collection<ActionableItem> getActionableItems() {
            return null;
         }

         @Override
         public Collection<TeamDefinition> getChildrenTeamDefs() {
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
      return new IAtsTeamDefinitionSentinel();
   }

   Collection<ActionableItem> getActionableItems();

   Collection<TeamDefinition> getChildrenTeamDefs();

   public String getProgramId();

   public Collection<String> getCscis();

}
