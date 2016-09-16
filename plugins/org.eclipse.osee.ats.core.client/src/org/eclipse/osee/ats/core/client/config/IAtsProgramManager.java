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
package org.eclipse.osee.ats.core.client.config;

import java.util.Collection;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public interface IAtsProgramManager {

   public boolean isApplicable(TeamWorkFlowArtifact teamArt);

   public IOperation createValidateReqChangesOp(TeamWorkFlowArtifact teamArt) throws OseeCoreException;

   public String getName();

   public Collection<IAtsProgram> getPrograms() throws OseeCoreException;

   public String getXProgramComboWidgetName();

   public IAtsProgram getProgram(TeamWorkFlowArtifact teamArt) throws OseeCoreException;

   public ArtifactToken getReviewAssigneeUserGroup(TeamWorkFlowArtifact teamArt) throws OseeCoreException;

   public ArtifactToken getPidsReviewAssigneeUserGroup() throws OseeCoreException;

}
