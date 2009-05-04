/*
 * Created on Feb 25, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging;

import java.util.Properties;



/**
 * @author b1122182
 */
public interface EndpointSend {
   public void start(Properties properties);
   public void send(Message message, ExceptionHandler exceptionHandler);
}
