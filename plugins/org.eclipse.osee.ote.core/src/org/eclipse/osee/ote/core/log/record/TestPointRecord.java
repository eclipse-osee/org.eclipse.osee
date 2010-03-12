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
package org.eclipse.osee.ote.core.log.record;

import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.TestCase;
import org.eclipse.osee.ote.core.TestScript;
import org.eclipse.osee.ote.core.environment.TestEnvironment;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor;
import org.eclipse.osee.ote.core.environment.interfaces.ITestPoint;
import org.eclipse.osee.ote.core.log.TestLevel;
import org.eclipse.osee.ote.core.testPoint.CheckPoint;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Ryan D. Brooks
 * @author Andrew M. Finkbeiner
 * @author Robert A. Fisher
 * @author Charles Shaw
 */
public class TestPointRecord extends TestRecord {
   /**
	 * 
	 */
	private static final long serialVersionUID = 921875066237859323L;
private int testPointNumber;
   protected ITestPoint testPoint;
   
   public TestPointRecord(ITestEnvironmentAccessor source,  ITestPoint testPoint, boolean timeStamp) {
	      this(source, source.getTestScript(), source.getTestScript().getTestCase(), testPoint, timeStamp);
	   }
   
   /**
    * TestPointRecord Constructor. Sets up a test point record of the result of the test point.
    * 
    * @param source 		The object requesting the logging.
    * @param accessor 		The test case the test point is in.
    * @param testPoint	 	The TestSubPoint object for the test point.
    * @param timeStamp 		<b>True </b> if a timestamp should be recorded, <b>False </b> if not.
    */
   public TestPointRecord(ITestEnvironmentAccessor source, TestScript script, TestCase accessor, ITestPoint testPoint, boolean timeStamp) {
      super(source, TestLevel.TEST_POINT, "", timeStamp);
      this.testPoint = testPoint;
      script.addTestPoint(testPoint.isPass());
      //this.testCase = accessor.getTestCase();
      if(accessor == null){
//         OseeLog.log(Activator.class, Level.INFO, "test case null");
      } else if(accessor.getTestScript() == null){
    	  OseeLog.log(TestEnvironment.class,		Level.INFO,
   				"test script null");
      }
      if(testPoint == null){
         OseeLog.log(TestEnvironment.class,		Level.INFO,
   				"test point null");
      }
      this.testPointNumber = script.recordTestPoint(testPoint.isPass());      
   }
   
   /**
    * TestPointRecord Constructor. Sets up a test point record of the result of the test point
    *
    * @param source The object requesting the logging.
    * @param script
    * @param testCase
    * @param testPoint The TestPoint object for the test point.
    */
   public TestPointRecord(ITestEnvironmentAccessor source, TestScript script, TestCase testCase, ITestPoint testPoint) {
      this(source, script, testCase, testPoint, true);
   }

   /**
    * TestPointRecord Constructor. Sets up a test point record of the result of the test point.
    * 
    * @param source The object requesting the logging.
    * @param script The test script object
    * @param testCase The test case object
    * @param testPointName The name of the item being tested.
    * @param expected The expected value for the test point.
    * @param actual The actual value for the test point.
    * @param passed <b>True </b> if the test point passed, <b>False </b> if not.
    * @param timeStamp <b>True </b> if a timestamp should be recorded, <b>False </b> if not.
    */
   public TestPointRecord(ITestEnvironmentAccessor source, TestScript script, TestCase testCase, String testPointName, String expected, String actual, boolean passed, boolean timeStamp) {
      this(source, script, testCase, new CheckPoint(testPointName, expected, actual, passed), timeStamp);      
   }

   /**
    * TestPointRecord Constructor. Sets up a test point record of the result of the test point.
    * 
    * @param source 		The object requesting the logging.
    * @param accessor 		The test case the test point is in.
    * @param testPointName 	The name of the item being tested.
    * @param expected 		The expected value for the test point.
    * @param actual 		The actual value for the test point.
    * @param passed 		<b>True </b> if the test point passed, <b>False </b> if not.
    */
   public TestPointRecord(ITestEnvironmentAccessor source, TestScript script, TestCase accessor, String testPointName, String expected, String actual, boolean passed) {
      this(source, script, accessor, testPointName, expected, actual, passed, true);
   }

   /**
    * @return 	Returns the testPoint.
    */
   public int getTestPointNumber() {
      return testPointNumber;
   }

   /**
    * @param testPointNumber
    */
   public void setTestPointNumber(int testPointNumber) {
      this.testPointNumber = testPointNumber;
   }

   /**
    * Converts element to XML formating.
    * 
    * @return Element	XML formated element.
    */
   public Element toXml(Document doc) {
      Element tpElement = doc.createElement("TestPoint");
      tpElement.appendChild(Jaxp.createElement(doc, "Number", String.valueOf(testPointNumber)));
      if (testPoint.isPass()) {
         tpElement.appendChild(Jaxp.createElement(doc, "Result", "PASSED"));
      }
      else {
         tpElement.appendChild(Jaxp.createElement(doc, "Result", "FAILED"));
      }
      tpElement.appendChild(this.getLocation(doc));
      tpElement.appendChild(testPoint.toXml(doc));

      return tpElement;
   }
}