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
package org.eclipse.osee.framework.jini.event;

import java.rmi.RemoteException;
import net.jini.core.lookup.ServiceItem;
import org.eclipse.osee.framework.jini.discovery.IServiceLookupListener;
import org.eclipse.osee.framework.jini.event.old.IOseeRemoteSubscriber;

/**
 * Singleton class which provides an interface for all OSEE Remote Events to the event service.
 * 
 * @author David Diepenbrock
 */
public class RemoteEventHandler implements IServiceLookupListener, IOseeRemoteSubscriber {

   public void serviceAdded(ServiceItem serviceItem) {
   }

   public void serviceChanged(ServiceItem serviceItem) {
   }

   public void serviceRemoved(ServiceItem serviceItem) {
   }

   public boolean receiveEventType(String event) throws RemoteException {
      return false;
   }

   public boolean receiveEventGuid(String event) throws RemoteException {
      return false;
   }

   //   /*
   //    * Reference to the singleton instance
   //    */
   //   private static RemoteEventHandler handler;
   //   /*
   //    * GUID to identify this entity as a publisher
   //    */
   //   private static final String handlerGUID = GUID.generateGuidStr();
   //   /*
   //    * The number of previous sequence numbers to maintain, per publisher, in order to identify
   //    * duplications. If duplicate events are being received, this number may need to be tuned.
   //    */
   //   private static final int SEQ_NUM_COUNT = 25;
   //
   //   private HashCollectionPlusMap<EventType, IRemoteEventListener, String, FixedSizePriorityQueue<Long>> eventData;
   //   private Map<String, Long> sequenceNumberMap;
   //   private List<IRemoteEventService> eventServices;
   //   private IOseeRemoteSubscriber thisRemoteReference;
   //   private ThreadPoolExecutor notificationThreadPool;
   //   private OseeLeaseRenewer leaseRenewer;
   //   private Map<ServiceID, OseeLease> leaseMap;
   //
   //   private RemoteEventHandler(boolean inEclipse) {
   //      /*
   //       * Using a HashSet will enforce that there will not be duplicate entries Create it using a
   //       * synchronized map & collection since we'll have multiple threads operating on the data.
   //       */
   //      eventData = new HashCollectionPlusMap<EventType, IRemoteEventListener, String, FixedSizePriorityQueue<Long>>(true,
   //            HashSet.class);
   //
   //      // Maintain a list of the remote eventServices
   //      eventServices = Collections.synchronizedList(new ArrayList<IRemoteEventService>());
   //
   //      // A list of sequence numbers used for sending events
   //      sequenceNumberMap = Collections.synchronizedMap(new HashMap<String, Long>());
   //
   //      // Creates a pool with 1 thread always ready, and will start as many new threads as needed.
   //      // These new threads will live for 5 minutes after they have become idle before dying.
   //      notificationThreadPool = new ThreadPoolExecutor(5, Integer.MAX_VALUE, 5 * 60, TimeUnit.SECONDS,
   //            new SynchronousQueue<Runnable>());
   //      notificationThreadPool.prestartAllCoreThreads();
   //
   //      leaseRenewer = new OseeLeaseRenewer();
   //      leaseMap = Collections.synchronizedMap(new HashMap<ServiceID, OseeLease>());
   //
   //      // Startup the class server if it isn't already running - ensures that the codebase is set
   //      try {
   //         JiniClassServer.getInstance();
   //      }
   //      catch (Exception ex) {
   //         ex.printStackTrace();
   //      }
   //
   //      // This should be the last line in the constructor
   //      if (inEclipse)
   //         ServiceDataStore.getEclipseInstance(EclipseJiniClassloader.getInstance()).addListener(this,
   //               IRemoteEventService.class);
   //      else
   //         ServiceDataStore.getNonEclipseInstance().addListener(this, IRemoteEventService.class);
   //   }
   //
   //   /**
   //    * Obtain the instance (singleton) of this class.
   //    * 
   //    * @param inEclipse Used to indicate if the request is originating from within Eclipse, or an
   //    *           outside program. Note that only one instance of this class is stored, subsequent
   //    *           calls return the existing instance, thus ignoring this value.
   //    * @return Return remote event handler reference.
   //    */
   //   public static RemoteEventHandler getInstance(boolean inEclipse) {
   //      if (handler == null)
   //         handler = new RemoteEventHandler(inEclipse);
   //      return handler;
   //   }
   //
   //   /**
   //    * Same as {@link RemoteEventHandler#getInstance(boolean)}, assumes true.
   //    */
   //   public static RemoteEventHandler getInstance() {
   //      return getInstance(true);
   //   }
   //
   //   private IOseeRemoteSubscriber getThisRemoteReference() throws ExportException {
   //      if (thisRemoteReference == null)
   //         thisRemoteReference = (IOseeRemoteSubscriber) OseeJini.getRemoteReference(this);
   //      return thisRemoteReference;
   //   }
   //
   //   /**
   //    * Attempts to send the eventInstance to all available event services.
   //    * 
   //    * @param eventInstance The event instance to send.
   //    * @return True iff at least one event service received the event
   //    */
   //   public boolean sendEvent(String eventInstance) {
   //
   //      boolean success = false;
   //      synchronized (eventServices) {
   //         for (IRemoteEventService eventService : eventServices) {
   //            try {
   //               eventService.publish(eventInstance);
   //               success = true;
   //            }
   //            catch (RemoteException ex) {
   //               ex.printStackTrace();
   //               // Most likely an error here is caused by a service which has shut down
   //               // but the ServiceDataStore has not notified us of its removal yet.
   //               // In those cases, there's nothing we can do, the boolean return is indication
   //               // enough of these errors.
   //            }
   //         }
   //      }
   //      return success;
   //   }
   //
   //   // private long getNextSequenceNumber(String eventType) {
   //   // long seqNum;
   //   // synchronized (sequenceNumberMap) {
   //   // Long previousSequenceNumber = sequenceNumberMap.get(eventType);
   //   //
   //   // if (previousSequenceNumber == null)
   //   // seqNum = 1;
   //   // else
   //   // seqNum = previousSequenceNumber.longValue() + 1;
   //   //
   //   // sequenceNumberMap.put(eventType, new Long(seqNum));
   //   // }
   //   // return seqNum;
   //   // }
   //
   //   /**
   //    * Receive events because we're subscribed to the eventType
   //    * 
   //    * @param event The event that occurred
   //    * @return false iff the eventType should no longer be sent to this
   //    * @throws RemoteException
   //    */
   //   public boolean receiveEventType(String event) throws RemoteException {
   //      // Serializable eventIdentifier = event.eventData.getEventType();
   //      EventType eventType = Event.getEventTypeFromEvent(event);
   //      Collection<IRemoteEventListener> typeListeners = eventData.getValues(eventType);
   //      // If there are no listeners, then the server shouldn't be sending us the events!
   //      // if (typeListeners == null) {
   //      // return false;
   //      // }
   //      // else {
   //      notificationThreadPool.execute(new ListenerNotifier(event, eventType, typeListeners));
   //      System.out.println("Received Event Type " + event);
   //      // }
   //      return true;
   //   }
   //
   //   /**
   //    * Receive events because we're subscribed to the eventGuid
   //    * 
   //    * @param event The event that occurred
   //    * @return false iff the eventGUID should no longer be sent to this
   //    * @throws RemoteException
   //    */
   //   public boolean receiveEventGuid(String event) throws RemoteException {
   //      // Serializable eventIdentifier = event.eventData.getEventGuid();
   //      // Collection<IOseeRemoteEventListener> guidListeners = eventData.getValues(eventIdentifier);
   //      // // If there are no listeners, then the server shouldn't be sending us the events!
   //      // if (guidListeners == null) {
   //      // return false;
   //      // }
   //      // else {
   //      // notificationThreadPool.execute(new ListenerNotifier(event, eventIdentifier,
   //      // guidListeners));
   //      // }
   //      return true;
   //   }
   //
   //   private class ListenerNotifier implements Runnable {
   //
   //      private Collection<IRemoteEventListener> listeners;
   //      private String event;
   //      private final EventType eventType;
   //
   //      public ListenerNotifier(String event, EventType eventType, Collection<IRemoteEventListener> listeners) {
   //         this.event = event;
   //         this.eventType = eventType;
   //         this.listeners = listeners;
   //      }
   //
   //      public void run() {
   //         /*
   //          * We maintain a collection of the last SEQ_NUM_COUNT sequence numbers for a given
   //          * publisher. If the received event's sequence number is in that collection, then we will
   //          * ignore this event. Otherwise, add this sequence number to the collection. Note that by
   //          * adding this number, the *smallest* (not the oldest) of the currently existing numbers
   //          * will be removed if the list is already full. The use of the FixedSizePriorityQueue
   //          * performs this removal.
   //          */
   //         // boolean isNewSeqNum = false;
   //         // Map<String, FixedSizePriorityQueue<Long>> seqNumMap =
   //         // eventData.getPlusObject(eventIdentifier);
   //         // if (seqNumMap != null) {
   //         // synchronized (seqNumMap) {
   //         // FixedSizePriorityQueue<Long> prevSeqNums = seqNumMap.get(event.publisherGUID);
   //         //
   //         // if (prevSeqNums == null) {
   //         // // If we had no data about the publisher for this event
   //         // seqNumMap.put(event, new FixedSizePriorityQueue<Long>(SEQ_NUM_COUNT, new Long(
   //         // event.sequenceNumber)));
   //         // isNewSeqNum = true;
   //         // }
   //         // else if (!prevSeqNums.contains(new Long(event.sequenceNumber))) {
   //         // prevSeqNums.add(new Long(event.sequenceNumber));
   //         // isNewSeqNum = true;
   //         // }
   //         //
   //         // }
   //         // }
   //         //         
   //         // if (isNewSeqNum) {
   //         synchronized (listeners) {
   //            for (IRemoteEventListener listener : listeners) {
   //               listener.notify(event);
   //            }
   //            // }
   //         }
   //
   //      }
   //   }
   //
   //   /**
   //    * Subscribes the provided listener to a particular type of event described by the eventType.
   //    * 
   //    * @return true iff subscription to at least one currently active event service was successful.
   //    *         Note that future event services may come on line, and we will automatically subscribe
   //    *         any previous subscriptions to these new services, so a return value of false does not
   //    *         necessarily mean that the listener will never be called. Unsubscribe should be called
   //    *         to cleanup the listener regardless of this return value.
   //    */
   //   // public boolean subscribe(IOseeRemoteEventListener listener, String eventType) {
   //   //      
   //   // return subscribe(listener, eventType.getCanonicalName());
   //   // }
   //   /**
   //    * Subscribes the provided listener to a particular event denoted by the eventGuid.
   //    * 
   //    * @return true iff subscription to at least one currently active event service was successful.
   //    *         Note that future event services may come on line, and we will automatically subscribe
   //    *         any previous subscriptions to these new services, so a return value of false does not
   //    *         necessarily mean that the listener will never be called. Unsubscribe should be called
   //    *         to cleanup the listener regardless of this return value.
   //    */
   //   // public boolean subscribe(IOseeRemoteEventListener listener, GUID eventGuid) {
   //   // /*
   //   // * Because the server is setup to match the event type and the event GUID of every incoming
   //   // * event to the subscriber's event key, we can use the same manner of notifying the server of
   //   // * our desire to subscribe.
   //   // */
   //   // return subscribe(listener, (Serializable) eventGuid);
   //   // }
   //   /**
   //    * Subscribes the provided listener to a particular event or type
   //    * 
   //    * @return true iff subscription to at least one currently active event service was successful.
   //    *         Note that future event services may come on line, and we will automatically subscribe
   //    *         any previous subscriptions to these new services, so a return value of false does not
   //    *         necessarily mean that the listener will never be called. Unsubscribe should be called
   //    *         to cleanup the listener regardless of this return value.
   //    */
   //   public boolean subscribe(IRemoteEventListener listener, EventType event) {
   //      // We only need to register the eventType with the eventService(s) if no other listeners for
   //      // that type already exist. Listeners will only be added to the hash if they have successfully
   //      // been registered with at least one eventService.
   //      boolean success = true;
   //
   //      Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
   //
   //      if (!eventData.containsKey(event)) {
   //         success = false;
   //         synchronized (eventServices) {
   //            for (IRemoteEventService eventService : eventServices) {
   //               try {
   //                  eventService.subscribe(event, getThisRemoteReference());
   //                  success = true;
   //                  // If we get here, then we succeeded in registering with at least one service
   //               }
   //               catch (RemoteException ex) {
   //                  // Most likely an error here is caused by a service which has shut down
   //                  // but the ServiceDataStore has not notified us of its removal yet.
   //                  // In those cases, there's nothing we can do, the boolean return is indication
   //                  // enough of these errors.
   //               }
   //            }
   //         }
   //      }
   //      eventData.put(event, listener);
   //
   //      return success;
   //   }
   //
   //   /**
   //    * Unsubscribes the provided listener to a particular type of event described by the eventType. // *
   //    * 
   //    * @return true iff unsubscription was successful. If the listener was not subscribed, or if for // *
   //    *         any reason unsubscription was not successful, false will be returned. //
   //    */
   //   // public boolean unsubscribe(IOseeRemoteEventListener listener, Class eventType) {
   //   // return unsubscribe(listener, (Serializable)eventType);
   //   // }
   //   //   
   //   // /**
   //   // * Unsubscribes the provided listener to a particular event denoted by the eventGuid.
   //   // *
   //   // * @return true iff unsubscription was successful. If the listener was not subscribed, or if
   //   // for
   //   // * any reason unsubscription was not successful, false will be returned.
   //   // */
   //   // public boolean unsubscribe(IOseeRemoteEventListener listener, GUID eventGuid) {
   //   // return unsubscribe(listener, (Serializable)eventGuid);
   //   // }
   //   /**
   //    * Unsubscribes the provided listener from the given event
   //    * 
   //    * @return true iff unsubscription was successful. If the listener was not subscribed, or if for
   //    *         any reason unsubscription was not successful, false will be returned.
   //    */
   //   public boolean unsubscribe(IRemoteEventListener listener, EventType event) {
   //      // We only want to unregister the event with the eventService(s) if no other listeners for
   //      // that type exist. Listeners will be removed from the hash regardless of the successfullness
   //      // of the unregistration attempt.
   //
   //      boolean success = eventData.removeValue(event, listener);
   //      if (success && !eventData.containsKey(event)) {
   //         success = unsubscribe(event);
   //      }
   //
   //      return success;
   //   }
   //
   //   /**
   //    * Attempt to unsubscribe the eventType from all current eventServices.
   //    * 
   //    * @return true iff unsubscribe was successfull for at least one eventService.
   //    */
   //   private boolean unsubscribe(EventType eventType) {
   //      boolean success = false;
   //      synchronized (eventServices) {
   //         for (IRemoteEventService eventService : eventServices) {
   //            try {
   //               eventService.unsubscribe(eventType, getThisRemoteReference());
   //               success = true;
   //            }
   //            catch (RemoteException ex) {
   //               // Most likely an error here is caused by a service which has shut down
   //               // but the ServiceDataStore has not notified us of its removal yet.
   //               // In those cases, there's nothing we can do, the boolean return is indication
   //               // enough of these errors.
   //            }
   //         }
   //      }
   //      return success;
   //   }
   //
   //   /**
   //    * This should be called before the JVM shuts down so that we can inform the event services that
   //    * we are going away.
   //    */
   //   public void onDispose() {
   //      ServiceDataStore.getNonEclipseInstance().removeListener(this);
   //      synchronized (leaseMap) {
   //         Set<Entry<ServiceID, OseeLease>> set = leaseMap.entrySet();
   //         Iterator<Entry<ServiceID, OseeLease>> iter = set.iterator();
   //         while (iter.hasNext()) {
   //            Entry<ServiceID, OseeLease> entry = iter.next();
   //
   //            leaseRenewer.cancelRenewal(entry.getValue()); // Cancel the renewal attempts
   //            try {
   //               entry.getValue().cancel(); // Cancel the lease
   //            }
   //            catch (UnknownLeaseException ex) {
   //               ex.printStackTrace();
   //            }
   //            catch (RemoteException ex) {
   //               ex.printStackTrace();
   //            }
   //
   //            iter.remove();
   //         }
   //      }
   //   }
   //
   //   /*
   //    * (non-Javadoc)
   //    * 
   //    * @see org.eclipse.osee.framework.jini.discovery.IServiceLookupListener#serviceAdded(net.jini.core.lookup.ServiceItem)
   //    */
   //   public void serviceAdded(ServiceItem serviceItem) {
   //      try {
   //         IRemoteEventService eventService = (IRemoteEventService) serviceItem.service;
   //         eventServices.add(eventService);
   //
   //         OseeLease lease = eventService.getLease(getThisRemoteReference(), Lease.FOREVER);
   //         leaseMap.put(serviceItem.serviceID, lease);
   //         leaseRenewer.startRenewal(lease);
   //
   //         synchronized (eventData) {
   //            // Set<Serializable> eventTypes = eventData.keySet();
   //            // for (Serializable eventType : eventTypes) {
   //            // eventService.subscribe(eventType, getThisRemoteReference());
   //            // }
   //         }
   //      }
   //      catch (ClassCastException ex) {
   //         ex.printStackTrace();
   //      }
   //      catch (ConnectException ex) {
   //      }
   //      catch (RemoteException ex) {
   //         ex.printStackTrace();
   //      }
   //   }
   //
   //   /*
   //    * (non-Javadoc)
   //    * 
   //    * @see org.eclipse.osee.framework.jini.discovery.IServiceLookupListener#serviceChanged(net.jini.core.lookup.ServiceItem)
   //    */
   //   public void serviceChanged(ServiceItem serviceItem) {
   //      // We don't need to do anything for these events
   //   }
   //
   //   /*
   //    * (non-Javadoc)
   //    * 
   //    * @see org.eclipse.osee.framework.jini.discovery.IServiceLookupListener#serviceRemoved(net.jini.core.lookup.ServiceItem)
   //    */
   //   public void serviceRemoved(ServiceItem serviceItem) {
   //      try {
   //         eventServices.remove((IRemoteEventService) serviceItem.service);
   //         OseeLease lease = leaseMap.remove(serviceItem.serviceID);
   //         if (lease != null)
   //            leaseRenewer.cancelRenewal(lease);
   //      }
   //      catch (ClassCastException ex) {
   //         Class[] is = serviceItem.service.getClass().getInterfaces();
   //         for (Class i : is) {
   //            System.out.println(i);
   //         }
   //         Class[] cs = serviceItem.service.getClass().getClasses();
   //         for (Class c : cs) {
   //            System.out.println(c);
   //         }
   //
   //         System.out.println(serviceItem.service.getClass());
   //         ex.printStackTrace();
   //      }
   //   }

}
