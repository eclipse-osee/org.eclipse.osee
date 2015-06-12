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
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.core.model.impl.AtsConfigObject;
import org.eclipse.osee.ats.impl.IAtsServer;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Donald G Dunne
 */
public class Program extends AtsConfigObject implements IAtsProgram {
   IAtsTeamDefinition teamDefinition = null;
   private final IAtsServer atsServer;

   public Program(Log logger, IAtsServer atsServer, ArtifactReadable artifact) {
      super(logger, atsServer, artifact);
      this.atsServer = atsServer;
   }

   private ArtifactReadable getArtifact() {
      return (ArtifactReadable) artifact;
   }

   @Override
   public String getTypeName() {
      return "Program";
   }

   @Override
   public long getUuid() {
      return artifact.getUuid();
   }

   @Override
   public IAtsTeamDefinition getTeamDefinition() {
      if (teamDefinition == null) {
         String teamDefGuid = getArtifact().getSoleAttributeValue(AtsAttributeTypes.TeamDefinition, "");
         Long uuid = atsServer.getStoreService().getUuidFromGuid(teamDefGuid);
         teamDefinition = atsServer.getConfig().getSoleByUuid(uuid, IAtsTeamDefinition.class);
      }
      return teamDefinition;
   }

   @Override
   public String getNamespace() {
      return getArtifact().getSoleAttributeValue(AtsAttributeTypes.Namespace, "");
   }

}
