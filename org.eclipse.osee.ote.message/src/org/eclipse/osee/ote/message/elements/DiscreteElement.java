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

import org.eclipse.osee.ote.core.MethodFormatter;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor;
import org.eclipse.osee.ote.core.testPoint.CheckGroup;
import org.eclipse.osee.ote.core.testPoint.CheckPoint;
import org.eclipse.osee.ote.core.testPoint.Operation;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.condition.EqualsCondition;
import org.eclipse.osee.ote.message.condition.IDiscreteElementCondition;
import org.eclipse.osee.ote.message.condition.InRangeCondition;
import org.eclipse.osee.ote.message.condition.ListCondition;
import org.eclipse.osee.ote.message.condition.NotInRangeCondition;
import org.eclipse.osee.ote.message.condition.PulseCondition;
import org.eclipse.osee.ote.message.data.MemoryResource;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.interfaces.ITestAccessor;

public abstract class DiscreteElement<T extends Comparable<T>> extends Element {

   private static final String FOR_2_PULSES = " FOR 2 PULSES";

   public DiscreteElement(Message<?, ?, ?> msg, String elementName, MessageData messageData, int byteOffset, int msb, int lsb, int originalMsb, int originalLsb) {
      super(msg, elementName, messageData, byteOffset, msb, lsb, originalMsb, originalLsb);
   }

   public DiscreteElement(Message<?, ?, ?> msg, String elementName, MessageData messageData, int byteOffset, int msb, int lsb) {
      super(msg, elementName, messageData, byteOffset, msb, lsb);
   }

   public DiscreteElement(Message<?, ?, ?> msg, String elementName, MessageData messageData, int bitOffset, int bitLength) {
      super(msg, elementName, messageData, bitOffset, bitLength);
   }

   public abstract void setValue(T obj);

   public abstract T getValue();

   public abstract String toString(T obj);

   public String valueOf() {
      return getValue().toString();
   }

   public abstract void parseAndSet(ITestEnvironmentAccessor accessor, String value) throws IllegalArgumentException;

   /**
    * Sets the element to the "value" passed.
    * 
    * @param accessor Reference to the accessor.
    * @param value The value to set.
    */
   public void set(ITestEnvironmentAccessor accessor, T value) {
      if (accessor != null) {
         accessor.getLogger().methodCalledOnObject(accessor, this.getFullName(), (new MethodFormatter()).add(value),
               this.msg.get());
      }

      setValue(value);

      if (accessor != null) {
         accessor.getLogger().methodEnded(accessor);
      }
   }

   /**
    * Verifies that the element is set to "value".
    * 
    * @param accessor Reference to the accessor.
    * @param checkGroup If this check is part of a larger set of checks which another method is going to log then the
    *           reference to the CheckGroup must be passed and this method will add the result of the check to the group
    *           with out logging a point.
    *           <p>
    *           If an outside method is not going to log the check then a <b>null </b> reference should be passed and
    *           this method will log the test point.
    * @param value Expected value
    * @return if the check passed
    */
   public boolean check(ITestAccessor accessor, CheckGroup checkGroup, T value) {

      if (accessor != null) {
         accessor.getLogger().methodCalledOnObject(accessor, this.getFullName(), (new MethodFormatter()).add(value),
               this.msg.get());
      }

      T actualValue = getValue();
      CheckPoint passFail =
            new CheckPoint(this.getFullName(), toString(value), toString(actualValue),
                  actualValue.equals(elementMask(value)), 0);

      if (checkGroup == null)
         accessor.getLogger().testpoint(accessor, accessor.getTestScript(), accessor.getTestCase(), passFail);
      else
         checkGroup.add(passFail);

      if (accessor != null) {
         accessor.getLogger().methodEnded(accessor);
      }
      return passFail.isPass();
   }

   public boolean checkNT(ITestAccessor accessor, CheckGroup checkGroup, T value) {
      accessor.getLogger().methodCalledOnObject(accessor, this.getFullName(), (new MethodFormatter()).add(value),
            this.msg.get());
      boolean v = getValue().equals(elementMask(value));
      accessor.getLogger().methodEnded(accessor);
      return v;
   }

   public boolean checkNotNT(ITestAccessor accessor, CheckGroup checkGroup, T value) {
      accessor.getLogger().methodCalledOnObject(accessor, this.getFullName(), (new MethodFormatter()).add(value),
            this.msg.get());
      boolean v = !getValue().equals(elementMask(value));
      accessor.getLogger().methodEnded(accessor);
      return v;
   }

   public final boolean check(ITestAccessor accessor, T value) {
      return this.check(accessor, (CheckGroup) null, value);
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
   public final boolean check(ITestAccessor accessor, T value, int milliseconds) throws InterruptedException {
      return check(accessor, (CheckGroup) null, value, milliseconds);
   }

   /**
    * Verifies that the element is set to a value within the range specified. Either end of the range can be set to be
    * inclusive or not.
    * 
    * @param accessor Reference to the accessor.
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
   public boolean checkRange(ITestAccessor accessor, CheckGroup checkGroup, T minValue, boolean minInclusive, T maxValue, boolean maxInclusive) {
      checkAccessor(accessor);
      accessor.getLogger().methodCalledOnObject(accessor, this.getFullName(),
            (new MethodFormatter()).add(minValue).add(minInclusive).add(maxValue).add(maxInclusive), this.msg.get());

      InRangeCondition<T> c = new InRangeCondition<T>(this, minValue, minInclusive, maxValue, maxInclusive);

      boolean pass = c.check();
      CheckPoint passFail =
            new CheckPoint(this.getFullName(), "In " + expectedRangeString(toString(minValue).toString(), minInclusive,
                  toString(maxValue), maxInclusive), toString(c.getLastCheckValue()), pass, 0);

      if (checkGroup == null)
         accessor.getLogger().testpoint(accessor, accessor.getTestScript(), accessor.getTestCase(), passFail);
      else
         checkGroup.add(passFail);
      accessor.getLogger().methodEnded(accessor);
      return passFail.isPass();
   }

   public boolean checkRangeNT(ITestAccessor accessor, T minValue, boolean minInclusive, T maxValue, boolean maxInclusive) {
      checkAccessor(accessor);
      accessor.getLogger().methodCalledOnObject(accessor, this.getFullName(),
            (new MethodFormatter()).add(minValue).add(minInclusive).add(maxValue).add(maxInclusive), this.msg.get());
      InRangeCondition<T> c = new InRangeCondition<T>(this, minValue, minInclusive, maxValue, maxInclusive);
      boolean pass = c.check();
      accessor.getLogger().methodEnded(accessor);
      return pass;
   }

   public boolean checkRangeNT(ITestAccessor accessor, T minValue, boolean minInclusive, T maxValue, boolean maxInclusive, int millis) throws InterruptedException {
      checkAccessor(accessor);
      accessor.getLogger().methodCalledOnObject(accessor, this.getFullName(),
            (new MethodFormatter()).add(minValue).add(minInclusive).add(maxValue).add(maxInclusive), this.msg.get());
      InRangeCondition<T> c = new InRangeCondition<T>(this, minValue, minInclusive, maxValue, maxInclusive);
      MsgWaitResult result = msg.get().waitForCondition(accessor, c, false, millis);
      accessor.getLogger().methodEnded(accessor);
      return result.isPassed();
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
   public final boolean checkRange(ITestAccessor accessor, CheckGroup checkGroup, T minValue, T maxValue) {
      return checkRange(accessor, checkGroup, minValue, true, maxValue, true);
   }

   /**
    * Verifies that the element is NOT set to "value".
    * 
    * @param accessor Reference to the accessor.
    * @param checkGroup If this check is part of a larger set of checks which another method is going to log then the
    *           reference to the CheckGroup must be passed and this method will add the result of the check to the group
    *           with out logging a point.
    *           <p>
    *           If an outside method is not going to log the check then a <b>null </b> reference should be passed and
    *           this method will log the test point.
    * @param value value to test against
    * @return if the check passed
    */
   public boolean checkNot(ITestAccessor accessor, CheckGroup checkGroup, T value) {
      if (accessor != null) {
         accessor.getLogger().methodCalledOnObject(accessor, this.getFullName(), (new MethodFormatter()).add(value),
               this.msg.get());
      }

      T actualValue = getValue();

      CheckPoint passFail =
            new CheckPoint(this.getFullName(), "Not " + toString(value), toString(actualValue),
                  !actualValue.equals(value), 0);

      if (checkGroup == null)
         accessor.getLogger().testpoint(accessor, accessor.getTestScript(), accessor.getTestCase(), passFail);
      else
         checkGroup.add(passFail);

      if (accessor != null) {
         accessor.getLogger().methodEnded(accessor);
      }
      return passFail.isPass();
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
   public final T checkMaintainRange(ITestAccessor accessor, T minValue, T maxValue, int milliseconds) throws InterruptedException {
      return checkMaintainRange(accessor, null, minValue, true, maxValue, true, milliseconds);
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
   public final T checkMaintainRange(ITestAccessor accessor, T minValue, boolean minInclusive, T maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      return checkMaintainRange(accessor, (CheckGroup) null, minValue, minInclusive, maxValue, maxInclusive,
            milliseconds);
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
   public final T checkMaintainNotRange(ITestAccessor accessor, T minValue, T maxValue, int milliseconds) throws InterruptedException {
      return checkMaintainNotRange(accessor, null, minValue, true, maxValue, true, milliseconds);
   }

   /**
    * Verifies that the element is not set to a value within the range specified for the entire time specified.
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
   public final T checkMaintainNotRange(ITestAccessor accessor, T minValue, boolean minInclusive, T maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      return checkMaintainNotRange(accessor, (CheckGroup) null, minValue, minInclusive, maxValue, maxInclusive,
            milliseconds);
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
   public final T checkMaintainRange(ITestAccessor accessor, CheckGroup checkGroup, T minValue, T maxValue, int milliseconds) throws InterruptedException {
      return checkMaintainRange(accessor, checkGroup, minValue, true, maxValue, true, milliseconds);
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
   public final boolean checkNotRange(ITestAccessor accessor, T minValue, boolean minInclusive, T maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      return checkNotRange(accessor, (CheckGroup) null, minValue, minInclusive, maxValue, maxInclusive, milliseconds);
   }

   /**
    * Verifies that the element is set to a value outside the range specified. Either end of the range can be set to be
    * inclusive or not.
    * 
    * @param accessor Reference to the accessor.
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
   public final boolean checkNotRange(ITestAccessor accessor, CheckGroup checkGroup, T minValue, boolean minInclusive, T maxValue, boolean maxInclusive) throws InterruptedException {
      return checkNotRange(accessor, (CheckGroup) null, minValue, minInclusive, maxValue, maxInclusive, 0);
   }

   /**
    * Waits until the element equals the "value" passed. Returns last value observed upon a time out.
    * 
    * @param accessor Reference to the accessor.
    * @param value The expected value to wait for.
    * @param milliseconds Number of milliseconds to wait before failing.
    * @return last value found. Either value expected or value found at timeout.
    * @throws InterruptedException
    */
   public T waitForValue(ITestEnvironmentAccessor accessor, T value, int milliseconds) throws InterruptedException {
      if (accessor != null) {
         accessor.getLogger().methodCalledOnObject(accessor, this.getFullName(),
               (new MethodFormatter()).add(value).add(milliseconds), this.msg.get());
      }
      EqualsCondition<T> c = new EqualsCondition<T>(this, value);
      msg.get().waitForCondition(accessor, c, false, milliseconds);
      if (accessor != null) {
         accessor.getLogger().methodEnded(accessor);
      }
      return c.getLastCheckValue();
   }

   /**
    * Waits until the element has a value other than the "value" passed. Returns last value observed upon a time out.
    * 
    * @param accessor Reference to the accessor.
    * @param value The expected value to wait for.
    * @param milliseconds Number of milliseconds to wait before failing.
    * @return last value observed
    * @throws InterruptedException
    */
   public T waitForNotValue(ITestEnvironmentAccessor accessor, T value, int milliseconds) throws InterruptedException {
      if (accessor != null) {
         accessor.getLogger().methodCalledOnObject(accessor, this.getFullName(),
               (new MethodFormatter()).add(value).add(milliseconds), this.msg.get());
      }
      EqualsCondition<T> c = new EqualsCondition<T>(this, true, value);
      msg.get().waitForCondition(accessor, c, false, milliseconds);
      if (accessor != null) {
         accessor.getLogger().methodEnded(accessor);
      }
      return c.getLastCheckValue();
   }

   /**
    * Waits until the element has a value within the range specified. Either end of the range can be inclusive or not.
    * 
    * @param accessor Reference to the accessor.
    * @param minValue The minimum value of the range.
    * @param minInclusive If the minumum value of the range is inclusive.
    * @param maxValue The maximum value of the range.
    * @param maxInclusive If the maximum value of the range is inclusive.
    * @param milliseconds Number of milliseconds to wait before failing.
    * @return last value observed
    * @throws InterruptedException
    */
   public T waitForRange(ITestEnvironmentAccessor accessor, T minValue, boolean minInclusive, T maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      if (accessor != null) {
         accessor.getLogger().methodCalledOnObject(
               accessor,
               this.getFullName(),
               (new MethodFormatter()).add(minValue).add(minInclusive).add(maxValue).add(maxInclusive).add(milliseconds),
               this.msg.get());
      }
      InRangeCondition<T> c = new InRangeCondition<T>(this, minValue, minInclusive, maxValue, maxInclusive);
      msg.get().waitForCondition(accessor, c, false, milliseconds);
      if (accessor != null) {
         accessor.getLogger().methodEnded(accessor);
      }
      return c.getLastCheckValue();
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
   public final T waitForRange(ITestEnvironmentAccessor accessor, T minValue, T maxValue, int milliseconds) throws InterruptedException {
      return waitForRange(accessor, minValue, true, maxValue, true, milliseconds);
   }

   /**
    * Waits until the element has a value within the range specified. Either end of the range can be inclusive or not.
    * 
    * @param accessor Reference to the accessor.
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
   public T waitForNotRange(ITestEnvironmentAccessor accessor, T minValue, boolean minInclusive, T maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      if (accessor != null) {
         accessor.getLogger().methodCalledOnObject(
               accessor,
               this.getFullName(),
               (new MethodFormatter()).add(minValue).add(minInclusive).add(maxValue).add(maxInclusive).add(milliseconds),
               this.msg.get());
      }

      NotInRangeCondition<T> c = new NotInRangeCondition<T>(this, minValue, minInclusive, maxValue, maxInclusive);
      msg.get().waitForCondition(accessor, c, false, milliseconds);

      if (accessor != null) {
         accessor.getLogger().methodEnded(accessor);
      }
      return c.getLastCheckValue();
   }

   /**
    * Waits until the element has a value within the range specified. Assumes range is inclusive.
    * 
    * @param accessor
    * @param minValue The minimum value of the range.
    * @param maxValue The maximum value of the range.
    * @param milliseconds Number of milliseconds to wait before failing.
    * @return last value observed
    * @throws InterruptedException
    */
   public final T waitForNotRange(ITestEnvironmentAccessor accessor, T minValue, T maxValue, int milliseconds) throws InterruptedException {
      return waitForRange(accessor, minValue, true, maxValue, true, milliseconds);
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
   public final T waitNotValue(ITestEnvironmentAccessor accessor, T value, int milliseconds) throws InterruptedException {
      return waitForNotValue(accessor, value, milliseconds);
   }

   /**
    * Verifies that the element is set to "value" within the number of "milliseconds" passed.
    * 
    * @param accessor Reference to the accessor.
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
   public boolean check(ITestAccessor accessor, CheckGroup checkGroup, T value, int milliseconds) throws InterruptedException {
      checkAccessor(accessor);

      accessor.getLogger().methodCalledOnObject(accessor, getFullName(),
            (new MethodFormatter()).add(value).add(milliseconds), msg.get());

      EqualsCondition<T> c = new EqualsCondition<T>(this, value);

      CheckPoint cp = waitWithCheckPoint(accessor, checkGroup, toString(value), c, false, milliseconds);
      accessor.getLogger().methodEnded(accessor);
      return cp.isPass();
   }

   /**
    * Verifies that the element is set to a value within the range specified. Either end of the range can be set to be
    * inclusive or not.
    * 
    * @param accessor Reference to the accessor.
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
    * @param milliseconds Number of milliseconds to wait for the element to be in the range.
    * @return if the check passed
    * @throws InterruptedException
    */
   public boolean checkRange(ITestAccessor accessor, CheckGroup checkGroup, T minValue, boolean minInclusive, T maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      checkAccessor(accessor);
      accessor.getLogger().methodCalledOnObject(accessor, getFullName(),
            (new MethodFormatter()).add(minValue).add(minInclusive).add(maxValue).add(maxInclusive).add(milliseconds),
            this.msg.get());

      InRangeCondition<T> c = new InRangeCondition<T>(this, minValue, minInclusive, maxValue, maxInclusive);
      CheckPoint cp =
            waitWithCheckPoint(accessor, checkGroup, "In " + expectedRangeString(toString(minValue), minInclusive,
                  toString(maxValue), maxInclusive), c, false, milliseconds);
      accessor.getLogger().methodEnded(accessor);
      return cp.isPass();
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
   public final boolean checkRange(ITestAccessor accessor, T minValue, boolean minInclusive, T maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      return checkRange(accessor, (CheckGroup) null, minValue, minInclusive, maxValue, maxInclusive, milliseconds);
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
   public final boolean checkRange(ITestAccessor accessor, T minValue, boolean minInclusive, T maxValue, boolean maxInclusive) {
      return checkRange(accessor, (CheckGroup) null, minValue, minInclusive, maxValue, maxInclusive);
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
   public final boolean checkRange(ITestAccessor accessor, CheckGroup checkGroup, T minValue, T maxValue, int milliseconds) throws InterruptedException {
      return checkRange(accessor, checkGroup, minValue, true, maxValue, true, milliseconds);
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
   public final boolean checkRange(ITestAccessor accessor, T minValue, T maxValue, int milliseconds) throws InterruptedException {
      return this.checkRange(accessor, (CheckGroup) null, minValue, true, maxValue, true, milliseconds);
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
   public final boolean checkRange(ITestAccessor accessor, T minValue, T maxValue) {
      return checkRange(accessor, (CheckGroup) null, minValue, true, maxValue, true);
   }

   /**
    * Verifies that the element is set to some value other than "value" within the number of "milliseconds" passed.
    * Passes if at any point with in the time allowed, the elment is set to a value other than "value".
    * 
    * @param accessor Reference to the accessor.
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
   public boolean checkNot(ITestAccessor accessor, CheckGroup checkGroup, T value, int milliseconds) throws InterruptedException {
      checkAccessor(accessor);
      accessor.getLogger().methodCalledOnObject(accessor, getFullName(),
            (new MethodFormatter()).add(value).add(milliseconds), this.msg.get());
      CheckPoint cp =
            waitWithCheckPoint(accessor, checkGroup, "Not " + toString(value),
                  new EqualsCondition<T>(this, true, value), false, milliseconds);
      accessor.getLogger().methodEnded(accessor);
      return cp.isPass();
   }

   /**
    * Verifies that the element is NOT set to "value".
    * 
    * @param accessor
    * @param value value to test against
    * @return if the check passed
    */
   public final boolean checkNot(ITestAccessor accessor, T value) {
      return checkNot(accessor, (CheckGroup) null, value);
   }

   /**
    * Verifies that the element is set to some value other than "value" within the number of "milliseconds" passed.
    * Passes if at any point with in the time allowed, the element is set to a value other than "value".
    * 
    * @param accessor
    * @param value value to test against.
    * @param milliseconds Number of milliseconds to wait for the element to equal the "value".
    * @return If the check passed.
    * @throws InterruptedException
    */
   public final boolean checkNot(ITestAccessor accessor, T value, int milliseconds) throws InterruptedException {
      return checkNot(accessor, (CheckGroup) null, value, milliseconds);
   }

   protected CheckPoint waitWithCheckPoint(ITestAccessor accessor, CheckGroup checkGroup, String expected, IDiscreteElementCondition<T> condition, boolean maintain, int milliseconds) throws InterruptedException {
      MsgWaitResult result = msg.get().waitForCondition(accessor, condition, maintain, milliseconds);
      CheckPoint passFail =
            new CheckPoint(getFullName(), expected, toString(condition.getLastCheckValue()), result.isPassed(),
                  result.getXmitCount(), result.getElapsedTime());

      if (checkGroup == null)
         accessor.getLogger().testpoint(accessor, accessor.getTestScript(), accessor.getTestCase(), passFail);
      else
         checkGroup.add(passFail);
      return passFail;
   }

   /**
    * Verifies that the element is set to a value outside the range specified. Either end of the range can be set to be
    * inclusive or not.
    * 
    * @param accessor Reference to the accessor.
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
    * @throws InterruptedException
    */
   public boolean checkNotRange(ITestAccessor accessor, CheckGroup checkGroup, T minValue, boolean minInclusive, T maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      checkAccessor(accessor);
      accessor.getLogger().methodCalledOnObject(accessor, getFullName(),
            (new MethodFormatter()).add(minValue).add(minInclusive).add(maxValue).add(maxInclusive).add(milliseconds),
            this.msg.get());
      CheckPoint cp =
            waitWithCheckPoint(accessor, checkGroup, "Not In " + expectedRangeString(toString(minValue), minInclusive,
                  toString(maxValue), maxInclusive), new NotInRangeCondition<T>(this, minValue, minInclusive, maxValue,
                  maxInclusive), false, milliseconds);
      accessor.getLogger().methodEnded(accessor);
      return cp.isPass();
   }

   public boolean checkNotRangeNT(ITestAccessor accessor, T minValue, boolean minInclusive, T maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      checkAccessor(accessor);
      accessor.getLogger().methodCalledOnObject(accessor, getFullName(),
            (new MethodFormatter()).add(minValue).add(minInclusive).add(maxValue).add(maxInclusive).add(milliseconds),
            this.msg.get());
      MsgWaitResult cp =
            msg.get().waitForCondition(accessor,
                  new NotInRangeCondition<T>(this, minValue, minInclusive, maxValue, maxInclusive), false, milliseconds);
      accessor.getLogger().methodEnded(accessor);
      return cp.isPassed();
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
   public final boolean checkNotRange(ITestAccessor accessor, T minValue, T maxValue) throws InterruptedException {
      return checkNotRange(accessor, (CheckGroup) null, minValue, true, maxValue, true);
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
   public final boolean checkNotRange(ITestAccessor accessor, T minValue, T maxValue, int milliseconds) throws InterruptedException {
      return checkNotRange(accessor, (CheckGroup) null, minValue, true, maxValue, true, milliseconds);
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
   public final boolean checkNotRange(ITestAccessor accessor, CheckGroup checkGroup, T minValue, T maxValue, int milliseconds) throws InterruptedException {
      return checkNotRange(accessor, checkGroup, minValue, true, maxValue, true, milliseconds);
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
   public final boolean checkNotRange(ITestAccessor accessor, CheckGroup checkGroup, T minValue, T maxValue) throws InterruptedException {
      return checkNotRange(accessor, checkGroup, minValue, true, maxValue, true);
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
   public final boolean checkNotRange(ITestAccessor accessor, T minValue, boolean minInclusive, T maxValue, boolean maxInclusive) throws InterruptedException {
      return checkNotRange(accessor, (CheckGroup) null, minValue, minInclusive, maxValue, maxInclusive);
   }

   /**
    * Verifies that the element is set to the "value" passed for the entire time passed into "milliseconds". Returns
    * value found that caused failure or last value observed if time expires.
    * 
    * @param accessor Reference to the accessor.
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
   public T checkMaintain(ITestAccessor accessor, CheckGroup checkGroup, T value, int milliseconds) throws InterruptedException {
      checkAccessor(accessor);
      accessor.getLogger().methodCalledOnObject(accessor, getFullName(),
            (new MethodFormatter()).add(value).add(milliseconds), this.msg.get());
      EqualsCondition<T> c = new EqualsCondition<T>(this, value);
      waitWithCheckPoint(accessor, checkGroup, toString(value), c, true, milliseconds);
      accessor.getLogger().methodEnded(accessor);
      return c.getLastCheckValue();
   }

   public T checkMaintainNT(ITestAccessor accessor, T value, int milliseconds) throws InterruptedException {
      checkAccessor(accessor);
      accessor.getLogger().methodCalledOnObject(accessor, getFullName(),
            (new MethodFormatter()).add(value).add(milliseconds), this.msg.get());
      EqualsCondition<T> c = new EqualsCondition<T>(this, value);
      msg.get().waitForCondition(accessor, c, true, milliseconds);
      accessor.getLogger().methodEnded(accessor);
      return c.getLastCheckValue();
   }

   public T checkMaintainNotNT(ITestAccessor accessor, T value, int milliseconds) throws InterruptedException {
      checkAccessor(accessor);
      accessor.getLogger().methodCalledOnObject(accessor, getFullName(),
            (new MethodFormatter()).add(value).add(milliseconds), this.msg.get());
      EqualsCondition<T> c = new EqualsCondition<T>(this, true, value);
      msg.get().waitForCondition(accessor, c, true, milliseconds);
      accessor.getLogger().methodEnded(accessor);
      return c.getLastCheckValue();
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
   public final T checkMaintain(ITestAccessor accessor, T value, int milliseconds) throws InterruptedException {
      return checkMaintain(accessor, (CheckGroup) null, value, milliseconds);
   }

   /**
    * Verifies that the element is set to a value other than the "value" passed for the entire time passed into
    * "milliseconds". Returns value found that caused failure or last value observed if time expires.
    * 
    * @param accessor Reference to the accessor.
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
   public T checkMaintainNot(ITestAccessor accessor, CheckGroup checkGroup, T value, int milliseconds) throws InterruptedException {
      checkAccessor(accessor);
      accessor.getLogger().methodCalledOnObject(accessor, getFullName(),
            (new MethodFormatter()).add(value).add(milliseconds), this.msg.get());

      EqualsCondition<T> c = new EqualsCondition<T>(this, true, value);
      waitWithCheckPoint(accessor, checkGroup,

      "Not " + toString(value), c, true, milliseconds);
      accessor.getLogger().methodEnded(accessor);
      return c.getLastCheckValue();
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
   public final T checkMaintainNot(ITestAccessor accessor, T value, int milliseconds) throws InterruptedException {
      return checkMaintainNot(accessor, (CheckGroup) null, value, milliseconds);
   }

   /**
    * Verifies that the element is set to a value within the range specified for the entire time specified.
    * 
    * @param accessor Reference to the accessor.
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
   public T checkMaintainRange(ITestAccessor accessor, CheckGroup checkGroup, T minValue, boolean minInclusive, T maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      checkAccessor(accessor);
      accessor.getLogger().methodCalledOnObject(accessor, getFullName(),
            (new MethodFormatter()).add(minValue).add(minInclusive).add(maxValue).add(maxInclusive).add(milliseconds),
            this.msg.get());

      InRangeCondition<T> c = new InRangeCondition<T>(this, minValue, minInclusive, maxValue, maxInclusive);
      waitWithCheckPoint(accessor, checkGroup, "In" + expectedRangeString(toString(minValue), minInclusive,
            toString(maxValue), maxInclusive), c, true, milliseconds);
      accessor.getLogger().methodEnded(accessor);
      return c.getLastCheckValue();
   }

   public T checkMaintainRangeNT(ITestAccessor accessor, T minValue, boolean minInclusive, T maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      checkAccessor(accessor);
      accessor.getLogger().methodCalledOnObject(accessor, getFullName(),
            (new MethodFormatter()).add(minValue).add(minInclusive).add(maxValue).add(maxInclusive).add(milliseconds),
            this.msg.get());
      InRangeCondition<T> c = new InRangeCondition<T>(this, minValue, minInclusive, maxValue, maxInclusive);
      msg.get().waitForCondition(accessor, c, true, milliseconds);
      accessor.getLogger().methodEnded(accessor);
      return c.getLastCheckValue();
   }

   /**
    * Verifies that the element is set to a value within the range specified for the entire time specified.
    * 
    * @param accessor Reference to the accessor.
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
   public T checkMaintainNotRange(ITestAccessor accessor, CheckGroup checkGroup, T minValue, boolean minInclusive, T maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      checkAccessor(accessor);
      accessor.getLogger().methodCalledOnObject(accessor, getFullName(),
            (new MethodFormatter()).add(minValue).add(minInclusive).add(maxValue).add(maxInclusive).add(milliseconds),
            this.msg.get());

      NotInRangeCondition<T> c = new NotInRangeCondition<T>(this, minValue, minInclusive, maxValue, maxInclusive);
      waitWithCheckPoint(accessor, checkGroup, "Not In" + expectedRangeString(toString(minValue), minInclusive,
            toString(maxValue), maxInclusive), c, true, milliseconds);
      accessor.getLogger().methodEnded(accessor);
      return c.getLastCheckValue();
   }

   public T checkMaintainNotRangeNT(ITestAccessor accessor, T minValue, boolean minInclusive, T maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      checkAccessor(accessor);
      accessor.getLogger().methodCalledOnObject(accessor, getFullName(),
            (new MethodFormatter()).add(minValue).add(minInclusive).add(maxValue).add(maxInclusive).add(milliseconds),
            this.msg.get());

      NotInRangeCondition<T> c = new NotInRangeCondition<T>(this, minValue, minInclusive, maxValue, maxInclusive);
      msg.get().waitForCondition(accessor, c, true, milliseconds);
      accessor.getLogger().methodEnded(accessor);
      return c.getLastCheckValue();
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
   public final T checkMaintainNotRange(ITestAccessor accessor, CheckGroup checkGroup, T minValue, T maxValue, int milliseconds) throws InterruptedException {
      return checkMaintainNotRange(accessor, checkGroup, minValue, true, maxValue, true, milliseconds);
   }

   public boolean checkPulse(ITestAccessor accessor, CheckGroup checkGroup, T pulsedValue, T nonPulsedValue, int milliseconds) throws InterruptedException {
      if (accessor == null) {
         throw new NullPointerException("The parameter accessor is null");
      }

      accessor.getLogger().methodCalledOnObject(accessor, getFullName(),
            (new MethodFormatter()).add(pulsedValue).add(nonPulsedValue).add(milliseconds), this.msg.get());
      final PulseCondition<T> c = new PulseCondition<T>(this, pulsedValue, nonPulsedValue);

      MsgWaitResult result = msg.get().waitForCondition(accessor, c, false, milliseconds);
      CheckPoint passFail =
            new CheckPoint(getFullName(), toString(pulsedValue) + FOR_2_PULSES,
                  toString(c.getLastCheckValue()) + " FOR " + c.getPulses() + " PULSES", result.isPassed(),
                  result.getElapsedTime());

      if (checkGroup == null)
         accessor.getLogger().testpoint(accessor, accessor.getTestScript(), accessor.getTestCase(), passFail);
      else
         checkGroup.add(passFail);
      accessor.getLogger().methodEnded(accessor);
      return passFail.isPass();
   }

   /**
    * @param accessor
    * @param value
    * @param nonPulsedValue
    * @throws InterruptedException
    */
   public final boolean checkPulse(ITestAccessor accessor, T pulsedValue, T nonPulsedValue) throws InterruptedException {
      return checkPulse(accessor, null, pulsedValue, nonPulsedValue);
   }

   /**
    * @param accessor
    * @param checkGroup
    * @param pulsedValue
    * @param nonPulsedValue
    * @throws InterruptedException
    */
   public final boolean checkPulse(ITestAccessor accessor, CheckGroup checkGroup, T pulsedValue, T nonPulsedValue) throws InterruptedException {
      return checkPulse(accessor, checkGroup, pulsedValue, nonPulsedValue, 1000);
   }

   public final boolean checkPulse(ITestAccessor accessor, T pulsedValue, T nonPulsedValue, int milliseconds) throws InterruptedException {
      return checkPulse(accessor, null, pulsedValue, nonPulsedValue, milliseconds);
   }

   public abstract T valueOf(MemoryResource mem);

   @Override
   public String toString() {

      return elementName + "=" + getValue().toString();
   }

   public int compareTo(DiscreteElement<T> o) {
      return getValue().compareTo(o.getValue());
   }

   /**
    * Verifies that the element is set to a value in the "list".
    * 
    * @param accessor
    * @param list List of values to check for
    * @return if check passed
    */
   public final boolean checkInList(ITestAccessor accessor, T[] list) {
      return checkList(accessor, null, true, list);
   }

   /**
    * Verifies that the element is set to a value NOT in the "list".
    * 
    * @param accessor
    * @param list List of values to check for
    * @return if check passed
    */
   public final boolean checkNotInList(ITestAccessor accessor, T[] list) {
      return checkList(accessor, null, false, list);
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
      ListCondition<T> c = new ListCondition<T>(this, isInList, list);

      if (accessor == null) {
         throw new NullPointerException("The parameter accessor is null");
      }
      accessor.getLogger().methodCalledOnObject(accessor, getFullName(),
            (new MethodFormatter()).add(isInList).add(list).add(milliseconds), this.msg.get());

      MsgWaitResult result = msg.get().waitForCondition(accessor, c, false, milliseconds);
      CheckGroup passFail = inList(accessor, isInList, c.getLastCheckValue(), list, result.getElapsedTime());
      assert result.isPassed() == passFail.isPass() : "result does not match checkgroup";
      if (checkGroup == null)
         accessor.getLogger().testpoint(accessor, accessor.getTestScript(), accessor.getTestCase(), passFail);
      else
         checkGroup.add(passFail);
      accessor.getLogger().methodEnded(accessor);
      return passFail.isPass();
   }

   /**
    * Verifies that the element is set to a value NOT in the "list".
    * 
    * @param accessor
    * @param checkGroup If this check is part of a larger set of checks which another method is going to log then the
    *           reference to the CheckGroup must be passed and this method will add the result of the check to the group
    *           with out logging a point.
    *           <p>
    *           If an outside method is not going to log the check then a <b>null </b> reference should be passed and
    *           this method will log the test point.
    * @param list List of values to check for
    * @return if check passed
    */
   public final boolean checkNotInList(ITestAccessor accessor, CheckGroup checkGroup, T[] list) {
      return this.checkList(accessor, checkGroup, false, list);
   }

   /**
    * Verifies that the element is set to a value NOT in the "list".
    * 
    * @param accessor
    * @param list List of values to check for
    * @param milliseconds Number of milliseconds to wait
    * @return if check passed
    * @throws InterruptedException
    */
   public final boolean checkNotInList(ITestAccessor accessor, T[] list, int milliseconds) throws InterruptedException {
      return this.checkList(accessor, (CheckGroup) null, false, list, milliseconds);
   }

   /**
    * Verifies that the element is set to a value IN or NOT IN the "list" passed. "wantInList" determines if checking
    * for IN the list or NOT.
    * 
    * @param accessor
    * @param wantInList Determines if checking for the element's value to be in or not in the "list". Passing TRUE will
    *           test for IN the "list".
    * @param list List of values to check for
    * @return if check passed
    */
   public final boolean checkList(ITestAccessor accessor, boolean wantInList, T[] list) {
      return this.checkList(accessor, null, wantInList, list);
   }

   /**
    * Verifies that the element is set to a value IN or NOT IN the "list" passed. "isInList" determines if checking for
    * IN the list or NOT.
    * 
    * @param accessor
    * @param isInList Determines if checking for the element's value to be in or not in the "list". Passing TRUE will
    *           test for IN the "list".
    * @param list List of values to check for
    * @param milliseconds Number of milliseconds to wait
    * @return if check passed
    * @throws InterruptedException
    */
   public final boolean checkList(ITestAccessor accessor, boolean isInList, T[] list, int milliseconds) throws InterruptedException {
      return checkList(accessor, (CheckGroup) null, isInList, list, milliseconds);
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
   public boolean checkList(ITestAccessor accessor, CheckGroup checkGroup, boolean wantInList, T[] list) {

      final T actualValue = getValue();

      // Check if the value is in the list
      CheckGroup passFail = inList(accessor, true, actualValue, list, 0);

      if (checkGroup == null)
         accessor.getLogger().testpoint(accessor, accessor.getTestScript(), accessor.getTestCase(), passFail);
      else
         checkGroup.add(passFail);

      return passFail.isPass();
   }

   /**
    * Verifies that the element is set to a value in the "list".
    * 
    * @param accessor
    * @param list List of values to check for
    * @param milliseconds Number of milliseconds to wait
    * @return if check passed
    * @throws InterruptedException
    */
   public final boolean checkInList(ITestAccessor accessor, T[] list, int milliseconds) throws InterruptedException {
      return this.checkList(accessor, (CheckGroup) null, true, list, milliseconds);
   }

   /**
    * Verifies that the element is set to a value NOT in the "list".
    * 
    * @param accessor
    * @param checkGroup If this check is part of a larger set of checks which another method is going to log then the
    *           reference to the CheckGroup must be passed and this method will add the result of the check to the group
    *           with out logging a point.
    *           <p>
    *           If an outside method is not going to log the check then a <b>null </b> reference should be passed and
    *           this method will log the test point.
    * @param list List of values to check for
    * @param milliseconds Number of milliseconds to wait
    * @return if check passed
    * @throws InterruptedException
    */
   public final boolean checkNotInList(ITestAccessor accessor, CheckGroup checkGroup, T[] list, int milliseconds) throws InterruptedException {
      return this.checkList(accessor, checkGroup, false, list, milliseconds);
   }

   /**
    * Verifies that the element is set to a value in the list for the entire time passed into milliseconds.
    * 
    * @param accessor
    * @param checkGroup If this check is part of a larger set of checks which another method is going to log then the
    *           reference to the CheckGroup must be passed and this method will add the result of the check to the group
    *           with out logging a point.
    *           <p>
    *           If an outside method is not going to log the check then a <b>null </b> reference should be passed and
    *           this method will log the test point.
    * @param list The list of values to check against
    * @param milliseconds Number of milliseconds to wait before failing.
    * @return last value observed
    * @throws InterruptedException
    */
   public final T checkMaintainInList(ITestAccessor accessor, CheckGroup checkGroup, T[] list, int milliseconds) throws InterruptedException {
      return this.checkMaintainList(accessor, checkGroup, list, true, milliseconds);
   }

   /**
    * Verifies that the element is set to a value not in the list for the entire time passed into milliseconds.
    * 
    * @param accessor
    * @param checkGroup If this check is part of a larger set of checks which another method is going to log then the
    *           reference to the CheckGroup must be passed and this method will add the result of the check to the group
    *           with out logging a point.
    *           <p>
    *           If an outside method is not going to log the check then a <b>null </b> reference should be passed and
    *           this method will log the test point.
    * @param list The list of values to check against
    * @param milliseconds Number of milliseconds to wait before failing.
    * @return last value observed
    * @throws InterruptedException
    */
   public final T checkMaintainNotInList(ITestAccessor accessor, CheckGroup checkGroup, T[] list, int milliseconds) throws InterruptedException {
      return this.checkMaintainList(accessor, checkGroup, list, false, milliseconds);
   }

   /**
    * Verifies that the element is set to a value in the "list".
    * 
    * @param accessor
    * @param checkGroup If this check is part of a larger set of checks which another method is going to log then the
    *           reference to the CheckGroup must be passed and this method will add the result of the check to the group
    *           with out logging a point.
    *           <p>
    *           If an outside method is not going to log the check then a <b>null </b> reference should be passed and
    *           this method will log the test point.
    * @param list List of values to check for
    * @param milliseconds Number of milliseconds to wait
    * @return if check passed
    * @throws InterruptedException
    */
   public final boolean checkInList(ITestAccessor accessor, CheckGroup checkGroup, T[] list, int milliseconds) throws InterruptedException {
      return this.checkList(accessor, checkGroup, true, list, milliseconds);
   }

   /**
    * Verifies that the element is set to a value in the "list".
    * 
    * @param accessor
    * @param checkGroup If this check is part of a larger set of checks which another method is going to log then the
    *           reference to the CheckGroup must be passed and this method will add the result of the check to the group
    *           with out logging a point.
    *           <p>
    *           If an outside method is not going to log the check then a <b>null </b> reference should be passed and
    *           this method will log the test point.
    * @param list List of values to check for
    * @return if check passed
    */
   public final boolean checkInList(ITestAccessor accessor, CheckGroup checkGroup, T[] list) {
      return checkList(accessor, checkGroup, true, list);
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

      accessor.getLogger().methodCalledOnObject(accessor, this.getFullName(),
            (new MethodFormatter()).add(list).add(isInList).add(milliseconds), this.getMessage());

      ListCondition<T> c = new ListCondition<T>(this, isInList, list);
      msg.get().waitForCondition(accessor, c, false, milliseconds);
      accessor.getLogger().methodEnded(accessor);
      return c.getLastCheckValue();
   }

   public T checkMaintainList(ITestAccessor accessor, CheckGroup checkGroup, T[] list, boolean isInList, int milliseconds) throws InterruptedException {
      accessor.getLogger().methodCalledOnObject(accessor, this.getFullName(),
            (new MethodFormatter()).add(list).add(isInList).add(milliseconds), this.getMessage());

      ListCondition<T> c = new ListCondition<T>(this, isInList, list);

      MsgWaitResult result = msg.get().waitForCondition(accessor, c, true, milliseconds);

      T value = c.getLastCheckValue();

      CheckGroup passFail = inList(accessor, isInList, value, list, result.getElapsedTime());
      if (checkGroup == null)
         accessor.getLogger().testpoint(accessor, accessor.getTestScript(), accessor.getTestCase(), passFail);
      else
         checkGroup.add(passFail);
      accessor.getLogger().methodEnded(accessor);
      return value;
   }

   /**
    * This method checks a an array of EnumBase objects to determine if a given EnumBase object is or isn't in the list.
    * It then returns a CheckGroup object that describes the checks and pass/fail status.
    * 
    * @param isInList <ul>
    *           <li><b>True </b> used to get a pass iff the item is in the list.</li>
    *           <li><b>False </b> used to get a pass iff the item is not in the list.</li>
    *           </ul>
    * @param value The EnumBase object to check for.
    * @param list The array of EnumBase objects to look through.
    * @return A CheckGroup object that describes all comparisons made and outcomes.
    */
   private CheckGroup inList(ITestAccessor accessor, boolean isInList, T value, T[] list, long elapsedTime) {
      accessor.getLogger().methodCalledOnObject(accessor, this.getFullName(),
            (new MethodFormatter()).add(isInList).add(value).add(list), this.getMessage());
      CheckGroup checkGroup;
      final String not = "Not ";

      int i = 0;
      // Build CheckPoint based on the type of check
      if (isInList) {
         checkGroup = new CheckGroup(Operation.OR, this.getFullName()); // Pass if at least one item
         // matches
         for (T val : list) {
            // Check if current item in the list matches.
            checkGroup.add(new CheckPoint("List Item: " + i, value.toString(), val.toString(), val.equals(value),
                  elapsedTime));
            i++;
         }
      } else {
         checkGroup = new CheckGroup(Operation.AND, this.getFullName()); // Pass iff none of the
         // items match
         for (T val : list) {
            checkGroup.add(new CheckPoint("List Item: " + i, not + value.toString(), val.toString(),
                  !val.equals(value), elapsedTime));
            i++;
         }
      }
      accessor.getLogger().methodEnded(accessor);
      return checkGroup;
   }

   /**
    * Sets the element to the first enumeration for the wait time and then it sets it to the second enumeration.
    * 
    * @param accessor
    * @param value1
    * @param value2
    * @param milliseconds
    * @throws InterruptedException
    */
   public synchronized void toggle(ITestEnvironmentAccessor accessor, T value1, T value2, int milliseconds) throws InterruptedException {
      accessor.getLogger().methodCalledOnObject(accessor, this.getFullName(),
            new MethodFormatter().add(value1).add(value2).add(milliseconds), this.msg.get());

      set(accessor, value1);
      accessor.setTimerFor(this, milliseconds);
      wait();
      accessor.getScriptCtrl().lock();
      set(accessor, value2);

      accessor.getLogger().methodEnded(accessor);
   }

   /**
    * Will be removed in MS_0.1.6.
    * 
    * @use {@link #getValue()} instead
    */
   @Deprecated
   public T get() {
      return getValue();
   }

   /**
    * gets this element's current value. Does logging
    * 
    * @param accessor
    */
   public T get(ITestEnvironmentAccessor accessor) {
      accessor.getLogger().methodCalled(accessor, new MethodFormatter());
      T v = getValue();
      accessor.getLogger().methodEnded(accessor);
      return v;
   }

   /**
    * get this elements current value
    * 
    * @return the value of this element
    */
   public T getNoLog() {
      return getValue();
   }

   @Deprecated
   public void set(T value) {
      setValue(value);
   }

   public void setNoLog(T value) {
      setValue(value);
   }

   @Deprecated
   public void setNoLog(ITestEnvironmentAccessor accessor, T value) {
      setValue(value);
   }

   private void checkAccessor(ITestEnvironmentAccessor accessor) {
      if (accessor == null) {
         throw new NullPointerException("The parameter accessor is null");
      }
   }

   abstract public T elementMask(T value);

}
