/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.event;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.data.TransactionToken;

/**
 * @author Donald G. Dunne
 */
public class FrameworkTopicEvent extends AbstractTopicEvent {

   public static Map<String, FrameworkTopicEvent> idToEvent = new HashMap<String, FrameworkTopicEvent>();

   public static final FrameworkTopicEvent LINK_MODIFIED =
      new FrameworkTopicEvent(EventType.LocalAndRemote, "osee/link/modified");

   public static final String TRANSACTION_ID = "transactionId";
   public static final String NAVIGATOR_ID = "navigatorItemId";

   private FrameworkTopicEvent(EventType eventType, String topic) {
      super(eventType, TransactionToken.SENTINEL, topic);
      idToEvent.put(topic, this);
   }

   public static FrameworkTopicEvent get(String topic) {
      return idToEvent.get(topic);
   }

}
