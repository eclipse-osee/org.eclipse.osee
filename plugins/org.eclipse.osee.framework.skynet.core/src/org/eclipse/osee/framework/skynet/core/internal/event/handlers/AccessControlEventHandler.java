/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.internal.event.handlers;

import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.event.FrameworkEventUtil;
import org.eclipse.osee.framework.skynet.core.event.listener.IAccessControlEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.AccessControlEvent;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.skynet.core.internal.event.EventHandlerLocal;
import org.eclipse.osee.framework.skynet.core.internal.event.Transport;

/**
 * @author Roberto E. Escobar
 */
public class AccessControlEventHandler implements EventHandlerLocal<IAccessControlEventListener, AccessControlEvent> {

   @Override
   public void handle(IAccessControlEventListener listener, Sender sender, AccessControlEvent event) {
      listener.handleAccessControlArtifactsEvent(sender, event);
   }

   @Override
   public void send(Transport transport, Sender sender, AccessControlEvent event) throws OseeCoreException {
      if (transport.isDispatchToLocalAllowed(sender)) {
         transport.sendLocal(sender, event);
      }
      if (sender.isLocal() && event.getEventType().isRemoteEventType()) {
         transport.sendRemote(FrameworkEventUtil.getRemoteAccessControlEvent(event));
      }
   }
}
