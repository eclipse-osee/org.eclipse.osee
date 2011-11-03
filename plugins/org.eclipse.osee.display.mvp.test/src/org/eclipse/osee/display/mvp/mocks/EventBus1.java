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

import org.eclipse.osee.display.mvp.event.EventBus;
import org.eclipse.osee.display.mvp.event.annotation.RouteTo;

/**
 * @author Roberto E. Escobar
 */
public interface EventBus1 extends EventBus {

   @RouteTo({Presenter2.class})
   public void sendEventMessage(EventMessage dto);

   @RouteTo({Presenter2.class, Presenter1.class})
   public void sendBroadcastMessage(String message);

   @RouteTo({Presenter1.class})
   public void sendP1Message(String message);

}
