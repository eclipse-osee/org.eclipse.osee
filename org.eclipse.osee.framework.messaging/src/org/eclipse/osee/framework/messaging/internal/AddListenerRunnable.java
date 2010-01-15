package org.eclipse.osee.framework.messaging.internal;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.eclipse.osee.framework.messaging.OseeMessagingListener;
import org.eclipse.osee.framework.messaging.OseeMessagingStatusCallback;
import org.eclipse.osee.framework.messaging.future.NodeInfo;

class AddListenerRunnable implements Runnable {

   private final NodeInfo nodeInfo;
   private final String topic;
   private final OseeMessagingListener listener;
   private final OseeMessagingStatusCallback statusCallback;
   private final CamelContext context;

   public AddListenerRunnable(CamelContext context, NodeInfo nodeInfo, String topic, final OseeMessagingListener listener, OseeMessagingStatusCallback statusCallback) {
      this.context = context;
      this.nodeInfo = nodeInfo;
      this.topic = topic;
      this.listener = listener;
      this.statusCallback = statusCallback;
   }

   @Override
   public void run() {
      try {
         //            checkTransport(component);
         context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() {
               from(nodeInfo.getComponentNameForRoutes() + topic).process(new ProcessorTranslator(listener));
            }
         });

         //            if (Component.VM.equals(component) && SystemTopic.JMS_HEALTH_STATUS.equals(topic)) {
         //               listener.process(jmsTransport.createStatusMessage());
         //            }
         statusCallback.success();
      } catch (Exception ex) {
         statusCallback.fail(ex);
      }
   }
}