/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.orcs.rest.internal.ws;

import org.eclipse.osee.framework.messaging.MessageID;

/**
 * MessageID for the server-to-server lightweight event topic on ActiveMQ.
 * <p>
 * This is a separate topic from the desktop client events to avoid
 * JAXB deserialization conflicts. Messages on this topic are plain JSON strings.
 */
public enum ServerToServerMessageId implements MessageID {

   INSTANCE;

   private static final String TOPIC_NAME = "topic:org.eclipse.osee.server.event.s2s";
   private static final String GUID = "S2S_ServerEvent_001";

   @Override
   public String getName() {
      return TOPIC_NAME;
   }

   @Override
   public Class<?> getSerializationClass() {
      return String.class;
   }

   @Override
   public boolean isReplyRequired() {
      return false;
   }

   @Override
   public String getId() {
      return GUID;
   }

   @Override
   public boolean isTopic() {
      return true;
   }
}
