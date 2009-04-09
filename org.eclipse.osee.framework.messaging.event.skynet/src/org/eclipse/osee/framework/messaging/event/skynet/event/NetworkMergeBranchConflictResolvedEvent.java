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
 * Skynet messaging event; Should not be subscribed to by OSEE applications.
 * 
 * @author Donald G. Dunne
 */
public class NetworkMergeBranchConflictResolvedEvent extends SkynetEventBase {
   private static final long serialVersionUID = 8339596149601337894L;
   private final int branchId;

   public NetworkMergeBranchConflictResolvedEvent(int branchId, NetworkSender networkSender) {
      super(networkSender);
      this.branchId = branchId;
   }

   public int getBranchId() {
      return branchId;
   }

   /* (non-Javadoc)
    * @see java.lang.Comparable#compareTo(T)
    */
   @Override
   public int compareTo(Object o) {
      return 0;
   }

}
