package org.eclipse.osee.framework.messaging.internal;

import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import org.apache.camel.Exchange;
import org.eclipse.osee.framework.messaging.OseeMessagingListener;

/**
 * @author b1528444
 */
class ProcessorTranslator implements org.apache.camel.Processor {

   private final OseeMessagingListener listener;

   public ProcessorTranslator(OseeMessagingListener listener) {
      this.listener = listener;
   }

   @Override
   public void process(Exchange exchange) throws Exception {
      org.apache.camel.Message message = exchange.getIn();
      Properties properties = new Properties();
      copyHeaderElements(properties, message);
      Object body = message.getBody();
      if(body instanceof Map){
         properties.putAll((Map<?,?>)body);
      }
      properties.put("body", message.getBody());
      listener.process(properties);
   }

   private void copyHeaderElements(Properties properties, org.apache.camel.Message message) {
      Map<String, Object> headers = message.getHeaders();
      for (Entry<String, Object> entry : headers.entrySet()) {
         if (entry.getKey() != null && entry.getValue() != null) {
            properties.put(entry.getKey(), entry.getValue());
         }
      }
   }
}
