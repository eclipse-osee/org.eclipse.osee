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

import org.eclipse.osee.framework.messaging.event.res.RemoteTopicEvent1;
import org.eclipse.osee.framework.skynet.core.event.FrameworkEventUtil;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.skynet.core.event.model.TopicEvent;
import org.eclipse.osee.framework.skynet.core.internal.event.EventHandlerRemote;
import org.eclipse.osee.framework.skynet.core.internal.event.Transport;

/**
 * @author Donald G. Dunne
 */
public class TopicRemoteEventHandler implements EventHandlerRemote<RemoteTopicEvent1> {

   @Override
   public void handle(Transport transport, Sender sender, RemoteTopicEvent1 remoteEvent) {
      TopicEvent event = FrameworkEventUtil.getTopicEvent(remoteEvent);
      transport.send(sender, event);
   }

}
