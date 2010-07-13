/*
 * Created on Jan 25, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.services.internal;

import java.util.logging.Level;

import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.OseeMessagingStatusCallback;

/**
 * @author Andrew M. Finkbeiner
 *
 */
public class OseeMessagingStatusImpl implements OseeMessagingStatusCallback {

	private String failureMessage;
	private Class<?> clazz;
	
	public OseeMessagingStatusImpl(String failureMessage, Class<?> clazz){
		this.failureMessage = failureMessage;
		this.clazz = clazz;
	}
	
	@Override
	public void fail(Throwable th) {
		th.printStackTrace();
		OseeLog.log(clazz, Level.SEVERE, failureMessage, th);
	}

	@Override
	public void success() {
	}

}
