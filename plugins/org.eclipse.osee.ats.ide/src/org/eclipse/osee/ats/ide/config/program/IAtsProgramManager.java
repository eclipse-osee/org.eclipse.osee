/*********************************************************************
 * Copyright (c) 2011 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.config.program;

import java.util.Collection;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.operation.IOperation;

/**
 * @author Donald G. Dunne
 */
public interface IAtsProgramManager {

   public boolean isApplicable(TeamWorkFlowArtifact teamArt);

   public IOperation createValidateReqChangesOp(TeamWorkFlowArtifact teamArt);

   public String getName();

   public Collection<IAtsProgram> getPrograms();

   public String getXProgramComboWidgetName();

   public IAtsProgram getProgram(TeamWorkFlowArtifact teamArt);

   public ArtifactToken getPidsReviewAssigneeUserGroup();

}
