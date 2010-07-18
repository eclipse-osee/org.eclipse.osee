/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.test.util;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.notify.INotificationManager;
import org.eclipse.osee.framework.ui.skynet.notify.OseeNotificationEvent;

/**
 * @author Donald G. Dunne
 */
public class TestNotificationManager implements INotificationManager {

   private List<OseeNotificationEvent> notificationEvents = new ArrayList<OseeNotificationEvent>();

   @Override
   public void addNotificationEvent(OseeNotificationEvent notificationEvent) {
      notificationEvents.add(notificationEvent);
   }

   @Override
   public void clear() {
      notificationEvents.clear();
   }

   @Override
   public List<OseeNotificationEvent> getNotificationEvents() {
      return notificationEvents;
   }

   @Override
   public void sendNotifications() throws OseeCoreException {
   }

}
