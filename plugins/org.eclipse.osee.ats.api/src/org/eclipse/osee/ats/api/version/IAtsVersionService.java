/*
 * Created on Jul 16, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.api.version;

import java.util.Collection;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;

public interface IAtsVersionService {

   boolean hasTargetedVersion(Object object);

   IAtsVersion getTargetedVersion(Object object);

   IAtsVersion setTargetedVersion(IAtsTeamWorkflow teamWf, IAtsVersion version);

   Collection<IAtsVersion> getVersions(IAtsTeamDefinition teamDef);

   void addVersion(IAtsTeamDefinition teamDef, IAtsVersion version);

   public IAtsTeamDefinition getTeamDefinition(IAtsVersion version);

   void setTeamDefinition(IAtsVersion version, IAtsTeamDefinition teamDef);

   public boolean isReleased(IAtsTeamWorkflow teamWf);

   public boolean isVersionLocked(IAtsTeamWorkflow teamWf);

}
