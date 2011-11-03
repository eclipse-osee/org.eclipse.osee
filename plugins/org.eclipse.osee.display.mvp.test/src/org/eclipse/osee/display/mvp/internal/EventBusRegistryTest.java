/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.display.mvp.internal;

import org.eclipse.osee.display.mvp.mocks.EventBus1;
import org.eclipse.osee.display.mvp.mocks.EventMessage;
import org.eclipse.osee.display.mvp.mocks.MockLog;
import org.eclipse.osee.display.mvp.mocks.Presenter1;
import org.eclipse.osee.display.mvp.mocks.Presenter2;
import org.eclipse.osee.logger.Log;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test Case for {@link EventBusRegistryImpl}
 * 
 * @author Roberto E. Escobar
 */
public class EventBusRegistryTest {

   @Test
   public void testEvent() {
      Log logger = new MockLog();
      EventBusRegistryImpl eventBus = new EventBusRegistryImpl(logger);

      Presenter1 presenter1 = new Presenter1();
      presenter1.setLogger(logger);

      Presenter2 presenter2 = new Presenter2();
      presenter2.setLogger(logger);

      eventBus.addSubscriber(presenter1);
      eventBus.addSubscriber(presenter2);

      EventBus1 busInstance = eventBus.getEventBus(EventBus1.class);

      String broadcastMessage = "broadcast message";
      busInstance.sendBroadcastMessage(broadcastMessage);
      Assert.assertEquals(broadcastMessage, presenter1.getBroadcastMessage());
      Assert.assertEquals(broadcastMessage, presenter2.getBroadcastMessage());

      String p1Message = "p1 message";
      busInstance.sendP1Message(p1Message);
      Assert.assertEquals(p1Message, presenter1.getP1Message());

      EventMessage eventMessage = new EventMessage(23L, "hello");
      busInstance.sendEventMessage(eventMessage);
      Assert.assertEquals(eventMessage, presenter2.getEventMessage());
   }
}
