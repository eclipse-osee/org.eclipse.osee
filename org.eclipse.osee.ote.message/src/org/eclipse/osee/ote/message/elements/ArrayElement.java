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
import org.eclipse.osee.ote.message.data.MemoryResource;
import org.eclipse.osee.ote.message.data.MessageData;

public class ArrayElement extends Element {

   public ArrayElement(Message<?, ?, ?> msg, String elementName, MessageData messageData, int byteOffset, int msb, int lsb, int originalMsb, int originalLsb) {
      super(msg, elementName, messageData, byteOffset, msb, lsb, originalMsb, originalLsb);
   }

   public ArrayElement(Message<?, ?, ?> msg, String elementName, MessageData messageData, int bitOffset, int bitLength) {
      super(msg, elementName, messageData, bitOffset, bitLength);
   }

   public ArrayElement(Message<?, ?, ?> msg, String elementName, MessageData messageData, int byteOffset, int msb, int lsb) {
      super(msg, elementName, messageData, byteOffset, msb, lsb);
   }

   @Override
   protected Element getNonMappingElement() {
      return null;
   }
   
   public void setValue(int index, byte value) {
      getMsgData().getMem().getData()[index + getMsgData().getMem().getOffset() + getArrayStartOffset()] = value;
   }
   
   public byte getValue(int index) {
      return getValue(getMsgData().getMem(), index);
   }

   public byte getValue(MemoryResource mem, int index) {
      return mem.getData()[index + mem.getOffset() + getArrayStartOffset()];

   }
   
   public void zeroize() {
      MemoryResource mem = getMsgData().getMem();
      mem.zeroizeFromOffset(getArrayStartOffset(), getLength());
   }

   public int getLength() {
      int currentMsgLength = getMsgData().getCurrentLength() - getMsgData().getOffset() - getArrayStartOffset();
      int length = getArrayEndOffset() - getArrayStartOffset();
      length = currentMsgLength < length ? currentMsgLength : length; 
      return length;
   }
   
   public int getArrayStartOffset() {
      return byteOffset + (msb / 8);
   }
   
   public int getArrayEndOffset() {
      return ((lsb - msb + 1) / 8) + byteOffset;
   }

   @Override
   public void visit(IElementVisitor visitor) {
      visitor.asArrayElement(this);
   }
   
   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ote.message.elements.Element#switchMessages(org.eclipse.osee.ote.message.Message[])
    */
   @Override
   public ArrayElement switchMessages(Collection<? extends Message<?, ?, ?>> messages) {
      return (ArrayElement) super.switchMessages(messages);
   }
   
}
