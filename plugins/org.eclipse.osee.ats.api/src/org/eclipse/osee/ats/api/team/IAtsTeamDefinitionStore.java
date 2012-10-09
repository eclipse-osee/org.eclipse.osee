/*
 * Created on Aug 2, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.api.team;

import org.eclipse.osee.ats.api.IAtsWorkItem;

public interface IAtsTeamDefinitionStore {

   IAtsTeamDefinition getTeamDefinition(IAtsWorkItem workItem);
}
