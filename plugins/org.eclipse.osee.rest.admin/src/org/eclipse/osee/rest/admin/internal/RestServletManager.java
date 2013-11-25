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
package org.eclipse.osee.rest.admin.internal;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.ws.rs.core.Application;
import org.eclipse.osee.event.EventService;
import org.eclipse.osee.logger.Log;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;

/**
 * @author Roberto E. Escobar
 */
public class RestServletManager {

   private final List<ServiceReference<Application>> pending =
      new CopyOnWriteArrayList<ServiceReference<Application>>();

   private HttpService httpService;
   private Log logger;
   private EventService eventService;

   private Thread thread;

   private volatile RestServletRegistry registry;

   public void setHttpService(HttpService httpService) {
      this.httpService = httpService;
   }

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setEventService(EventService eventService) {
      this.eventService = eventService;
   }

   public void start() throws Exception {
      RestComponentFactory factory = new RestComponentFactory(logger);
      registry = new RestServletRegistry(logger, httpService, eventService, factory);
      thread = createRegistrationThread(registry);
      thread.start();
   }

   public void stop() {
      if (thread != null && thread.isAlive()) {
         thread.interrupt();
      }
      registry = null;
   }

   private boolean isReady() {
      return registry != null;
   }

   public void addApplication(ServiceReference<Application> reference) {
      if (isReady()) {
         try {
            registry.register(reference);
         } catch (Exception ex) {
            throw new RuntimeException(ex);
         }
      } else {
         pending.add(reference);
      }
   }

   public void removeApplication(ServiceReference<Application> reference) {
      if (isReady()) {
         registry.deregister(reference);
      } else {
         pending.remove(reference);
      }
   }

   private Thread createRegistrationThread(final RestServletRegistry registry) {
      return new Thread("Register Pending Rest Services") {
         @Override
         public void run() {
            for (ServiceReference<Application> reference : pending) {
               try {
                  registry.register(reference);
               } catch (Exception ex) {
                  throw new RuntimeException(ex);
               }
            }
            pending.clear();
         }
      };
   }
}
