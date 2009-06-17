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
import org.eclipse.osee.ote.core.MethodFormatter;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor;
import org.eclipse.osee.ote.core.testPoint.CheckGroup;
import org.eclipse.osee.ote.core.testPoint.CheckPoint;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.data.MemoryResource;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.elements.nonmapping.NonMappingCharElement;
import org.eclipse.osee.ote.message.interfaces.ITestAccessor;
import org.eclipse.osee.ote.message.listener.MessageSystemListener;

/**
 * @author John Butler
 * @author Robert A. Fisher
 */
public class CharElement extends DiscreteElement<Character> {

   /**
    * @param message
    * @param elementName
    */
   public CharElement(Message<?, ?, ?> message, String elementName, MessageData messageData, int byteOffset, int msb, int lsb) {
      this(message, elementName, messageData, byteOffset, msb, lsb, msb, lsb);
   }

   public CharElement(Message<?, ?, ?> message, String elementName, MessageData messageData, int byteOffset, int msb, int lsb, int originalLsb, int originalMsb) {
      super(message, elementName, messageData, byteOffset, msb, lsb, originalLsb, originalMsb);
   }

   public CharElement(Message<?, ?, ?> message, String elementName, MessageData messageData, int bitOffset, int bitLength) {
      super(message, elementName, messageData, bitOffset, bitLength);
   }

   @Override
   public CharElement switchMessages(Collection<? extends Message<?, ?, ?>> messages) {
      return (CharElement) super.switchMessages(messages);
   }

   /**
    * Checks that this element correctly forwards a message sent from cause with the value passed.
    * 
    * @param accessor
    * @param cause The originator of the signal
    * @param value The value sent by cause and being forwarded by this element
    * @throws InterruptedException
    */
   public void checkForwarding(ITestAccessor accessor, CharElement cause, Character value) throws InterruptedException {
      /* check for 0 to begine */
      check(accessor, (char) 0, 0);

      /* Set the DP1 Mux Signal */
      cause.set(accessor, value);

      /* Chk Value on DP2 */
      check(accessor, value, 1000);

      /* Set DP1 to 0 */
      cause.set(accessor, (char) 0);

      /* Init DP2 Mux to 0 */
      set(accessor, (char) 0);

      /* Chk Value on DP2 is still set */
      check(accessor, value, 500);

      /* Chk DP2 is 0 for two-pulse signals and high for four-oulse signal */
      check(accessor, (char) 0, 500);

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
      return this.checkNot(accessor, (CheckGroup) null, value, milliseconds);
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
      if (accessor != null) {
         accessor.getLogger().methodCalledOnObject(accessor, this.getFullName(),
               (new MethodFormatter()).add(value).add(milliseconds), getMessage());
      }
      long time = accessor.getEnvTime();
      String currentValue;
      boolean result;
      if (milliseconds > 0) {
         final MessageSystemListener listener = getMessage().getListener();
         org.eclipse.osee.ote.core.environment.interfaces.ICancelTimer cancelTimer =
               accessor.setTimerFor(listener, milliseconds);

         accessor.getLogger().debug(accessor, "waiting............", true);

         while (result = !(currentValue = getString(accessor, value.length())).equals(value)) {
            listener.waitForData(); // will also return if the timer (set above)
            // expires
            /*
             * NOTE: had to add isTimedOut() because we were getting data at the same time we're timing
             * out, so the notifyAll() isn't guaranteed to work since we would not be in a waiting
             * state at that time - so we're forced to save the fact that we timed out.
             */
            if (listener.isTimedOut()) {
               break;
            }
         }
         cancelTimer.cancelTimer();
         accessor.getLogger().debug(accessor, "done waiting", true);
      } else {
         result = !(currentValue = getString(accessor, value.length())).equals(value);
      }
      time = accessor.getEnvTime() - time;
      CheckPoint passFail = new CheckPoint(this.getFullName(), "Not " + value, currentValue, result, time);

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
    * Verifies that the element is set to "value" within the number of "milliseconds" passed.
    * 
    * @param accessor
    * @param value Expected value.
    * @param milliseconds Number of milliseconds to wait for the element to equal the "value".
    * @return If the check passed.
    * @throws InterruptedException
    */
   public boolean check(ITestAccessor accessor, String value, int milliseconds) throws InterruptedException {
      return this.check(accessor, (CheckGroup) null, value, milliseconds);
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
      if (accessor != null) {
         accessor.getLogger().methodCalledOnObject(accessor, this.getFullName(),
               (new MethodFormatter()).add(value).add(milliseconds), this.getMessage());
      }
      long time = accessor.getEnvTime();
      String currentValue;
      if (milliseconds > 0) {
         MessageSystemListener listener = getMessage().getListener();
         org.eclipse.osee.ote.core.environment.interfaces.ICancelTimer cancelTimer =
               accessor.setTimerFor(listener, milliseconds);

         accessor.getLogger().debug(accessor, "waiting............", true);

         while (!compareString(currentValue = getString(accessor, value.length()), value)) {
            listener.waitForData(); // will also return if the timer (set above)
            // expires
            /*
             * NOTE: had to add isTimedOut() because we were getting data at the same time we're timing
             * out, so the notifyAll() isn't guaranteed to work since we would not be in a waiting
             * state at that time - so we're forced to save the fact that we timed out.
             */
            if (listener.isTimedOut()) {
               break;
            }
         }

         cancelTimer.cancelTimer();
         accessor.getLogger().debug(accessor, "done waiting", true);
      } else {
         currentValue = getString(accessor, value.length());
      }
      time = accessor.getEnvTime() - time;
      CheckPoint passFail =
            new CheckPoint(this.getFullName(), value, currentValue, compareString(currentValue, value), time);

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
    * @param string
    * @param value
    * @return
    */
   private boolean compareString(String string, String value) {
      return string.equals(value);
   }

   /**
    * Verifies that the string starting at the element is not set to "value".
    * 
    * @param accessor
    * @param value Expected value
    * @return if the check passed
    */
   public boolean checkNot(ITestAccessor accessor, String value) {
      return this.checkNot(accessor, (CheckGroup) null, value);
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
      if (accessor != null) {
         accessor.getLogger().methodCalledOnObject(accessor, this.getFullName(), (new MethodFormatter()).add(value),
               this.getMessage());
      }

      String actualValue = getString(accessor, value.length());

      CheckPoint passFail =
            new CheckPoint(this.getFullName(), "Not " + value, actualValue, value.compareTo(actualValue) != 0, 0);

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
    * Verifies that the string starting at the element is set to "value".
    * 
    * @param accessor
    * @param value Expected value
    * @return if the check passed
    */
   public boolean check(ITestAccessor accessor, String value) {
      return this.check(accessor, (CheckGroup) null, value);
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
      if (accessor != null) {
         accessor.getLogger().methodCalledOnObject(accessor, this.getFullName(), (new MethodFormatter()).add(value),
               this.getMessage());
      }
      String actualValue = getString(accessor, value.length());

      CheckPoint passFail =
            new CheckPoint(this.getFullName(), value, actualValue, value.compareTo(actualValue) == 0, 0);

      if (checkGroup == null)
         accessor.getLogger().testpoint(accessor, accessor.getTestScript(), accessor.getTestCase(), passFail);
      else
         checkGroup.add(passFail);

      if (accessor != null) {
         accessor.getLogger().methodEnded(accessor);
      }
      return passFail.isPass();
   }

   public Character get(ITestEnvironmentAccessor accessor) {
      return (Character) this.getValue();
   }

   /**
    * Returns the string of length "stringLength" starting as the position of the element.
    * 
    * @param accessor
    * @param stringLength the length of the string to return
    * @return the string starting with this element
    */
   public String getString(ITestEnvironmentAccessor accessor, int stringLength) {
      return getASCIIString(stringLength);
   }

   /**
    * Sets the element and the next ("value".length() -1) bytes to "value".charAt().
    * 
    * @param accessor
    * @param value the string to set the bytes to
    */
   public void parseAndSet(ITestEnvironmentAccessor accessor, String value) {
      if ((value.length() + this.getByteOffset()) > this.getMessage().getData().length) throw new IllegalArgumentException(
            "Setting a String whose length exceeds the Message bounds!");

      if (accessor != null) {
         accessor.getLogger().methodCalledOnObject(accessor, this.getFullName(), (new MethodFormatter()).add(value),
               this.getMessage());
      }

      setASCIIString(value);

      if (accessor != null) {
         accessor.getLogger().methodEnded(accessor);
      }
   }

   public void set(ITestEnvironmentAccessor a, String value) {
      parseAndSet(a, value);
   }

   public void setValue(String value) {
      parseAndSet(null, value);
   }

   /**
    * Sets the element and the next ("value".length() -1) bytes to "value".charAt() and immediately sends the message
    * that contains it..
    * 
    * @param accessor
    * @param value the string to set the bytes to
    */
   public void setAndSend(ITestEnvironmentAccessor accessor, String value) {
      this.parseAndSet(accessor, value);
      super.sendMessage();
   }

   /**
    * Sets the element and the next ("value".length() -1) bytes to "value".charAt(). <b>No Log Record gets created in
    * the Script Log File.</b>
    * 
    * @param accessor
    * @param value the string to set the bytes to
    */
   public void setNoLog(ITestEnvironmentAccessor accessor, String value) {
      this.parseAndSet(accessor, value);
   }

   /**
    * Sets the element to the "value" passed and immediately sends the meessage that contains it..
    * 
    * @param accessor
    * @param value The value to set.
    */
   public void setAndSend(ITestEnvironmentAccessor accessor, Character value) {
      this.set(accessor, value);
      super.sendMessage();
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
      if (accessor != null) {
         accessor.getLogger().methodCalledOnObject(accessor, this.getFullName(),
               (new MethodFormatter()).add(value).add(milliseconds), this.getMessage());
      }
      String currentValue;
      if (milliseconds > 0) {
         MessageSystemListener listener = getMessage().getListener();
         org.eclipse.osee.ote.core.environment.interfaces.ICancelTimer cancelTimer =
               accessor.setTimerFor(listener, milliseconds);

         accessor.getLogger().debug(accessor, "waiting............", true);

         while ((currentValue = getString(accessor, value.length())).equals(value)) {
            listener.waitForData(); // will also return if the timer (set above)
            // expires
            /*
             * NOTE: had to add isTimedOut() because we were getting data at the same time we're timing
             * out, so the notifyAll() isn't guaranteed to work since we would not be in a waiting
             * state at that time - so we're forced to save the fact that we timed out.
             */
            if (listener.isTimedOut()) {
               break;
            }
         }
         cancelTimer.cancelTimer();
         accessor.getLogger().debug(accessor, "done waiting", true);
      } else {
         currentValue = getString(accessor, value.length());
      }
      if (accessor != null) {
         accessor.getLogger().methodCalledOnObject(accessor, this.getFullName(), (new MethodFormatter()).add(value),
               this.getMessage());
      }
      return currentValue;
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
      if (accessor != null) {
         accessor.getLogger().methodCalledOnObject(accessor, this.getFullName(),
               (new MethodFormatter()).add(value).add(milliseconds), this.getMessage());
      }
      String currentValue;
      if (milliseconds > 0) {

         MessageSystemListener listener = getMessage().getListener();
         org.eclipse.osee.ote.core.environment.interfaces.ICancelTimer cancelTimer =
               accessor.setTimerFor(listener, milliseconds);

         accessor.getLogger().debug(accessor, "waiting............", true);

         while (!(currentValue = getString(accessor, value.length())).equals(value)) {
            listener.waitForData(); // will also return if the timer (set above)
            // expires
            /*
             * NOTE: had to add isTimedOut() because we were getting data at the same time we're timing
             * out, so the notifyAll() isn't guaranteed to work since we would not be in a waiting
             * state at that time - so we're forced to save the fact that we timed out.
             */
            if (listener.isTimedOut()) {
               break;
            }
         }
         cancelTimer.cancelTimer();
         accessor.getLogger().debug(accessor, "done waiting", true);
      } else {
         currentValue = getString(accessor, value.length());
      }
      if (accessor != null) {
         accessor.getLogger().methodCalledOnObject(accessor, this.getFullName(), (new MethodFormatter()).add(value),
               this.getMessage());
      }
      return currentValue;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ote.message.elements.Element#setValue(java.lang.Object)
    */
   @Override
   public void setValue(Character value) {
      getMsgData().getMem().setInt(value, byteOffset, msb, lsb);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ote.message.elements.Element#getValue()
    */
   @Override
   public Character getValue() {
      return (char) getMsgData().getMem().getInt(byteOffset, msb, lsb);
   }

   @Override
   public Character valueOf(MemoryResource otherMem) {
      return new Character((char) otherMem.getInt(byteOffset, msb, lsb));
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ote.message.elements.Element#valueOf(java.lang.Object)
    */
   @Override
   public String toString(Character obj) {
      return obj.toString();
   }

   private String getASCIIString(int length) {
      return getMsgData().getMem().getASCIIString(byteOffset, length);
   }

   private void setASCIIString(String value) {
      getMsgData().getMem().setASCIIString(value, byteOffset);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.Element#visit(org.eclipse.osee.ote.message.elements.IElementVisitor)
    */
   @Override
   public void visit(IElementVisitor visitor) {
      visitor.asCharElement(this);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.Element#getNonMappingElement(org.eclipse.osee.ote.message.elements.Element)
    */
   @Override
   protected NonMappingCharElement getNonMappingElement() {
      return new NonMappingCharElement(this);
   }

   @Override
   public Character elementMask(Character value) {
      return value;
   }
}