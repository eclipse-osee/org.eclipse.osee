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
package org.eclipse.osee.framework.messaging;

import java.util.Collection;
import org.eclipse.osee.framework.messaging.id.MessageId;
import org.eclipse.osee.framework.messaging.id.ProtocolId;

/**
 * @author Andrew M. Finkbeiner
 */
public interface MessagingGateway {

   public void send(Message message, ExceptionHandler exceptionHandler);

   public void dispose();

   public boolean bind(EndpointSend endpoint);

   public boolean unbind(EndpointSend endpoint);

   public boolean bind(EndpointReceive endpoint);

   public boolean unbind(EndpointReceive endpoint);

   public Collection<EndpointSend> getSendEndpoints();

   public Collection<EndpointReceive> getReceiveEndpoints();

   public boolean bindSendProtocol(ProtocolId protocolId, EndpointSend endpoint);

   public boolean unbindSendProtocol(ProtocolId protocolId, EndpointSend endpoint);

   public boolean bindSendMessage(MessageId messageId, ProtocolId protocolId);

   public boolean unbindSendMessage(MessageId messageId, ProtocolId protocolId);

   public void addSendListener(MessageId messageId, SendListener sendListener);

   public void removeSendListener(MessageId messageId, SendListener sendListener);

   public void addReceiveListener(MessageId messageId, ReceiveListener receiveListener);

   public void removeReceiveListener(MessageId messageId, ReceiveListener receiveListener);
}
