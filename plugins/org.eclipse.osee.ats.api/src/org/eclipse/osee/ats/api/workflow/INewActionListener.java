/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.ats.api.workflow;

import java.util.List;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.AtsWorkDefinitionToken;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;

/**
 * @author Donald G. Dunne
 */
public interface INewActionListener {

   /**
    * Called after Action and team workflows are created and before persist of Action
    */
   public default void actionCreated(IAtsAction action) {
      // for override
   }

   /**
    * Called after team workflow and initialized and before persist of Action
    */
   public default void teamCreated(IAtsAction action, IAtsTeamWorkflow teamWf, IAtsChangeSet changes) {
      // for override
   }

   public default AtsWorkDefinitionToken getOverrideWorkDefinitionId(IAtsTeamDefinition teamDef) {
      // for override
      return null;
   }

   /**
    * @return the artifact token to use for team workflow for applicableAis
    */
   public default ArtifactToken getArtifactToken(List<IAtsActionableItem> applicableAis) {
      // for override
      return null;
   }

   public default ArtifactTypeToken getOverrideArtifactType(IAtsTeamDefinition teamDef) {
      // for override
      return ArtifactTypeToken.SENTINEL;
   }
}
