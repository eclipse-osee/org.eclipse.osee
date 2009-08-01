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

import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.elements.FixedPointElement;
import org.eclipse.osee.ote.message.interfaces.ITestAccessor;

/**
 * @author Andy Jury
 */
public class NonMappingFixedPointElement extends FixedPointElement {

  /**
   * Copy constructor.
   * 
   * @param element
   */
   public NonMappingFixedPointElement(FixedPointElement element) {
      super(null, element.getElementName(), element.getMsgData(), 
            0, false, element.getByteOffset(), element.getMsb(), element.getLsb());
      // This is being done so it doesn't get added to the element list hash map.
      this.msg = new WeakReference<Message<?,?,?>>(element.getMessage()); 
      for(Object obj:element.getElementPath()){
         this.getElementPath().add(obj);
      }
   }
   
   /**
    * @param message
    * @param elementName
    * @param messageData
    * @param resolution
    * @param signed
    * @param byteOffset
    * @param msb
    * @param lsb
    */
   public NonMappingFixedPointElement(Message<?,?,?> message, String elementName, MessageData messageData, double resolution, boolean signed, int byteOffset, int msb, int lsb) {
      super(message, elementName, messageData, resolution, signed, byteOffset, msb, lsb);
   }

   /**
    * @param message
    * @param elementName
    * @param messageData
    * @param resolution
    * @param minVal
    * @param signed
    * @param byteOffset
    * @param msb
    * @param lsb
    */
   public NonMappingFixedPointElement(Message<?,?,?> message, String elementName, MessageData messageData, double resolution, double minVal, boolean signed, int byteOffset, int msb, int lsb) {
      super(message, elementName, messageData, resolution, minVal, signed, byteOffset, msb, lsb);
   }

   /**
    * @param message
    * @param elementName
    * @param messageData
    * @param resolution
    * @param signed
    * @param byteOffset
    * @param msb
    * @param lsb
    * @param originalLsb
    * @param originalMsb
    */
   public NonMappingFixedPointElement(Message<?,?,?> message, String elementName, MessageData messageData, double resolution, boolean signed, int byteOffset, int msb, int lsb, int originalLsb, int originalMsb) {
      super(message, elementName, messageData, resolution, signed, byteOffset, msb, lsb, originalLsb, originalMsb);
   }

   /**
    * @param message
    * @param elementName
    * @param messageData
    * @param resolution
    * @param minVal
    * @param signed
    * @param byteOffset
    * @param msb
    * @param lsb
    * @param originalLsb
    * @param originalMsb
    */
   public NonMappingFixedPointElement(Message<?,?,?> message, String elementName, MessageData messageData, double resolution, double minVal, boolean signed, int byteOffset, int msb, int lsb, int originalLsb,
         int originalMsb) {
      super(message, elementName, messageData, resolution, minVal, signed, byteOffset, msb, lsb, originalLsb, originalMsb);
   }

   /**
    * @param message
    * @param elementName
    * @param messageData
    * @param resolution
    * @param minVal
    * @param signed
    * @param bitOffset
    * @param bitLength
    */
   public NonMappingFixedPointElement(Message<?,?,?> message, String elementName, MessageData messageData, double resolution, double minVal, boolean signed, int bitOffset, int bitLength) {
      super(message, elementName, messageData, resolution, minVal, signed, bitOffset, bitLength);
   }


   public void checkForwarding(ITestAccessor accessor, FixedPointElement cause, double value) throws InterruptedException {
      throwNoMappingElementException();
   }

   /**
    * @return Returns the minVal.
    */
   public double getMinVal() {
      throwNoMappingElementException();
      return 0;
      }
   /**
    * @return Returns the resolution.
    */
   public double getResolution() {
      throwNoMappingElementException();
      return 0;
      }

   /**
    * @return Returns the signed.
    */
   public boolean isSigned() {
      throwNoMappingElementException();
      return false;
      }
   
   @Override
   public boolean isNonMappingElement() {
      return true;
   }
}
