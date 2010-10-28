/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *public static final CoreAttributeTypes   Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.test.mocks;

import org.eclipse.osee.framework.skynet.core.event.listener.ITransactionEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.skynet.core.event.model.TransactionEvent;

/**
 * @author Roberto E. Escobar
 */
public class MockTransactionEventListener implements ITransactionEventListener {
   private TransactionEvent resultTransEvent;
   private Sender resultSender;
   private int eventCount;

   public MockTransactionEventListener() {
      clear();
   }

   @Override
   public void handleTransactionEvent(Sender sender, TransactionEvent transEvent) {
      incrementEventCount();
      resultTransEvent = transEvent;
      resultSender = sender;
   }

   public TransactionEvent getResultTransEvent() {
      return resultTransEvent;
   }

   public Sender getResultSender() {
      return resultSender;
   }

   public int getEventCount() {
      return eventCount;
   }

   public boolean wasEventReceived() {
      return eventCount > 0;
   }

   private void incrementEventCount() {
      eventCount++;
   }

   public void clear() {
      eventCount = 0;
      resultSender = null;
      resultTransEvent = null;
   }
}