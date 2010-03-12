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

/**
 * TODO:ANDY: Many check functions in this class are wrong, should be using all doubles without casting to/from int's.
 * 
 * @author Ryan D. Brooks
 * @author Andrew M. Finkbeiner
 */
public abstract class RealElement extends NumericElement<Double> {

   @Override
   public void visit(IElementVisitor visitor) {
      visitor.asRealElement(this);
   }

   public RealElement(Message<?, ?, ?> message, String elementName, MessageData messageData, int byteOffset, int msb, int lsb) {
      this(message, elementName, messageData, byteOffset, msb, lsb, msb, lsb);
   }

   public RealElement(Message<?, ?, ?> message, String elementName, MessageData messageData, int byteOffset, int msb, int lsb, int originalLsb, int originalMsb) {
      super(message, elementName, messageData, byteOffset, msb, lsb, originalLsb, originalMsb);
   }

   public RealElement(Message<?, ?, ?> message, String elementName, MessageData messageData, int bitOffset, int bitLength) {
      super(message, elementName, messageData, bitOffset, bitLength);
   }

   @Override
   public RealElement switchMessages(Collection<? extends Message<?, ?, ?>> messages) {
      return (RealElement) super.switchMessages(messages);
   }

   public void setValue(Float value) {
      setValue(value.doubleValue());
   }

   public String valueOf() {
      return getValue().toString();
   }

   public long getRaw() {
      return getRaw(getMsgData().getMem());
   }

   public long getRaw(MemoryResource mem) {
      return mem.getLong(byteOffset, msb, lsb);
   }

   public abstract void set(ITestEnvironmentAccessor accessor, double value);

   public abstract void setAndSend(ITestEnvironmentAccessor accessor, double value);

   /**
    * sets the bit pattern for this element. All hex values must be in the form of: <br>
    * <p>
    * <code><b>0x[<I>hex characters</I>]L</b></code><br>
    * <p>
    * The trailing 'L' signals java to treat the value as a long integer.
    * 
    * @param hex a bit patter to set the element to. The pattern is not limited to hexadecimal
    */
   public abstract void setHex(long hex);

   protected abstract double toDouble(long value);

   protected abstract long toLong(double value);

   @Override
   public void parseAndSet(ITestEnvironmentAccessor accessor, String value) throws IllegalArgumentException {
      this.set(accessor, Double.parseDouble(value));
   }

   @Override
   public String toString(Double obj) {
      return obj.toString();
   }

   @Override
   public Double elementMask(Double value) {
      return value;
   }
   
   @Override
   public long getNumericBitValue() {
      return getRaw();
   }
}