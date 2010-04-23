/*
 * Created on Apr 20, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
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
