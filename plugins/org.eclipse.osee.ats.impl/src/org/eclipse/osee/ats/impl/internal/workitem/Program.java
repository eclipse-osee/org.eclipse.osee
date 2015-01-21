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
import org.eclipse.osee.ats.impl.IAtsServer;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Donald G Dunne
 */
public class Program extends AtsConfigObject implements IAtsProgram {
   IAtsTeamDefinition teamDefinition = null;

   public Program(Log logger, IAtsServer atsServer, ArtifactReadable artifact) {
      super(logger, atsServer, artifact);
   }

   @Override
   public String getTypeName() {
      return "Program";
   }

   @Override
   public Long getUuid() {
      return artifact.getLocalId().longValue();
   }

   @Override
   public IAtsTeamDefinition getTeamDefinition() {
      if (teamDefinition == null) {
         teamDefinition =
            getAtsServer().getConfig().getSoleByGuid(
               artifact.getSoleAttributeValue(AtsAttributeTypes.TeamDefinition, ""), IAtsTeamDefinition.class);
      }
      return teamDefinition;
   }

   @Override
   public String getNamespace() {
      return artifact.getSoleAttributeValue(AtsAttributeTypes.Namespace, "");
   }

}
