/*********************************************************************
 * Copyright (c) 2021 Boeing
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
package org.eclipse.osee.ats.ide.integration.tests.skynet.core;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.event.listener.IBranchEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEvent;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;

/**
 * @author Dominic Guss
 */
public class BranchEventListenerAsync implements IBranchEventListener {
   private final List<Pair<Sender, BranchEvent>> eventPairs;
   private static final long WAIT_TIME_MSEC = 1000L;

   public BranchEventListenerAsync() {
      eventPairs = new ArrayList<Pair<Sender, BranchEvent>>();
   }

   public synchronized void reset() {
      eventPairs.clear();
   }

   @Override
   public List<? extends IEventFilter> getEventFilters() {
      return null;
   }

   @Override
   public synchronized void handleBranchEvent(Sender sender, BranchEvent branchEvent) {
      eventPairs.add(new Pair<Sender, BranchEvent>(sender, branchEvent));
      notify();
   }

   public synchronized List<Pair<Sender, BranchEvent>> getResults(BranchEventType branchEventType)
      throws InterruptedException {
      while (eventNull(branchEventType)) {
         wait(WAIT_TIME_MSEC);
      }
      return eventPairs;
   }

   private synchronized boolean eventNull(BranchEventType eventType) {
      for (Pair<Sender, BranchEvent> eventPair : eventPairs) {
         if (eventPair.getSecond().getEventType().equals(eventType)) {
            return false;
         }
      }
      return true;
   }
}
