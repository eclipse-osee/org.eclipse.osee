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
package org.eclipse.osee.ats.rest.internal.workitem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.agile.IAgileTeam;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinitionService;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.rest.internal.AtsServerImpl;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Donald G. Dunne
 */
public class AtsTeamDefinitionService implements IAtsTeamDefinitionService {

   private final AtsServerImpl atsServer;

   public AtsTeamDefinitionService(AtsServerImpl atsServer) {
      this.atsServer = atsServer;
   }

   @Override
   public IAtsTeamDefinition getTeamDefinition(IAtsWorkItem workItem) throws OseeCoreException {
      IAtsTeamDefinition teamDef = null;
      String teamDefGuid =
         ((ArtifactReadable) workItem.getStoreObject()).getSoleAttributeAsString(AtsAttributeTypes.TeamDefinition, "");
      if (Strings.isValid(teamDefGuid)) {
         teamDef = atsServer.getConfigItem(teamDefGuid);
      }
      return teamDef;
   }

   @Override
   public Collection<IAtsVersion> getVersions(IAtsTeamDefinition teamDef) {
      List<IAtsVersion> versions = new ArrayList<>();
      for (ArtifactReadable verArt : ((ArtifactReadable) teamDef.getStoreObject()).getRelated(
         AtsRelationTypes.TeamDefinitionToVersion_Version)) {
         versions.add(atsServer.getConfigItemFactory().getVersion(verArt));
      }
      return versions;
   }

   @Override
   public IAtsTeamDefinition getTeamDefHoldingVersions(IAtsTeamDefinition teamDef) {
      return teamDef.getTeamDefinitionHoldingVersions();
   }

   @Override
   public IAtsTeamDefinition getTeamDefHoldingVersions(IAtsProgram program) {
      return atsServer.getProgramService().getTeamDefHoldingVersions(program);
   }

   @Override
   public IAtsTeamDefinition getTeamDefinition(String name) {
      IAtsTeamDefinition teamDef = null;
      ArtifactId teamDefArt = atsServer.getArtifactByName(AtsArtifactTypes.TeamDefinition, name);
      if (teamDefArt != null) {
         teamDef = atsServer.getConfigItemFactory().getTeamDef(teamDefArt);
      }
      return teamDef;
   }

   @Override
   public Collection<IAtsTeamDefinition> getTeamDefinitions(IAgileTeam agileTeam) {
      List<IAtsTeamDefinition> teamDefs = new LinkedList<>();
      for (ArtifactReadable atsTeamArt : atsServer.getArtifact(agileTeam).getRelated(
         AtsRelationTypes.AgileTeamToAtsTeam_AtsTeam)) {
         teamDefs.add(atsServer.getConfigItemFactory().getTeamDef(atsTeamArt));
      }
      return teamDefs;
   }

}
