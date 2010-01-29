/*
 * Created on Jan 20, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging;

import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author b1528444
 *
 */
public interface ReplyConnection {
	public boolean isReplyRequested();
	public void send(Object body, Class<?> clazz, final OseeMessagingStatusCallback statusCallback) throws OseeCoreException;
}
