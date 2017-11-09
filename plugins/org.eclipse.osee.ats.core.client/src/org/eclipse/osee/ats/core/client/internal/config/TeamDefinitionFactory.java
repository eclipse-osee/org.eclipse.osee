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
package org.eclipse.osee.ats.core.client.internal.config;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.core.client.util.AtsUtilClient;
import org.eclipse.osee.ats.core.config.ITeamDefinitionFactory;
import org.eclipse.osee.ats.core.config.TeamDefinition;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.util.GUID;

/**
 * @author Donald G. Dunne
 */
public class TeamDefinitionFactory implements ITeamDefinitionFactory {

   @Override
   public IAtsTeamDefinition createTeamDefinition(String name, long id, IAtsChangeSet changes, AtsApi atsApi) {
      ArtifactToken artifact = changes.createArtifact(AtsArtifactTypes.TeamDefinition, name, GUID.create(), id);
      return new TeamDefinition(atsApi.getLogger(), atsApi, artifact);
   }

   @Override
   public IAtsTeamDefinition createTeamDefinition(String name, IAtsChangeSet changes, AtsApi atsApi) {
      return createTeamDefinition(name, AtsUtilClient.createConfigObjectId(), changes, atsApi);
   }

}
