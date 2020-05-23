/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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
