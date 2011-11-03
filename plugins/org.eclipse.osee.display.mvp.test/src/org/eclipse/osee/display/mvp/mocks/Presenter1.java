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

/**
 * @author Roberto E. Escobar
 */
public class Presenter1 extends AbstractPresenter<View1, EventBus1> {

   private String broadcastMessage;
   private String p1Message;

   @EndPoint
   public void onBroadcastMessage(String message) {
      getLogger().info("Broadcast Message: [%s]", message);
      broadcastMessage = message;
   }

   @EndPoint
   public void onP1Message(String message) {
      getLogger().info("P1 Message: [%s]", message);
      p1Message = message;
   }

   public String getBroadcastMessage() {
      return broadcastMessage;
   }

   public String getP1Message() {
      return p1Message;
   }

}
