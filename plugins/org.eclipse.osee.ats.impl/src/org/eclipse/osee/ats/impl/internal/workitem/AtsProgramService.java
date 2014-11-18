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
package org.eclipse.osee.ats.impl.internal.workitem;

import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.program.IAtsProgramService;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.impl.IAtsServer;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Donald G. Dunne
 */
public class AtsProgramService implements IAtsProgramService {

   private final IAtsServer atsServer;

   public AtsProgramService(IAtsServer atsServer) {
      this.atsServer = atsServer;
   }

   @Override
   public IAtsTeamDefinition getTeamDefinition(IAtsProgram atsProgram) {
      IAtsTeamDefinition teamDef = null;
      ArtifactReadable artifact = (ArtifactReadable) atsProgram.getStoreObject();
      String teamDefGuid = artifact.getSoleAttributeValue(AtsAttributeTypes.TeamDefinition, null);
      if (Strings.isValid(teamDefGuid)) {
         teamDef = (IAtsTeamDefinition) atsServer.getConfig().getSoleByGuid(teamDefGuid);
      }
      return teamDef;
   }

}
