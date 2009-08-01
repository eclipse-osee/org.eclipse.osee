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
package org.eclipse.osee.ote.core.log;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.eclipse.osee.framework.jdk.core.persistence.Xmlizable;
import org.eclipse.osee.ote.core.MethodFormatter;
import org.eclipse.osee.ote.core.TestCase;
import org.eclipse.osee.ote.core.TestScript;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor;
import org.eclipse.osee.ote.core.environment.interfaces.ITestLogger;
import org.eclipse.osee.ote.core.environment.interfaces.ITestPoint;
import org.eclipse.osee.ote.core.log.record.AttentionRecord;
import org.eclipse.osee.ote.core.log.record.DebugRecord;
import org.eclipse.osee.ote.core.log.record.RequirementRecord;
import org.eclipse.osee.ote.core.log.record.SevereRecord;
import org.eclipse.osee.ote.core.log.record.SupportRecord;
import org.eclipse.osee.ote.core.log.record.TestPointRecord;
import org.eclipse.osee.ote.core.log.record.TestRecord;
import org.eclipse.osee.ote.core.log.record.TraceRecord;
import org.eclipse.osee.ote.core.log.record.WarningRecord;

/**
 * @author Ryan D. Brooks
 * @author Robert A. Fisher
 * @author Andrew M. Finkbeiner
 * @author Charles Shaw
 */
public class TestLogger extends Logger implements ITestLogger {

   /**
    * TestLogger Constructor. Sets logging level and filter.
    */
   public TestLogger() {
      super("osee.test.core.log", null);
      setLevel(Level.ALL);
   }

   /**
    * For communicating with the user running an interactive script only allowed in interactive
    * scripts (use sparingly).
    */
   public void attention(ITestEnvironmentAccessor source, String message) {
      // TODO: this is not just a straight log
      log(new AttentionRecord(source, message));
   }

   /**
    * Records a debugging message in the log. Will never be seen during demos.
    * 
    * @param source The object requesting the logging (Usually "this" is passed in).
    * @param message The log message.
    */
   public void debug(ITestEnvironmentAccessor source, String message) {
      log(new DebugRecord(source, message));
   }

   /**
    * Records a debugging message in the log. Will never be seen during the demos.
    * 
    * @param source The object requesting the logging (Usually "this" is passed in).
    * @param message The log message.
    * @param timeStamp <b>True</b> if you want a time stamp included, <b>False</b> if you do not.
    */
   public void debug(ITestEnvironmentAccessor source, String message, boolean timeStamp) {
      log(new DebugRecord(source, message, timeStamp));
   }

   /**
    * Records a warning message in the log.
    * 
    * @param source The object requesting the logging (Usually "this" is passed in).
    * @param message The log message.
    */
   public void warning(ITestEnvironmentAccessor source, String message) {
      log(new WarningRecord(source, message));
   }

   /**
    * Internal - logging from, say, guts of messaging API
    * 
    * @param source The object requesting the logging (Usually "this" is passed in).
    * @param message The log message.
    */
   public void support(ITestEnvironmentAccessor source, String message) {
      log(new SupportRecord(source, message));
   }

   /**
    * Records a severe message in the log.
    * 
    * @param source The object requesting the logging (Usually "this" is passed in).
    * @param message The log message.
    */
   public void severe(ITestEnvironmentAccessor source, String message) {
      log(new SevereRecord(source, message));
   }

   /**
    * Records a severe message in the log.
    * 
    * @param source The object requesting the logging (Usually "this" is passed in).
    * @param thrown
    */
   public void severe(Object source, Throwable thrown) {
      throwing(source.getClass().getName(), null, thrown);
   }

   public void testpoint(ITestEnvironmentAccessor env, TestScript script, TestCase testCase, boolean passed,
         String testPointName, String exp, String act) {
      log(new TestPointRecord(env, script, testCase, testPointName, exp, act, passed));
   }

   public void testpoint(TestPointRecord record) {
      log(record);
   }

   public void testpoint(ITestEnvironmentAccessor env, TestScript script, TestCase testCase, ITestPoint testPoint) {
      log(new TestPointRecord(env, script, testCase, testPoint));
   }

   /**
    * Records that a test case began in the log.
    * 
    * @param testCase The test case whose start is to be recorded.
    */
   public void testCaseBegan(TestCase testCase) {
      log(testCase.getTestRecord());
   }

   /**
    * Record a trace event. Such as when a function starts or stops.
    * 
    * @param source The object requesting the logging (Usually "this" is passed in).
    * @param objectName
    * @param methodName
    * @param methodArguments
    * @param startFlag
    */
   public void trace(ITestEnvironmentAccessor source, String objectName, String methodName, MethodFormatter methodArguments,
         boolean startFlag) {
      log(new TraceRecord(source, objectName, methodName, methodArguments, startFlag));
   }

   /**
    * Record a requirement to the log.
    * 
    * @param source The object requesting the logging (Usually "this" is passed in).
    * @param message The log message.
    */
   public void requirement(ITestEnvironmentAccessor source, String message) {
      log(new RequirementRecord(source, message));
   }

   /**
    * Log the beginning of a method with no arguments.
    * 
    * @param source The object requesting the logging (Usually "this" is passed in).
    */
   public void methodCalled(ITestEnvironmentAccessor source) {
      methodCalled(source, new MethodFormatter(), 2);
   }

   /**
    * Log the beginning of a method with a formatted string holding the arguments.
    * 
    * @param source The object requesting the logging (Usually "this" is passed in).
    * @param arguments The arguments in a formatted string.
    */
   public void methodCalled(ITestEnvironmentAccessor source, MethodFormatter arguments) {
      methodCalled(source, arguments, 2);
   }

//   /**
//    * Log the beginning of a method with a the MethodFormatter formatted argument object.
//    * 
//    * @param source The object requesting the logging (Usually "this" is passed in).
//    * @param methodFormat Reference to the MethodFormatter formatted argument list.
//    */
//   public void methodCalled(ITestEnvironmentAccessor source, MethodFormatter methodFormat) {
//      methodCalled(source, methodFormat, 2);
//   }

   /**
    * Log the beginning of a method.
    * 
    * @param source The object requesting the logging (Usually "this" is passed in).
    * @param arguments The arguments in a formatted string.
    * @param methodCount The number of methods called to reach this point.
    */
   public void methodCalled(ITestEnvironmentAccessor source, MethodFormatter arguments, int methodCount) {
      String methodName = (new Exception()).getStackTrace()[methodCount].getMethodName();
      trace(source, "", methodName, arguments, true);
   }

   /**
    * Log the start of a method with the object variable name.
    * 
    * @param source The object requesting the logging (Usually "this" is passed in).
    * @param objectName The class variable name.
    */
   public void methodCalledOnObject(ITestEnvironmentAccessor source, String objectName) {
      methodCalledOnObject(source, objectName, new MethodFormatter(), 2);
   }

//   /**
//    * Log the start of a method with the object variable name and a formatted string with the
//    * arguments.
//    * 
//    * @param source The object requesting the logging (Usually "this" is passed in).
//    * @param objectName The class variable name.
//    * @param arguments The arguments in a formatted string.
//    */
//   public void methodCalledOnObject(ITestEnvironmentAccessor source, String objectName, MethodFormatter arguments) {
//      methodCalledOnObject(source, objectName, arguments, 2);
//   }

   /**
    * Log the start of a method with the object variable name and a MethodFormatter formatted
    * argument object.
    * 
    * @param source The object requesting the logging (Usually "this" is passed in).
    * @param objectName The class variable name.
    * @param methodFormat Reference to the MethodFormatter formatted argument list.
    */
   public void methodCalledOnObject(ITestEnvironmentAccessor source, String objectName, MethodFormatter methodFormat) {
      methodCalledOnObject(source, objectName, methodFormat, 2);
   }

   /**
    * Log the start of a method with the object variable name.
    * 
    * @param source The object requesting the logging (Usually "this" is passed in).
    * @param objectName The class variable name.
    * @param arguments The arguments in a formatted string.
    * @param methodCount The number of methods called to reach this point.
    */
   public void methodCalledOnObject(ITestEnvironmentAccessor source, String objectName, MethodFormatter arguments,
         int methodCount) {

      String methodName = (new Exception()).getStackTrace()[methodCount].getMethodName();
      trace(source, objectName, methodName, arguments, true);
   }

   /**
    * Specifies that a method has ended. This must be paired with the methodBegin() call.
    * 
    * @param source The object requesting the logging (Usually "this" is passed in).
    */
   public void methodEnded(ITestEnvironmentAccessor source) {
      trace(source, "", "", new MethodFormatter(), false);
   }

   public void log(TestRecord record) {
      this.log((LogRecord) record);
   }

   public void methodCalledOnObject(ITestEnvironmentAccessor source, String objectName, MethodFormatter methodFormat,
         Xmlizable xmlObject) {
      methodCalledOnObject(source, objectName, methodFormat, xmlObject, 2);
   }

   private void methodCalledOnObject(ITestEnvironmentAccessor source, String objectName, MethodFormatter methodFormat,
         Xmlizable xmlObject, int methodCount) {
      String methodName = (new Exception()).getStackTrace()[methodCount].getMethodName();
      TraceRecord record = new TraceRecord(source, objectName, methodName, methodFormat, true);
      record.addAdditionalElement(xmlObject);
      log(record);
   }
}