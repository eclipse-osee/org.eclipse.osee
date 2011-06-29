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
package org.eclipse.osee.ats.notify;

import java.util.Collection;
import org.eclipse.osee.ats.core.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.workflow.transition.ITransitionListener;
import org.eclipse.osee.ats.core.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.util.AtsNotifyUsers;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.IBasicUser;
import org.eclipse.osee.framework.core.util.IWorkPage;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.skynet.notify.OseeNotificationManager;

/**
 * @author Donald G. Dunne
 */
public class AtsNotificationTransitionListener implements ITransitionListener {

   @Override
   public void transitioning(TransitionResults results, AbstractWorkflowArtifact sma, IWorkPage fromState, IWorkPage toState, Collection<? extends IBasicUser> toAssignees) {
      // do nothing
   }

   @Override
   public void transitioned(AbstractWorkflowArtifact awa, IWorkPage fromState, IWorkPage toState, Collection<? extends IBasicUser> toAssignees, SkynetTransaction transaction) throws OseeCoreException {

      AtsNotifyUsers.getInstance().notify(awa, AtsNotifyUsers.NotifyType.Subscribed,
         AtsNotifyUsers.NotifyType.Completed, AtsNotifyUsers.NotifyType.Completed);

      OseeNotificationManager.getInstance().sendNotifications();
   }

}
