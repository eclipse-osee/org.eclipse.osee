/*
 * Created on Jul 23, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
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
