/*
 * Created on Jan 18, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.internal;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.messaging.future.MessageService;
import org.eclipse.osee.framework.messaging.future.MessageServiceProvider;
import org.eclipse.osee.framework.messaging.internal.activemq.ConnectionNodeFactoryImpl;

/**
 * @author b1528444
 */
public class MessageServiceProviderImpl implements MessageServiceProvider {

   private MessageServiceImpl messageService;
   private ExecutorService executor;
   private ClassLoader contextClassLoader;

   MessageServiceProviderImpl(ClassLoader contextClassLoader) {
      this.contextClassLoader = contextClassLoader;
   }

   public void start() throws Exception {
      Thread.currentThread().setContextClassLoader(contextClassLoader);
      executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
      messageService = new MessageServiceImpl(new ConnectionNodeFactoryImpl("1.0", Integer.toString(hashCode()), null, executor));
   }

   public void stop() throws Exception {
      messageService.stop();
   }

   @Override
   public MessageService getMessageService() throws OseeCoreException {
      return messageService;
   }
}
