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
package org.eclipse.osee.framework.messaging.event.res;

import org.eclipse.osee.framework.messaging.MessageID;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteAccessControlEvent1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteBranchEvent1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteBroadcastEvent1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemotePersistEvent1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteTransactionEvent1;

public enum ResMessages implements MessageID {
   RemoteBranchEvent1(true, "Aylfa1wlKXIbX2gOrVgA", "topic:org.eclipse.osee.coverage.msgs.RemoteBranchEvent1", RemoteBranchEvent1.class, false),
   RemoteBroadcastEvent1(true, "Aylfa1y3ZBSIGbVU3JgA", "topic:org.eclipse.osee.coverage.msgs.RemoteBroadcastEvent1", RemoteBroadcastEvent1.class, false),
   RemotePersistEvent1(true, "AISIbRj0KGBv62x2pMAA", "topic:org.eclipse.osee.coverage.msgs.RemotePersistEvent1", RemotePersistEvent1.class, false),
   RemoteTransactionEvent1(true, "AAn_QHkqUhz3vJKwp8QA", "topic:org.eclipse.osee.coverage.msgs.RemoteTransactionEvent1", RemoteTransactionEvent1.class, false),
   RemoteAccessControlEvent1(true, "AFRkIhdPkwExx96ioXgA", "topic:org.eclipse.osee.coverage.msgs.RemoteAccessControlEvent1", RemoteAccessControlEvent1.class, false);

   private String name;
   private Class<?> clazz;
   boolean isReplyRequired;
   private String guid;
   private boolean isTopic;

   ResMessages(boolean isTopic, String guid, String name, Class<?> clazz, boolean isReplyRequired) {
      this.guid = guid;
      this.name = name;
      this.clazz = clazz;
      this.isReplyRequired = isReplyRequired;
      this.isTopic = isTopic;
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
      return isTopic;
   }
}
