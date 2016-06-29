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

import java.util.logging.Level;
import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.event.AbstractTopicEvent;
import org.eclipse.osee.framework.core.event.EventType;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.event.filter.BranchUuidEventFilter;
import org.eclipse.osee.framework.skynet.core.event.model.TopicEvent;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.osgi.service.event.Event;

/**
 * @author Roberto E. Escobar
 */
public final class EventUtil {
   private static BranchUuidEventFilter commonBranchUuidEvenFilter = new BranchUuidEventFilter(CoreBranches.COMMON);
   private static ObjectMapper mapper;

   private EventUtil() {
      // Utility Class
   }

   public static BranchUuidEventFilter getCommonBranchFilter() {
      return EventUtil.commonBranchUuidEvenFilter;
   }

   public static String getObjectSafeName(Object object) {
      try {
         return object.toString();
      } catch (Exception ex) {
         return object.getClass().getSimpleName() + " - exception on toString: " + ex.getLocalizedMessage();
      }
   }

   public static void eventLog(String message, Throwable ex) {
      eventLog(ex, message);
   }

   public static void eventLog(Throwable ex, String message, Object... args) {
      try {
         if (isEventDebugConsole()) {
            StringBuilder builder = new StringBuilder();
            builder.append(formatMessage(message, args));
            if (ex != null) {
               builder.append(" <<ERROR>> ");
               builder.append(ex.toString());
            }
            System.err.println(builder.toString());
         }
         if (ex != null) {
            OseeLog.log(Activator.class, Level.SEVERE, formatMessage(message, args), ex);
         } else {
            OseeLog.log(Activator.class, Level.FINE, formatMessage(message, args));
         }
      } catch (Throwable th) {
         OseeLog.log(Activator.class, Level.SEVERE, th);
      }
   }

   public static void eventLog(String output, Object... args) {
      eventLog(null, output, args);
   }

   private static String formatMessage(String message, Object... args) {
      try {
         return String.format(message, args);
      } catch (RuntimeException ex) {
         return String.format(
            "Exception message could not be formatted: [%s] with the following arguments [%s].  Cause [%s]", message,
            Collections.toString(",", args), ex.toString());
      }
   }

   private static boolean isEventDebugConsole() {
      String debugConsole = System.getProperty("eventDebug", "");
      return "console".equals(debugConsole);
   }

   public static ObjectMapper getEventJacksonMapper() {
      if (mapper == null) {
         mapper = new ObjectMapper();
      }
      return mapper;
   }

   /**
    * Topic Events can have "json" property set. Desearialize as given class
    */
   public static <T> T getTopicJson(Event event, Class<T> class1) {
      try {
         return getEventJacksonMapper().readValue((String) event.getProperty("json"), class1);
      } catch (Exception ex) {
         throw new OseeWrappedException(ex, "Error reading topic json [%s]", event.toString());
      }
   }

   /**
    * Create topic event given topic and oject to searialize to json property
    */
   public static TopicEvent createTopic(AbstractTopicEvent topic, Object event) {
      return createTopic(topic.getTopic(), event, topic.getEventType());
   }

   /**
    * Create topic event given topic and oject to searialize to json property
    */
   public static TopicEvent createTopic(String topic, Object event, EventType eventType) {
      try {
         return new TopicEvent(topic, "json", EventUtil.getEventJacksonMapper().writeValueAsString(event), eventType);
      } catch (Exception ex) {
         throw new OseeWrappedException(ex, "Error reading topic json [%s]", event.toString());
      }
   }
}
