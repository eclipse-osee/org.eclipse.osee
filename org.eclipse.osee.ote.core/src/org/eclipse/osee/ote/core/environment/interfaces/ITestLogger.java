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
package org.eclipse.osee.ote.core.environment.interfaces;

import java.util.logging.Handler;
import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.persistence.Xmlizable;
import org.eclipse.osee.ote.core.MethodFormatter;
import org.eclipse.osee.ote.core.TestCase;
import org.eclipse.osee.ote.core.TestScript;
import org.eclipse.osee.ote.core.log.record.TestPointRecord;
import org.eclipse.osee.ote.core.log.record.TestRecord;

public interface ITestLogger {
   public void addHandler(Handler handler);
   public void debug(ITestEnvironmentAccessor source, String message);
   public void debug(ITestEnvironmentAccessor source, String message, boolean timeStamp);
   public void info(String message);
   public void log(TestRecord record);
   public void log(Level level, String message, Throwable th);
   public void methodCalled(ITestEnvironmentAccessor source);
   public void methodCalled(ITestEnvironmentAccessor source, MethodFormatter methodFormat);
   public void methodCalledOnObject(ITestEnvironmentAccessor source, String objectName);
   public void methodCalledOnObject(ITestEnvironmentAccessor source, String objectName, MethodFormatter methodFormat);
   public void methodCalledOnObject(ITestEnvironmentAccessor source, String objectName, MethodFormatter methodFormat, Xmlizable xmlObject);
   public void methodEnded(ITestEnvironmentAccessor source);
   public void removeHandler(Handler handler);
   public void support(ITestEnvironmentAccessor source, String message);
   public void severe(ITestEnvironmentAccessor source, String message);
   public void severe(Object source, Throwable thrown);
   public void severe(String message);
   public void testCaseBegan(TestCase testCase);
   public void testpoint(ITestEnvironmentAccessor env, TestScript script, TestCase testCase, boolean passed, String testPointName, String expected, String actual);
   public void testpoint(ITestEnvironmentAccessor env, TestScript script, TestCase testCase, ITestPoint testPoint);
   public void testpoint(TestPointRecord record);
   public void warning(ITestEnvironmentAccessor source, String message);
   public void warning(String message);
}
