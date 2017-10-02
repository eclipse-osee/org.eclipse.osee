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
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.Assert;
import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.CoreActivityTypes;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.event.res.RemoteEvent;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.event.filter.BranchUuidEventFilter;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.event.listener.EventQosType;
import org.eclipse.osee.framework.skynet.core.event.listener.IEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.AccessTopicEvent;
import org.eclipse.osee.framework.skynet.core.event.model.AccessTopicEventPayload;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent.ArtifactEventType;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEvent;
import org.eclipse.osee.framework.skynet.core.event.model.EventBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event.model.EventModType;
import org.eclipse.osee.framework.skynet.core.event.model.RemoteEventServiceEventType;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.skynet.core.event.model.TopicEvent;
import org.eclipse.osee.framework.skynet.core.event.model.TransactionEvent;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.internal.ServiceUtil;
import org.eclipse.osee.framework.skynet.core.internal.event.EventListenerRegistry;

/**
 * Front end to OSEE events. Provides ability to add and remove different event listeners as well as the ability to kick
 * framework events.
 *
 * @author Donald G. Dunne
 */
public final class OseeEventManager {

   private OseeEventManager() {
      // Utility Class
   }

   private static OseeEventService getEventService() {
      return ServiceUtil.getEventService();
   }

   private static EventListenerRegistry getEventListeners() {
      return Activator.getEventListeners();
   }

   /**
    * Add a priority listener. This should only be done for caches where they need to be updated before all other
    * listeners are called.
    */
   public static void addPriorityListener(IEventListener listener) {
      getEventListeners().addListener(EventQosType.PRIORITY, listener);
   }

   public static void addListener(IEventListener listener) {
      getEventListeners().addListener(EventQosType.NORMAL, listener);
   }

   public static void removeAllListeners() {
      getEventListeners().clearAll();
   }

   public static void removeListener(IEventListener listener) {
      getEventListeners().removeListener(listener);
   }

   public static EventSystemPreferences getPreferences() {
      return Activator.getEventPreferences();
   }

   public static boolean isEventManagerConnected() {
      boolean result = false;
      try {
         OseeEventService eventService = getEventService();
         result = eventService.isConnected();
      } catch (Exception ex) {
         // Do Nothing;
      }
      return result;
   }

   public static String getConnectionDetails() {
      EventSystemPreferences preferences = getPreferences();
      StringBuilder sb = new StringBuilder();
      sb.append("oseeEventBrokerUri [" + preferences.getOseeEventBrokerUri() + "]");
      sb.append("eventDebug [" + preferences.getEventDebug() + "]");
      return sb.toString();
   }

   public static int getNumberOfListeners() {
      return getEventListeners().size();
   }

   ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
   /////////////////////////////////// NEW EVENT MODEL - TOPICS with JSON ////////////////////////////////////////////
   ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

   // Kick LOCAL and REMOTE topic event
   public static void kickTopicEvent(Object source, TopicEvent topicEvent) {
      Assert.isNotNull(source);
      Assert.isNotNull(topicEvent);
      Assert.isNotNull(topicEvent.getEventType(), "TopicEvent.eventType can not be null");
      Assert.isTrue(Strings.isValid(topicEvent.getTopic()), "TopicEvent.topic can not be null.");
      getEventService().send(source, topicEvent);
   }

   // Kick LOCAL and REMOTE topic event with payload
   public static void kickAccessTopicEvent(Object source, AccessTopicEventPayload payload, AccessTopicEvent accesstopicEvent) {
      try {
         TopicEvent topicEvent = EventUtil.createTopic(accesstopicEvent, payload);
         kickTopicEvent(source, topicEvent);
      } catch (Exception ex) {
         OseeLog.logf(OseeEventManager.class, Level.SEVERE, ex, "Error kicking event [%s][%s]", accesstopicEvent,
            payload);
      }
      try {
         if (accesstopicEvent != AccessTopicEvent.USER_AUTHENTICATED) {
            String message = String.format("USER_AUTHENTICATED [%s] Payload [%s]", UserManager.getUser().getUserId(),
               EventUtil.getEventJacksonMapper().writeValueAsString(payload));
            ServiceUtil.getOseeClient().getActivityLogEndpoint().createEntry(CoreActivityTypes.ACCESS_CONTROL_MODIFIED,
               0L, ActivityLog.COMPLETE_STATUS, message);
         }
      } catch (Exception ex) {
         OseeLog.logf(OseeEventManager.class, Level.SEVERE, ex, "Error logging activity event [%s][%s]",
            accesstopicEvent, payload);
      }
   }

   ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
   /////////////////////////////////// LEGACY EVENT MODEL - serialized jaxb object ///////////////////////////////////
   /////////////////////////////////////////////T//////////////////////////////////////////////////////////////////////

   // Kick LOCAL remote-event event
   public static void kickLocalRemEvent(Object source, RemoteEventServiceEventType remoteEventServiceEventType) {
      getEventService().send(source, remoteEventServiceEventType);
   }

   //Kick LOCAL and REMOTE branch events
   public static void kickBranchEvent(Object source, BranchEvent branchEvent) {
      getEventService().send(source, branchEvent);
   }

   // Kick LOCAL and REMOTE transaction deleted event
   public static void kickTransactionEvent(Object source, final TransactionEvent transactionEvent) {
      getEventService().send(source, transactionEvent);
   }

   // Kick LOCAL and REMOTE transaction event
   public static void kickPersistEvent(Object source, ArtifactEvent artifactEvent) {
      getEventService().send(source, artifactEvent);
   }

   // Kick LOCAL transaction event
   public static void kickLocalArtifactReloadEvent(Object source, Collection<? extends ArtifactToken> artifacts) {
      if (isDisableEvents()) {
         return;
      }
      ArtifactEvent artifactEvent =
         new ArtifactEvent(artifacts.iterator().next().getBranch(), ArtifactEventType.RELOAD_ARTIFACTS);
      for (ArtifactToken guidArt : artifacts) {
         artifactEvent.addArtifact(new EventBasicGuidArtifact(EventModType.Reloaded, guidArt));
      }
      getEventService().send(source, artifactEvent);
   }

   public static boolean isDisableEvents() {
      return getPreferences().isDisableEvents();
   }

   // Turn off all event processing including LOCAL and REMOTE
   public static void setDisableEvents(boolean disableEvents) {
      getPreferences().setDisableEvents(disableEvents);
   }

   // Return report showing all listeners registered
   public static String getListenerReport() {
      String toReturn = null;
      if (OseeEventManager.isEventManagerConnected()) {
         toReturn = getEventListeners().toString();
      } else {
         toReturn = "Event system is NOT active";
      }
      return toReturn;
   }

   public static List<IEventFilter> getEventFiltersForBranch(final BranchId branch) {
      try {
         List<IEventFilter> eventFilters = new ArrayList<>(2);
         eventFilters.add(new BranchUuidEventFilter(branch));
         return eventFilters;
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return null;
   }

   /////////////////////////////////// LEGACY TEST API ////////////////////////////////////////////
   // Only Used for Testing purposes
   public static void internalTestSendRemoteEvent(final RemoteEvent remoteEvent) {
      getEventService().receive(remoteEvent);
   }

   // Only Used for Testing purposes
   public static void internalTestProcessBranchEvent(Sender sender, BranchEvent branchEvent) {
      getEventService().receive(sender, branchEvent);
   }

   // Only Used for Testing purposes
   public static void internalTestProcessEventArtifactsAndRelations(Sender sender, ArtifactEvent artifactEvent) {
      getEventService().receive(sender, artifactEvent);
   }

}
