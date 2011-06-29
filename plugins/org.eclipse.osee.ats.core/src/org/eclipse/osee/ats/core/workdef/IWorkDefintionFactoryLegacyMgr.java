/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
