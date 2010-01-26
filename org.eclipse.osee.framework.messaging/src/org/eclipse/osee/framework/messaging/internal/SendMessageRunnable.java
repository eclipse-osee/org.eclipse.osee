package org.eclipse.osee.framework.messaging.internal;

import org.apache.camel.ProducerTemplate;
import org.eclipse.osee.framework.messaging.OseeMessagingStatusCallback;
import org.eclipse.osee.framework.messaging.future.NodeInfo;

class SendMessageRunnable implements Runnable {

   private final NodeInfo nodeInfo;
   private final String topic;
   private final Object body;
   private final OseeMessagingStatusCallback statusCallback;
   private final ProducerTemplate template;

   public SendMessageRunnable(ProducerTemplate template, NodeInfo nodeInfo, String topic, Object body, OseeMessagingStatusCallback statusCallback) {
      this.template = template;
      this.nodeInfo = nodeInfo;
      this.topic = topic;
      this.body = body;
      this.statusCallback = statusCallback;
   }

   @Override
   public void run() {
      try {
         template.sendBody(nodeInfo.getComponentNameForRoutes() + topic, body);
         statusCallback.success();
      } catch (Exception ex) {
         statusCallback.fail(ex);
      }
   }
}