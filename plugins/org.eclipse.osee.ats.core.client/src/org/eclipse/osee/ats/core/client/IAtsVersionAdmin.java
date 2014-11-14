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
package org.eclipse.osee.ats.core.client;

import java.util.Collection;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.version.IAtsVersionService;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public interface IAtsVersionAdmin extends IAtsVersionService {

   Collection<TeamWorkFlowArtifact> getTargetedForTeamWorkflowArtifacts(IAtsVersion verArt) throws OseeCoreException;

   void invalidateVersionCache();

   void invalidateVersionCache(IAtsTeamWorkflow teamWf) throws OseeCoreException;

   Branch getBranch(IAtsVersion version);

   IAtsVersion store(IAtsVersion version, IAtsTeamDefinition teamDef);

}