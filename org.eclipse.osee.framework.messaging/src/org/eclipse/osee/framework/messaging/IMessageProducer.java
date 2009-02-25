/*
 * Created on Feb 25, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging;

/**
 * @author b1122182
 */
public interface IMessageProducer {

   public void send(ITopic topic, IMessage message);

   public void send(Object //IDestination 
   destination, IMessage message);

}
