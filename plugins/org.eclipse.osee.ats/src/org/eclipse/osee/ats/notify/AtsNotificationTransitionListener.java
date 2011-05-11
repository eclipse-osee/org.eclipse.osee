/*
 * Created on May 13, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.notify;

import java.util.Collection;
import org.eclipse.osee.ats.core.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.workflow.transition.ITransitionListener;
import org.eclipse.osee.ats.util.AtsNotifyUsers;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.IWorkPage;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.skynet.notify.OseeNotificationManager;

/**
 * @author Donald G. Dunne
 */
public class AtsNotificationTransitionListener implements ITransitionListener {

   @Override
   public Result transitioning(AbstractWorkflowArtifact sma, IWorkPage fromState, IWorkPage toState, Collection<User> toAssignees) {
      return Result.TrueResult;
   }

   @Override
   public void transitioned(AbstractWorkflowArtifact awa, IWorkPage fromState, IWorkPage toState, Collection<User> toAssignees, SkynetTransaction transaction) throws OseeCoreException {

      AtsNotifyUsers.getInstance().notify(awa, AtsNotifyUsers.NotifyType.Subscribed,
         AtsNotifyUsers.NotifyType.Completed, AtsNotifyUsers.NotifyType.Completed);

      OseeNotificationManager.getInstance().sendNotifications();
   }

}
