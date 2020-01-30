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

import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.TeamDefinition;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.config.JaxTeamDefinition;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.core.model.impl.AtsConfigObject;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.logger.Log;

/**
 * @author Donald G Dunne
 */
public class TeamDefinition extends AtsConfigObject implements IAtsTeamDefinition {

   public TeamDefinition(Log logger, AtsApi atsApi, JaxTeamDefinition jaxTeamDef) {
      super(logger, atsApi, ArtifactToken.valueOf(jaxTeamDef.getId(), jaxTeamDef.getGuid(), jaxTeamDef.getName(),
         atsApi.getAtsBranch(), TeamDefinition), TeamDefinition);
   }

   public TeamDefinition(Log logger, AtsApi atsApi, ArtifactToken artifact) {
      super(logger, atsApi, artifact, TeamDefinition);
   }

}
