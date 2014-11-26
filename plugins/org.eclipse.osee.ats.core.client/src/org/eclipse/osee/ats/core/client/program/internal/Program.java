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
package org.eclipse.osee.ats.core.client.program.internal;

import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.core.client.IAtsClient;
import org.eclipse.osee.ats.core.client.internal.config.AtsConfigObject;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class Program extends AtsConfigObject implements IAtsProgram {

   public Program(IAtsClient atsClient, Artifact artifact) {
      super(atsClient, artifact);
   }

   @Override
   public Long getUuid() {
      return getId();
   }

   @Override
   public IAtsTeamDefinition getTeamDefinition() {
      IAtsTeamDefinition teamDef = null;
      String teamDefGuid = getAttributeValue(AtsAttributeTypes.TeamDefinition, "");
      if (Strings.isValid(teamDefGuid)) {
         teamDef = getAtsClient().getConfig().getSoleByGuid(teamDefGuid, IAtsTeamDefinition.class);
      }
      return teamDef;
   }

   @Override
   public String getNamespace() {
      return getAttributeValue(AtsAttributeTypes.Namespace, "");
   }

   @Override
   public String getTypeName() {
      return artifact.getArtifactTypeName();
   }

}
