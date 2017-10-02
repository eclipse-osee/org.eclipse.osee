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
   public void handle(Transport transport, Sender sender, RemoteTopicEvent1 remoteEvent)  {
      TopicEvent event = FrameworkEventUtil.getTopicEvent(remoteEvent);
      transport.send(sender, event);
   }

}
