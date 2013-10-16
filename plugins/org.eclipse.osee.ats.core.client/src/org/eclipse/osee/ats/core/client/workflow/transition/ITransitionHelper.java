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
package org.eclipse.osee.ats.core.client.workflow.transition;

import java.util.Collection;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public interface ITransitionHelper {

   public String getName();

   public boolean isPrivilegedEditEnabled();

   public boolean isOverrideTransitionValidityCheck();

   public Collection<? extends AbstractWorkflowArtifact> getAwas();

   /**
    * @return Result.isTrue with text if reason provided
    * @return Result.isFalse if no reason given
    * @return Result.isCancelled to cancel transition
    */
   public Result getCompleteOrCancellationReason();

   /**
    * @return Result.isTrue with text if hours provided
    * @return Result.isFalse if no extra hours given
    * @return Result.isCancelled to cancel transition
    */
   public Result handleExtraHoursSpent();

   public Collection<? extends IAtsUser> getToAssignees(AbstractWorkflowArtifact awa) throws OseeCoreException;

   public String getToStateName();

   boolean isOverrideAssigneeCheck();

   boolean isWorkingBranchInWork(TeamWorkFlowArtifact teamArt) throws OseeCoreException;

   boolean isBranchInCommit(TeamWorkFlowArtifact teamArt) throws OseeCoreException;

   public boolean isSystemUser() throws OseeCoreException;

   public boolean isSystemUserAssingee(AbstractWorkflowArtifact awa) throws OseeCoreException;

}
