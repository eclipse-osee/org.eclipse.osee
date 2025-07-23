/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.ats.api.event;

import java.util.Collection;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.util.AtsTopicEvent;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

/**
 * @author Donald G. Dunne
 */
public interface IAtsEventService {

   /**
    * Used to post and send OSGI events. Can use postEvent and sendEvent methods directly.
    */
   EventAdmin getEventAdmin();

   /**
    * Initiate asynchronous, ordered delivery of an event. This method returns to the caller before delivery of the
    * event is completed. Events are delivered in the order that they are received by this method.
    *
    * @param event The event to send to all listeners which subscribe to the topic of the event.
    * @throws SecurityException If the caller does not have {@code TopicPermission[topic,PUBLISH]} for the topic
    * specified in the event.
    */
   void postEvent(Event event);

   /**
    * Initiate synchronous delivery of an event. This method does not return to the caller until delivery of the event
    * is completed.
    *
    * @param event The event to send to all listeners which subscribe to the topic of the event.
    * @throws SecurityException If the caller does not have {@code TopicPermission[topic,PUBLISH]} for the topic
    * specified in the event.
    */
   void sendEvent(Event event);

   /**
    * Used to register for osgi events
    */
   BundleContext getBundleContext(String pluginId);

   /**
    * Post the simplest AtsTopicEvent with only work item ids as the payload
    */
   void postAtsWorkItemTopicEvent(AtsTopicEvent event, Collection<IAtsWorkItem> workItems,
      TransactionId transactionToken);

   void postAtsActionTopicEvent(AtsTopicEvent event, Collection<IAtsAction> actions, TransactionId transactionToken);

   void registerAtsWorkItemTopicEvent(IAtsWorkItemTopicEventListener listener, AtsTopicEvent... events);

   void deRegisterAtsWorkItemTopicEvent(IAtsWorkItemTopicEventListener listener);
}
