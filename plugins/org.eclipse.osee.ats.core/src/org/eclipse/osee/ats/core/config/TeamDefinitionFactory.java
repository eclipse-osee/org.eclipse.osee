/*
 * Created on Jun 6, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.config;

import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.core.config.impl.TeamDefinition;
import org.eclipse.osee.framework.jdk.core.util.HumanReadableId;

/**
 * @author Donald G. Dunne
 */
public class TeamDefinitionFactory {

   public static IAtsTeamDefinition createTeamDefinition(String guid, String name) {
      return createTeamDefinition(name, guid, HumanReadableId.generate());
   }

   public static IAtsTeamDefinition createTeamDefinition(String name, String guid, String humanReadableId) {
      if (guid == null) {
         throw new IllegalArgumentException("guid can not be null");
      }
      IAtsTeamDefinition teamDef = new TeamDefinition(name, guid, humanReadableId);
      AtsConfigCache.cache(teamDef);
      return teamDef;
   }

   public static IAtsTeamDefinition getOrCreate(String guid, String name) {
      IAtsTeamDefinition teamDef = AtsConfigCache.getSoleByGuid(guid, IAtsTeamDefinition.class);
      if (teamDef == null) {
         teamDef = createTeamDefinition(guid, name);
      }
      return teamDef;
   }

}
