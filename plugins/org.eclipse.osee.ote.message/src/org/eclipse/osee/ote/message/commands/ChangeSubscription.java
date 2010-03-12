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
package org.eclipse.osee.ote.message.commands;

import java.io.Serializable;
import java.net.InetSocketAddress;

import org.eclipse.osee.ote.message.tool.MessageMode;



public final class ChangeSubscription implements Serializable {
   private static final long serialVersionUID = 2863442398798431500L;
   private final String msgName;
   private final MessageMode mode;
   private final int newMemTypeOrdinal;
   private final int oldMemTypeOrdinal;
   private final InetSocketAddress oldAddress;
   private final InetSocketAddress newAddress;
   
   public ChangeSubscription(
         final String msgName, 
         final MessageMode mode,
         final int oldMemTypeOrdinal, 
         final int newMemTypeOrdinal, 
         final InetSocketAddress oldAddress,
         final InetSocketAddress newAddress) {
      this.msgName = msgName;
      this.mode = mode;
      this.newMemTypeOrdinal = newMemTypeOrdinal;
      this.oldMemTypeOrdinal = oldMemTypeOrdinal;
      this.oldAddress = oldAddress;
      this.newAddress = newAddress;
   }

   public String getMsgName() {
      return msgName;
   }

   /**
    * @return Returns the mode.
    */
   public MessageMode getMode() {
      return mode;
   }

   public int getNewMemTypeOrdinal() {
      return newMemTypeOrdinal;
   }
   
   public int getOldMemTypeOrdinal() {
      return oldMemTypeOrdinal;
   }
   
   public InetSocketAddress getOldAddress() {
      return oldAddress;
   }

   public InetSocketAddress getNewAddress() {
      return newAddress;
   }
}
