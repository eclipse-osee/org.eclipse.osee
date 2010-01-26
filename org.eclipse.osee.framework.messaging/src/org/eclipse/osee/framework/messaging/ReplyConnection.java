/*
 * Created on Jan 20, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging;

/**
 * @author b1528444
 *
 */
public interface ReplyConnection {
	public boolean isReplyRequested();
	public void send(Object body, final OseeMessagingStatusCallback statusCallback);
}
