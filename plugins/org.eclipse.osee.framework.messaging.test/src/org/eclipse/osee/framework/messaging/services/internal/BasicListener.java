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
package org.eclipse.osee.framework.messaging.services.internal;

import java.util.Map;
import org.eclipse.osee.framework.messaging.OseeMessagingListener;
import org.eclipse.osee.framework.messaging.ReplyConnection;
import org.eclipse.osee.framework.messaging.test.msg.TestMessage;

/**
 * @author Andrew M. Finkbeiner
 *
 */
public class BasicListener extends OseeMessagingListener {

   private int id;
   private boolean received = false;
   
   public BasicListener(int id) {
      this.id = id;
   }
   
   @Override
   public Class<?> getClazz() {
      return TestMessage.class;
   }
   
   @Override
   public void process(Object message, Map<String, Object> headers, ReplyConnection replyConnection) {
      System.out.println(message + "  -  " + id);
      received = true;
   }

   public boolean isReceived(){
      return received;
   }
   
   public String toString(){
      return "BasicListener " + id;
   }
}
