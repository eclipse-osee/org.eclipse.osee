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
package org.eclipse.osee.ats.core.client.team;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.team.IAtsConfigItemFactory;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinitionService;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.core.client.internal.AtsClientService;
import org.eclipse.osee.ats.core.config.IAtsConfig;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class AtsTeamDefinitionService implements IAtsTeamDefinitionService {

   private final IAtsConfig config;
   private final IAtsConfigItemFactory configFactory;

   public AtsTeamDefinitionService(IAtsConfig config, IAtsConfigItemFactory configFactory) {
      this.config = config;
      this.configFactory = configFactory;
   }

   @Override
   public IAtsTeamDefinition getTeamDefinition(IAtsWorkItem workItem) throws OseeCoreException {
      IAtsTeamDefinition teamDef = null;
      String teamDefGuid =
         ((Artifact) workItem.getStoreObject()).getSoleAttributeValue(AtsAttributeTypes.TeamDefinition, "");
      if (Strings.isValid(teamDefGuid)) {
         Long uuid = AtsClientService.get().getStoreService().getUuidFromGuid(teamDefGuid);
         teamDef = (IAtsTeamDefinition) config.getSoleByUuid(uuid);
      }
      return teamDef;
   }

   @Override
   public Collection<IAtsVersion> getVersions(IAtsTeamDefinition teamDef) {
      List<IAtsVersion> versions = new ArrayList<IAtsVersion>();
      for (Artifact verArt : ((Artifact) teamDef.getStoreObject()).getRelatedArtifacts(AtsRelationTypes.TeamDefinitionToVersion_Version)) {
         versions.add(configFactory.getVersion(verArt));
      }
      return versions;
   }

}
