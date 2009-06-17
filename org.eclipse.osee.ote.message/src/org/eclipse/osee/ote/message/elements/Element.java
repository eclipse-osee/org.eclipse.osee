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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ote.core.environment.interfaces.ITimeout;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.MessageSystemException;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.enums.MemType;

/**
 * @author Ryan D. Brooks
 * @author Andrew M. Finkbeiner
 */
public abstract class Element  implements ITimeout {
   protected WeakReference<Message<?,?,?>> msg;
   protected String elementName;
   private volatile boolean timedOut;
   private List<Object> elementPath;
   private String fullName;
   
   protected int byteOffset;
   protected int lsb;
   protected WeakReference<MessageData> messageData;
   protected int msb;
   protected int originalMsb;
   protected int originalLsb;
   private String elementPathAsString;

   public Element(Message<?,?,?> msg, String elementName, MessageData messageData, int byteOffset, int msb, int lsb, int originalMsb, int originalLsb) {
      this.msg = new WeakReference<Message<?,?,?>>(msg);
      this.elementName = elementName;
      this.messageData = new WeakReference<MessageData>(messageData);
      this.byteOffset = byteOffset;
      this.lsb = lsb;
      this.msb = msb;
      this.originalLsb = originalLsb;
      this.originalMsb = originalMsb;
      // if (msg!=null)
      // msg.addElement(this);
      elementPath = new ArrayList<Object>();
      fullName = (msg != null ? msg.getName() : messageData.getName()) + "." + this.elementName;
   }

   public Element(Message<?,?,?> msg, String elementName, MessageData messageData, int bitOffset, int bitLength) {
      this(msg, elementName, messageData, 0, 0, 0, 0, 0);
      this.byteOffset = bitOffset / 8;
      this.msb = bitOffset % 8;
      this.lsb = msb + (bitLength - 1);
   }

   public Element(Message<?,?,?> msg, String elementName, MessageData messageData, int byteOffset, int msb, int lsb) {
      this(msg, elementName, messageData, byteOffset, msb, lsb, msb, lsb);
   }

   public int getMsb() {
      return msb;
   }

   public int getLsb() {
      return lsb;
   }

   public int getByteOffset() {
      return byteOffset;
   }

   public MessageData getMsgData() {
      return messageData.get();
   }

   public int getBitLength() {
      return (Math.abs(getMsb() - getLsb()) + 1);
   }

   public int getStartingBit() {
      return ((getByteOffset() * 8)) + Math.min(getMsb(), getLsb());
   }

   /*
    * protected void set(TestEnvironmentAccessor accessor, long value) { if (accessor != null) {
    * accessor.getLogger().methodCalledOnObject(accessor, this.getFullName(), (new
    * MethodFormatter()).add(value)); } ((MemHolder) current.get()).getMem().s.setLong(value); if
    * (accessor != null) { accessor.getLogger().methodEnded(accessor); } }
    */
   /**
    * @return Returns full name string.
    */
   public String getFullName() {
      return fullName;
   }

   public String getName() {
      return this.elementName;
   }

   public String getDescriptiveName(){
      return this.elementName;
   }
   
   /**
    * @return Returns the msg.
    */
   public Message<?,?,?> getMessage() {
      return msg.get();
   }

   public String getElementName() {
      return elementName;
   }

   public boolean isTimedOut() {
      return this.timedOut;
   }

   public void setTimeout(boolean timeout) {
      this.timedOut = timeout;
   }

   public MemType getType() {
      return messageData.get().getType();
   }

   /**
    * @return Returns the lsb.
    */
   public int getOriginalLsb() {
      return originalLsb;
   }

   /**
    * @return Returns the msb.
    */
   public int getOriginalMsb() {
      return originalMsb;
   }

   private int calculateBitsToShift() {
      int size = lsb - msb + 1;
      return 32 - size;
   }

   private int calculateLongBitsToShift() {
       int size = lsb - msb + 1;
       return 64 - size;
    }
   
   protected int signExtend(int value) {
      int bitsToShift = calculateBitsToShift();
      return (value << bitsToShift) >> (bitsToShift);
   }

   protected int removeSign(int value) {
      int bitsToShift = calculateBitsToShift();
      return (value << bitsToShift) >>> (bitsToShift);
   }
   
   protected long removeSign(long value) {
       int bitsToShift = calculateLongBitsToShift();
       return (value << bitsToShift) >>> (bitsToShift);
    }
   

   /**
    * @return whether this message maps solely to PubSub
    */
   public boolean isNonMappingElement() {
      return false;
   }

   /**
    * Looks for the element matching this elements name inside one of the messages passed
    * 
    * @param messages Those messages mapped to a certain physical type, one of whom contains a
    *            mapping to this element
    * @return An element of one of the messages passed with the same name as this element or this
    *         element if no match is found.
    */
   public Element switchMessages(Collection<? extends Message<?,?,?>> messages) {
      for (Message<?, ?, ?> currentMessage : messages) {
//         System.out.println("SwitchMessages" + currentMessage.getMessageName());
         Element el = currentMessage.getElement(this.getElementPath());
         if (el != null && currentMessage.isValidElement(this, el)) {
            return el;
         }
      }
      return this.getNonMappingElement();
   }

   protected void sendMessage() {
      this.getMessage().send();
   }

   /**
    * This method returns a properly formatted string that describes the range and
    * inclusive/exclusive properties of each end of the range.
    * 
    * @param minValue The minimum value of the range.
    * @param minInclusive If the minumum value of the range is inclusive. If true the actual value
    *            must not < and not = to the range value.
    * @param maxValue The maximum value of the range.
    * @param maxInclusive If the maximum value of the range is inclusive. If true the actual value
    *            must not > and not = to the range value.
    * @return the string holding "[", "]", "(", or ")"
    */
   protected static String expectedRangeString(Object minValue, boolean minInclusive, Object maxValue, boolean maxInclusive) {
      // A means for a return value
      String retVal;

      // Start with the proper symbol for the lower bound
      if (minInclusive)
         retVal = "[";
      else
         retVal = "(";

      // Add in the minimum and maximum values
      retVal += minValue + " .. " + maxValue;

      // End with the proper symbol for the upper bound
      if (maxInclusive)
         retVal += "]";
      else
         retVal += ")";

      // Return the formatted string
      return retVal;
   }

   protected abstract Element getNonMappingElement();

   protected void throwNoMappingElementException() {
      throw new MessageSystemException("The element " + msg.get().getName() + "." + elementName + " does not exist for the message's current MemType!! "
            + "\nIt shouldn't be used for this environment type!!", Level.SEVERE);
   }

   /**
    * @return the elementPath
    */
   public List<Object> getElementPath() {
      return elementPath;
   }

   public void addPath(Object... objs) {
      for (Object obj : objs) {
         elementPath.add(obj);
      }
      elementPath.add(this.getName());
   }

   @Override
   public String toString() {

      return elementName;
   }

   /**
    * 
    */
   public void zeroize() {

      getMsgData().getMem().setLong(0L, byteOffset, msb, lsb);

   }
   
   public void visit(IElementVisitor visitor) {
       visitor.asGenericElement(this);
    }
   
   public String getElementPathAsString(){
      if(elementPathAsString == null){
         StringBuilder sb = new StringBuilder();
         for(int i = 1; i < elementPath.size(); i++){
            Object obj = elementPath.get(i);
            if(obj instanceof String){
               sb.append(obj);
               
            } else if (obj instanceof Integer){
               sb.delete(sb.length() -1, sb.length());
               sb.append("[");
               sb.append(((Integer)obj).intValue());
               sb.append("]");
            }
            if(i < elementPath.size()-1){
               sb.append(".");
            }
         }
         elementPathAsString = sb.toString();
      }
      return elementPathAsString;
   }
   
}