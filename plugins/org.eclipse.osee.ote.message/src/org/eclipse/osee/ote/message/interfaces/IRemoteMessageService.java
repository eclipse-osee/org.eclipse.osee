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
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.EnumSet;
import java.util.Map;
import org.eclipse.osee.ote.message.commands.RecordCommand;
import org.eclipse.osee.ote.message.commands.SetElementValue;
import org.eclipse.osee.ote.message.commands.SetMessageModeCmd;
import org.eclipse.osee.ote.message.commands.SubscribeToMessage;
import org.eclipse.osee.ote.message.commands.UnSubscribeToMessage;
import org.eclipse.osee.ote.message.commands.ZeroizeElement;
import org.eclipse.osee.ote.message.enums.MemType;
import org.eclipse.osee.ote.message.tool.SubscriptionDetails;
import org.eclipse.osee.ote.message.tool.rec.IMessageEntryFactory;

/**
 * defines the operations clients can request of a remote message manager service
 * 
 * @author Andrew M. Finkbeiner
 */
public interface IRemoteMessageService extends Remote {

   void unsubscribeToMessage(UnSubscribeToMessage cmd) throws RemoteException;

   /**
    * Sets a message element to a specified value
    * 
    * @param cmd
    * @throws RemoteException
    */
   void setElementValue(SetElementValue cmd) throws RemoteException;

   void zeroizeElement(ZeroizeElement cmd) throws RemoteException;

 //  int changeSubscription(ChangeSubscription cmd) throws RemoteException;

   /**
    * Notifies service to send message updates to the specified ip address
    * 
    * @param cmd
    * @throws RemoteException
    */
   SubscriptionDetails subscribeToMessage(SubscribeToMessage cmd) throws RemoteException;

   SubscriptionDetails setReaderWriterMode(SetMessageModeCmd cmd) throws RemoteException;

   EnumSet<MemType> getAvailablePhysicalTypes() throws RemoteException;

   boolean startRecording(RecordCommand cmd) throws RemoteException;

   InetSocketAddress getRecorderSocketAddress() throws RemoteException;

   InetSocketAddress getMsgUpdateSocketAddress() throws RemoteException;

   void stopRecording() throws RemoteException;
   
   void terminateService() throws RemoteException;

   void reset() throws RemoteException;

   void setupRecorder(IMessageEntryFactory factory)throws RemoteException;

   public Map<String, Throwable> getCancelledSubscriptions()throws RemoteException;
}
