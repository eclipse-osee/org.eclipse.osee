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
package org.eclipse.osee.framework.messaging.internal;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.messaging.MessageService;
import org.eclipse.osee.framework.messaging.MessageServiceProvider;
import org.eclipse.osee.framework.messaging.internal.activemq.ConnectionNodeFactoryImpl;

/**
 * @author Andrew M. Finkbeiner
 */
public class MessageServiceProviderImpl implements MessageServiceProvider {

   private MessageServiceImpl messageService;
   private ExecutorService executor;
   private ClassLoader contextClassLoader;

   MessageServiceProviderImpl(ClassLoader contextClassLoader) {
      this.contextClassLoader = contextClassLoader;
   }

   public void start() throws Exception {
//      Thread.currentThread().setContextClassLoader(contextClassLoader);
      executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
      messageService = new MessageServiceImpl(new ConnectionNodeFactoryImpl("1.0", Integer.toString(hashCode()), executor));
   }

   public void stop() throws Exception {
      messageService.stop();
      executor.shutdown();
   }

   @Override
   public MessageService getMessageService() throws OseeCoreException {
      return messageService;
   }
}
