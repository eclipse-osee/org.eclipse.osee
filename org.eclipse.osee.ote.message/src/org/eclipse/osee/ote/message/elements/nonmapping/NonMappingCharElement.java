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
import org.eclipse.osee.ote.message.elements.CharElement;
import org.eclipse.osee.ote.message.interfaces.ITestAccessor;

/**
 * @author Andy Jury
 */
public class NonMappingCharElement extends CharElement {

   public NonMappingCharElement(CharElement element) {
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
   public NonMappingCharElement(Message<?, ?, ?> message, String elementName, MessageData messageData, int byteOffset, int msb, int lsb) {
      super(message, elementName, messageData, byteOffset, msb, lsb);
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
   public NonMappingCharElement(Message<?, ?, ?> message, String elementName, MessageData messageData, int byteOffset, int msb, int lsb, int originalLsb, int originalMsb) {
      super(message, elementName, messageData, byteOffset, msb, lsb, originalLsb, originalMsb);
   }

   /**
    * @param message
    * @param elementName
    * @param messageData
    * @param bitOffset
    * @param bitLength
    */
   public NonMappingCharElement(Message<?, ?, ?> message, String elementName, MessageData messageData, int bitOffset, int bitLength) {
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
   public void checkForwarding(ITestAccessor accessor, CharElement cause, char value) throws InterruptedException {

      throwNoMappingElementException();
   }

   /**
    * Verifies that the element is set to "value".
    * 
    * @param accessor
    * @param value Expected value
    * @return if the check passed
    */
   public boolean check(ITestAccessor accessor, char value) {
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
   public boolean check(ITestAccessor accessor, CheckGroup checkGroup, char value) {
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
   public boolean checkRange(ITestAccessor accessor, char minValue, char maxValue) {
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
   public boolean checkRange(ITestAccessor accessor, CheckGroup checkGroup, char minValue, char maxValue) {
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
   public boolean checkRange(ITestAccessor accessor, char minValue, boolean minInclusive, char maxValue, boolean maxInclusive) {
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
   public boolean checkRange(ITestAccessor accessor, CheckGroup checkGroup, char minValue, boolean minInclusive, char maxValue, boolean maxInclusive) {
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
   public boolean checkNot(ITestAccessor accessor, char value) {
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
   public boolean checkNot(ITestAccessor accessor, CheckGroup checkGroup, char value) {
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
   public boolean checkNotRange(ITestAccessor accessor, char minValue, char maxValue) {
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
   public boolean checkNotRange(ITestAccessor accessor, CheckGroup checkGroup, char minValue, char maxValue) {
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
   public boolean checkNotRange(ITestAccessor accessor, char minValue, boolean minInclusive, char maxValue, boolean maxInclusive) {
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
   public boolean checkNotRange(ITestAccessor accessor, CheckGroup checkGroup, char minValue, boolean minInclusive, char maxValue, boolean maxInclusive) {
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
   public boolean check(ITestAccessor accessor, char value, int milliseconds) throws InterruptedException {
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
   public boolean check(ITestAccessor accessor, CheckGroup checkGroup, char value, int milliseconds) throws InterruptedException {
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
    * @param milliseconds Number of milliseconds to wait for the element to be within the range.
    * @return if the check passed
    * @throws InterruptedException
    */
   public boolean checkRange(ITestAccessor accessor, char minValue, char maxValue, int milliseconds) throws InterruptedException {
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
   public boolean checkRange(ITestAccessor accessor, CheckGroup checkGroup, char minValue, char maxValue, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return false;
   }

   /**
    * Verifies that the element is set to a value within the range specified. Either end of the range can be set to be
    * inclusive or not.
    * 
    * @param accessor
    * @param minValue The minimum value of the range.
    * @param minInclusive If the minimum value of the range is inclusive.
    * @param maxValue The maximum value of the range.
    * @param maxInclusive If the maximum value of the range is inclusive.
    * @param milliseconds Number of milliseconds to wait for the element to be within the range.
    * @return if the check passed
    * @throws InterruptedException
    */
   public boolean checkRange(ITestAccessor accessor, char minValue, boolean minInclusive, char maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
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
   public boolean checkRange(ITestAccessor accessor, CheckGroup checkGroup, char minValue, boolean minInclusive, char maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
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
   public boolean checkNot(ITestAccessor accessor, char value, int milliseconds) throws InterruptedException {
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
   public boolean checkNot(ITestAccessor accessor, CheckGroup checkGroup, char value, int milliseconds) throws InterruptedException {
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
   public boolean checkNotRange(ITestAccessor accessor, char minValue, char maxValue, int milliseconds) throws InterruptedException {
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
   public boolean checkNotRange(ITestAccessor accessor, CheckGroup checkGroup, char minValue, char maxValue, int milliseconds) throws InterruptedException {
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
   public boolean checkNotRange(ITestAccessor accessor, char minValue, boolean minInclusive, char maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
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
   public boolean checkNotRange(ITestAccessor accessor, CheckGroup checkGroup, char minValue, boolean minInclusive, char maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return false;
   }

   /**
    * Verifies that the element is Not set to "value" within the number of "milliseconds" passed.
    * 
    * @param accessor
    * @param value Expected value.
    * @param milliseconds Number of milliseconds to wait for the element to equal the "value".
    * @return If the check passed.
    * @throws InterruptedException
    */
   public boolean checkNot(ITestAccessor accessor, String value, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return false;
   }

   /**
    * Verifies that the element is Not set to "value" within the number of "milliseconds" passed.
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
   public boolean checkNot(ITestAccessor accessor, CheckGroup checkGroup, String value, int milliseconds) throws InterruptedException {

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
   public boolean check(ITestAccessor accessor, String value, int milliseconds) throws InterruptedException {
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
   public boolean check(ITestAccessor accessor, CheckGroup checkGroup, String value, int milliseconds) throws InterruptedException {

      throwNoMappingElementException();
      return false;
   }

   /**
    * Verifies that the string starting at the element is not set to "value".
    * 
    * @param accessor
    * @param value Expected value
    * @return if the check passed
    */
   public boolean checkNot(ITestAccessor accessor, String value) {
      throwNoMappingElementException();
      return false;
   }

   /**
    * Verifies that the string starting at the element is not set to "value".
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
   public boolean checkNot(ITestAccessor accessor, CheckGroup checkGroup, String value) {

      throwNoMappingElementException();
      return false;
   }

   /**
    * Verifies that the string starting at the element is set to "value".
    * 
    * @param accessor
    * @param value Expected value
    * @return if the check passed
    */
   public boolean check(ITestAccessor accessor, String value) {
      throwNoMappingElementException();
      return false;
   }

   /**
    * Verifies that the string starting at the element is set to "value".
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
   public boolean check(ITestAccessor accessor, CheckGroup checkGroup, String value) {

      throwNoMappingElementException();
      return false;
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
   public char checkMaintain(ITestAccessor accessor, CheckGroup checkGroup, char value, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return ' ';
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
   public char checkMaintainNot(ITestAccessor accessor, CheckGroup checkGroup, char value, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return ' ';
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
   public char checkMaintainRange(ITestAccessor accessor, CheckGroup checkGroup, char minValue, char maxValue, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return ' ';
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
   public char checkMaintainRange(ITestAccessor accessor, CheckGroup checkGroup, char minValue, boolean minInclusive, char maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return ' ';
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
   public char checkMaintainNotRange(ITestAccessor accessor, CheckGroup checkGroup, char minValue, char maxValue, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return ' ';
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
   public char checkMaintainNotRange(ITestAccessor accessor, CheckGroup checkGroup, char minValue, boolean minInclusive, char maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return ' ';
   }

   public Character get(ITestEnvironmentAccessor accessor) {
      throwNoMappingElementException();
      return ' ';
   }

   /**
    * Returns the string of length "stringLength" starting as the position of the element.
    * 
    * @param accessor
    * @param stringLength the length of the string to return
    * @return the string starting with this element
    */
   public String getString(ITestEnvironmentAccessor accessor, int stringLength) {
      throwNoMappingElementException();
      return null;
   }

   /**
    * Sets the element and the next ("value".length() -1) bytes to "value".charAt().
    * 
    * @param accessor
    * @param value the string to set the bytes to
    */
   public void parseAndSet(ITestEnvironmentAccessor accessor, String value) {

      throwNoMappingElementException();
   }

   /**
    * Sets the element and the next ("value".length() -1) bytes to "value".charAt(). <b>No Log Record gets created in
    * the Script Log File.</b>
    * 
    * @param accessor
    * @param value the string to set the bytes to
    */
   public void setNoLog(ITestEnvironmentAccessor accessor, String value) {
      throwNoMappingElementException();
   }

   /**
    * Sets the element to the "value" passed.
    * 
    * @param accessor
    * @param value The value to set.
    */
   public void set(ITestEnvironmentAccessor accessor, char value) {
      throwNoMappingElementException();
   }

   /**
    * Sets the element to the "value" passed and immediately sends the message that contains it.
    * 
    * @param accessor
    * @param value The value to set.
    */
   public void setAndSend(ITestEnvironmentAccessor accessor, char value) {
      throwNoMappingElementException();
   }

   /**
    * Sets the element to the "value" passed. <b>No Log Record gets created in the Script Log File.</b>
    * 
    * @param accessor
    * @param value The value to set.
    */
   public void setNoLog(ITestEnvironmentAccessor accessor, char value) {
      throwNoMappingElementException();
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
   public char waitForNotRange(ITestEnvironmentAccessor accessor, char minValue, boolean minInclusive, char maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return ' ';
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
   public char waitForNotRange(ITestEnvironmentAccessor accessor, char minValue, char maxValue, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return ' ';
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
   public char waitForRange(ITestEnvironmentAccessor accessor, char minValue, boolean minInclusive, char maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return ' ';
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
   public char waitForRange(ITestEnvironmentAccessor accessor, char minValue, char maxValue, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return ' ';
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
   public char waitNotValue(ITestEnvironmentAccessor accessor, char value, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return ' ';
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
   public char waitForValue(ITestEnvironmentAccessor accessor, char value, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return ' ';
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
   protected String waitForValue(ITestEnvironmentAccessor accessor, String value, int milliseconds) throws InterruptedException {

      throwNoMappingElementException();
      return null;
   }

   /**
    * Waits until the element does not equal the "value" passed. Returns last value observed upon a timout.
    * 
    * @param accessor
    * @param value The expected value to wait for.
    * @param milliseconds Number of milliseconds to wait before failing.
    * @return last value found. Either value expected or value found at timeout.
    * @throws InterruptedException
    */
   protected String waitForNotValue(ITestEnvironmentAccessor accessor, String value, int milliseconds) throws InterruptedException {

      throwNoMappingElementException();
      return null;
   }

   @Override
   public void setValue(Character value) {

      throwNoMappingElementException();
   }

   @Override
   public Character getValue() {
      throwNoMappingElementException();
      return ' ';
   }

   @Override
   public String toString(Character obj) {
      throwNoMappingElementException();
      return null;
   }

   @Override
   public boolean isNonMappingElement() {
      return true;
   }

}
