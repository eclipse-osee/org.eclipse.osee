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
package org.eclipse.osee.display.mvp.mocks;

import org.eclipse.osee.display.mvp.event.annotation.EndPoint;
import org.eclipse.osee.display.mvp.presenter.AbstractPresenter;
import org.eclipse.osee.display.mvp.presenter.annotation.IsPresenterFor;

/**
 * @author Roberto E. Escobar
 */
@IsPresenterFor(View2.class)
public class Presenter2 extends AbstractPresenter<View2, EventBus1> {

   private String broadcastMessage;
   private EventMessage eventMessage;

   @EndPoint
   public void onEventMessage(EventMessage message) {
      getLogger().info("Send Message: [%s]", message);
      eventMessage = message;
   }

   @EndPoint
   public void onBroadcastMessage(String message) {
      getLogger().info("Broadcast Message: [%s]", message);
      broadcastMessage = message;
   }

   public String getBroadcastMessage() {
      return broadcastMessage;
   }

   public EventMessage getEventMessage() {
      return eventMessage;
   }

}
