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
package org.eclipse.osee.ats.core.workflow;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.logger.Log;

/**
 * @author Donald G. Dunne
 */
public class TeamWorkflow extends WorkItem implements IAtsTeamWorkflow {

   public TeamWorkflow(Log logger, IAtsServices services, ArtifactToken artifact) {
      super(logger, services, artifact);
   }

   @Override
   public Set<IAtsActionableItem> getActionableItems()  {
      Set<IAtsActionableItem> ais = new HashSet<>();
      Collection<ArtifactId> artIds =
         services.getAttributeResolver().getArtifactIdReferences(artifact, AtsAttributeTypes.ActionableItemReference);
      for (ArtifactId artId : artIds) {
         IAtsActionableItem ai = services.getConfigItem(artId);
         Conditions.assertNotNull(ai, "ai can not be null for artId %s", artId);
         ais.add(ai);
      }
      return ais;
   }

   @Override
   public IAtsTeamDefinition getTeamDefinition()  {
      IAtsTeamDefinition teamDef = null;
      ArtifactId teamDefId = services.getAttributeResolver().getSoleArtifactIdReference(artifact,
         AtsAttributeTypes.TeamDefinitionReference, ArtifactId.SENTINEL);
      if (teamDefId.isValid()) {
         teamDef = services.getConfigItem(teamDefId);
      }
      return teamDef;
   }
}
