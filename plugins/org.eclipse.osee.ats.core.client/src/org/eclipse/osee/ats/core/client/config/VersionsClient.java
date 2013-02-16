/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.client.config;

import java.util.Collection;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class VersionsClient {

   public static Collection<TeamWorkFlowArtifact> getTargetedForTeamWorkflows(IAtsVersion verArt) throws OseeCoreException {
      Artifact artifact = AtsObjectsClient.getSoleArtifact(verArt);
      return artifact.getRelatedArtifactsOfType(AtsRelationTypes.TeamWorkflowTargetedForVersion_Workflow,
         TeamWorkFlowArtifact.class);
   }

}
