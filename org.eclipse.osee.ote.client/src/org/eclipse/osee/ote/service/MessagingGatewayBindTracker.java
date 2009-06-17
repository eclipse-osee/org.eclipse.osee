/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.service;

import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.EndpointReceive;
import org.eclipse.osee.framework.messaging.EndpointSend;
import org.eclipse.osee.framework.messaging.MessagingGateway;
import org.eclipse.osee.ote.service.core.OteClientEndpointSend;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Andrew M. Finkbeiner
 *
 */
public class MessagingGatewayBindTracker extends ServiceTracker {

   private EndpointSend send;
   private EndpointReceive receive;
   
   public MessagingGatewayBindTracker(BundleContext context, EndpointSend send, EndpointReceive receive) {
      super(context, MessagingGateway.class.getName(), null);
      this.send = send;
      this.receive = receive;
   }

   @Override
   public Object addingService(ServiceReference reference) {
      Object obj = context.getService(reference);
      MessagingGateway messagingGateway = (MessagingGateway)obj;
      if(!messagingGateway.bind(send)){
         OseeLog.log(Activator.class, Level.SEVERE, String.format("Unable to bind %s to the MessagingGateway.", send.toString()));
      }
      if(!messagingGateway.bind(receive)){
         OseeLog.log(Activator.class, Level.SEVERE, String.format("Unable to bind %s to the MessagingGateway.", receive.toString()));
      }
      if(!messagingGateway.bindSendProtocol(OteClientEndpointSend.OTE_CLIENT_SEND_PROTOCOL, send)){
         OseeLog.log(Activator.class, Level.SEVERE, String.format("Unable to bind %s to %s through the MessagingGateway.", OteClientEndpointSend.OTE_CLIENT_SEND_PROTOCOL.toString(), send.toString()));
      }
      return super.addingService(reference);
   }

   /* (non-Javadoc)
    * @see org.osgi.util.tracker.ServiceTracker#removedService(org.osgi.framework.ServiceReference, java.lang.Object)
    */
   @Override
   public void removedService(ServiceReference reference, Object service) {
      Object obj = context.getService(reference);
      MessagingGateway messagingGateway = (MessagingGateway)obj;
      if(!messagingGateway.unbindSendProtocol(OteClientEndpointSend.OTE_CLIENT_SEND_PROTOCOL, send)){
         OseeLog.log(Activator.class, Level.SEVERE, String.format("Unable to bind %s to %s through the MessagingGateway.", OteClientEndpointSend.OTE_CLIENT_SEND_PROTOCOL.toString(), send.toString()));
      }    
      if(!messagingGateway.unbind(send)){
         OseeLog.log(Activator.class, Level.SEVERE, String.format("Unable to unbind %s to the MessagingGateway.", send.toString()));
      }
      if(!messagingGateway.unbind(receive)){
         OseeLog.log(Activator.class, Level.SEVERE, String.format("Unable to bind %s to the MessagingGateway.", receive.toString()));
      }
      super.removedService(reference, service);
   }
   
   

   
   
}
