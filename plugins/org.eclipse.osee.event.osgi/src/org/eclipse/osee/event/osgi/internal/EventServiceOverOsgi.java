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

import java.util.Map;
import org.eclipse.osee.event.EventService;
import org.eclipse.osee.logger.Log;
import org.osgi.service.event.EventAdmin;

/**
 * @author Roberto E. Escobar
 */
public class EventServiceOverOsgi implements EventService {

   private EventAdmin eventAdmin;
   private Log log;

   public void setEventAdmin(EventAdmin eventAdmin) {
      this.eventAdmin = eventAdmin;
   }

   public void setLogger(Log log) {
      this.log = log;
   }

   public Log getLogger() {
      return log;
   }

   public void start() {
      // do nothing
   }

   public void stop() {
      // do nothing
   }

   @Override
   public void postEvent(String topic, Map<String, ?> data) {
      try {
         eventAdmin.postEvent(EventOsgiUtil.toOsgiEvent(topic, data));
      } catch (Throwable th) {
         handleError(th, "Error during postEvent for topic[%s] data[%s]", topic, data);
      }
   }

   @Override
   public void sendEvent(String topic, Map<String, ?> data) {
      try {
         eventAdmin.sendEvent(EventOsgiUtil.toOsgiEvent(topic, data));
      } catch (Throwable th) {
         handleError(th, "Error during sendEvent for topic[%s] data[%s]", topic, data);
      }
   }

   private void handleError(Throwable th, String message, Object... args) {
      Log log = getLogger();
      if (log != null) {
         log.error(th, message, args);
      }
   }

}
