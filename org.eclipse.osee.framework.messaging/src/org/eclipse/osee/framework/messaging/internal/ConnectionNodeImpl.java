/*
 * Created on Jan 15, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.internal;

import java.util.concurrent.ExecutorService;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.eclipse.osee.framework.messaging.OseeMessagingListener;
import org.eclipse.osee.framework.messaging.OseeMessagingStatusCallback;
import org.eclipse.osee.framework.messaging.future.ConnectionNode;
import org.eclipse.osee.framework.messaging.future.NodeInfo;

/**
 * @author b1122182
 */
public class ConnectionNodeImpl implements ConnectionNode {

   private final NodeInfo nodeInfo;
   private final ExecutorService executor;
   private final CamelContext context;
   private final ProducerTemplate template;

   ConnectionNodeImpl(NodeInfo nodeInfo, ExecutorService executor, CamelContext context, ProducerTemplate template) {
      this.executor = executor;
      this.nodeInfo = nodeInfo;
      this.context = context;
      this.template = template;
   }

   @Override
   public void send(String topic, Object body, OseeMessagingStatusCallback statusCallback) {
      process(new SendMessageRunnable(template, nodeInfo, topic, body, statusCallback));
   }

   @Override
   public void subscribe(String topic, OseeMessagingListener listener, OseeMessagingStatusCallback statusCallback) {
      process(new AddListenerRunnable(context, nodeInfo, topic, listener, statusCallback));

   }

   private void process(Runnable runnable) {
      if (nodeInfo.isVMComponent()) {
         runnable.run();
      } else {
         executor.execute(runnable);
      }
   }

   //   private void checkTransport(Component component) throws OseeCoreException {
   //      if (component.equals(Component.JMS) && !jmsTransport.isAvailable()) {
   //         throw new OseeCoreException("JmsComponent is not available.");
   //      }
   //   }

}
