/*
 * Created on Mar 3, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.notify;

import java.util.Collection;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.notify.OseeNotificationEvent;

/**
 * @author Donald G. Dunne
 */
public interface IAtsNotification {

   /**
    * Descriptive name that admin can identify the group/notification that will get sent
    * 
    * @return name
    */
   public String getNotificationName();

   public Collection<OseeNotificationEvent> getNotificationEvents() throws OseeCoreException;

}
