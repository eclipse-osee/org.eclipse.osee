/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.internal.message.event.send;

import org.eclipse.osee.ote.message.event.OteEventMessage;
import org.eclipse.osee.ote.message.event.OteEventMessageUtil;
import org.eclipse.osee.ote.message.event.send.OteEventMessageCallable;
import org.osgi.service.event.Event;


public class OteEventMessageFutureMultipleResponseImpl<T extends OteEventMessage, R extends OteEventMessage> extends OteEventMessageFutureImpl<T, R> {


   public OteEventMessageFutureMultipleResponseImpl(Class<R> recieveClasstype, OteEventMessageCallable<T, R> callable, T sentMessage, String responseTopic, int responseId, long timeout) {
      super(recieveClasstype, callable, sentMessage, responseTopic, responseId, timeout);
   }

   @Override
   public void handleEvent(Event event) {
      try {
         R msg = recieveClasstype.newInstance();
         OteEventMessageUtil.putBytes(event, msg);
         callable.call(sentMessage, msg, this);
      } catch (InstantiationException e) {
         e.printStackTrace();
      } catch (IllegalAccessException e) {
         e.printStackTrace();
      }
   }
}
