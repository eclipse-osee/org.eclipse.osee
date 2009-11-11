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
 * @author Jeff C. Phillips
 */
public class NetworkNewBranchEvent extends SkynetEventBase {
   private static final long serialVersionUID = 8339596149601337894L;
   private final int branchId;

   public NetworkNewBranchEvent(int branchId, NetworkSender networkSender) {
      super(networkSender);
      this.branchId = branchId;
   }

   public int getId() {
      return branchId;
   }

}
