/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.team;

import java.util.Collection;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.ai.ActionableItem;
import org.eclipse.osee.ats.api.config.TeamDefinition;
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

      }
      return new IAtsTeamDefinitionSentinel();
   }

   Collection<ActionableItem> getActionableItems();

   Collection<TeamDefinition> getChildrenTeamDefs();

}
