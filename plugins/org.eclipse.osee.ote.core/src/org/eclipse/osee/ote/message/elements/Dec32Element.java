/*
 * Created on Nov 6, 2013
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ote.message.elements;

import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.data.MemoryResource;
import org.eclipse.osee.ote.message.data.MessageData;

/**
 * @author Michael P. Masterson
 */
public class Dec32Element extends Float32Element {

   /**
    * @param message
    * @param elementName
    * @param messageData
    * @param byteOffset
    * @param msb
    * @param lsb
    */
   public Dec32Element(Message<?, ?, ?> message, String elementName, MessageData messageData,
         int byteOffset, int msb, int lsb) {
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
   public Dec32Element(Message<?, ?, ?> message, String elementName, MessageData messageData,
         int byteOffset, int msb, int lsb, int originalLsb, int originalMsb) {
      super(message, elementName, messageData, byteOffset, msb, lsb, originalLsb, originalMsb);
   }

   /**
    * @param message
    * @param elementName
    * @param messageData
    * @param bitOffset
    * @param bitLength
    */
   public Dec32Element(Message<?, ?, ?> message, String elementName, MessageData messageData,
         int bitOffset, int bitLength) {
      super(message, elementName, messageData, bitOffset, bitLength);
   }

   @Override
   protected double toDouble(long value) {
      return super.toDouble(value * 4);
   }

   @Override
   protected long toLong(double value) {
      return super.toLong(value * 4);
   }

   @Override
   public void setValue(Double value) {
      super.setValue(value * 4);
   }

   @Override
   public Double getValue() {
      return super.getValue() / 4;
   }

   @Override
   public Double valueOf(MemoryResource mem) {
      return super.valueOf(mem) / 4;
   }
}
