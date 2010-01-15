/*
 * Created on Jan 15, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.internal;

import java.util.concurrent.ExecutorService;
import org.apache.activemq.camel.CamelConnectionFactory;
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

   //   private final OseeTransportListener jmsTransport;
   //   private final CamelConnectionFactory activeMqRemoteConnectionFactory;
   //   private final CamelConnectionFactory vmConnectionFactory;
   //   private final StatusNotifier notifier;

   public ConnectionNodeFactoryImpl(CamelContext context, ExecutorService executor) {
      this.executor = executor;
      this.context = context;
   }

   //      activeMqRemoteConnectionFactory = new CamelConnectionFactory();
   //      activeMqRemoteConnectionFactory.setBrokerURL(String.format("failover:(%s)", commaSeperatedUriList));
   //
   //      vmConnectionFactory = new CamelConnectionFactory();
   //      vmConnectionFactory.setBrokerURL("vm://localhost?broker.persistent=false");
   //
   //      notifier = new StatusNotifier();
   //
   //      jmsTransport = new OseeTransportListener(Component.JMS, notifier);
   //   }

   @Override
   public ConnectionNode create(NodeInfo nodeInfo) throws OseeCoreException {

      CamelConnectionFactory camelConnectionFactory = new CamelConnectionFactory();
      //      camelConnectionFactory.setBrokerURL(String.format("failover:(%s)", uri));
      camelConnectionFactory.setBrokerURL(nodeInfo.getUri().toASCIIString());
      camelConnectionFactory.setAlwaysSessionAsync(true);
      camelConnectionFactory.setAlwaysSyncSend(false);
      /*
       * if we use failover: sends pend not sure about route adding, probably
       * pend also. Need to thread those operations so we don't block
       * applications use transport listener for lifecycle events
       */
      //      camelConnectionFactory.setTransportListener(jmsTransport);
      camelConnectionFactory.setCamelContext(context);
      camelConnectionFactory.getCamelContext().addComponent(nodeInfo.getComponent().getComponentName(),
            ActiveMQComponent.jmsComponent(camelConnectionFactory));

      ProducerTemplate template = camelConnectionFactory.getCamelContext().createProducerTemplate();
      return new ConnectionNodeImpl(nodeInfo, executor, context, template);
   }

   //   void start() throws Exception {
   //      context.start();
   //      //      activeMqRemoteConnectionFactory.setAlwaysSessionAsync(true);
   //      //      activeMqRemoteConnectionFactory.setAlwaysSyncSend(false);
   //      //      /*
   //      //       * if we use failover: sends pend not sure about route adding, probably
   //      //       * pend also. Need to thread those operations so we don't block
   //      //       * applications use transport listener for lifecycle events
   //      //       */
   //      //      activeMqRemoteConnectionFactory.setTransportListener(jmsTransport);
   //      //      activeMqRemoteConnectionFactory.setCamelContext(context);
   //      //      activeMqRemoteConnectionFactory.getCamelContext().addComponent(Component.JMS.getComponentName(),
   //      //            JmsComponent.jmsComponent(activeMqRemoteConnectionFactory));
   //      //
   //      //      vmConnectionFactory.setCamelContext(context);
   //      //      vmConnectionFactory.getCamelContext().addComponent(Component.VM.getComponentName(),
   //      //            ActiveMQComponent.jmsComponent(vmConnectionFactory));
   //      //      vmConnectionFactory.setObjectMessageSerializationDefered(true);
   //      //
   //      //      template = activeMqRemoteConnectionFactory.getCamelContext().createProducerTemplate();
   //      //      activeMqRemoteConnectionFactory.getCamelContext().start();
   //      //      jmsTransport.setAvailable(true);
   //   }
   //
   //   void stop() throws Exception {
   //      //      template.stop();
   //      context.stop();
   //   }

   //   public final class StatusNotifier {
   //
   //      public void notify(Properties properties) {
   //         Object object = properties.get("component");
   //         if (object instanceof String) {
   //            // THIS SHOULD GO AWAY
   //            String component = (String) object;
   //
   //            String healthTopic = null;
   //            if (Component.JMS.getComponentName().equals(component)) {
   //               healthTopic = SystemTopic.JMS_HEALTH_STATUS;
   //            }
   //            // -------- //
   //
   //            if (healthTopic != null) {
   //               sendMessage(Component.VM, healthTopic, properties, new OseeMessagingStatusCallback() {
   //                  @Override
   //                  public void success() {
   //                  }
   //
   //                  @Override
   //                  public void fail(Throwable th) {
   //                     OseeLog.log(Activator.class, Level.SEVERE, th);
   //                  }
   //               });
   //            }
   //         }
   //      }
   //
   //      public void notifyError(Component component, Throwable object) {
   //         OseeLog.log(Activator.class, Level.SEVERE, object);
   //      }
   //   }

}
