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
package org.eclipse.osee.ats.api.team;

import java.util.Collection;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.agile.IAgileTeam;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.framework.core.data.ArtifactId;

/**
 * @author Donald G. Dunne
 */
public interface IAtsTeamDefinitionService {

   IAtsTeamDefinition getTeamDefinition(IAtsWorkItem workItem);

   Collection<IAtsVersion> getVersions(IAtsTeamDefinition teamDef);

   IAtsTeamDefinition getTeamDefHoldingVersions(IAtsTeamDefinition teamDef);

   IAtsTeamDefinition getTeamDefHoldingVersions(IAtsProgram program);

   IAtsTeamDefinition getTeamDefinition(String name);

   Collection<IAtsTeamDefinition> getTeamDefinitions(IAgileTeam agileTeam);

   /**
    * @return this object casted, else if hard artifact constructed, else load and construct
    */
   IAtsTeamDefinition getTeamDefinitionById(ArtifactId teamDefId);

}
