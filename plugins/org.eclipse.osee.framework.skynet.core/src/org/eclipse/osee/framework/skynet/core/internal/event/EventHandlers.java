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

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.messaging.event.res.RemoteEvent;
import org.eclipse.osee.framework.skynet.core.event.listener.IEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.FrameworkEvent;

/**
 * @author Roberto E. Escobar
 */
public class EventHandlers {

   private final Map<Class<? extends FrameworkEvent>, EventHandlerLocal<? extends IEventListener, ? extends FrameworkEvent>> handlers =
      new HashMap<>();

   private final Map<Class<? extends RemoteEvent>, EventHandlerRemote<? extends RemoteEvent>> remoteHandlers =
      new HashMap<>();

   public void addLocalHandler(Class<? extends FrameworkEvent> clazz, EventHandlerLocal<? extends IEventListener, ? extends FrameworkEvent> handler) {
      handlers.put(clazz, handler);
   }

   public void removeLocalHandler(Class<? extends FrameworkEvent> clazz) {
      handlers.remove(clazz);
   }

   public void addRemoteHandler(Class<? extends RemoteEvent> clazz, EventHandlerRemote<? extends RemoteEvent> handler) {
      remoteHandlers.put(clazz, handler);
   }

   public void removeRemoteHandler(Class<? extends RemoteEvent> clazz) {
      remoteHandlers.remove(clazz);
   }

   @SuppressWarnings("unchecked")
   public <H extends EventHandlerRemote<? extends RemoteEvent>> H getRemoteHandler(RemoteEvent event) {
      return (H) remoteHandlers.get(event.getClass());
   }

   @SuppressWarnings("unchecked")
   public <H extends EventHandlerLocal<? extends IEventListener, ? extends FrameworkEvent>> H getLocalHandler(FrameworkEvent event) {
      return (H) handlers.get(event.getClass());
   }

   public int sizeLocal() {
      return handlers.size();
   }

   public int sizeRemote() {
      return remoteHandlers.size();
   }

}
