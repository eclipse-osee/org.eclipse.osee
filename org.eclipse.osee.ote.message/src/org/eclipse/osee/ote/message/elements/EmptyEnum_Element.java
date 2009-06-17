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
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.data.MemoryResource;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.elements.nonmapping.NonMappingEmptyEnumElement;
import org.eclipse.osee.ote.message.enums.EmptyEnum;
import org.eclipse.osee.ote.message.interfaces.ITestAccessor;

/**
 * @author Michael P. Masterson
 */
public class EmptyEnum_Element extends DiscreteElement<EmptyEnum> {

   /**
    * @param message
    * @param elementName
    */
   public EmptyEnum_Element(Message<?, ?, ?> message, String elementName, MessageData messageData, int byteOffset, int msb, int lsb) {
      this(message, elementName, messageData, byteOffset, msb, lsb, msb, lsb);
   }

   public EmptyEnum_Element(Message<?, ?, ?> message, String elementName, MessageData messageData, int byteOffset, int msb, int lsb, int originalLsb, int originalMsb) {
      super(message, elementName, messageData, byteOffset, msb, lsb, originalLsb, originalMsb);
   }

   public EmptyEnum_Element(Message<?, ?, ?> message, String elementName, MessageData messageData, int bitOffset, int bitLength) {
      super(message, elementName, messageData, bitOffset, bitLength);
   }

   /**
    * Checks that this element correctly forwards a message sent from cause with the value passed.
    * 
    * @param accessor
    * @param cause The originator of the signal
    * @param value The value sent by cause and being forwarded by this element
    * @throws InterruptedException
    */
   public void checkForwarding(ITestAccessor accessor, EmptyEnum_Element cause, EmptyEnum value) throws InterruptedException {
      /* check for 0 to begin */
      check(accessor, EmptyEnum.toEnum(0), 0);

      /* Set the DP1 Signal */
      cause.set(accessor, value);

      /* Check Value on DP2 */
      check(accessor, value, 1000);

      /* Set DP1 to 0 */
      cause.set(accessor, EmptyEnum.toEnum(0));

      /* Set DP2 Mux to 0 */
      set(accessor, EmptyEnum.toEnum(0));

      /* Check Value on DP2 is still set */
      check(accessor, value, 500);

      /* Check DP2 is 0 for two-pulse signals and high for four-pulse signal */
      check(accessor, EmptyEnum.toEnum(0), 500);

   }

   @Override
   public EmptyEnum_Element switchMessages(Collection<? extends Message<?, ?, ?>> messages) {
      // TODO Auto-generated method stub
      return (EmptyEnum_Element) super.switchMessages(messages);
   }

   /**
    * Sets the element to the "value" passed and immediately sends the message that contains it.
    * 
    * @param accessor
    * @param enumeration The value to set.
    */
   public void setAndSend(ITestEnvironmentAccessor accessor, EmptyEnum enumeration) {
      this.set(accessor, enumeration);
      super.sendMessage();
   }

   /**
    * Returns the current value of the element.
    * 
    * @return The current value of the element.
    */
   public EmptyEnum get(ITestEnvironmentAccessor accessor) {
      return (EmptyEnum) getValue();
   }

   /* (non-Javadoc)
       * @see org.eclipse.osee.ote.message.elements.Element#getNonMappingElement()
       */
   @Override
   protected Element getNonMappingElement() {
      return new NonMappingEmptyEnumElement(this);
   }

   @Override
   public void visit(IElementVisitor visitor) {
      visitor.asEmptyEnumElement(this);
   }

   @Override
   public void parseAndSet(ITestEnvironmentAccessor accessor, String value) throws IllegalArgumentException {
      int intValue = Integer.parseInt(value);
      getMsgData().getMem().setInt(intValue, byteOffset, msb, lsb);
   }

   @Override
   public EmptyEnum getValue() {
      return toEnum(getMsgData().getMem().getInt(byteOffset, msb, lsb));
   }

   @Override
   public void setValue(EmptyEnum obj) {
      getMsgData().getMem().setInt(obj.getValue(), byteOffset, msb, lsb);
   }

   @Override
   public String toString(EmptyEnum obj) {
      return "EmptyEnum_" + obj.getValue();
   }

   @Override
   public EmptyEnum valueOf(MemoryResource mem) {
      return toEnum(mem.getInt(byteOffset, msb, lsb));
   }

   private EmptyEnum toEnum(int intValue) {
      return EmptyEnum.toEnum(intValue);
   }

   @Override
   public EmptyEnum elementMask(EmptyEnum value) {
      return value;
   }

}
