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
import java.util.UUID;

import org.eclipse.osee.ote.message.enums.DataType;
import org.eclipse.osee.ote.message.tool.MessageMode;

/**
 * @author Ryan D. Brooks
 * @author Andrew M. Finkbeiner
 */
public class SubscribeToMessage implements Serializable {

   private static final long serialVersionUID = -8639822314152666969L;
   private final String message;
   private final DataType type;
   private final MessageMode mode;
   private final InetSocketAddress address;
   private final UUID key;

   public SubscribeToMessage(String message, DataType type, MessageMode mode, InetSocketAddress address, UUID key) {
      super();
      this.message = message;
      this.type = type;
      this.mode = mode;
      this.address = address;
      this.key = key;
   }

   /**
    * @param message The message to set.
    */
   public String getMessage() {
      return message;
   }

   public DataType getType() {
      return type;
   }

   public MessageMode getMode() {
      return mode;
   }
   
   public InetSocketAddress getAddress(){
      return address;
   }
   
   public UUID getKey(){
      return key;
   }
}
