/*
 * Created on Jun 15, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.notify;

import java.util.Collection;
import org.eclipse.osee.framework.skynet.core.User;

/**
 * @author Donald G. Dunne
 */
public class OseeNotificationEvent {

   private final Collection<User> users;
   private final String id;
   private final String type;
   private final String description;

   public OseeNotificationEvent(Collection<User> users, String id, String type, String description) {
      this.users = users;
      this.id = id;
      this.type = type;
      this.description = description;
   }

   public String toString() {
      return type + " - " + id + " - " + users + " - " + description;
   }

   public String getId() {
      return id;
   }

   public String getType() {
      return type;
   }

   public String getDescription() {
      return description;
   }

   public Collection<User> getUsers() {
      return users;
   }
}
