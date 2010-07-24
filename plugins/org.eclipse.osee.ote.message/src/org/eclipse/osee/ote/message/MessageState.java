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

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ote.message.enums.DataType;
import org.eclipse.osee.ote.message.tool.MessageMode;

/**
 * Holds a state of a message intended to be transferred from the message manager server to an interest client. In other
 * words, helps synchronize two remote instances of a message object
 * 
 * @author Ken J. Aguilar
 * @see org.eclipse.osee.ote.message.Message
 */
public final class MessageState implements Serializable {

   private static final long serialVersionUID = -8977593021184452337L;

   private final DataType currentMemType;
   private final byte[] data;
   private final Set<DataType> availableMemTypes;
   private final MessageMode mode;

   public MessageState(final DataType currentMemType, final byte[] data, final Set<DataType> availableMemTypes, final MessageMode mode) {
      this.currentMemType = currentMemType;
      this.data = data;

      this.availableMemTypes = new HashSet<DataType>(availableMemTypes);
      this.mode = mode;
   }

   public DataType getCurrentMemType() {
      return currentMemType;
   }

   public byte[] getData() {
      return data;
   }

   public Set<DataType> getAvailableMemTypes() {
      return availableMemTypes;
   }

   public MessageMode getMode() {
      return mode;
   }

}
