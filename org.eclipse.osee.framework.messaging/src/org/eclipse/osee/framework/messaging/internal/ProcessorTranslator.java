package org.eclipse.osee.framework.messaging.internal;

import java.util.Map;

import org.apache.camel.Exchange;
import org.eclipse.osee.framework.messaging.OseeMessagingListener;

/**
 * @author b1528444
 */
public class ProcessorTranslator implements org.apache.camel.Processor {

   private final OseeMessagingListener listener;

   public ProcessorTranslator(OseeMessagingListener listener) {
      this.listener = listener;
   }

   @Override
   public void process(Exchange exchange) throws Exception {
	   Map<String, Object> headers = exchange.getIn().getHeaders();
	   Class<?> pojoType = listener.getClazz();
	   Object messageBody;
	   if(pojoType == null){
		   messageBody = exchange.getIn().getBody();
	   } else {
		   messageBody = JAXBUtil.unmarshal(exchange.getIn().getBody().toString(), pojoType);
	   }
	   listener.process(messageBody, headers, new ReplyConnectionImpl());
   }
}
