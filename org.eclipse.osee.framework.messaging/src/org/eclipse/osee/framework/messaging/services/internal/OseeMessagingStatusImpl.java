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
 * @author b1528444
 *
 */
class OseeMessagingStatusImpl implements OseeMessagingStatusCallback {

	private String message;
	private Class<?> clazz;
	
	public OseeMessagingStatusImpl(String message, Class<?> clazz){
		this.message = message;
		this.clazz = clazz;
	}
	
	@Override
	public void fail(Throwable th) {
		OseeLog.log(clazz, Level.SEVERE, message, th);
	}

	@Override
	public void success() {
	}

}
