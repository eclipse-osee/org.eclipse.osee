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
import org.eclipse.osee.ote.message.enums.MemType;
import org.eclipse.osee.ote.message.interfaces.IMsgToolServiceClient;
import org.eclipse.osee.ote.message.tool.MessageMode;




/**
 * @author Ryan D. Brooks
 * @author Andrew M. Finkbeiner
 */
public class SubscribeToMessage implements Serializable {



   /**
    * 
    */
   private static final long serialVersionUID = -8639822314152666969L;
   private final String message;
   private final MemType type;
   private final MessageMode mode;
   private final IMsgToolServiceClient callback;
   /**
    * 
    */
   public SubscribeToMessage(String message, MemType type, MessageMode mode, IMsgToolServiceClient callback) {
      super();
      this.message = message;
      this.type = type;
      this.mode = mode;
      this.callback = callback;
   }
   
   public IMsgToolServiceClient getCallback() {
      return callback;
   }
   
   /**
    * @param message The message to set.
    */
   public String getMessage() {
      return message;
   }
   
   public MemType getType() {
      return type;
   }

   public MessageMode getMode() {
      return mode;
   }
}
