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
package org.eclipse.osee.ote.core.testPoint;

import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.ote.core.environment.interfaces.ITestPoint;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * @author Robert A. Fisher
 */
public class CheckPoint implements ITestPoint {
   private final String testPointName;
   private String expected;
   private final String actual;
   private final boolean pass;
   private final long elpasedTime;
   private final int numTransmissions;

   /**
    * CheckPoint objects are used for describing the result of a check and can be logged directly to a the logger as a testPoint or can be added to a CheckGroup
    * if it is just a part of a larger series of checks being performed that all constitute one overall check.
    * 
    * @param testPointName 	The item being tested. (i.e. TSD Button).
    * @param expected 		The expected condition for a pass point.
    * @param actual 		The actual condition during the check.
    * @param pass 			The result of the check.
    */
   public CheckPoint(String testPointName, String expected, String actual, boolean pass, long elapsedTime) {
      this(testPointName, expected, actual, pass, 0, elapsedTime);
   }

   public CheckPoint(String testPointName, String expected, String actual, boolean pass, int numTransmissions, long elapsedTime) {
      this.testPointName = testPointName;
      this.expected = expected.equals("") ? " " : this.convertNonPrintableCharacers(expected);
      this.actual = actual.equals("") ? " " : this.convertNonPrintableCharacers(actual);
      this.pass = pass;
      this.elpasedTime = elapsedTime;
      this.numTransmissions = numTransmissions;
   }

   public CheckPoint(String testPointName, Object expected, Object actual, boolean pass, long elapsedTime){
      this(testPointName, expected.toString(), actual.toString(), pass, elapsedTime);
   }

   public CheckPoint(String testPointName, Object expected, Object actual, boolean pass){
      this(testPointName, expected.toString(), actual.toString(), pass, 0);
   }

   public CheckPoint(String testPointName, String expected, String actual, boolean pass) {
      this(testPointName, expected, actual, pass, 0);      
   }
   /**
    * @return Returns the actual.
    */
   public String getActual() {
      return actual;
   }

   /**
    * @return Returns the expected.
    */
   public String getExpected() {
      return expected;
   }

   /**
    * @return Returns the pass.
    */
   public boolean isPass() {
      return pass;
   }


   public void setExpected(String expected) {
      this.expected = expected;
   }

   public Element toXml(Document doc) {
      Element checkPointElement = doc.createElement("CheckPoint");

      checkPointElement.appendChild(Jaxp.createElement(doc, "TestPointName", testPointName));
      checkPointElement.appendChild(Jaxp.createElement(doc, "Expected", expected));
      checkPointElement.appendChild(Jaxp.createElement(doc, "Actual", actual));
      checkPointElement.appendChild(Jaxp.createElement(doc, "Result", pass ? "PASSED" : "FAILED"));
      checkPointElement.appendChild(Jaxp.createElement(doc, "ElapsedTime", Long.toString(this.elpasedTime)));   
      checkPointElement.appendChild(Jaxp.createElement(doc, "NumberOfTransmissions", Integer.toString(this.numTransmissions)));  

      return checkPointElement;
   }

   /**
    * @return the elpasedTime
    */
   public long getElpasedTime() {
      return elpasedTime;
   }

   /**
    * @return the numTransmissions
    */
   public int getNumTransmissions() {
      return numTransmissions;
   }

   private String convertNonPrintableCharacers( String message)
   {
      StringBuffer buff = new StringBuffer();
      char currentChar;
      for( int i = 0 ; i < message.length() ; i++ )
      {
         currentChar = message.charAt(i);
         if( currentChar < 32 || currentChar > 126 )
         {
            buff.append(" ASCII=" + (int)currentChar + " ");
         } else if (currentChar == '<'){
            buff.append(" less-than ");
         } else if (currentChar == '>'){
            buff.append(" greater-than ");
         } else if (currentChar == '&'){
            buff.append(" ampersand ");
         }
         else
            buff.append(currentChar);
      }

      return buff.toString();
   }
}