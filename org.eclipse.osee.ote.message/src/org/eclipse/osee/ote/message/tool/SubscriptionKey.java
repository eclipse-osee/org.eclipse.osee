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
package org.eclipse.osee.ote.message.tool;

import java.io.Serializable;
import org.eclipse.osee.ote.message.enums.MemType;

/**
 * @author Ken J. Aguilar
 */
public final class SubscriptionKey implements Serializable {

   /**
    * 
    */
   private static final long serialVersionUID = 4385205425559852952L;

   private final int id;
   private final MemType type;
   private final MessageMode mode;
   private final String messageClassName;

   public SubscriptionKey(int id, MemType type, MessageMode mode, String messageClassName) {
      this.id = id;
      this.type = type;
      this.mode = mode;
      this.messageClassName = messageClassName;
   }

   public int getId() {
      return id;
   }

   public MemType getType() {
      return type;
   }

   public MessageMode getMode() {
      return mode;
   }

   public String getMessageClassName() {
      return messageClassName;
   }
}
