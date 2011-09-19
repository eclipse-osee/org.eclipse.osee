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
package org.eclipse.osee.event.osgi.internal;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

/**
 * @author Roberto E. Escobar
 */
public class EventHandlerProxy implements EventHandler {

   private final org.eclipse.osee.event.EventHandler handler;

   public EventHandlerProxy(org.eclipse.osee.event.EventHandler handler) {
      this.handler = handler;
   }

   @Override
   public void handleEvent(Event event) {
      handler.onEvent(EventOsgiUtil.toOseeEvent(event));
   }
}
