/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.client.program.internal;

import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.program.IAtsProgramService;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.core.client.IAtsClient;
import org.eclipse.osee.ats.core.config.IAtsConfig;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class AtsProgramService implements IAtsProgramService {

   private final IAtsConfig config;
   private final IAtsClient atsClient;

   public AtsProgramService(IAtsClient atsClient, IAtsConfig config) {
      this.atsClient = atsClient;
      this.config = config;
   }

   @Override
   public IAtsTeamDefinition getTeamDefinition(IAtsProgram atsProgram) {
      IAtsTeamDefinition teamDef = atsProgram.getTeamDefinition();
      if (teamDef == null) {
         Artifact artifact = (Artifact) atsProgram.getStoreObject();
         String teamDefGuid = artifact.getSoleAttributeValue(AtsAttributeTypes.TeamDefinition, null);
         if (Strings.isValid(teamDefGuid)) {
            Long uuid = atsClient.getStoreService().getUuidFromGuid(teamDefGuid);
            if (uuid != null) {
               teamDef = (IAtsTeamDefinition) config.getSoleByUuid(uuid);
            }
         }
      }
      return teamDef;
   }

   @Override
   public IAtsProgram getProgram(IAtsWorkItem workItem) {
      throw new UnsupportedOperationException("Not implemented yet");
   }

   @Override
   public IAtsProgram getProgramByGuid(String guid) {
      throw new UnsupportedOperationException("Not implemented yet");
   }

}
