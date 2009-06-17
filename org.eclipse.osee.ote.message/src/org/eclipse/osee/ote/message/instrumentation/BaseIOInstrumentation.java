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
package org.eclipse.osee.ote.message.instrumentation;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.message.MessageSystemTestEnvironment;



/**
 * @author Andrew M. Finkbeiner
 *
 */
public class BaseIOInstrumentation implements IOInstrumentation {

   private final List<InetSocketAddress> addresses;
   private final DatagramChannel channel;
   
   public BaseIOInstrumentation() throws IOException{
      addresses = new ArrayList<InetSocketAddress>();
      channel = DatagramChannel.open();
      channel.configureBlocking(true);
      
   }
   
   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.instrumentation.IOInstrumentation#command(byte[])
    */
   public void command(byte[] cmd) {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.instrumentation.IOInstrumentation#register(java.net.InetSocketAddress)
    */
   public void register(InetSocketAddress address) {
      if (address == null) {
         throw new IllegalArgumentException("address cannot be null");
      }
      addresses.add(address);
   }
   
   public void unregister(InetSocketAddress address) {
      addresses.remove(address);
   }
   
   public void send(byte[] bytes){
      send(ByteBuffer.wrap(bytes));
   }
   
   public void send(byte[] bytes, int offset, int length) {
      send(ByteBuffer.wrap(bytes, offset, length));
   }
   
   public void send(ByteBuffer bytes){
	   bytes.mark();
	   for(InetSocketAddress address:addresses){
		   try {
			   channel.send(bytes, address);
			   bytes.reset();
		   }
		   catch (Exception ex) {
			   OseeLog.log(MessageSystemTestEnvironment.class,
					   Level.SEVERE,  
					   ex.toString(), ex);
		   }
	   }
   }

}
