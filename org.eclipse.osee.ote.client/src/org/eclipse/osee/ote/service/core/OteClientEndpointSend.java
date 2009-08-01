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
package org.eclipse.osee.ote.service.core;

import java.util.Properties;
import org.eclipse.osee.framework.messaging.EndpointSend;
import org.eclipse.osee.framework.messaging.ExceptionHandler;
import org.eclipse.osee.framework.messaging.Message;
import org.eclipse.osee.framework.messaging.id.ProtocolId;
import org.eclipse.osee.framework.messaging.id.StringName;
import org.eclipse.osee.framework.messaging.id.StringNamespace;
import org.eclipse.osee.framework.messaging.id.StringProtocolId;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironment;
import org.eclipse.osee.ote.service.IOteClientService;

/**
 * @author Andrew M. Finkbeiner
 */
public class OteClientEndpointSend implements EndpointSend {

   public static ProtocolId OTE_CLIENT_SEND_PROTOCOL =
         new StringProtocolId(new StringNamespace("org.eclipse.osee.ote.service.core"), new StringName(
               "OteClientEndpointSend"));
   private IOteClientService clientService;

   @Override
   public void send(Message message, ExceptionHandler exceptionHandler) {
      if (clientService == null) {
         exceptionHandler.handleException(new Exception(String.format(
               "Unable to send message [%s], no client service is available.", message.toString())));
      } else {
         ITestEnvironment env = clientService.getConnectedEnvironment();
         if (env == null) {
            exceptionHandler.handleException(new Exception(String.format(
                  "Unable to send message [%s], there is not a connected environment.", message.toString())));
         } else {
            try {
               env.sendMessage(message);
            } catch (Throwable th) {
               exceptionHandler.handleException(new Exception(String.format("Unable to send message [%s]",
                     message.toString()), th));
            }
         }
      }
   }

   @Override
   public void start(Properties properties) {
   }

   /**
    * @param testClientServiceImpl
    */
   public void setTestClientService(IOteClientService testClientServiceImpl) {
      this.clientService = testClientServiceImpl;
   }

}
