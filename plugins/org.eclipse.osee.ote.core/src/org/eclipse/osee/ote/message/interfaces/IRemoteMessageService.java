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
package org.eclipse.osee.ote.message.interfaces;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Set;

import org.eclipse.osee.ote.message.commands.RecordCommand;
import org.eclipse.osee.ote.message.commands.SetElementValue;
import org.eclipse.osee.ote.message.commands.SubscribeToMessage;
import org.eclipse.osee.ote.message.commands.UnSubscribeToMessage;
import org.eclipse.osee.ote.message.commands.ZeroizeElement;
import org.eclipse.osee.ote.message.enums.DataType;
import org.eclipse.osee.ote.message.tool.SubscriptionDetails;
import org.eclipse.osee.ote.message.tool.rec.IMessageEntryFactory;

/**
 * defines the operations clients can request of a remote message manager service
 * 
 * @author Andrew M. Finkbeiner
 */
public interface IRemoteMessageService {

   void unsubscribeToMessage(UnSubscribeToMessage cmd);

   /**
    * Sets a message element to a specified value
    */
   void setElementValue(SetElementValue cmd);

   void zeroizeElement(ZeroizeElement cmd);

   /**
    * Notifies service to send message updates to the specified ip address
    */
   SubscriptionDetails subscribeToMessage(SubscribeToMessage cmd);

   Set<? extends DataType> getAvailablePhysicalTypes();

   boolean startRecording(RecordCommand cmd);

   InetSocketAddress getRecorderSocketAddress();

   InetSocketAddress getMsgUpdateSocketAddress();

   void stopRecording();

   void terminateService();

   void reset();

   void setupRecorder(IMessageEntryFactory factory);

   public Map<String, Throwable> getCancelledSubscriptions();
}
