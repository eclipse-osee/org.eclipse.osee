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
 * @author Donald G. Dunne
 */
public class NetworkTransactionDeletedEvent extends SkynetEventBase {

   private static final long serialVersionUID = -2467438797592036593L;
   private final int[] transactionIds;

   /**
    * @return the transactionIds
    */
   public int[] getTransactionIds() {
      return transactionIds;
   }

   /**
    * @param branchId
    * @param transactionId
    * @param author
    */
   public NetworkTransactionDeletedEvent(NetworkSender networkSender, int[] transactionIds) {
      super(networkSender);
      this.transactionIds = transactionIds;
   }

}
