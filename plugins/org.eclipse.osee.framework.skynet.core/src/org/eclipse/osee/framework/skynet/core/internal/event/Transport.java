/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.internal.event;

import org.eclipse.osee.framework.messaging.event.res.RemoteEvent;
import org.eclipse.osee.framework.skynet.core.event.listener.IEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.FrameworkEvent;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;

/**
 * @author Roberto E. Escobar
 */
public interface Transport extends ConnectionStatus {

   boolean isLoopbackEnabled();

   boolean isDispatchToLocalAllowed(Sender sender);

   <E extends FrameworkEvent> void send(final Object object, final E event);

   <E extends FrameworkEvent> void send(final Sender sender, final E event);

   <E extends FrameworkEvent, L extends IEventListener, H extends EventHandlerLocal<L, E>> void sendLocal(Sender sender, E event);

   void sendRemote(RemoteEvent remoteEvent);

}
