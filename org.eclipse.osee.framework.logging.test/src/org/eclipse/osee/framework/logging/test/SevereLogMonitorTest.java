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
package org.eclipse.osee.framework.logging.test;

import java.util.logging.Level;

import junit.framework.TestCase;

import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;

/**
 * @author b1528444
 *
 */
public class SevereLogMonitorTest extends TestCase {

	public void testCatchingOfException(){
		
		boolean madeItInException = false;
		SevereLoggingMonitor monitorLog = new SevereLoggingMonitor();
		OseeLog.registerLoggerListener(monitorLog);
		try{
			throw new Exception("this is my test exception");
		} catch (Exception ex){
			madeItInException = true;
			OseeLog.log(SevereLogMonitorTest.class, Level.SEVERE, "caught our exception in a junit", ex);
		}
		assertTrue(madeItInException);
		assertTrue(String.format("%d SevereLogs during test.", monitorLog.getSevereLogs().size()), monitorLog.getSevereLogs().size() == 1);
	}
	
}
