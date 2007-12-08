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
package org.eclipse.osee.framework.ui.plugin.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.swt.widgets.Display;

/**
 * @author Jeff C. Phillips
 */
public abstract class EventManager {
   @SuppressWarnings("unchecked")
   private final Map<Class, Set<IEventReceiver>> receiverMap;
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(EventManager.class);
   private boolean localCallbacksEnabled = true;
   private Set<IEventReceiver> subscribeAll;

   @SuppressWarnings("unchecked")
   protected EventManager() {
      super();
      subscribeAll = new HashSet<IEventReceiver>();
      receiverMap = new HashMap<Class, Set<IEventReceiver>>();
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

         if (receiver.runOnEventInDisplayThread() && !isDisplayThread()) {

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

   protected boolean isDisplayThread() {
      if (Display.getCurrent() == null) return false;

      return Display.getCurrent().getThread() == Thread.currentThread();
   }

   protected void callReceiverOnEvent(IEventReceiver receiver, Event event) {
      try {
         receiver.onEvent(event);
      } catch (Exception e) {
         logger.log(Level.SEVERE, "Exception occured during method onEvent() in class EventManager ", e);
      }
   }

   protected Collection<IEventReceiver> getReceivers(Event event) {
      ArrayList<IEventReceiver> receivers = new ArrayList<IEventReceiver>();
      // Get all receivers subscribed by Event type
      if (receiverMap.containsKey(event.getClass())) receivers.addAll(receiverMap.get(event.getClass()));

      receivers.addAll(subscribeAll);
      return receivers;
   }

   /**
    * Register event by event type. NOTE This should not be used. All registrations should be for specific artifact,
    * guids versus the entire event type group. This will reduce the amount of network traffic that must pass all the
    * changes between platforms. Use register(event, artifact,eventReceiver or register(event,guid,eventReceiver)
    * instead
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

   //	   /**
   //	    * Register for event by guid
   //	    * 
   //	    * @param event
   //	    * @param guid
   //	    * @param eventReceiver
   //	    */
   //	   public void register(Class<? extends Event> event, String guid, IEventReceiver eventReceiver) {
   //	      Set<IEventReceiver> receivers = guidReceiverMap.get(guid, event);
   //	      if (receivers == null) {
   //	         receivers = new HashSet<IEventReceiver>();
   //	         if (!receivers.contains(eventReceiver))
   //	            guidReceiverMap.put(guid, event, receivers);
   //	      }
   //	      receivers.add(eventReceiver);
   //	   }

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

   //	   /**
   //	    * Unregister for Event by guid
   //	    * 
   //	    * @param event
   //	    * @param guid
   //	    * @param eventReceiver
   //	    */
   //	   public void unRegister(Class<? extends Event> event, String guid, IEventReceiver eventReceiver) {
   //	      Collection<IEventReceiver> receivers = guidReceiverMap.get(guid, event);
   //
   //	      if (receivers != null) {
   //	         receivers.remove(eventReceiver);
   //	      }
   //	   }

   /**
    * Unregister for all eventType and eventType,guid registrations for a given IEventReceiver
    * 
    * @param eventReceiver
    */
   @SuppressWarnings("unchecked")
   public void unRegisterAll(IEventReceiver eventReceiver) {
      // Remove all subscription to Event type
      for (Class cclass : receiverMap.keySet())
         if (eventReceiver.getClass() == cclass) receiverMap.get(cclass).remove(eventReceiver);

      //	      // Remove all subscriptions to Event type, Guid
      //	      Map<String, Class> guidEventMap = guidReceiverMap.keySet();
      //	      for (String guid : guidEventMap.keySet()) {
      //	         Set <IEventReceiver>receivers = new HashSet<IEventReceiver>();
      //	         receivers.addAll(guidReceiverMap.get(guid, guidEventMap.get(guid)));
      //	         for (IEventReceiver receiver : receivers)
      //	            if (eventReceiver.equals(receiver))
      //	               guidReceiverMap.get(guid, guidEventMap.get(guid)).remove(eventReceiver);
      //	      }
      if (subscribeAll.contains(eventReceiver)) subscribeAll.remove(eventReceiver);
   }

   public boolean isLocalCallbacksEnabled() {
      return localCallbacksEnabled;
   }

   public void setLocalCallbacksEnabled(boolean localCallbacksEnabled) {
      this.localCallbacksEnabled = localCallbacksEnabled;
   }

}
