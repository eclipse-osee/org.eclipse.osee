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
package org.eclipse.osee.ats.core.util.hooks;

import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.notify.AtsNotificationEventFactory;
import org.eclipse.osee.ats.api.notify.AtsNotifyType;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workflow.hooks.IAtsTransitionHook;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * Contributed via AtsWorkItemServiceImpl
 *
 * @author Donald G. Dunne
 */
public class AtsNotificationTransitionHook implements IAtsTransitionHook {

   @Override
   public void transitioned(IAtsWorkItem workItem, IStateToken fromState, IStateToken toState, Collection<? extends AtsUser> toAssignees, IAtsChangeSet changes) {
      try {
         changes.addWorkItemNotificationEvent(
            AtsNotificationEventFactory.getWorkItemNotificationEvent(AtsCoreUsers.SYSTEM_USER, workItem,
               AtsNotifyType.Subscribed, AtsNotifyType.Completed, AtsNotifyType.Cancelled));
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsNotificationTransitionHook.class, Level.SEVERE, "Error adding ATS Notification Event", ex);
      }
   }

   @Override
   public String getDescription() {
      return "Adds notification events for transitions";
   }

}
