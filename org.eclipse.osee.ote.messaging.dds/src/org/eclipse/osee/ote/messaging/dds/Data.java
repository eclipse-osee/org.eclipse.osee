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
package org.eclipse.osee.ote.messaging.dds;

import java.nio.ByteBuffer;

/**
 * Provides the DDS system with a generic means of handling any data type. Any messages
 * that are sent through the system must implement this interface so that the DDS system
 * has a means of handling them in a platform independent manner.
 * 
 * @author Robert A. Fisher
 * @author David Diepenbrock
 */
public interface Data {
    
   /**
    * This method is used to get a byte array representation of the data. This data
    * should be in such a form that it can be passed to the <code>setFromByteArray</code>
    * method to acquire the "same" object.
    * 
    * @return A byte array version of the state information for this <code>Data</code>
    */
   public byte[] toByteArray();
   public ByteBuffer toByteBuffer();
   /**
    * This method is used to restore a data item's state from a byte array. This method
    * should work in such a manner that the array received from a <code>Data</code>'s
    * <code>toByteArray</code> method can be passed to this method to construct an object
    * that is the "same".
    * 
    * @param input The byte array to build the state information from.
    */
   public void setFromByteArray(byte[] input);
   public void setFromByteBuffer(ByteBuffer buffer);
   public void copyFrom(Data buffer);
   /**
    * This method is here for future functionality that is described in the DDS specification
    * but has not been implemented or used.
    */
   public Object getKeyValue();
   public int getOffset();

}
