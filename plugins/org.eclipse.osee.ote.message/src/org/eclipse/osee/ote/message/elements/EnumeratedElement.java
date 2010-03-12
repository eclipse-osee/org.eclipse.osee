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
import org.eclipse.osee.ote.message.elements.nonmapping.NonMappingEnumeratedElement;
import org.eclipse.osee.ote.message.interfaces.ITestAccessor;

/**
 * @author Ryan D. Brooks
 * @author Andrew M. Finkbeiner
 */
public class EnumeratedElement<T extends Enum<T> & IEnumValue<T>> extends DiscreteElement<T> {

   private final Class<T> clazz;
   private final T[] values;

   public EnumeratedElement(Message<?, ?, ?> msg, String elementName, Class<T> clazz, MessageData messageData, int byteOffset, int msb, int lsb, int originalMsb, int originalLsb) {
      super(msg, elementName, messageData, byteOffset, msb, lsb, originalLsb, originalMsb);
      this.clazz = clazz;

      values = clazz.getEnumConstants();
   }

   public EnumeratedElement(Message<?, ?, ?> msg, String elementName, Class<T> clazz, MessageData messageData, int byteOffset, int msb, int lsb) {
      this(msg, elementName, clazz, messageData, byteOffset, msb, lsb, msb, lsb);
   }

   private T getValue(int intVal) {
      T undefined = null;
      for (T val : values) {
         if (val.getIntValue() == intVal) {
            return val;
         } else if (undefined == null) {
            undefined = val.getEnum(intVal);
         }
      }
      return undefined;
   }

   public EnumeratedElement(Message<?, ?, ?> message, String elementName, Class<T> clazz, MessageData messageData, int bitOffset, int bitLength) {
      super(message, elementName, messageData, bitOffset, bitLength);
      this.clazz = clazz;

      values = clazz.getEnumConstants();
   }

   public int getIntValue() {
      return getMsgData().getMem().getInt(byteOffset, msb, lsb);
   }

   /**
    * Sets the element to the "value" passed and immediately sends the meessage that contains it.
    * 
    * @param accessor
    * @param enumeration The value to set.
    */
   public void setAndSend(ITestEnvironmentAccessor accessor, T enumeration) {
      this.set(accessor, enumeration);
      super.sendMessage();
   }

   /**
    * Waits until the element is set to a value not in the "list" passed
    * 
    * @param accessor
    * @param list The list of values to check against
    * @param milliseconds Number of milliseconds to wait before failing.
    * @return last value observed
    * @throws InterruptedException
    */
   public T waitForNotInList(ITestAccessor accessor, T[] list, int milliseconds) throws InterruptedException {
      return this.waitForList(accessor, list, false, milliseconds);
   }

   /**
    * Waits until the element is set to a value in the "list" passed
    * 
    * @param accessor
    * @param list The list of values to check against
    * @param milliseconds Number of milliseconds to wait before failing.
    * @return last value observed
    * @throws InterruptedException
    */
   public T waitForInList(ITestAccessor accessor, T[] list, int milliseconds) throws InterruptedException {
      return this.waitForList(accessor, list, true, milliseconds);
   }

   /**
    * This function will verify that this signal is pulsed for 2 cycles.
    * 
    * @param accessor
    * @param value The value to be checked
    * @throws InterruptedException
    */
   public void checkPulse(ITestAccessor accessor, T value) throws InterruptedException {

      T nonPulsedValue = getValue(0);
      if (value == nonPulsedValue) nonPulsedValue = getValue(1);

      checkPulse(accessor, value, nonPulsedValue);

   }

   @SuppressWarnings("unchecked")
   public EnumeratedElement<T> switchMessages(Collection<? extends Message<?, ?, ?>> messages) {
      return (EnumeratedElement<T>) super.switchMessages(messages);
   }

   protected T toEnum(int intValue) {
      final T val = getValue(intValue);
      if (val != null) {
         return val;
      }
      throw new IllegalArgumentException(
            "No enum value associated with the integer value " + intValue + " for element " + getName());
   }

   @Override
   public T getValue() {
      return toEnum(getMsgData().getMem().getInt(byteOffset, msb, lsb));
   }

   @Override
   public T valueOf(MemoryResource otherMem) {
      return toEnum(otherMem.getInt(byteOffset, msb, lsb));
   }

   @Override
   public void setValue(T obj) {
      getMsgData().getMem().setInt(obj.getIntValue(), byteOffset, msb, lsb);
   }

   public T[] getEnumValues() {
      return clazz.getEnumConstants();
   }

   @Override
   public String toString(T obj) {
      return String.format("%s [%d]", obj.name(), obj.getIntValue());
   }

   @Override
   public void parseAndSet(ITestEnvironmentAccessor accessor, String value) throws IllegalArgumentException {
      this.set(accessor, Enum.valueOf(clazz, value));
   }

   public void set(String value) throws IllegalArgumentException {
      this.setValue(Enum.valueOf(clazz, value));
   }

   public void setbyEnumIndex(int index) throws IllegalArgumentException {
      getMsgData().getMem().setInt(values[index].getIntValue(), byteOffset, msb, lsb);
   }

   @Override
   public void visit(IElementVisitor visitor) {
      visitor.asEnumeratedElement(this);
   }

   @Override
   protected NonMappingEnumeratedElement<T> getNonMappingElement() {
      return new NonMappingEnumeratedElement<T>(this);
   }

   public String valueOf() {
      return String.format("%s [%d]", getValue().name(), getIntValue());
   }

   public Class<T> getEnumClass() {
      return clazz;
   }

   @Override
   public T elementMask(T value) {
      return value;
   }
}