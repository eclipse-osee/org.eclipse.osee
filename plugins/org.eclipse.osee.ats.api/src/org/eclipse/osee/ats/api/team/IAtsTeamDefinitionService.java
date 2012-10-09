/*
 * Created on Aug 2, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.api.team;

import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

public interface IAtsTeamDefinitionService {

   IAtsTeamDefinition getTeamDefinition(IAtsWorkItem workItem) throws OseeCoreException;
}
