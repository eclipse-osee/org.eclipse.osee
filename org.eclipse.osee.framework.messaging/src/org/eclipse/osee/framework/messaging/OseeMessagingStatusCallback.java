/*
 * Created on Oct 19, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging;


/**
 * @author b1528444
 *
 */
public interface OseeMessagingStatusCallback {
	void success();
	void fail(Throwable th);
}
