/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.core;

import org.eclipse.osee.framework.messaging.MessageID;

/**
 * @author Andrew M. Finkbeiner
 */
public enum OteBaseMessages implements MessageID {
   RequestOteHost(true, "ABjyjamBQRvvgsdgwers", "topic:lba.ote.get.host", null, true);

   private String name;
   private Class<?> clazz;
   boolean isReplyRequired;
   private String guid;
   private String destination;

   OteBaseMessages(boolean isTopic, String guid, String name, Class<?> clazz, boolean isReplyRequired) {
      this.guid = guid;
      this.name = name;
      this.clazz = clazz;
      this.isReplyRequired = isReplyRequired;
      if (isTopic) {
         destination = "topic:" + guid;
      } else {
         destination = guid;
      }
   }

   @Override
   public String getName() {
      return name;
   }

   @Override
   public Class<?> getSerializationClass() {
      return clazz;
   }

   @Override
   public boolean isReplyRequired() {
      return isReplyRequired;
   }

   @Override
   public String getId() {
      return guid;
   }

   @Override
   public boolean isTopic() {
      return true;
   }
}
