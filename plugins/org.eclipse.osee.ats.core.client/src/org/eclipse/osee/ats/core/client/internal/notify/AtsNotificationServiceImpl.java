/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.client.internal.notify;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.notify.AtsNotificationEvent;
import org.eclipse.osee.ats.api.notify.AtsNotifyType;
import org.eclipse.osee.ats.api.notify.IAtsNotificationService;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.core.client.internal.AtsClientService;
import org.eclipse.osee.ats.core.client.notify.AtsNotificationManager;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.utility.OseeNotificationEvent;
import org.eclipse.osee.framework.ui.skynet.notify.OseeNotificationManager;

/**
 * @author Donald G. Dunne
 */
public class AtsNotificationServiceImpl implements IAtsNotificationService {

   public AtsNotificationServiceImpl() {
   }

   @Override
   public void addNotificationEvent(AtsNotificationEvent notificationEvent) throws OseeCoreException {
      OseeNotificationManager.getInstance().addNotificationEvent(
         new OseeNotificationEvent(AtsClientService.get().getUserAdmin().getOseeUsers(notificationEvent.getUsers()),
            notificationEvent.getId(), notificationEvent.getType(), notificationEvent.getDescription()));
   }

   @Override
   public void clear() {
      OseeNotificationManager.getInstance().clear();
   }

   @Override
   public List<AtsNotificationEvent> getNotificationEvents() throws OseeCoreException {
      List<AtsNotificationEvent> events = new ArrayList<AtsNotificationEvent>();
      for (OseeNotificationEvent event : OseeNotificationManager.getInstance().getNotificationEvents()) {
         events.add(new AtsNotificationEvent(AtsClientService.get().getUserAdmin().getAtsUsers(event.getUsers()),
            event.getId(), event.getType(), event.getDescription()));
      }
      return events;
   }

   @Override
   public void sendNotifications() {
      OseeNotificationManager.getInstance().sendNotifications();
   }

   @Override
   public void notify(IAtsWorkItem workItem, Collection<? extends IAtsUser> notifyUsers, AtsNotifyType... notifyTypes) throws OseeCoreException {
      AtsNotificationManager.notify((AbstractWorkflowArtifact) AtsClientService.get().getArtifact(workItem),
         notifyUsers, notifyTypes);
   }

}
