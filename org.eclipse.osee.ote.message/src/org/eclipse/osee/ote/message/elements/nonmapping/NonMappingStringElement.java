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
import org.eclipse.osee.ote.message.elements.StringElement;
import org.eclipse.osee.ote.message.interfaces.ITestAccessor;

/**
 * @author Andy Jury
 */
public class NonMappingStringElement extends StringElement {

   /**
    * Copy constructor.
    * 
    * @param element
    */
    public NonMappingStringElement(StringElement element) {
       super(null, element.getElementName(), element.getMsgData(), 
             element.getByteOffset(), element.getMsb(), element.getLsb());
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
    * @param byteOffset
    * @param msb
    * @param lsb
    */
   public NonMappingStringElement(Message<?,?,?> message, String elementName, MessageData messageData, int byteOffset, int msb, int lsb) {
      super(message, elementName, messageData, byteOffset, msb, lsb);
   }

   /**
    * @param message
    * @param elementName
    * @param messageData
    * @param bitOffset
    * @param bitLength
    */
   public NonMappingStringElement(Message<?,?,?> message, String elementName, MessageData messageData, int bitOffset, int bitLength) {
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
   public NonMappingStringElement(Message<?,?,?> message, String elementName, MessageData messageData, int byteOffset, int msb, int lsb, int originalLsb, int originalMsb) {
      super(message, elementName, messageData, byteOffset, msb, lsb, originalLsb, originalMsb);
   }
   
   public String get(ITestEnvironmentAccessor accessor){
      throwNoMappingElementException();
      return null;
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
   public boolean checkTrimWhiteSpace(ITestAccessor accessor, String value, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return false;
   }
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
    * Verifies that the element is set to some value other than "value" within the number of
    * "milliseconds" passed. Passes if at any point with in the time allowed, the elment is set to a
    * value other than "value".
    * 
    * @param accessor
    * @param checkGroup If this check is part of a larger set of checks which another method is
    *           going to log then the reference to the CheckGroup must be passed and this method
    *           will add the result of the check to the group with out logging a point.
    *           <p>
    *           If an outside method is not going to log the check then a <b>null </b> reference
    *           should be passed and this method will log the test point.
    * @param value value to test against.
    * @param milliseconds Number of milliseconds to wait for the element to equal the "value".
    * @return If the check passed.
    * @throws InterruptedException
    */
   public boolean checkNot(ITestAccessor accessor, CheckGroup checkGroup, String value, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return false;
   }

   /**
    * Verifies that the element is set to the "value" passed for the entire time passed into
    * "milliseconds". Returns value found that caused failure or last value observed if time
    * expires.
    * 
    * @param accessor
    * @param checkGroup If this check is part of a larger set of checks which another method is
    *           going to log then the reference to the CheckGroup must be passed and this method
    *           will add the result of the check to the group with out logging a point.
    *           <p>
    *           If an outside method is not going to log the check then a <b>null </b> reference
    *           should be passed and this method will log the test point.
    * @param value
    * @param milliseconds
    * @return last value observed. Either value expected or value found at timeout.
    * @throws InterruptedException
    */
   public String checkMaintain(ITestAccessor accessor, CheckGroup checkGroup, String value, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return "";
   }

   /**
    * Verifies that the element is set to the "value" passed for the entire time passed into
    * "milliseconds". Returns value found that caused failure or last value observed if time
    * expires.
    * 
    * @param accessor
    * @param checkGroup If this check is part of a larger set of checks which another method is
    *           going to log then the reference to the CheckGroup must be passed and this method
    *           will add the result of the check to the group with out logging a point.
    *           <p>
    *           If an outside method is not going to log the check then a <b>null </b> reference
    *           should be passed and this method will log the test point.
    * @param value
    * @param milliseconds
    * @return last value observed
    * @throws InterruptedException
    */
   public String checkMaintainNot(ITestAccessor accessor, CheckGroup checkGroup, String value, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return "";
   }

   /**
    * Waits until the element has a value other than the "value" passed. Returns last value observed
    * upon a timout.
    * 
    * @param accessor
    * @param value The expected value to wait for.
    * @param milliseconds Number of milliseconds to wait before failing.
    * @return last value observed
    * @throws InterruptedException
    */
   public String waitForNotValue(ITestEnvironmentAccessor accessor, String value, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return "";
   }


   @Override
   public void parseAndSet(ITestEnvironmentAccessor accessor, String value) throws IllegalArgumentException {
      throwNoMappingElementException();
   }
   
   @Override
   public void setAndSend(ITestEnvironmentAccessor accessor, String value) throws IllegalArgumentException {
      throwNoMappingElementException();
   }
   
   @Override
   public boolean isNonMappingElement() {
      return true;
   }
}
