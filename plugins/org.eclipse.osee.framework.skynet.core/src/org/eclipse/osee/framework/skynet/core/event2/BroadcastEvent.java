/*
 * Created on May 26, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.event2;

import java.util.Collection;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.event.BroadcastEventType;
import org.eclipse.osee.framework.skynet.core.event.msgs.NetworkSender;

/**
 * @author Donald G. Dunne
 */
public class BroadcastEvent extends FrameworkEvent {

   BroadcastEventType broadcastEventType;
   Collection<User> users;
   String message;
   NetworkSender networkSender;

   public BroadcastEventType getBroadcastEventType() {
      return broadcastEventType;
   }

   public void setBroadcastEventType(BroadcastEventType broadcastEventType) {
      this.broadcastEventType = broadcastEventType;
   }

   public String getMessage() {
      return message;
   }

   public void setMessage(String message) {
      this.message = message;
   }

   public Collection<User> getUsers() {
      return users;
   }

   public void setUsers(Collection<User> users) {
      this.users = users;
   }

   public void addUser(User user) {
      this.users.add(user);
   }

   public NetworkSender getNetworkSender() {
      return networkSender;
   }

   public void setNetworkSender(NetworkSender networkSender) {
      this.networkSender = networkSender;
   }
}
