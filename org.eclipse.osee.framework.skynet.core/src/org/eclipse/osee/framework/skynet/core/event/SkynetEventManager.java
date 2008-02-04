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
package org.eclipse.osee.framework.skynet.core.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.osee.framework.jdk.core.type.DoubleKeyHashMap;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.event.Event;
import org.eclipse.osee.framework.ui.plugin.event.EventManager;
import org.eclipse.osee.framework.ui.plugin.event.IEventReceiver;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.swt.widgets.Display;

public class SkynetEventManager extends EventManager {

   // Guid, Event, IEventReceiver
   private final DoubleKeyHashMap<Class<?>, String, Set<IEventReceiver>> guidReceiverMap;
   private final Map<Class<?>, Set<IEventReceiver>> receiverMap;
   private final static SkynetEventManager reference = new SkynetEventManager();
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(SkynetEventManager.class);
   private boolean localCallbacksEnabled = true;
   private Set<IEventReceiver> subscribeAll;

   public static SkynetEventManager getInstance() {
      return reference;
   }

   private SkynetEventManager() {
      super();
      subscribeAll = new HashSet<IEventReceiver>();
      receiverMap = new HashMap<Class<?>, Set<IEventReceiver>>();
      guidReceiverMap = new DoubleKeyHashMap<Class<?>, String, Set<IEventReceiver>>();
   }

   public void kick(Event[] events) {
      for (Event event : events)
         kick(event);
   }

   /**
    * @param event
    */
   public void kick(final Event event) {
      // wrap in case listeners want to be removed while being in a kick
      for (final IEventReceiver receiver : new ArrayList<IEventReceiver>(getReceivers(event))) {
         if (receiver == null) continue;
         if (receiver.runOnEventInDisplayThread() && !Displays.isDisplayThread()) {

            Display.getDefault().asyncExec(new Runnable() {
               public void run() {
                  callReceiverOnEvent(receiver, event);
               }
            });
         } else {
            callReceiverOnEvent(receiver, event);
         }
      }
   }

   protected void callReceiverOnEvent(IEventReceiver receiver, Event event) {
      try {
         receiver.onEvent(event);
      } catch (Exception e) {
         logger.log(Level.SEVERE, "Exception occured during method onEvent() in class EventManager ", e);
      }
   }

   protected Collection<IEventReceiver> getReceivers(Event event) {
      Set<IEventReceiver> receivers = new HashSet<IEventReceiver>();
      for (Class<?> eventClass = event.getClass(); !eventClass.equals(Object.class); eventClass =
            eventClass.getSuperclass()) {
         // Get all receivers subscribed by Event type
         if (receiverMap.containsKey(eventClass) && receiverMap.get(eventClass) != null) receivers.addAll(receiverMap.get(eventClass));

         // Get all receivers subscribed by Event type and Guid
         if ((event.equals(GuidEvent.class)) && (guidReceiverMap.containsKey(eventClass, ((GuidEvent) event).getGuid()))) receivers.addAll(guidReceiverMap.get(
               eventClass, ((GuidEvent) event).getGuid()));

         receivers.addAll(subscribeAll);
      }
      return receivers;
   }

   /**
    * Register event by event type. Kicking of events follows inheritance rules such that registration by a subclass
    * event like LocalNewBranchEvent will only call onEvent() when that specific event occurs. Registering for a
    * superclass event like BranchEvent will result in onEvent() being called for any subclass of BranchEvent.
    * 
    * @param event
    * @param eventReceiver
    */
   public void register(Class<? extends Event> event, IEventReceiver eventReceiver) {
      Set<IEventReceiver> receivers = receiverMap.get(event);

      if (receivers == null) {
         receivers = new HashSet<IEventReceiver>();
         receiverMap.put(event, receivers);
      }
      receivers.add(eventReceiver);
   }

   public void registerAll(IEventReceiver eventReceiver) {
      subscribeAll.add(eventReceiver);
   }

   /**
    * Register for event by artifact
    * 
    * @param event
    * @param artifact
    * @param eventReceiver
    */
   public void register(Class<? extends Event> event, Artifact artifact, IEventReceiver eventReceiver) {
      register(event, artifact.getGuid(), eventReceiver);
   }

   /**
    * Register for event by guid
    * 
    * @param event
    * @param guid
    * @param eventReceiver
    */
   public void register(Class<? extends Event> event, String guid, IEventReceiver eventReceiver) {
      Set<IEventReceiver> receivers = guidReceiverMap.get(event, guid);
      if (receivers == null) {
         receivers = new HashSet<IEventReceiver>();
         if (!receivers.contains(eventReceiver)) guidReceiverMap.put(event, guid, receivers);
      }
      receivers.add(eventReceiver);
   }

   /**
    * @param event
    * @param eventReceiver
    */
   public void unRegister(Class<? extends Event> event, IEventReceiver eventReceiver) {
      Collection<IEventReceiver> receivers = receiverMap.get(event);

      if (receivers != null) {
         receivers.remove(eventReceiver);
      }
   }

   /**
    * Unregister for Event by artifact
    * 
    * @param event
    * @param artifact
    * @param eventReceiver
    */
   public void unRegister(Class<? extends Event> event, Artifact artifact, IEventReceiver eventReceiver) {
      unRegister(event, artifact.getGuid(), eventReceiver);
   }

   /**
    * Unregister for Event by guid
    * 
    * @param event
    * @param eventId
    * @param eventReceiver
    */
   public void unRegister(Class<? extends Event> event, String eventId, IEventReceiver eventReceiver) {
      Collection<IEventReceiver> receivers = guidReceiverMap.get(event, eventId);

      if (receivers != null) {
         receivers.remove(eventReceiver);
      }
   }

   /**
    * Unregister for all eventType and eventType,guid registrations for a given IEventReceiver
    * 
    * @param eventReceiver
    */
   public void unRegisterAll(IEventReceiver eventReceiver) {
      // Remove all subscription to Event type
      for (Collection<IEventReceiver> receivers : receiverMap.values())
         receivers.remove(eventReceiver);

      // Remove all subscriptions to Event type, Guid
      Map<Class<?>, String> guidEventMap = guidReceiverMap.keySet();
      for (Class<?> clazz : guidEventMap.keySet()) {
         Set<IEventReceiver> receivers = new HashSet<IEventReceiver>();
         receivers.addAll(guidReceiverMap.get(clazz, guidEventMap.get(clazz)));
         for (IEventReceiver receiver : receivers)
            if (eventReceiver.equals(receiver)) guidReceiverMap.get(clazz, guidEventMap.get(clazz)).remove(
                  eventReceiver);
      }
      if (subscribeAll.contains(eventReceiver)) subscribeAll.remove(eventReceiver);
   }

   public boolean isLocalCallbacksEnabled() {
      return localCallbacksEnabled;
   }

   public void setLocalCallbacksEnabled(boolean localCallbacksEnabled) {
      // logger.log(Level.INFO, "LocalCallbacksEnabled = " + localCallbacksEnabled);
      this.localCallbacksEnabled = localCallbacksEnabled;
   }

}
