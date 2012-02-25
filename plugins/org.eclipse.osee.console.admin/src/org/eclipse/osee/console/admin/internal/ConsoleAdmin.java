/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.console.admin.internal;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.osee.console.admin.ConsoleAdminConstants;
import org.eclipse.osee.console.admin.ConsoleCommand;
import org.eclipse.osee.event.EventService;
import org.eclipse.osee.executor.admin.ExecutorAdmin;
import org.eclipse.osee.logger.Log;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Roberto E. Escobar
 */
public class ConsoleAdmin {

   private final List<ServiceReference<ConsoleCommand>> pending =
      new CopyOnWriteArrayList<ServiceReference<ConsoleCommand>>();

   private Log logger;
   private EventService eventService;
   private ExecutorAdmin executorAdmin;

   private CommandDispatcher dispatcher;
   private Thread thread;
   private ServiceRegistration<?> cmdRef;

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setEventService(EventService eventService) {
      this.eventService = eventService;
   }

   public void setExecutorAdmin(ExecutorAdmin executorAdmin) {
      this.executorAdmin = executorAdmin;
   }

   public ExecutorAdmin getExecutorAdmin() {
      return executorAdmin;
   }

   public Log getLogger() {
      return logger;
   }

   public EventService getEventService() {
      return eventService;
   }

   public void start(BundleContext context) throws Exception {
      dispatcher = new CommandDispatcher(getLogger(), getExecutorAdmin());
      thread = new Thread("Register Pending Osee Console Commands") {
         @Override
         public void run() {
            for (ServiceReference<ConsoleCommand> reference : pending) {
               register(reference);
            }
            pending.clear();
         }
      };
      thread.start();
      CommandProviderImpl command = new CommandProviderImpl();
      command.setConsoleAdmin(this);
      cmdRef = context.registerService(CommandProvider.class.getName(), command, null);
   }

   public void stop(BundleContext context) {
      if (thread != null && thread.isAlive()) {
         thread.interrupt();
      }
      if (cmdRef != null) {
         cmdRef.unregister();
         cmdRef = null;
      }
   }

   private boolean isReady() {
      return getDispatcher() != null && getLogger() != null;
   }

   public void addCommand(ServiceReference<ConsoleCommand> reference) {
      if (isReady()) {
         register(reference);
      } else {
         pending.add(reference);
      }
   }

   public void removeCommand(ServiceReference<ConsoleCommand> reference) {
      if (isReady()) {
         unregister(reference);
      } else {
         pending.remove(reference);
      }
   }

   private void unregister(ServiceReference<ConsoleCommand> reference) {
      String componentName = ConsoleAdminUtils.getComponentName(reference);
      String contextName = ConsoleAdminUtils.getContextName(reference);

      getDispatcher().unregister(componentName);
      getLogger().debug("De-registering command for '%s' with alias '%s'\n", componentName, contextName);

      notifyDeRegistration(componentName, contextName);
   }

   private void register(ServiceReference<ConsoleCommand> reference) {
      String componentName = ConsoleAdminUtils.getComponentName(reference);
      String contextName = ConsoleAdminUtils.getContextName(reference);
      try {
         Bundle bundle = reference.getBundle();
         ConsoleCommand consoleCommand = bundle.getBundleContext().getService(reference);

         getDispatcher().register(componentName, consoleCommand);
         notifyRegistration(componentName, contextName);
         getLogger().debug("Registered command for '%s' with alias '%s'\n", componentName, contextName);
      } catch (Exception ex) {
         throw new RuntimeException(ex);
      }
   }

   private void notifyRegistration(String componentName, String contextName) {
      Map<String, String> data = ConsoleAdminUtils.toMap(componentName, contextName);
      getEventService().postEvent(ConsoleAdminConstants.REST_REGISTRATION_EVENT, data);
   }

   private void notifyDeRegistration(String componentName, String contextName) {
      Map<String, String> data = ConsoleAdminUtils.toMap(componentName, contextName);
      getEventService().postEvent(ConsoleAdminConstants.REST_DEREGISTRATION_EVENT, data);
   }

   public CommandDispatcher getDispatcher() {
      return dispatcher;
   }
}
