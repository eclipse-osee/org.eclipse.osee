/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.display.mvp;

/**
 * @author Roberto E. Escobar
 */
public enum MessageTypeEnum implements MessageType {
   STANDARD,
   INFORMATION,
   WARNING,
   ERROR,
   CUSTOM;

   public final CustomMessageType messageType;

   private MessageTypeEnum() {
      messageType = new CustomMessageType(name(), ordinal());
   }

   @Override
   public String getName() {
      return messageType.getName();
   }

   @Override
   public int getLevel() {
      return messageType.getLevel();
   }

   public static MessageTypeEnum fromMessageType(MessageType messageType) {
      MessageTypeEnum messageTypeEnum = MessageTypeEnum.CUSTOM;
      for (MessageTypeEnum enumType : values()) {
         if (enumType.equals(messageType)) {
            messageTypeEnum = enumType;
            break;
         }
      }
      return messageTypeEnum;
   }
}
