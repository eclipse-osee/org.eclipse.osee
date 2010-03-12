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
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.TestCase;
import org.eclipse.osee.ote.core.environment.TestEnvironment;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor;
import org.eclipse.osee.ote.core.log.TestLevel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Ryan D. Brooks
 * @author Andrew M. Finkbeiner
 */
public class TestCaseRecord extends TestRecord {
   /**
	 * 
	 */
	private static final long serialVersionUID = -5049608072548003705L;
	private TestCase testCase;

   /**
    * TestCaseRecorded Constructor. Sets up a test case log message.
    * 
    * @param source The object requesting the logging.
    * @param testCase The current test case to be logged.
    */
   public TestCaseRecord(ITestEnvironmentAccessor source, TestCase testCase) {
      super(source, TestLevel.TEST_POINT, "Test Case " + testCase.getTestCaseNumber() + " began.", true);
      this.testCase = testCase;
      if (testCase.getTestEnvironment() == null){
         OseeLog.log(TestEnvironment.class, 
               Level.INFO,
 				"env null");
      }
      /*
      else if (testCase.getTestEnvironment().getStatusBoard() == null)
       	OseeLog.log(Activator.class, Level.INFO, "nullstatus board");

     testCase.getTestEnvironment().getStatusBoard().setCurrentScriptCurrentTestCase(testCase.getTestCaseNumber());
     */
   }

   /**
    * Convert an element to XML format.
    * 
    * @return XML formated element.
    */
   public Element toXml(Document doc) {
      return testCase.toXml(doc);
   }
}