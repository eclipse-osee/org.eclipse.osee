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
package org.eclipse.osee.ote.core.environment;

import org.eclipse.osee.framework.messaging.EndpointReceive;
import org.eclipse.osee.framework.messaging.EndpointSend;
import org.eclipse.osee.framework.messaging.MessagingGateway;
import org.eclipse.osee.framework.messaging.id.ProtocolId;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Andrew M. Finkbeiner
 *
 */
public class OteEnvironmentTrackerCustomizer implements ServiceTrackerCustomizer {

   private EndpointReceive receive;
   private EndpointSend send;
   private BundleContext context;
   private ProtocolId protocolId;
   
   public OteEnvironmentTrackerCustomizer(BundleContext context, EndpointReceive receive, EndpointSend send, ProtocolId protocolId){
      this.context = context;
      this.receive = receive;
      this.send = send;
      this.protocolId = protocolId;
   }
   
   public Object addingService(ServiceReference reference) {
      MessagingGateway gateway = (MessagingGateway)context.getService(reference);
      gateway.bind(receive);
      gateway.bind(send);
      gateway.bindSendProtocol(protocolId, send);
      return null;
   }

   public void modifiedService(ServiceReference reference, Object service) {
   }

   public void removedService(ServiceReference reference, Object service) {
      MessagingGateway gateway = (MessagingGateway)context.getService(reference);
      gateway.unbind(receive);
      gateway.unbindSendProtocol(protocolId, send);
      gateway.unbind(send);
   }
}
