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
package org.eclipse.osee.framework.skynet.core.event.systems;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArraySet;
import org.eclipse.osee.framework.skynet.core.event.EventSystemPreferences;
import org.eclipse.osee.framework.skynet.core.event.listener.IEventListener;

/**
 * @author Roberto E. Escobar
 */
public class EventManagerData {

   private final Collection<IEventListener> priorityListeners = new CopyOnWriteArraySet<IEventListener>();
   private final Collection<IEventListener> listeners = new CopyOnWriteArraySet<IEventListener>();
   private final EventSystemPreferences preferences = new EventSystemPreferences();

   private InternalEventManager messageEventManager;

   public void setMessageEventManager(InternalEventManager messageEventManager) {
      this.messageEventManager = messageEventManager;
   }

   public Collection<IEventListener> getPriorityListeners() {
      return priorityListeners;
   }

   public Collection<IEventListener> getListeners() {
      return listeners;
   }

   public EventSystemPreferences getPreferences() {
      return preferences;
   }

   public InternalEventManager getMessageEventManager() {
      return messageEventManager;
   }

}
