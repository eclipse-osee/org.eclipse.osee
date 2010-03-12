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
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.elements.IntegerElement;
import org.eclipse.osee.ote.message.interfaces.ITestAccessor;

/**
 * @author Andy Jury
 */
public class NonMappingIntegerElement extends IntegerElement {

   /**
    * Copy constructor.
    * 
    * @param element
    */
   public NonMappingIntegerElement(IntegerElement element) {
      super(null, element.getElementName(), element.getMsgData(), element.getByteOffset(), element.getMsb(),
            element.getLsb());
      // This is being done so it doesn't get added to the element list hash map.
      this.msg = new WeakReference<Message<?, ?, ?>>(element.getMessage());
      for (Object obj : element.getElementPath()) {
         this.getElementPath().add(obj);
      }
   }

   /**
    * @param message
    * @param elementName
    * @param messageData
    * @param byteOffset
    * @param msb
    * @param lsb
    */
   public NonMappingIntegerElement(Message<?, ?, ?> message, String elementName, MessageData messageData, int byteOffset, int msb, int lsb) {
      super(message, elementName, messageData, byteOffset, msb, lsb);
   }

   /**
    * @param message
    * @param elementName
    * @param messageData
    * @param bitOffset
    * @param bitLength
    */
   public NonMappingIntegerElement(Message<?, ?, ?> message, String elementName, MessageData messageData, int bitOffset, int bitLength) {
      super(message, elementName, messageData, bitOffset, bitLength);
   }

   /**
    * @param message
    * @param elementName
    * @param messageData
    * @param byteOffset
    * @param msb
    * @param lsb
    * @param originalLsb
    * @param originalMsb
    */
   public NonMappingIntegerElement(Message<?, ?, ?> message, String elementName, MessageData messageData, int byteOffset, int msb, int lsb, int originalLsb, int originalMsb) {
      super(message, elementName, messageData, byteOffset, msb, lsb, originalLsb, originalMsb);
   }

   @Override
   public String toString(Integer obj) {
      throwNoMappingElementException();
      return null;
   }

   @Override
   public void setValue(Integer value) {
      throwNoMappingElementException();
   }

   @Override
   public Integer getValue() {
      throwNoMappingElementException();
      return 0;
   }

   /**
    * Checks that this element correctly forwards a message sent from cause with the value passed.
    * 
    * @param accessor
    * @param cause The originator of the signal
    * @param value The value sent by cause and being forwarded by this element
    * @throws InterruptedException
    */
   public void checkForwarding(ITestAccessor accessor, IntegerElement cause, int value) throws InterruptedException {

      throwNoMappingElementException();
   }

   /**
    * Verifies that the element is set to "value".
    * 
    * @param accessor
    * @param value Expected value
    * @return if the check passed
    */
   public boolean check(ITestAccessor accessor, int value) {
      throwNoMappingElementException();
      return false;
   }

   /**
    * Verifies that the element is set to "value".
    * 
    * @param accessor
    * @param checkGroup If this check is part of a larger set of checks which another method is going to log then the
    *           reference to the CheckGroup must be passed and this method will add the result of the check to the group
    *           with out logging a point.
    *           <p>
    *           If an outside method is not going to log the check then a <b>null </b> reference should be passed and
    *           this method will log the test point.
    * @param value Expected value
    * @return if the check passed
    */
   public boolean check(ITestAccessor accessor, CheckGroup checkGroup, int value) {
      throwNoMappingElementException();
      return false;
   }

   /**
    * Verifies that the element is set to a value within the range specified. Assumes that both ends of the range are
    * inclusive.
    * 
    * @param accessor
    * @param minValue The minimum value of the range.
    * @param maxValue The maximum value of the range.
    * @return if the check passed
    */
   public boolean checkRange(ITestAccessor accessor, int minValue, int maxValue) {
      throwNoMappingElementException();
      return false;
   }

   /**
    * Verifies that the element is set to a value within the range specified. Assumes that both ends of the range are
    * inclusive.
    * 
    * @param accessor
    * @param checkGroup If this check is part of a larger set of checks which another method is going to log then the
    *           reference to the CheckGroup must be passed and this method will add the result of the check to the group
    *           with out logging a point.
    *           <p>
    *           If an outside method is not going to log the check then a <b>null </b> reference should be passed and
    *           this method will log the test point.
    * @param minValue The minimum value of the range.
    * @param maxValue The maximum value of the range.
    * @return if the check passed
    */
   public boolean checkRange(ITestAccessor accessor, CheckGroup checkGroup, int minValue, int maxValue) {
      throwNoMappingElementException();
      return false;
   }

   /**
    * Verifies that the element is set to a value within the range specified. Either end of the range can be set to be
    * inclusive or not.
    * 
    * @param accessor
    * @param minValue The minimum value of the range.
    * @param minInclusive If the minumum value of the range is inclusive.
    * @param maxValue The maximum value of the range.
    * @param maxInclusive If the maximum value of the range is inclusive.
    * @return if the check passed
    */
   public boolean checkRange(ITestAccessor accessor, int minValue, boolean minInclusive, int maxValue, boolean maxInclusive) {
      throwNoMappingElementException();
      return false;
   }

   /**
    * Verifies that the element is set to a value within the range specified. Either end of the range can be set to be
    * inclusive or not.
    * 
    * @param accessor
    * @param checkGroup If this check is part of a larger set of checks which another method is going to log then the
    *           reference to the CheckGroup must be passed and this method will add the result of the check to the group
    *           with out logging a point.
    *           <p>
    *           If an outside method is not going to log the check then a <b>null </b> reference should be passed and
    *           this method will log the test point.
    * @param minValue The minimum value of the range.
    * @param minInclusive If the minumum value of the range is inclusive.
    * @param maxValue The maximum value of the range.
    * @param maxInclusive If the maximum value of the range is inclusive.
    * @return if the check passed
    */
   public boolean checkRange(ITestAccessor accessor, CheckGroup checkGroup, int minValue, boolean minInclusive, int maxValue, boolean maxInclusive) {
      throwNoMappingElementException();
      return false;
   }

   /**
    * Verifies that the element is NOT set to "value".
    * 
    * @param accessor
    * @param value value to test against
    * @return if the check passed
    */
   public boolean checkNot(ITestAccessor accessor, int value) {
      throwNoMappingElementException();
      return false;
   }

   /**
    * Verifies that the element is NOT set to "value".
    * 
    * @param accessor
    * @param checkGroup If this check is part of a larger set of checks which another method is going to log then the
    *           reference to the CheckGroup must be passed and this method will add the result of the check to the group
    *           with out logging a point.
    *           <p>
    *           If an outside method is not going to log the check then a <b>null </b> reference should be passed and
    *           this method will log the test point.
    * @param value value to test against
    * @return if the check passed
    */
   public boolean checkNot(ITestAccessor accessor, CheckGroup checkGroup, int value) {
      throwNoMappingElementException();
      return false;
   }

   /**
    * Verifies that the element is set to a value outside the range specified. Assumes that both ends of the range are
    * inclusive. Therefore observed value may not equal either of the range values.
    * 
    * @param accessor
    * @param minValue The minimum value of the range.
    * @param maxValue The maximum value of the range.
    * @return if the check passed
    */
   public boolean checkNotRange(ITestAccessor accessor, int minValue, int maxValue) {
      throwNoMappingElementException();
      return false;
   }

   /**
    * Verifies that the element is set to a value outside the range specified. Assumes that both ends of the range are
    * inclusive. Therefore observed value may not equal either of the range values.
    * 
    * @param accessor
    * @param checkGroup If this check is part of a larger set of checks which another method is going to log then the
    *           reference to the CheckGroup must be passed and this method will add the result of the check to the group
    *           with out logging a point.
    *           <p>
    *           If an outside method is not going to log the check then a <b>null </b> reference should be passed and
    *           this method will log the test point.
    * @param minValue The minimum value of the range.
    * @param maxValue The maximum value of the range.
    * @return if the check passed
    */
   public boolean checkNotRange(ITestAccessor accessor, CheckGroup checkGroup, int minValue, int maxValue) {
      throwNoMappingElementException();
      return false;
   }

   /**
    * Verifies that the element is set to a value outside the range specified. Either end of the range can be set to be
    * inclusive or not.
    * 
    * @param accessor
    * @param minValue The minimum value of the range.
    * @param minInclusive If the minumum value of the range is inclusive. If true the actual value must not < and not =
    *           to the range value.
    * @param maxValue The maximum value of the range.
    * @param maxInclusive If the maximum value of the range is inclusive. If true the actual value must not > and not =
    *           to the range value.
    * @return if the check passed
    */
   public boolean checkNotRange(ITestAccessor accessor, int minValue, boolean minInclusive, int maxValue, boolean maxInclusive) {
      throwNoMappingElementException();
      return false;
   }

   /**
    * Verifies that the element is set to a value outside the range specified. Either end of the range can be set to be
    * inclusive or not.
    * 
    * @param accessor
    * @param checkGroup If this check is part of a larger set of checks which another method is going to log then the
    *           reference to the CheckGroup must be passed and this method will add the result of the check to the group
    *           with out logging a point.
    *           <p>
    *           If an outside method is not going to log the check then a <b>null </b> reference should be passed and
    *           this method will log the test point.
    * @param minValue The minimum value of the range.
    * @param minInclusive If the minumum value of the range is inclusive. If true the actual value must not < and not =
    *           to the range value.
    * @param maxValue The maximum value of the range.
    * @param maxInclusive If the maximum value of the range is inclusive. If true the actual value must not > and not =
    *           to the range value.
    * @return if the check passed
    */
   public boolean checkNotRange(ITestAccessor accessor, CheckGroup checkGroup, int minValue, boolean minInclusive, int maxValue, boolean maxInclusive) {
      throwNoMappingElementException();
      return false;
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
   public boolean check(ITestAccessor accessor, int value, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return false;
   }

   /**
    * Verifies that the element is set to "value" within the number of "milliseconds" passed.
    * 
    * @param accessor
    * @param checkGroup If this check is part of a larger set of checks which another method is going to log then the
    *           reference to the CheckGroup must be passed and this method will add the result of the check to the group
    *           with out logging a point.
    *           <p>
    *           If an outside method is not going to log the check then a <b>null </b> reference should be passed and
    *           this method will log the test point.
    * @param value Expected value.
    * @param milliseconds Number of milliseconds to wait for the element to equal the "value".
    * @return If the check passed.
    * @throws InterruptedException
    */
   public boolean check(ITestAccessor accessor, CheckGroup checkGroup, int value, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return false;
   }

   /**
    * This function will verify that this signal is pulsed for 2 cycles.
    * 
    * @param accessor
    * @param value The value to be checked
    * @throws InterruptedException
    */
   public void checkPulse(ITestAccessor accessor, int value) throws InterruptedException {

      throwNoMappingElementException();
   }

   /**
    * @param accessor
    * @param pulsedValue
    * @param nonPulsedValue
    * @throws InterruptedException
    */
   public void checkPulse(ITestAccessor accessor, int pulsedValue, int nonPulsedValue) throws InterruptedException {
      throwNoMappingElementException();
   }

   /**
    * @param accessor
    * @param checkGroup
    * @param pulsedValue
    * @param nonPulsedValue
    * @throws InterruptedException
    */
   public void checkPulse(ITestAccessor accessor, CheckGroup checkGroup, int pulsedValue, int nonPulsedValue) throws InterruptedException {
      throwNoMappingElementException();
   }

   public void checkPulse(ITestAccessor accessor, int pulsedValue, int nonPulsedValue, int milliseconds) throws InterruptedException {
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
   public void checkPulse(ITestAccessor accessor, CheckGroup checkGroup, int pulsedValue, int nonPulsedValue, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
   }

   /**
    * Verifies that the element is set to a value within the range specified. Assumes that both ends of the range are
    * inclusive.
    * 
    * @param accessor
    * @param minValue The minimum value of the range.
    * @param maxValue The maximum value of the range.
    * @param milliseconds Number of milliseconds to wait for the element to be within the range.
    * @return if the check passed
    * @throws InterruptedException
    */
   public boolean checkRange(ITestAccessor accessor, int minValue, int maxValue, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return false;
   }

   /**
    * Verifies that the element is set to a value within the range specified. Assumes that both ends of the range are
    * inclusive.
    * 
    * @param accessor
    * @param checkGroup If this check is part of a larger set of checks which another method is going to log then the
    *           reference to the CheckGroup must be passed and this method will add the result of the check to the group
    *           with out logging a point.
    *           <p>
    *           If an outside method is not going to log the check then a <b>null </b> reference should be passed and
    *           this method will log the test point.
    * @param minValue The minimum value of the range.
    * @param maxValue The maximum value of the range.
    * @param milliseconds Number of milliseconds to wait for the element to be within the range.
    * @return if the check passed
    * @throws InterruptedException
    */
   public boolean checkRange(ITestAccessor accessor, CheckGroup checkGroup, int minValue, int maxValue, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return false;
   }

   /**
    * Verifies that the element is set to a value within the range specified. Either end of the range can be set to be
    * inclusive or not.
    * 
    * @param accessor
    * @param minValue The minimum value of the range.
    * @param minInclusive If the minumum value of the range is inclusive.
    * @param maxValue The maximum value of the range.
    * @param maxInclusive If the maximum value of the range is inclusive.
    * @param milliseconds Number of milliseconds to wait for the element to be within the range.
    * @return if the check passed
    * @throws InterruptedException
    */
   public boolean checkRange(ITestAccessor accessor, int minValue, boolean minInclusive, int maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return false;
   }

   /**
    * Verifies that the element is set to a value within the range specified. Either end of the range can be set to be
    * inclusive or not.
    * 
    * @param accessor
    * @param checkGroup If this check is part of a larger set of checks which another method is going to log then the
    *           reference to the CheckGroup must be passed and this method will add the result of the check to the group
    *           with out logging a point.
    *           <p>
    *           If an outside method is not going to log the check then a <b>null </b> reference should be passed and
    *           this method will log the test point.
    * @param minValue The minimum value of the range.
    * @param minInclusive If the minumum value of the range is inclusive.
    * @param maxValue The maximum value of the range.
    * @param maxInclusive If the maximum value of the range is inclusive.
    * @param milliseconds Number of milliseconds to wait for the element to be within the range.
    * @return if the check passed
    * @throws InterruptedException
    */
   public boolean checkRange(ITestAccessor accessor, CheckGroup checkGroup, int minValue, boolean minInclusive, int maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return false;
   }

   /**
    * Verifies that the element is set to some value other than "value" within the number of "milliseconds" passed.
    * Passes if at any point with in the time allowed, the elment is set to a value other than "value".
    * 
    * @param accessor
    * @param value value to test against.
    * @param milliseconds Number of milliseconds to wait for the element to equal the "value".
    * @return If the check passed.
    * @throws InterruptedException
    */
   public boolean checkNot(ITestAccessor accessor, int value, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return false;
   }

   /**
    * Verifies that the element is set to some value other than "value" within the number of "milliseconds" passed.
    * Passes if at any point with in the time allowed, the elment is set to a value other than "value".
    * 
    * @param accessor
    * @param checkGroup If this check is part of a larger set of checks which another method is going to log then the
    *           reference to the CheckGroup must be passed and this method will add the result of the check to the group
    *           with out logging a point.
    *           <p>
    *           If an outside method is not going to log the check then a <b>null </b> reference should be passed and
    *           this method will log the test point.
    * @param value value to test against.
    * @param milliseconds Number of milliseconds to wait for the element to equal the "value".
    * @return If the check passed.
    * @throws InterruptedException
    */
   public boolean checkNot(ITestAccessor accessor, CheckGroup checkGroup, int value, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return false;
   }

   /**
    * Verifies that the element is set to a value outside the range specified. Assumes that both ends of the range are
    * inclusive.
    * 
    * @param accessor
    * @param minValue The minimum value of the range.
    * @param maxValue The maximum value of the range.
    * @param milliseconds Number of milliseconds to wait for the element to be outside the range.
    * @return if the check passed
    * @throws InterruptedException
    */
   public boolean checkNotRange(ITestAccessor accessor, int minValue, int maxValue, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return false;
   }

   /**
    * Verifies that the element is set to a value outside the range specified. Assumes that both ends of the range are
    * inclusive.
    * 
    * @param accessor
    * @param checkGroup If this check is part of a larger set of checks which another method is going to log then the
    *           reference to the CheckGroup must be passed and this method will add the result of the check to the group
    *           with out logging a point.
    *           <p>
    *           If an outside method is not going to log the check then a <b>null </b> reference should be passed and
    *           this method will log the test point.
    * @param minValue The minimum value of the range.
    * @param maxValue The maximum value of the range.
    * @param milliseconds Number of milliseconds to wait for the element to be outside the range.
    * @return if the check passed
    * @throws InterruptedException
    */
   public boolean checkNotRange(ITestAccessor accessor, CheckGroup checkGroup, int minValue, int maxValue, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return false;
   }

   /**
    * Verifies that the element is set to a value outside the range specified. Either end of the range can be set to be
    * inclusive or not.
    * 
    * @param accessor
    * @param minValue The minimum value of the range.
    * @param minInclusive If the minumum value of the range is inclusive. If true the actual value must not < and not =
    *           to the range value.
    * @param maxValue The maximum value of the range.
    * @param maxInclusive If the maximum value of the range is inclusive. If true the actual value must not > and not =
    *           to the range value.
    * @param milliseconds Number of milliseconds to wait for the element to be outside the range.
    * @return if the check passed
    * @throws InterruptedException
    */
   public boolean checkNotRange(ITestAccessor accessor, int minValue, boolean minInclusive, int maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return false;
   }

   /**
    * Verifies that the element is set to a value outside the range specified. Either end of the range can be set to be
    * inclusive or not.
    * 
    * @param accessor
    * @param checkGroup If this check is part of a larger set of checks which another method is going to log then the
    *           reference to the CheckGroup must be passed and this method will add the result of the check to the group
    *           with out logging a point.
    *           <p>
    *           If an outside method is not going to log the check then a <b>null </b> reference should be passed and
    *           this method will log the test point.
    * @param minValue The minimum value of the range.
    * @param minInclusive If the minumum value of the range is inclusive. If true the actual value must not < and not =
    *           to the range value.
    * @param maxValue The maximum value of the range.
    * @param maxInclusive If the maximum value of the range is inclusive. If true the actual value must not > and not =
    *           to the range value.
    * @param milliseconds Number of milliseconds to wait for the element to be outside the range.
    * @return if the check passed
    * @throws InterruptedException
    */
   public boolean checkNotRange(ITestAccessor accessor, CheckGroup checkGroup, int minValue, boolean minInclusive, int maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return false;
   }

   /**
    * Verifies that the element is set to the "value" passed for the entire time passed into "milliseconds". Returns
    * value found that caused failure or last value observed if time expires.
    * 
    * @param accessor
    * @param value
    * @param milliseconds
    * @return last value observed. Either value expected or value found at timeout.
    * @throws InterruptedException
    */
   public int checkMaintain(ITestAccessor accessor, int value, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   /**
    * Verifies that the element is set to the "value" passed for the entire time passed into "milliseconds". Returns
    * value found that caused failure or last value observed if time expires.
    * 
    * @param accessor
    * @param checkGroup If this check is part of a larger set of checks which another method is going to log then the
    *           reference to the CheckGroup must be passed and this method will add the result of the check to the group
    *           with out logging a point.
    *           <p>
    *           If an outside method is not going to log the check then a <b>null </b> reference should be passed and
    *           this method will log the test point.
    * @param value
    * @param milliseconds
    * @return last value observed. Either value expected or value found at timeout.
    * @throws InterruptedException
    */
   public int checkMaintain(ITestAccessor accessor, CheckGroup checkGroup, int value, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   /**
    * Verifies that the element is set to the "value" passed for the entire time passed into "milliseconds". Returns
    * value found that caused failure or last value observed if time expires.
    * 
    * @param accessor
    * @param value
    * @param milliseconds
    * @return last value observed
    * @throws InterruptedException
    */
   public int checkMaintainNot(ITestAccessor accessor, int value, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   /**
    * Verifies that the element is set to the "value" passed for the entire time passed into "milliseconds". Returns
    * value found that caused failure or last value observed if time expires.
    * 
    * @param accessor
    * @param checkGroup If this check is part of a larger set of checks which another method is going to log then the
    *           reference to the CheckGroup must be passed and this method will add the result of the check to the group
    *           with out logging a point.
    *           <p>
    *           If an outside method is not going to log the check then a <b>null </b> reference should be passed and
    *           this method will log the test point.
    * @param value
    * @param milliseconds
    * @return last value observed
    * @throws InterruptedException
    */
   public int checkMaintainNot(ITestAccessor accessor, CheckGroup checkGroup, int value, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   /**
    * Verifies that the element is set to a value within the range specified for the entire time specified. Assumes
    * range is inclusive.
    * 
    * @param accessor
    * @param checkGroup If this check is part of a larger set of checks which another method is going to log then the
    *           reference to the CheckGroup must be passed and this method will add the result of the check to the group
    *           with out logging a point.
    *           <p>
    *           If an outside method is not going to log the check then a <b>null </b> reference should be passed and
    *           this method will log the test point.
    * @param minValue The minimum value of the range.
    * @param maxValue The maximum value of the range.
    * @param milliseconds Number of milliseconds to wait before failing.
    * @return last value observed
    * @throws InterruptedException
    */
   public int checkMaintainRange(ITestAccessor accessor, CheckGroup checkGroup, int minValue, int maxValue, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   /**
    * Verifies that the element is set to a value within the range specified for the entire time specified.
    * 
    * @param accessor
    * @param checkGroup If this check is part of a larger set of checks which another method is going to log then the
    *           reference to the CheckGroup must be passed and this method will add the result of the check to the group
    *           with out logging a point.
    *           <p>
    *           If an outside method is not going to log the check then a <b>null </b> reference should be passed and
    *           this method will log the test point.
    * @param minValue The minimum value of the range.
    * @param minInclusive If the minumum value of the range is inclusive.
    * @param maxValue The maximum value of the range.
    * @param maxInclusive If the maximum value of the range is inclusive.
    * @param milliseconds Number of milliseconds to wait before failing.
    * @return last value observed
    * @throws InterruptedException
    */
   public int checkMaintainRange(ITestAccessor accessor, CheckGroup checkGroup, int minValue, boolean minInclusive, int maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   /**
    * Verifies that the element is set to a value within the range specified for the entire time specified. Assumes
    * range is inclusive.
    * 
    * @param accessor
    * @param minValue The minimum value of the range.
    * @param maxValue The maximum value of the range.
    * @param milliseconds Number of milliseconds to wait before failing.
    * @return last value observed
    * @throws InterruptedException
    */
   public int checkMaintainRange(ITestAccessor accessor, int minValue, int maxValue, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   /**
    * Verifies that the element is set to a value within the range specified for the entire time specified.
    * 
    * @param accessor
    * @param minValue The minimum value of the range.
    * @param minInclusive If the minumum value of the range is inclusive.
    * @param maxValue The maximum value of the range.
    * @param maxInclusive If the maximum value of the range is inclusive.
    * @param milliseconds Number of milliseconds to wait before failing.
    * @return last value observed
    * @throws InterruptedException
    */
   public int checkMaintainRange(ITestAccessor accessor, int minValue, boolean minInclusive, int maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   // /**
   // * Verifies that the element is set to a value within the range specified for the entire time
   // specified. Assumes range is inclusive.
   // *
   // * @param accessor
   // * @param minValue The minimum value of the range.
   // * @param maxValue The maximum value of the range.
   // * @param milliseconds Number of milliseconds to wait before failing.
   // * @return last value observed
   // * @throws InterruptedException
   // */
   // public int checkMaintainRange(ITestAccessor accessor, int minValue, int maxValue, int
   // milliseconds) throws InterruptedException {
   // return checkMaintainRange(accessor, (CheckGroup)null, minValue, true, maxValue, true,
   // milliseconds);}
   //
   // /**
   // * Verifies that the element is set to a value within the range specified for the entire time
   // specified.
   // *
   // * @param accessor
   // * @param minValue The minimum value of the range.
   // * @param minInclusive If the minumum value of the range is inclusive.
   // * @param maxValue The maximum value of the range.
   // * @param maxInclusive If the maximum value of the range is inclusive.
   // * @param milliseconds Number of milliseconds to wait before failing.
   // * @return last value observed
   // * @throws InterruptedException
   // */
   // public int checkMaintainRange(ITestAccessor accessor, int minValue, boolean minInclusive, int
   // maxValue, boolean maxInclusive,
   // int milliseconds) throws InterruptedException {
   // return checkMaintainRangeBase(accessor, (CheckGroup)null, minValue, minInclusive, maxValue,
   // maxInclusive, milliseconds);
   // }

   /**
    * Verifies that the element is set to a value within the range specified for the entire time specified. Assumes
    * range is inclusive.
    * 
    * @param accessor
    * @param checkGroup If this check is part of a larger set of checks which another method is going to log then the
    *           reference to the CheckGroup must be passed and this method will add the result of the check to the group
    *           with out logging a point.
    *           <p>
    *           If an outside method is not going to log the check then a <b>null </b> reference should be passed and
    *           this method will log the test point.
    * @param minValue The minimum value of the range.
    * @param maxValue The maximum value of the range.
    * @param milliseconds Number of milliseconds to wait before failing.
    * @return last value observed
    * @throws InterruptedException
    */
   public int checkMaintainNotRange(ITestAccessor accessor, CheckGroup checkGroup, int minValue, int maxValue, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   /**
    * Verifies that the element is set to a value within the range specified for the entire time specified.
    * 
    * @param accessor
    * @param checkGroup If this check is part of a larger set of checks which another method is going to log then the
    *           reference to the CheckGroup must be passed and this method will add the result of the check to the group
    *           with out logging a point.
    *           <p>
    *           If an outside method is not going to log the check then a <b>null </b> reference should be passed and
    *           this method will log the test point.
    * @param minValue The minimum value of the range.
    * @param minInclusive If the minumum value of the range is inclusive. If true the actual value must not < and not =
    *           to the range value.
    * @param maxValue The maximum value of the range.
    * @param maxInclusive If the maximum value of the range is inclusive. If true the actual value must not > and not =
    *           to the range value.
    * @param milliseconds Number of milliseconds to wait before failing.
    * @return last value observed
    * @throws InterruptedException
    */
   public int checkMaintainNotRange(ITestAccessor accessor, CheckGroup checkGroup, int minValue, boolean minInclusive, int maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   /**
    * Returns the current value of the element.
    * 
    * @return The current value of the element.
    */
   public Integer get(ITestEnvironmentAccessor accessor) {
      throwNoMappingElementException();
      return 0;
   }

   /**
    * Sets the element to the "value" passed.
    * 
    * @param accessor
    * @param value The value to set.
    */
   public void set(ITestEnvironmentAccessor accessor, int value) {
      throwNoMappingElementException();
   }

   @Override
   public void setAndSend(ITestEnvironmentAccessor accessor, int value) {
      throwNoMappingElementException();
   }

   /**
    * Sets the element to the "value" passed.
    * 
    * @param accessor
    * @param value The value to set.
    */
   public void setNoLog(ITestEnvironmentAccessor accessor, int value) {
      throwNoMappingElementException();
   }

   /**
    * Waits until the element equals the "value" passed. Returns last value observed upon a timout.
    * 
    * @param accessor
    * @param value The expected value to wait for.
    * @param milliseconds Number of milliseconds to wait before failing.
    * @return last value found. Either value expected or value found at timeout.
    * @throws InterruptedException
    */
   public int waitForValue(ITestEnvironmentAccessor accessor, int value, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   /**
    * Waits until the element has a value other than the "value" passed. Returns last value observed upon a timout.
    * 
    * @param accessor
    * @param value The expected value to wait for.
    * @param milliseconds Number of milliseconds to wait before failing.
    * @return last value observed
    * @throws InterruptedException
    */
   public int waitForNotValue(ITestEnvironmentAccessor accessor, int value, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   /**
    * Waits until the element has a value within the range specified. Assumes the range is inclusive.
    * 
    * @param accessor
    * @param minValue The minimum value of the range.
    * @param maxValue The maximum value of the range.
    * @param milliseconds Number of milliseconds to wait before failing.
    * @return last value observed
    * @throws InterruptedException
    */
   public int waitForRange(ITestEnvironmentAccessor accessor, int minValue, int maxValue, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   /**
    * Waits until the element has a value within the range specified. Either end of the range can be inclusive or not.
    * 
    * @param accessor
    * @param minValue The minimum value of the range.
    * @param minInclusive If the minumum value of the range is inclusive. If true the actual value must not < and not =
    *           to the range value.
    * @param maxValue The maximum value of the range.
    * @param maxInclusive If the maximum value of the range is inclusive. If true the actual value must not > and not =
    *           to the range value.
    * @param milliseconds Number of milliseconds to wait before failing.
    * @return last value observed
    * @throws InterruptedException
    */
   public int waitForRange(ITestEnvironmentAccessor accessor, int minValue, boolean minInclusive, int maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   /**
    * Waits until the element has a value within the range specified. Range is assumes to be inclusive.
    * 
    * @param accessor
    * @param minValue The minimum value of the range.
    * @param maxValue The maximum value of the range.
    * @param milliseconds Number of milliseconds to wait before failing.
    * @return last value observed
    * @throws InterruptedException
    */
   public int waitForNotRange(ITestEnvironmentAccessor accessor, int minValue, int maxValue, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   /**
    * Waits until the element has a value within the range specified. Either end of the range can be inclusive or not.
    * 
    * @param accessor
    * @param minValue The minimum value of the range.
    * @param minInclusive If the minumum value of the range is inclusive. If true the actual value must not < and not =
    *           to the range value.
    * @param maxValue The maximum value of the range.
    * @param maxInclusive If the maximum value of the range is inclusive. If true the actual value must not > and not =
    *           to the range value.
    * @param milliseconds Number of milliseconds to wait before failing.
    * @return last value observed
    * @throws InterruptedException
    */
   public int waitForNotRange(ITestEnvironmentAccessor accessor, int minValue, boolean minInclusive, int maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   @Override
   public void parseAndSet(ITestEnvironmentAccessor accessor, String value) throws IllegalArgumentException {
      throwNoMappingElementException();
   }

   @Override
   public boolean isNonMappingElement() {
      return true;
   }
}
