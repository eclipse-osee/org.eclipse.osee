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
package org.eclipse.osee.ote.message.elements.nonmapping;

import java.lang.ref.WeakReference;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor;
import org.eclipse.osee.ote.core.testPoint.CheckGroup;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.data.MemoryResource;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.elements.EnumeratedElement;
import org.eclipse.osee.ote.message.elements.IEnumValue;
import org.eclipse.osee.ote.message.interfaces.ITestAccessor;

/**
 * @author Andy Jury
 */
public class NonMappingEnumeratedElement<T extends Enum<T> & IEnumValue<T>> extends EnumeratedElement<T> {

   public NonMappingEnumeratedElement(EnumeratedElement<T> element) {
      this(null, element.getElementName(), element.getEnumClass(), element.getMsgData(), element.getByteOffset(),
            element.getMsb(), element.getLsb());
      // This is being done so it doesn't get added to the element list hash map.
      this.msg = new WeakReference<Message<?, ?, ?>>(element.getMessage());
      for (Object obj : element.getElementPath()) {
         this.getElementPath().add(obj);
      }
   }

   /**
    * @param msg
    * @param elementName
    * @param clazz
    * @param messageData
    * @param byteOffset
    * @param msb
    * @param lsb
    * @param originalMsb
    * @param originalLsb
    */
   public NonMappingEnumeratedElement(Message<?, ?, ?> msg, String elementName, Class<T> clazz, MessageData messageData, int byteOffset, int msb, int lsb, int originalMsb, int originalLsb) {
      super(msg, elementName, clazz, messageData, byteOffset, msb, lsb, originalMsb, originalLsb);
   }

   /**
    * @param msg
    * @param elementName
    * @param clazz
    * @param messageData
    * @param byteOffset
    * @param msb
    * @param lsb
    */
   public NonMappingEnumeratedElement(Message<?, ?, ?> msg, String elementName, Class<T> clazz, MessageData messageData, int byteOffset, int msb, int lsb) {
      super(msg, elementName, clazz, messageData, byteOffset, msb, lsb);
   }

   /**
    * @param message
    * @param elementName
    * @param clazz
    * @param messageData
    * @param bitOffset
    * @param bitLength
    */
   public NonMappingEnumeratedElement(Message<?, ?, ?> message, String elementName, Class<T> clazz, MessageData messageData, int bitOffset, int bitLength) {
      super(message, elementName, clazz, messageData, bitOffset, bitLength);
   }

   /**
    * Verifies that the element is set to a value IN or NOT IN the "list" passed. "wantInList" determines if checking
    * for IN the list or NOT.
    * 
    * @param accessor
    * @param checkGroup If this check is part of a larger set of checks which another method is going to log then the
    *           reference to the CheckGroup must be passed and this method will add the result of the check to the group
    *           with out logging a point.
    *           <p>
    *           If an outside method is not going to log the check then a <b>null </b> reference should be passed and
    *           this method will log the test point.
    * @param wantInList Determines if checking for the element's value to be in or not in the "list". Passing TRUE will
    *           test for IN the "list".
    * @param list List of values to check for
    * @return if check passed
    */
   @Override
   public boolean checkList(ITestAccessor accessor, CheckGroup checkGroup, boolean wantInList, T[] list) {
      throwNoMappingElementException();
      return false;
   }

   /**
    * Verifies that the element is set to a value IN or NOT IN the "list" passed. "isInList" determines if checking for
    * IN the list or NOT.
    * 
    * @param accessor
    * @param checkGroup If this check is part of a larger set of checks which another method is going to log then the
    *           reference to the CheckGroup must be passed and this method will add the result of the check to the group
    *           with out logging a point.
    *           <p>
    *           If an outside method is not going to log the check then a <b>null </b> reference should be passed and
    *           this method will log the test point.
    * @param isInList Determines if checking for the element's value to be in or not in the "list". Passing TRUE will
    *           test for IN the "list".
    * @param list List of values to check for
    * @param milliseconds Number of milliseconds to wait
    * @return if check passed
    * @throws InterruptedException
    */
   public boolean checkList(ITestAccessor accessor, CheckGroup checkGroup, boolean isInList, T[] list, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return false;
   }

   /**
    * Verifies that the element is set to a value in (or not in as determined by "isInList") the list for the entire
    * time passed into milliseconds.
    * 
    * @param accessor
    * @param checkGroup If this check is part of a larger set of checks which another method is going to log then the
    *           reference to the CheckGroup must be passed and this method will add the result of the check to the group
    *           with out logging a point.
    *           <p>
    *           If an outside method is not going to log the check then a <b>null </b> reference should be passed and
    *           this method will log the test point.
    * @param list The list of values to check against
    * @param isInList If the value is expected to be in or not in the "list"
    * @param milliseconds Number of milliseconds to wait before failing.
    * @return last value observed
    * @throws InterruptedException
    */
   public T checkMaintainList(ITestAccessor accessor, CheckGroup checkGroup, T[] list, boolean isInList, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return null;
   }

   /**
    * Waits until the element is set to a value either in or not in the "list" as determined by "isInList".
    * 
    * @param accessor
    * @param list The list of values to check against
    * @param isInList If the value is expected to be in or not in the "list"
    * @param milliseconds Number of milliseconds to wait before failing.
    * @return last value observed
    * @throws InterruptedException
    */
   public T waitForList(ITestAccessor accessor, T[] list, boolean isInList, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return null;
   }

   /**
    * Waits until the element has a value other than the "value" passed. Returns last value observed upon a time-out.
    * 
    * @param accessor
    * @param enumeration The expected value to wait for.
    * @param milliseconds Number of milliseconds to wait before failing.
    * @return last value observed
    * @throws InterruptedException
    */
   public T waitForNotValue(ITestEnvironmentAccessor accessor, T enumeration, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return null;
   }

   @Override
   /**
    * Sets the element to the first enumeration for the wait time and then it sets it to the second
    * enumeration.
    * 
    * @param accessor
    * @param enumeration1
    * @param enumeration2
    * @param milliseconds
    * @throws InterruptedException
    */
   public void toggle(ITestEnvironmentAccessor accessor, T enumeration1, T enumeration2, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
   }

   @Override
   /**
    * Returns the current value of the element.
    * 
    * @return The current value of the element.
    */
   public T get(ITestEnvironmentAccessor accessor) {
      throwNoMappingElementException();
      return null;
   }

   @Override
   /**
    * Sets the element to the "value" passed.
    * 
    * @param accessor
    * @param enumeration The value to set.
    */
   public void set(ITestEnvironmentAccessor accessor, T enumeration) {
      throwNoMappingElementException();
   }

   @Override
   public void setAndSend(ITestEnvironmentAccessor accessor, T enumeration) {
      throwNoMappingElementException();
   }

   @Override
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
      throwNoMappingElementException();
      return null;
   }

   @Override
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
      throwNoMappingElementException();
      return null;
   }

   @Override
   /**
    * Verifies that the element is set to "value".
    * 
    * @param accessor
    * @param checkGroup If this check is part of a larger set of checks which another method is
    *           going to log then the reference to the CheckGroup must be passed and this method
    *           will add the result of the check to the group with out logging a point.
    *           <p>
    *           If an outside method is not going to log the check then a <b>null </b> reference
    *           should be passed and this method will log the test point.
    * @param enumeration Expected value
    * @return if the check passed
    */
   public boolean check(ITestAccessor accessor, CheckGroup checkGroup, T enumeration) {
      throwNoMappingElementException();
      return false;
   }

   @Override
   /**
    * Verifies that the element is NOT set to "value".
    * 
    * @param accessor
    * @param checkGroup If this check is part of a larger set of checks which another method is
    *           going to log then the reference to the CheckGroup must be passed and this method
    *           will add the result of the check to the group with out logging a point.
    *           <p>
    *           If an outside method is not going to log the check then a <b>null </b> reference
    *           should be passed and this method will log the test point.
    * @param enumeration value to test against
    * @return if the check passed
    */
   public boolean checkNot(ITestAccessor accessor, CheckGroup checkGroup, T enumeration) {
      throwNoMappingElementException();
      return false;
   }

   @Override
   /**
    * Verifies that the element is set to "value" within the number of "milliseconds" passed.
    * 
    * @param accessor
    * @param checkGroup If this check is part of a larger set of checks which another method is
    *           going to log then the reference to the CheckGroup must be passed and this method
    *           will add the result of the check to the group with out logging a point.
    *           <p>
    *           If an outside method is not going to log the check then a <b>null </b> reference
    *           should be passed and this method will log the test point.
    * @param enumeration Expected value.
    * @param milliseconds Number of milliseconds to wait for the element to equal the "value".
    * @return If the check passed.
    * @throws InterruptedException
    */
   public boolean check(ITestAccessor accessor, CheckGroup checkGroup, T enumeration, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return false;
   }

   @Override
   /**
    * Verifies that the element is set to some value other than "value" within the number of
    * "milliseconds" passed. Passes if at any point with in the time allowed, the element is set to
    * a value other than "value".
    * 
    * @param accessor
    * @param checkGroup If this check is part of a larger set of checks which another method is
    *           going to log then the reference to the CheckGroup must be passed and this method
    *           will add the result of the check to the group with out logging a point.
    *           <p>
    *           If an outside method is not going to log the check then a <b>null </b> reference
    *           should be passed and this method will log the test point.
    * @param enumeration value to test against.
    * @param milliseconds Number of milliseconds to wait for the element to equal the "value".
    * @return If the check passed.
    * @throws InterruptedException
    */
   public boolean checkNot(ITestAccessor accessor, CheckGroup checkGroup, T enumeration, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return false;
   }

   @Override
   /**
    * Verifies that the element is set to the "value" passed for the entire time passed into
    * "milliseconds". Returns value found that caused failure or last value observed if time
    * expires.
    * 
    * @param enumeration
    * @param checkGroup If this check is part of a larger set of checks which another method is
    *           going to log then the reference to the CheckGroup must be passed and this method
    *           will add the result of the check to the group with out logging a point.
    *           <p>
    *           If an outside method is not going to log the check then a <b>null </b> reference
    *           should be passed and this method will log the test point.
    * @param milliseconds
    * @return last value observed. Either value expected or value found at timeout.
    * @throws InterruptedException
    */
   public T checkMaintain(ITestAccessor accessor, CheckGroup checkGroup, T enumeration, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return null;
   }

   @Override
   /**
    * Verifies that the element is set to a value other than the "value" passed for the entire time
    * passed into "milliseconds". Returns value found that caused failure or last value observed if
    * time expires.
    * 
    * @param enumeration
    * @param checkGroup If this check is part of a larger set of checks which another method is
    *           going to log then the reference to the CheckGroup must be passed and this method
    *           will add the result of the check to the group with out logging a point.
    *           <p>
    *           If an outside method is not going to log the check then a <b>null </b> reference
    *           should be passed and this method will log the test point.
    * @param milliseconds
    * @return last value observed
    * @throws InterruptedException
    */
   public T checkMaintainNot(ITestAccessor accessor, CheckGroup checkGroup, T enumeration, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return null;
   }

   /**
    * This function will verify that this signal is pulsed for 2 cycles.
    * 
    * @param accessor
    * @param value The value to be checked
    * @throws InterruptedException
    */
   public void checkPulse(ITestAccessor accessor, T value) throws InterruptedException {
      throwNoMappingElementException();
   }

   /**
    * @param accessor
    * @param checkGroup
    * @param pulsedValue
    * @param nonPulsedValue
    * @param milliseconds
    * @throws InterruptedException
    */
   public boolean checkPulse(ITestAccessor accessor, CheckGroup checkGroup, T pulsedValue, T nonPulsedValue, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return false;
   }

   protected T toEnum(int intValue) {
      throwNoMappingElementException();
      return null;
   }

   public T[] getEnumValues() {
      throwNoMappingElementException();
      return null;
   }

   @Override
   public void parseAndSet(ITestEnvironmentAccessor accessor, String value) throws IllegalArgumentException {
      throwNoMappingElementException();
   }

   @Override
   public boolean isNonMappingElement() {
      return true;
   }

   @Override
   public void setbyEnumIndex(int index) throws IllegalArgumentException {
      throwNoMappingElementException();
   }

   @Override
   public void setValue(T obj) {
      throwNoMappingElementException();
   }

   @Override
   public String toString(T obj) {
      throwNoMappingElementException();
      return super.toString(obj);
   }

   @Override
   public String valueOf() {
      throwNoMappingElementException();
      return super.valueOf();
   }

   @Override
   public T valueOf(MemoryResource otherMem) {
      throwNoMappingElementException();
      return super.valueOf(otherMem);
   }

}
