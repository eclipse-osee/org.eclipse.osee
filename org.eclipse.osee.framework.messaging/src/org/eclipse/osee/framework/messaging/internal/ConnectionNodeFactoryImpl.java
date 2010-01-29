/*
 * Created on Jan 15, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.internal;

import java.util.concurrent.ExecutorService;

import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.messaging.future.ConnectionNode;
import org.eclipse.osee.framework.messaging.future.ConnectionNodeFactory;
import org.eclipse.osee.framework.messaging.future.NodeInfo;

/**
 * @author b1122182
 */
public class ConnectionNodeFactoryImpl implements ConnectionNodeFactory {

   private final CamelContext context;
   private final ExecutorService executor;

   public ConnectionNodeFactoryImpl(CamelContext context, ExecutorService executor) {
      this.executor = executor;
      this.context = context;
   }

   @Override
   public ConnectionNode create(NodeInfo nodeInfo) throws OseeCoreException {

//      CamelConnectionFactory camelConnectionFactory = new CamelConnectionFactory();
//      //      camelConnectionFactory.setBrokerURL(String.format("failover:(%s)", uri));
//      camelConnectionFactory.setBrokerURL(nodeInfo.getUri().toASCIIString());
//      camelConnectionFactory.setAlwaysSessionAsync(true);
//      camelConnectionFactory.setAlwaysSyncSend(false);
      /*
       * if we use failover: sends pend not sure about route adding, probably
       * pend also. Need to thread those operations so we don't block
       * applications use transport listener for lifecycle events
       */
      //      camelConnectionFactory.setTransportListener(jmsTransport);
//      camelConnectionFactory.setCamelContext(context);
//      camelConnectionFactory.getCamelContext().addComponent(nodeInfo.getComponentName(),
//            ActiveMQComponent.jmsComponent(camelConnectionFactory));

	  ActiveMQComponent component = ActiveMQComponent.activeMQComponent(nodeInfo.getUri().toASCIIString());
//	  component.setMaxConcurrentConsumers(4);
	  component.setUsePooledConnection(true);
	  
      context.addComponent(nodeInfo.getComponentName(),component);
      ProducerTemplate template = context.createProducerTemplate();
      return new ConnectionNodeImpl(nodeInfo, executor, context, template);
   }

}
