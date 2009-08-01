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
import org.eclipse.osee.ote.core.testPoint.CheckGroup;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.data.MemoryResource;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.elements.nonmapping.NonMappingLongIntegerElement;
import org.eclipse.osee.ote.message.interfaces.ITestAccessor;

/**
 * @author Ryan D. Brooks
 * @author Andrew M. Finkbeiner
 */
public class LongIntegerElement extends NumericElement<Long> {

   @Override
   public void visit(IElementVisitor visitor) {
      visitor.asLongIntegerElement(this);
   }

   public LongIntegerElement(Message<?, ?, ?> message, String elementName, MessageData messageData, int byteOffset, int msb, int lsb) {
      this(message, elementName, messageData, byteOffset, msb, lsb, msb, lsb);
   }

   public LongIntegerElement(Message<?, ?, ?> message, String elementName, MessageData messageData, int bitOffset, int bitLength) {
      super(message, elementName, messageData, bitOffset, bitLength);
   }

   public LongIntegerElement(Message<?, ?, ?> message, String elementName, MessageData messageData, int byteOffset, int msb, int lsb, int originalLsb, int originalMsb) {
      super(message, elementName, messageData, byteOffset, msb, lsb, originalLsb, originalMsb);
   }

   @Override
   public LongIntegerElement switchMessages(Collection<? extends Message<?, ?, ?>> messages) {
      return (LongIntegerElement) super.switchMessages(messages);
   }

   @Override
   public String toString(Long obj) {
      long value = elementMask(obj);
      return value + "(0x" + Long.toHexString(value).toUpperCase() + ")";
   }

   @Override
   public void setValue(Long value) {
      getMsgData().getMem().setLong((Long) value, byteOffset, msb, lsb);
   }

   @Override
   public Long getValue() {
      return new Long(getMsgData().getMem().getLong(byteOffset, msb, lsb));
   }

   @Override
   public Long valueOf(MemoryResource mem) {
      return new Long(mem.getLong(byteOffset, msb, lsb));
   }

   /**
    * Checks that this element correctly forwards a message sent from cause with the value passed.
    * 
    * @param accessor
    * @param cause The originator of the signal
    * @param value The value sent by cause and being forwarded by this element
    * @throws InterruptedException
    */
   public void checkForwarding(ITestAccessor accessor, LongIntegerElement cause, long value) throws InterruptedException {
      value = removeSign(value);
      /* check for 0 to begine */
      check(accessor, 0, 0);

      /* Set the DP1 Mux Signal */
      cause.set(accessor, value);

      /* Chk Value on DP2 */
      check(accessor, value, 1000);

      /* Set DP1 to 0 */
      cause.set(accessor, 0);

      /* Init DP2 Mux to 0 */
      set(accessor, 0);

      /* Chk Value on DP2 is still set */
      check(accessor, value, 500);

      /* Chk DP2 is 0 for two-pulse signals and high for four-pulse signal */
      check(accessor, 0, 500);

   }

   /**
    * Verifies that the element is set to "value" within the number of "milliseconds" passed.
    * 
    * @param accessor
    * @param value Expected value.
    * @param milliseconds Number of milliseconds to wait for the element to equal the "value".
    * @return If the check passed.
    * @throws InterruptedException
    */
   public boolean check(ITestAccessor accessor, long value, int milliseconds) throws InterruptedException {
      return super.check(accessor, (CheckGroup) null, removeSign(value), milliseconds);
   }

   /**
    * This function will verify that this signal is pulsed for 2 cycles.
    * 
    * @param accessor
    * @param value The value to be checked
    * @throws InterruptedException
    */
   public void checkPulse(ITestAccessor accessor, long value) throws InterruptedException {
      long nonPulsedValue = 0;
      if (value == 0) nonPulsedValue = 1;

      checkPulse(accessor, value, nonPulsedValue);

   }

   /**
    * Sets the element to the "value" passed.
    * 
    * @param accessor
    * @param value The value to set.
    */
   public void set(ITestEnvironmentAccessor accessor, long value) {
      super.set(accessor, value);
   }

   /**
    * Sets the element to the "value" passed.
    * 
    * @param accessor
    * @param value The value to set.
    */
   @Deprecated
   public void set(long value) {
      setValue(value);
   }

   /**
    * Sets the element to the "value" passed and immediately sends the message that contains it..
    * 
    * @param accessor
    * @param value The value to set.
    */
   public void setAndSend(ITestEnvironmentAccessor accessor, long value) {
      this.set(accessor, value);
      super.sendMessage();
   }

   @Override
   public void parseAndSet(ITestEnvironmentAccessor accessor, String value) throws IllegalArgumentException {
      this.set(accessor, Long.parseLong(value));
   }

   @Override
   protected NonMappingLongIntegerElement getNonMappingElement() {
      return new NonMappingLongIntegerElement(this);
   }

   @Override
   public Long elementMask(Long value) {
      return removeSign(value);
   }

   @Override
   public long getNumericBitValue() {
      return getValue();
   }
   
}