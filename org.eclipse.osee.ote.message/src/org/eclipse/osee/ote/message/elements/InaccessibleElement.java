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
package org.eclipse.osee.ote.message.elements;

import java.util.Collection;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.data.MessageData;

public class InaccessibleElement extends Element {

   // TODO - this is a temporary hack for (PLT/CPG)_UFD_CMD. When cdb adds
   // individual elements, delete this class!!! Make sure to remove it from
   // MuxSignalType too!! Also make sure to take code out of Message Generation
   // too.
   public InaccessibleElement(Message<?,?,?> message, String elementName, MessageData messageData, int byteOffset, int msb,
         int lsb) {
      this(message, elementName, messageData, byteOffset, msb, lsb, msb, lsb);
   }

   public InaccessibleElement(Message<?,?,?> message, String elementName, MessageData messageData, int byteOffset, int msb,
         int lsb, int originalLsb, int originalMsb) {
      super(message, elementName, messageData, byteOffset, msb, lsb, originalLsb, originalMsb);
   }

   @Override
   public InaccessibleElement switchMessages(Collection<? extends Message<?,?,?>> messages) {
      return (InaccessibleElement) super.switchMessages(messages);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.Element#getNonMappingElement()
    */
   @Override
   protected Element getNonMappingElement() {
      return null;
   }

public int compareTo(InaccessibleElement o) {
    return 0;
}
   
   
}
