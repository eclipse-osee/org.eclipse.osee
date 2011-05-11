/*
 * Created on May 12, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.workflow.transition;

import java.util.Collection;
import org.eclipse.osee.ats.core.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.IWorkPage;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;

/**
 * @author Donald G. Dunne
 */
public interface ITransitionListener {

   /**
    * @return Result of operation. If Result.isFalse(), transition will not continue and Result.popup will occur.
    */
   public Result transitioning(AbstractWorkflowArtifact sma, IWorkPage fromState, IWorkPage toState, Collection<User> toAssignees) throws OseeCoreException;

   public void transitioned(AbstractWorkflowArtifact sma, IWorkPage fromState, IWorkPage toState, Collection<User> toAssignees, SkynetTransaction transaction) throws OseeCoreException;

}
