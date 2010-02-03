/*
 * Created on Jan 18, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.internal;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.messaging.future.MessageService;
import org.eclipse.osee.framework.messaging.future.MessageServiceProvider;

/**
 * @author b1528444
 * 
 */
public class MessageServiceProviderImpl implements MessageServiceProvider {

	private MessageServiceImpl messageService;
	private CamelContext camelContext;
	private ExecutorService executor;

	MessageServiceProviderImpl() {
	}

	public void start() throws Exception {
		camelContext = new DefaultCamelContext();
		camelContext.start();
		executor = Executors.newFixedThreadPool(Runtime.getRuntime()
				.availableProcessors());
		messageService = new MessageServiceImpl(new ConnectionNodeFactoryImpl(
				camelContext, executor));
	}

	public void stop() throws Exception {
		messageService.stop();
		camelContext.stop();
	}

	@Override
	public MessageService getMessageService() throws OseeCoreException {

		return messageService;
	}
}
