/*
 * Created on Jul 16, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.api.version;

import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

public interface IAtsVersionService {

   boolean hasTargetedVersion(Object object) throws OseeCoreException;

   IAtsVersion getTargetedVersion(Object object) throws OseeCoreException;

   IAtsVersion setTargetedVersion(IAtsTeamWorkflow teamWf, IAtsVersion version) throws OseeCoreException;

   IAtsVersion setTargetedVersionAndStore(IAtsTeamWorkflow teamWf, IAtsVersion build) throws OseeCoreException;

   IAtsTeamDefinition getTeamDefinition(IAtsVersion version) throws OseeCoreException;

   void setTeamDefinition(IAtsVersion version, IAtsTeamDefinition teamDef) throws OseeCoreException;

   boolean isReleased(IAtsTeamWorkflow teamWf) throws OseeCoreException;

   boolean isVersionLocked(IAtsTeamWorkflow teamWf) throws OseeCoreException;

   void removeTargetedVersion(IAtsTeamWorkflow teamWf) throws OseeCoreException;

   void removeTargetedVersionAndStore(IAtsTeamWorkflow teamWf) throws OseeCoreException;

}
