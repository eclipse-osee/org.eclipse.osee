/*
 * Created on Apr 30, 2015
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ote.message.elements;

import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor;
import org.eclipse.osee.ote.core.testPoint.CheckGroup;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.data.MemoryResource;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.elements.nonmapping.NonMappingLongIntegerElement;
import org.eclipse.osee.ote.message.interfaces.ITestAccessor;

/**
 * MIL-STD-1760E standard 16 bit Binary Scientific Notation Integer format.
 * <dl>
 * <dt>Base<dd>2's compliment integer located in the first 12 most significant bits
 * <dt>Exponent<dd>unsigned int located in the last 4 (least significant) bits
 * <dt>Conversion<dd>LogicalValue = (Base) * 16<sup>Exponent</sup> 
 * </dl>
 *            
 * @author Michael P. Masterson
 */
public class BiSci16Element extends LongIntegerElement {

   public BiSci16Element(Message<?, ?, ?> message, String elementName, MessageData messageData, int byteOffset, int msb, int lsb) {
      this(message, elementName, messageData, byteOffset, msb, lsb, msb, lsb);
   }

   public BiSci16Element(Message<?, ?, ?> message, String elementName, MessageData messageData, int bitOffset, int bitLength) {
      super(message, elementName, messageData, bitOffset, bitLength);
   }

   public BiSci16Element(Message<?, ?, ?> message, String elementName, MessageData messageData, int byteOffset, int msb, int lsb, int originalLsb, int originalMsb) {
      super(message, elementName, messageData, byteOffset, msb, lsb, originalLsb, originalMsb);
   }

   @Override
   public void setValue(Long value) {
      super.setValue(convertLogicalValueToBiSci(value));
   }
   
   @Override
   public Long getValue() {
      return convertBiSciToLogicalValue(super.getValue());
   }
   
   @Override
   public Long valueOf(MemoryResource mem) {
      return convertBiSciToLogicalValue(super.valueOf(mem));
   }
   
   /**
    * Verifies that the element is set to "value" within the number of "milliseconds" passed.
    * 
    * @param value Expected value.
    * @param milliseconds Number of milliseconds to wait for the element to equal the "value".
    * @return If the check passed.
    */
   @Override
   public boolean check(ITestAccessor accessor, long value, int milliseconds) throws InterruptedException {
      return super.check(accessor, (CheckGroup) null, value, milliseconds);
   }

   /**
    * This function will verify that this signal is pulsed for 2 cycles.
    * 
    * @param value The value to be checked
    */
   @Override
   public void checkPulse(ITestAccessor accessor, long value) throws InterruptedException {
      long nonPulsedValue = 0;
      if (value == 0) {
         nonPulsedValue = 1;
      }

      checkPulse(accessor, value, nonPulsedValue);
   }

   /**
    * Sets the element to the "value" passed.
    * 
    * @param value The value to set.
    */
   @Override
   public void set(ITestEnvironmentAccessor accessor, long value) {
      super.set(accessor, value);
   }

   /**
    * Sets the element to the "value" passed and immediately sends the message that contains it..
    * 
    * @param value The value to set.
    */
   @Override
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
      return value;
   }

   @Override
   public long getNumericBitValue() {
      return getRaw();
   }
   
   /**
    * @return the raw bits of this element without conversion from the binary scientific notation format.
    */
   public long getRaw() {
      return getRaw(getMsgData().getMem());
   }

   /**
    * @param mem
    * @return the raw bits of this element without conversion from the binary scientific notation format.
    */
   public long getRaw(MemoryResource mem) {
      return mem.getLong(byteOffset, msb, lsb);
   }
   
   
   /**
    * Sets the raw long straight into memory without conversion
    * @param hex
    */
   public void setHex(long hex) {
      getMsgData().getMem().setLong(hex, byteOffset, msb, lsb);
   }
   
   /**
    * Checks that this element correctly forwards a message sent from cause with the value passed.
    * 
    * @param cause The originator of the signal
    * @param value The value sent by cause and being forwarded by this element
    */
   @Override
   public void checkForwarding(ITestAccessor accessor, LongIntegerElement cause, long value) throws InterruptedException {
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
   
   
   /*package*/ Long convertLogicalValueToBiSci(long logical) {
      long biSci = 0;
      boolean isNeg = false;
      if( logical < 0 ) {
         isNeg = true;
         logical = -logical;
      }
      
      long base =  Math.abs(logical);
      int powersOf16 = 0;
      boolean round = false;
      while( base > 0x0800) {
         powersOf16++;
         if( round ) 
            base--;
         
         round = base % 16 > 7;
         base = base >>> 4;
         
         if( round)
            base++;
      }
      
      if( isNeg ) {
         base = 0x1000 - base;
      }
      
      final long shiftedBase = base << 4;
      biSci = shiftedBase + powersOf16;
      
      return biSci & 0xFFFF;
   }
   
   /*package*/ Long convertBiSciToLogicalValue(long biSci) {
      long logical = 0;
      long base = ((short)biSci) >> 4; // cast to short to ensure sign extension to long
      long powersOf16 = biSci & 0xF;
      
      long shift = powersOf16 * 4;
      logical = base << shift;
      
      return logical;
   }
   

}
