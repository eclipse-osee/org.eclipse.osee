/*
 * Created on Apr 20, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.notify;

import java.util.List;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public interface INotificationManager {

   public void addNotificationEvent(OseeNotificationEvent notificationEvent);

   public void clear();

   public List<OseeNotificationEvent> getNotificationEvents();

   public void sendNotifications() throws OseeCoreException;

}
