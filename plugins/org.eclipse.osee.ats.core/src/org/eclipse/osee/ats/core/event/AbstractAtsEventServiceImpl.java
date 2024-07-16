/*********************************************************************
 * Copyright (c) 2018 Boeing
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

package org.eclipse.osee.ats.core.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.event.IAtsEventService;
import org.eclipse.osee.ats.api.event.IAtsWorkItemTopicEventListener;
import org.eclipse.osee.ats.api.util.AtsTopicEvent;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.event.FrameworkTopicEvent;
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
   public void postAtsWorkItemTopicEvent(AtsTopicEvent event, Collection<IAtsWorkItem> workItems, TransactionId transaction) {
      try {
         // Send event locally using OSGI events
         HashMap<String, Object> properties = new HashMap<>();
         String idsString = AtsObjects.toIdsString(";", workItems);
         properties.put(AtsTopicEvent.WORK_ITEM_IDS_KEY, idsString);
         if (transaction != null && transaction.isValid()) {
            properties.put(FrameworkTopicEvent.TRANSACTION_ID, transaction.getIdString());
         }

         Event osgiEvent = new Event(event.getTopic(), properties);
         eventAdmin.postEvent(osgiEvent);
      } catch (OseeCoreException ex) {
         OseeLog.log(getClass(), Level.SEVERE, ex);
      }
   }

   @Override
   public void registerAtsWorkItemTopicEvent(IAtsWorkItemTopicEventListener listener, AtsTopicEvent... events) {
      for (AtsTopicEvent event : events) {
         workItemEventListeners.put(event.getTopic(), listener);
      }
   }

   @Override
   public void deRegisterAtsWorkItemTopicEvent(IAtsWorkItemTopicEventListener listener) {
      Set<String> toRemove = new HashSet<>();
      Set<Entry<String, List<IAtsWorkItemTopicEventListener>>> entrySet = workItemEventListeners.entrySet();
      for (Entry<String, List<IAtsWorkItemTopicEventListener>> entry : entrySet) {
         List<IAtsWorkItemTopicEventListener> listeners = entry.getValue();
         if (listeners.contains(listener)) {
            toRemove.add(entry.getKey());
         }
      }
      synchronized (workItemEventListeners) {
         for (String key : toRemove) {
            workItemEventListeners.removeValue(key, listener);
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
            if (listeners != null && !listeners.isEmpty()) {
               List<ArtifactId> workItemArtIds = getWorkItemArtIds(event);
               reloadWorkItemsAsNecessry(workItemArtIds, event);
               for (IAtsWorkItemTopicEventListener listener : listeners) {
                  try {
                     if (listener.isDisposed()) {
                        continue;
                     }
                     listener.handleEvent(topicEvent, workItemArtIds);
                  } catch (Exception ex) {
                     OseeLog.logf(getClass(), Level.SEVERE, ex,
                        "Error processing work item transition event handler for - %s", listener);
                  }
               }
            }
         }
      } catch (Exception ex) {
         // do nothing
      }
   }

   protected void reloadWorkItemsAsNecessry(Collection<ArtifactId> ids, Event event) {
      // do nothing
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
