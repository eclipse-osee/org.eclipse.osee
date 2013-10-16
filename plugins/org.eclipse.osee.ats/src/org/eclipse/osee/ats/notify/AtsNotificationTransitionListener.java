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
import org.eclipse.osee.ats.api.notify.AtsNotifyType;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.core.client.notify.AtsNotificationManager;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.client.workflow.transition.ITransitionListener;
import org.eclipse.osee.ats.core.client.workflow.transition.TransitionResults;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.skynet.notify.OseeNotificationManager;

/**
 * @author Donald G. Dunne
 */
public class AtsNotificationTransitionListener implements ITransitionListener {

   @Override
   public void transitioning(TransitionResults results, AbstractWorkflowArtifact sma, IStateToken fromState, IStateToken toState, Collection<? extends IAtsUser> toAssignees) {
      // do nothing
   }

   @Override
   public void transitioned(AbstractWorkflowArtifact awa, IStateToken fromState, IStateToken toState, Collection<? extends IAtsUser> toAssignees, SkynetTransaction transaction) throws OseeCoreException {

      AtsNotificationManager.notify(awa, AtsNotifyType.Subscribed, AtsNotifyType.Completed, AtsNotifyType.Cancelled);

      OseeNotificationManager.getInstance().sendNotifications();
   }

}
