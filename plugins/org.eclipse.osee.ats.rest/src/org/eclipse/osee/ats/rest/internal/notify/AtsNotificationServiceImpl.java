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
package org.eclipse.osee.ats.rest.internal.notify;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.notify.AtsNotificationEvent;
import org.eclipse.osee.ats.api.notify.AtsNotifyType;
import org.eclipse.osee.ats.api.notify.IAtsNotificationService;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public class AtsNotificationServiceImpl implements IAtsNotificationService {

   public AtsNotificationServiceImpl() {
   }

   @Override
   public void addNotificationEvent(AtsNotificationEvent notificationEvent) throws OseeCoreException {
      // TODO - feature[ats_<QPLVQ>]: Add notification for ATS cancel to the server
   }

   @Override
   public void clear() {
      // TODO - feature[ats_<QPLVQ>]: Add notification for ATS cancel to the server
   }

   @Override
   public List<AtsNotificationEvent> getNotificationEvents() throws OseeCoreException {
      // TODO - feature[ats_<QPLVQ>]: Add notification for ATS cancel to the server
      return Collections.emptyList();
   }

   @Override
   public void sendNotifications() {
      // TODO - feature[ats_<QPLVQ>]: Add notification for ATS cancel to the server
   }

   @Override
   public void notify(IAtsWorkItem workItem, Collection<? extends IAtsUser> notifyUsers, AtsNotifyType... notifyTypes) throws OseeCoreException {
      // TODO - feature[ats_<QPLVQ>]: Add notification for ATS cancel to the server
   }

}
