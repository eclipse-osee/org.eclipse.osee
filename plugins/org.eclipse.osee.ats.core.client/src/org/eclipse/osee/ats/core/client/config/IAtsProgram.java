/*
 * Created on Apr 6, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.client.config;

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public interface IAtsProgram {

   public String getName();

   public boolean isActive() throws OseeCoreException;

   public Artifact getArtifact() throws OseeCoreException;

   public String getStaticIdPrefix() throws OseeCoreException;

   public String getProgramName() throws OseeCoreException;

   public TeamDefinitionArtifact getTeamDefHoldingVersions() throws OseeCoreException;

}
