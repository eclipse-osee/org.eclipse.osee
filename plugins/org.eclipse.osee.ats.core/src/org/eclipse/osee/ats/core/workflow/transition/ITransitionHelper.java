/*
 * Created on May 23, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.workflow.transition;

import java.util.Collection;
import org.eclipse.osee.ats.core.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.IBasicUser;
import org.eclipse.osee.framework.core.util.Result;

/**
 * @author Donald G. Dunne
 */
public interface ITransitionHelper {

   public String getName();

   public boolean isPriviledgedEditEnabled();

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

   public Collection<? extends IBasicUser> getToAssignees() throws OseeCoreException;

   public String getToStateName();

   boolean isOverrideAssigneeCheck();

   boolean isWorkingBranchInWork(TeamWorkFlowArtifact teamArt) throws OseeCoreException;

   boolean isBranchInCommit(TeamWorkFlowArtifact teamArt) throws OseeCoreException;

   public boolean isSystemUser() throws OseeCoreException;

   public boolean isSystemUserAssingee(AbstractWorkflowArtifact awa) throws OseeCoreException;

}
