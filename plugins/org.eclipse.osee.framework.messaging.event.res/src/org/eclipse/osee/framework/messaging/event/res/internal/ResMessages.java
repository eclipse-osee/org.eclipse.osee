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

package org.eclipse.osee.framework.messaging.event.res.internal;

import org.eclipse.osee.framework.messaging.MessageID;
import org.eclipse.osee.framework.messaging.event.res.RemoteArtifactTopicEvent;
import org.eclipse.osee.framework.messaging.event.res.RemoteEvent;
import org.eclipse.osee.framework.messaging.event.res.RemoteTopicEvent1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteBranchEvent1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteBroadcastEvent1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemotePersistEvent1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteTransactionEvent1;

/**
 * @author Donald G. Dunne
 */
public enum ResMessages implements MessageID {
   RemoteBranchEvent1(true, "Aylfa1wlKXIbX2gOrVgA", "topic:org.eclipse.osee.coverage.msgs.RemoteBranchEvent1", RemoteBranchEvent1.class, false),
   RemoteBroadcastEvent1(true, "Aylfa1y3ZBSIGbVU3JgA", "topic:org.eclipse.osee.coverage.msgs.RemoteBroadcastEvent1", RemoteBroadcastEvent1.class, false),
   RemotePersistEvent1(true, "AISIbRj0KGBv62x2pMAA", "topic:org.eclipse.osee.coverage.msgs.RemotePersistEvent1", RemotePersistEvent1.class, false),
   RemoteTopicEvent1(true, "ARqNVjHQVAGmszjGOhwA", "topic:org.eclipse.osee.coverage.msgs.RemoteTopicEvent1", RemoteTopicEvent1.class, false),
   RemoteTransactionEvent1(true, "AAn_QHkqUhz3vJKwp8QA", "topic:org.eclipse.osee.coverage.msgs.RemoteTransactionEvent1", RemoteTransactionEvent1.class, false),
   RemoteTopicArtifactEvent(true, "ArqnvjHQVAGmszzgohwA", "topic:artifact.topic.event", RemoteArtifactTopicEvent.class, false);

   private String name;
   private Class<? extends RemoteEvent> clazz;
   boolean isReplyRequired;
   private String guid;
   private boolean isTopic;

   ResMessages(boolean isTopic, String guid, String name, Class<? extends RemoteEvent> clazz, boolean isReplyRequired) {
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
   public Class<? extends RemoteEvent> getSerializationClass() {
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
