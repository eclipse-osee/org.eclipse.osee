/*********************************************************************
 * Copyright (c) 2010 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.messaging.data;

import org.eclipse.osee.framework.messaging.MessageID;

/**
 * @author Andrew M. Finkbeiner
 */
public enum TestMessages implements MessageID {
   TestTopic(true, "ABVlXX3B9UaWogL++MgA", "topic:someTopic", null),
   JMS_TOPIC(true, "ABWApt8OtWlAnz5CJXQA", "topic:test.topic.Mynewthing.removeme", TestMessage.class),
   VM_TOPIC(true, "ABWHSTHuTlQb5xWueMAA", "topic:inThisJVM", TestMessage.class),
   test(true, "ABWoNNdp0RnrO5T5bWwA", "test", null),
   test2(true, "ABWpvHZTpBTR+PhVrwgA", "test2", null),
   replyTopic(true, "ABWswvHPoR6RpnW9oGAA", "topic:someTopicThatNeedsAReply", null, true);

   private String name;
   private Class<?> clazz;
   boolean isReplyRequired;
   private String guid;

   TestMessages(boolean isTopic, String guid, String name, Class<?> clazz, boolean isReplyRequired) {
      this.guid = guid;
      this.name = name;
      this.clazz = clazz;
      this.isReplyRequired = isReplyRequired;
   }

   TestMessages(boolean isTopic, String guid, String name, Class<?> clazz) {
      this.name = name;
      this.clazz = clazz;
      this.isReplyRequired = false;
      this.guid = guid;
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
