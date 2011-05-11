/*
 * Created on May 11, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.workdef;

import org.eclipse.osee.ats.core.config.TeamDefinitionArtifact;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public interface IWorkDefintionFactoryLegacyMgr {

   public WorkDefinitionMatch getWorkFlowDefinitionFromId(String id) throws OseeCoreException;

   public WorkDefinitionMatch getWorkFlowDefinitionFromReverseId(String id) throws OseeCoreException;

   public WorkDefinitionMatch getWorkFlowDefinitionFromArtifact(Artifact artifact) throws OseeCoreException;

   public WorkDefinitionMatch getWorkFlowDefinitionFromTeamDefition(TeamDefinitionArtifact teamDefinition) throws OseeCoreException;

   public String getOverrideId(String legacyId);

}
