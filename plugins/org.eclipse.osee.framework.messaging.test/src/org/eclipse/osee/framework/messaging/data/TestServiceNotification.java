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
package org.eclipse.osee.framework.messaging.data;

import org.eclipse.osee.framework.messaging.services.ServiceNotification;
import org.eclipse.osee.framework.messaging.services.messages.ServiceHealth;

/**
 * @author Roberto E. Escobar
 */
public class TestServiceNotification implements ServiceNotification {

   private volatile int serviceUpdates;
   private volatile int serviceAway;
   private volatile boolean onServiceGoneReceived;
   private volatile boolean onServiceUpdateReceived;
   private volatile boolean serviceIsGone;

   public TestServiceNotification() {
      super();
      reset();
   }

   public synchronized void setServiceGone(boolean isGone) {
      serviceIsGone = isGone;
   }

   public synchronized void reset() {
      serviceIsGone = false;
      serviceUpdates = 0;
      serviceAway = 0;
      onServiceGoneReceived = false;
      onServiceUpdateReceived = false;
   }

   @Override
   public synchronized void onServiceGone(ServiceHealth serviceHealth) {
      serviceAway++;
      onServiceGoneReceived = true;
      notify();
   }

   @Override
   public synchronized void onServiceUpdate(ServiceHealth serviceHealth) {
      serviceUpdates++;
      onServiceUpdateReceived = true;
      notify();
   }

   @Override
   public boolean isServiceGone(ServiceHealth serviceHealth) {
      return serviceIsGone;
   }

   public int getServiceUpdatesCount() {
      return serviceUpdates;
   }

   public int getServiceAwayCount() {
      return serviceAway;
   }

   public boolean wasOnServiceGoneReceived() {
      return onServiceGoneReceived;
   }

   public boolean wasOnServiceUpdateReceived() {
      return onServiceUpdateReceived;
   }

}
