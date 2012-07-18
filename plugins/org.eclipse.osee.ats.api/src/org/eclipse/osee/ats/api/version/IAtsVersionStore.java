/*
 * Created on Jul 16, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.api.version;

import java.util.Collection;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

public interface IAtsVersionStore {

   public IAtsTeamWorkflow setTargetedVersionLink(IAtsTeamWorkflow teamWf, IAtsVersion version) throws OseeCoreException;

   public IAtsVersion getTargetedVersion(Object object) throws OseeCoreException;

   public Collection<IAtsVersion> getVersions(IAtsTeamDefinition teamDef) throws OseeCoreException;

   public IAtsTeamDefinition getTeamDefinition(IAtsVersion version) throws OseeCoreException;

   public void setTeamDefinition(IAtsVersion version, IAtsTeamDefinition teamDef) throws OseeCoreException;

}
