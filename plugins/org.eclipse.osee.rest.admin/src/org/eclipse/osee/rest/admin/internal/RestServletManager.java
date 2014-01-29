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

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import javax.ws.rs.core.Application;
import org.eclipse.osee.authorization.admin.AuthorizationAdmin;
import org.eclipse.osee.executor.admin.CancellableCallable;
import org.eclipse.osee.executor.admin.ExecutorAdmin;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.rest.admin.internal.filters.SecurityContextFilter;
import org.eclipse.osee.rest.admin.internal.filters.SecurityContextProviderImpl;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;

/**
 * @author Roberto E. Escobar
 */
public class RestServletManager {

   private static final ApplicationEntry END_OF_QUEUE = new ApplicationEntry(Op.END_OF_QUEUE, null);

   private final LinkedBlockingQueue<ApplicationEntry> pending = new LinkedBlockingQueue<ApplicationEntry>();

   private HttpService httpService;
   private Log logger;
   private ExecutorAdmin executorAdmin;
   private AuthorizationAdmin authorizationAdmin;

   private final AtomicReference<RestServletRegistry> registryRef = new AtomicReference<RestServletRegistry>();
   private final AtomicReference<Future<?>> futureRef = new AtomicReference<Future<?>>();

   public void setHttpService(HttpService httpService) {
      this.httpService = httpService;
   }

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setExecutorAdmin(ExecutorAdmin executorAdmin) {
      this.executorAdmin = executorAdmin;
   }

   public void setAuthorizationAdmin(AuthorizationAdmin authorizationAdmin) {
      this.authorizationAdmin = authorizationAdmin;
   }

   public void start() throws Exception {
      SecurityContextProvider contextProvider = new SecurityContextProviderImpl(logger, authorizationAdmin);
      SecurityContextFilter securityFilter = new SecurityContextFilter(contextProvider);

      RestComponentFactory factory = new RestComponentFactory(logger, securityFilter);
      RestServletRegistry newRegistry = new RestServletRegistry(logger, httpService, factory);
      RestServletRegistry registry = registryRef.getAndSet(newRegistry);
      if (registry != null) {
         registry.cleanUp();
      }

      CancellableCallable<Void> callable = newRegistrationHelper();
      Future<?> newFuture = executorAdmin.schedule(callable);
      Future<?> future = futureRef.getAndSet(newFuture);
      stopWorker(future);
   }

   public void stop() {
      pending.offer(END_OF_QUEUE);
      Future<?> future = futureRef.getAndSet(null);
      stopWorker(future);

      RestServletRegistry registry = registryRef.getAndSet(null);
      if (registry != null) {
         registry.cleanUp();
      }
   }

   private void stopWorker(Future<?> future) {
      if (future != null) {
         try {
            future.get(3, TimeUnit.SECONDS);
         } catch (Exception ex) {
            logger.warn(ex, "Error waiting for registration worker to complete");
            future.cancel(true);
         }
      }
   }

   public void addApplication(ServiceReference<Application> reference) {
      pending.offer(new ApplicationEntry(Op.ADD, reference));
   }

   public void removeApplication(ServiceReference<Application> reference) {
      pending.offer(new ApplicationEntry(Op.REMOVE, reference));
   }

   private CancellableCallable<Void> newRegistrationHelper() {
      return new CancellableCallable<Void>() {
         @Override
         public Void call() throws Exception {
            logger.debug("Start - REST Application Registration Helper");
            boolean isEndOfQueue = false;
            while (!isEndOfQueue) {
               Set<ApplicationEntry> toProcess = new HashSet<ApplicationEntry>();
               ApplicationEntry entry = pending.take();
               pending.drainTo(toProcess);
               toProcess.add(entry);

               for (ApplicationEntry item : toProcess) {
                  checkForCancelled();

                  RestServletRegistry registry = registryRef.get();
                  if (registry != null) {
                     ServiceReference<Application> reference = item.getReference();
                     switch (item.getOp()) {
                        case ADD:
                           registry.register(reference);
                           break;
                        case REMOVE:
                           registry.deregister(reference);
                           break;
                        default:
                           isEndOfQueue = true;
                           break;
                     }
                  } else {
                     logger.debug("Registry was null while worker was processing [reg/de-reg]-istrations");
                     isEndOfQueue = true;
                  }
               }
            }
            logger.debug("Stop - REST Application Registration Helper");
            return null;
         }
      };
   }

   private static enum Op {
      ADD,
      REMOVE,
      END_OF_QUEUE;
   }

   private static final class ApplicationEntry {
      private final Op op;
      private final ServiceReference<Application> reference;

      public ApplicationEntry(Op op, ServiceReference<Application> reference) {
         super();
         this.op = op;
         this.reference = reference;
      }

      public Op getOp() {
         return op;
      }

      public ServiceReference<Application> getReference() {
         return reference;
      }

   }
}
