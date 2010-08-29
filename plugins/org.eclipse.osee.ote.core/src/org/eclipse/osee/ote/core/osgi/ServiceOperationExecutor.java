/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.core.osgi;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * A special service tracker that allows the atomic execution of an operation against a service. This provides a way for
 * a complex operation to interact with an OSGI service without fear of the service being removed in the middle of the
 * operation
 * 
 * @author Ken J. Aguilar
 */
public class ServiceOperationExecutor extends ServiceTracker {

   private IServiceOperation operationInProgress = null;
   private final ReentrantLock operationLock = new ReentrantLock();
   private final Condition operationComplete = operationLock.newCondition();

   public ServiceOperationExecutor(BundleContext context, String serviceName) {
      super(context, serviceName, null);
   }

   @Override
   public void close() {
      waitForOperationComplete();
      super.close();
   }

   @Override
   public void removedService(ServiceReference reference, Object service) {
      waitForOperationComplete();
      super.removedService(reference, service);
   }

   protected void waitForOperationComplete() {
      operationLock.lock();
      try {
         if (operationInProgress != null) {
            operationInProgress.interrupt();
            while (operationInProgress != null) {
               operationComplete.await();
            }
         }
      } catch (InterruptedException e) {
         /*
          * we got interrupted ourselves!! Standard practice is to call the current threads interrupt() if we can't
          * re-throw the interrupted exception
          */
         Thread.currentThread().interrupt();
      } finally {
         operationLock.unlock();
      }
   }

   /**
    * executes the operation.
    * 
    * @return true if the operation completed or false if the dependent service was not acquired
    * @throws InterruptedException if the service operation was interrupted
    */
   public boolean executeOperation(IServiceOperation operation) throws InterruptedException {

      final Object service;
      // lock so we can safely get the service and set the operation in progress
      operationLock.lock();
      service = getService();
      if (service == null) {
         // the tracked service has not been acquired yet
         return false;
      }
      operationInProgress = operation;
      /*
       * unlock since we do not want to be holding the lock while the operation is in progress.
       */
      operationLock.unlock();

      try {
         operationInProgress.doOperation(service);
      } finally {
         /*
          * if we get interrupted or something else happens make sure we signal operation complete so that we can
          * prevent deadlocks if something was waiting on the operation
          */
         operationLock.lock();
         operationInProgress = null;
         operationComplete.signal();
         operationLock.unlock();

      }
      return true;
   }
}
