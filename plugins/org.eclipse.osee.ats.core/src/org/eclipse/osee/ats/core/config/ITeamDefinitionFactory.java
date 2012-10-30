/*
 * Created on Aug 27, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.config;

import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;

public interface ITeamDefinitionFactory {

   IAtsTeamDefinition createTeamDefinition(String guid, String title);

   IAtsTeamDefinition getOrCreate(String guid, String name);

}
