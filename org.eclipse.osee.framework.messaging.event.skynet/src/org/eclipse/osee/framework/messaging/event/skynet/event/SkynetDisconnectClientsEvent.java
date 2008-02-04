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
 * This message will be issued by the skynet event service to all clients in order to disconnect them from the network.
 * This message should only be issued by administrators.
 * 
 * @author Roberto E. Escobar
 */
public class SkynetDisconnectClientsEvent extends NetworkBroadcastEvent {

   private static final long serialVersionUID = -3159755675253937318L;
   private String[] userIds;

   public SkynetDisconnectClientsEvent(String[] userIds, int branchId, int transactionId, String reason, int author) {
      super(branchId, transactionId, reason, author);
      this.userIds = userIds;
   }

   public String[] getUserIds() {
      return userIds;
   }
}
