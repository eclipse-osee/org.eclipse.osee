/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.internal.event;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.skynet.core.event.listener.IEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.skynet.core.event.model.TopicEvent;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

/**
 * @author Donald G. Dunne
 */
public class TopicEventAdmin implements IEventListener {

   public static EventAdmin eventAdmin;

   public void setEventAdmin(EventAdmin eventAdmin) {
      TopicEventAdmin.eventAdmin = eventAdmin;
   }

   public void handleTopicEvent(TopicEvent event, Sender sender) {
      if (eventAdmin != null) {
         Map<String, String> properties = new HashMap<>();
         properties.putAll(event.getProperties());
         Event sendEvent = new Event(event.getTopic(), properties);
         eventAdmin.postEvent(sendEvent);
      }
   }

}
