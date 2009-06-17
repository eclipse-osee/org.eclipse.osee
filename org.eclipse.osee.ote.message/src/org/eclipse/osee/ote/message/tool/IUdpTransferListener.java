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
package org.eclipse.osee.ote.message.tool;

/**
 * @author Ken J. Aguilar
 */
public interface IUdpTransferListener {
   /**
    * called by the file transfer handler when a file transfer is complete
    * @param config the transfer configuration
    */
   void onTransferComplete(TransferConfig config);
   
   /**
    * called when the an error is experienced during transfer operations. The handle for this
    * transfer will be automatically stopped prior to this method being called
    * @param config
    * @param t
    */
   void onTransferException(TransferConfig config, Throwable t);
}
