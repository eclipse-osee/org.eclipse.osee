/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.framework.skynet.core.internal.event;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.event.TopicEvent;
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.eclipse.osee.framework.skynet.core.event.listener.IEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

/**
 * @author Donald G. Dunne
 */
public class TopicEventAdmin implements IEventListener {

   public static EventAdmin eventAdmin;

   public void handleTopicEvent(TopicEvent event, Sender sender) {
      if (eventAdmin == null) {
         eventAdmin = OsgiUtil.getService(TopicEventAdmin.class, EventAdmin.class);
      }
      if (eventAdmin != null) {
         Map<String, String> properties = new HashMap<>();
         properties.putAll(event.getProperties());
         Event sendEvent = new Event(event.getTopic(), properties);
         eventAdmin.postEvent(sendEvent);
      }
   }

}
