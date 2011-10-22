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
package org.eclipse.osee.executor.admin.mock;

import java.util.Map;
import org.eclipse.osee.event.EventService;

/**
 * @author Roberto E. Escobar
 */
public class MockEventService implements EventService {

   private String topic;
   private Map<String, ?> data;
   private int postEvent;
   private int sendEvent;

   @Override
   public void postEvent(String topic, Map<String, ?> data) {
      this.topic = topic;
      this.data = data;
      postEvent++;
   }

   @Override
   public void sendEvent(String topic, Map<String, ?> data) {
      this.topic = topic;
      this.data = data;
      sendEvent++;
   }

   public void reset() {
      this.topic = null;
      this.data = null;
      postEvent = 0;
      sendEvent = 0;
   }

   public String getTopic() {
      return topic;
   }

   public Map<String, ?> getData() {
      return data;
   }

   public int getPostEventCount() {
      return postEvent;
   }

   public int getSendEventCount() {
      return sendEvent;
   }
}
