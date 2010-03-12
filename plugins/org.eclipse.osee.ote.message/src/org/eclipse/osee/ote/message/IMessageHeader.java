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
package org.eclipse.osee.ote.message;

import org.eclipse.osee.ote.message.elements.Element;

/**
 * Defines operations for setting and getting the raw bytes that 
 * comprise a message header as well as getting the name of the 
 * message that the header is attached to
 * @author Ken J. Aguilar
 */
public interface IMessageHeader {
   public String getMessageName();
   public int getHeaderSize();
   
   /**
    * Sets the data that backs this header. 
    * @param data
    */
//   public void copyData(byte[] data);
   public byte[] getData();
   
   public Element[] getElements();
   /**
    * @param data
    */
   public void setNewBackingBuffer(byte[] data);
}
