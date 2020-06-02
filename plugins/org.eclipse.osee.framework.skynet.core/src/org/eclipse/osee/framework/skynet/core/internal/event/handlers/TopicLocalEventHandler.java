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

package org.eclipse.osee.framework.skynet.core.internal.event.handlers;

import org.eclipse.osee.framework.core.event.EventType;
import org.eclipse.osee.framework.skynet.core.event.EventUtil;
import org.eclipse.osee.framework.skynet.core.event.FrameworkEventUtil;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.skynet.core.event.model.TopicEvent;
import org.eclipse.osee.framework.skynet.core.internal.event.EventHandlerLocal;
import org.eclipse.osee.framework.skynet.core.internal.event.TopicEventAdmin;
import org.eclipse.osee.framework.skynet.core.internal.event.Transport;

/**
 * @author Donald G. Dunne
 */
public class TopicLocalEventHandler implements EventHandlerLocal<TopicEventAdmin, TopicEvent> {

   @Override
   public void handle(TopicEventAdmin listener, Sender sender, TopicEvent event) {
      EventUtil.eventLog(String.format("IEM: handleTopicEvent [%s]", event));
      listener.handleTopicEvent(event, sender);
   }

   @Override
   public void send(Transport transport, Sender sender, TopicEvent event) {
      if (transport.isDispatchToLocalAllowed(sender)) {
         transport.sendLocal(sender, event);
      }
      if (sender.isLocal() && (event.getEventType() == EventType.LocalAndRemote || event.getEventType() == EventType.RemoteOnly)) {
         transport.sendRemote(FrameworkEventUtil.getRemoteTopicEvent(event));
      }
   }

}
