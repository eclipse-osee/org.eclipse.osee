/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.messaging.event.skynet.event;

/**
 * @author Robert A. Fisher
 */
public class NetworkBroadcastEvent extends SkynetEventBase {

   private static final long serialVersionUID = 4199206432501390599L;
   private String message;
   private final String broadcastEventType;
   private final String[] userIds;

   /**
    * @return the broadcastEventType
    */
   public String getBroadcastEventTypeName() {
      return broadcastEventType;
   }

   public NetworkBroadcastEvent(String broadcastEventType, String message, String[] userIds, NetworkSender networkSender) {
      super(networkSender);
      this.broadcastEventType = broadcastEventType;
      this.message = message;
      this.userIds = userIds;
   }

   public NetworkBroadcastEvent(String broadcastEventType, String message, NetworkSender networkSender) {
      this(broadcastEventType, message, new String[] {}, networkSender);
   }

   /**
    * @return the userIds
    */
   public String[] getUserIds() {
      return userIds;
   }

   public String getMessage() {
      return message;
   }

   public void setMessage(String message) {
      this.message = message;
   }
}
