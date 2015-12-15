/*
 * Created on Nov 18, 2014
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.rest.internal.workitem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinitionService;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.rest.internal.AtsServerImpl;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.data.ArtifactReadable;

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
         Long uuid = atsServer.getStoreService().getUuidFromGuid(teamDefGuid);
         teamDef = (IAtsTeamDefinition) atsServer.getConfig().getSoleByUuid(uuid);
      }
      return teamDef;
   }

   @Override
   public Collection<IAtsVersion> getVersions(IAtsTeamDefinition teamDef) {
      List<IAtsVersion> versions = new ArrayList<>();
      for (ArtifactReadable verArt : ((ArtifactReadable) teamDef.getStoreObject()).getRelated(AtsRelationTypes.TeamDefinitionToVersion_Version)) {
         versions.add(atsServer.getConfigItemFactory().getVersion(verArt));
      }
      return versions;
   }

}
