/*******************************************************************************
 * Copyright (c) 2018 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.event.IAtsEventService;
import org.eclipse.osee.ats.api.event.IAtsWorkItemTopicEventListener;
import org.eclipse.osee.ats.api.util.AtsTopicEvent;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLog;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventHandler;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractAtsEventServiceImpl implements IAtsEventService, EventHandler {

   private EventAdmin eventAdmin;
   private final HashCollection<String, IAtsWorkItemTopicEventListener> workItemEventListeners = new HashCollection<>();

   @Override
   public EventAdmin getEventAdmin() {
      return eventAdmin;
   }

   public void setEventAdmin(EventAdmin eventAdmin) {
      this.eventAdmin = eventAdmin;
   }

   @Override
   public void postEvent(Event event) {
      eventAdmin.postEvent(event);
   }

   @Override
   public void sendEvent(Event event) {
      eventAdmin.sendEvent(event);
   }

   @Override
   public void postAtsWorkItemTopicEvent(AtsTopicEvent event, Collection<IAtsWorkItem> workItems) {
      try {
         // Send event locally using OSGI events
         HashMap<String, Object> properties = new HashMap<>();
         String idsString = AtsObjects.toIdsString(";", workItems);
         properties.put(AtsTopicEvent.WORK_ITEM_IDS_KEY, idsString);

         Event osgiEvent = new Event(event.getTopic(), properties);
         eventAdmin.postEvent(osgiEvent);
      } catch (OseeCoreException ex) {
         OseeLog.log(getClass(), Level.SEVERE, ex);
      }
   }

   @Override
   public void registerAtsWorkItemTopicEvent(IAtsWorkItemTopicEventListener listener, AtsTopicEvent... events) {
      for (AtsTopicEvent event : events) {
         List<IAtsWorkItemTopicEventListener> listeners = workItemEventListeners.getValues(event.getTopic());
         if (listeners == null) {
            listeners = new LinkedList<IAtsWorkItemTopicEventListener>();
            workItemEventListeners.put(event.getTopic(), listeners);
         }
         if (!listeners.contains(listener)) {
            listeners.add(listener);
         }
      }
   }

   @Override
   public void deRegisterAtsWorkItemTopicEvent(IAtsWorkItemTopicEventListener listener) {
      for (Entry<String, List<IAtsWorkItemTopicEventListener>> entry : workItemEventListeners.entrySet()) {
         List<IAtsWorkItemTopicEventListener> listeners = entry.getValue();
         if (!listeners.contains(listener)) {
            listeners.remove(listener);
         }
      }
   }

   @Override
   public void handleEvent(Event event) {
      try {
         String topic = event.getTopic();
         AtsTopicEvent topicEvent = AtsTopicEvent.get(topic);
         if (topicEvent != null) {
            List<IAtsWorkItemTopicEventListener> listeners = workItemEventListeners.getValues(topic);
            if (!listeners.isEmpty()) {
               List<ArtifactId> workItemArtIds = getWorkItemArtIds(event);
               for (IAtsWorkItemTopicEventListener listener : listeners) {
                  try {
                     if (listener.isDisposed()) {
                        continue;
                     }
                     listener.handleEvent(topicEvent, workItemArtIds);
                  } catch (Exception ex) {
                     OseeLog.logf(getClass().getClass(), Level.SEVERE, ex,
                        "Error processing work item transition event handler for - %s", listener);
                  }
               }
            }
         }
      } catch (Exception ex) {
         // do nothing
      }
   }

   private List<ArtifactId> getWorkItemArtIds(Event event) {
      String ids = (String) event.getProperty(AtsTopicEvent.WORK_ITEM_IDS_KEY);
      List<ArtifactId> workItemArtIds = new ArrayList<>();
      for (Long workItemId : Collections.fromString(ids, ";", Long::valueOf)) {
         ArtifactId workItemArtId = ArtifactId.valueOf(workItemId);
         workItemArtIds.add(workItemArtId);
      }
      return workItemArtIds;
   }

}
