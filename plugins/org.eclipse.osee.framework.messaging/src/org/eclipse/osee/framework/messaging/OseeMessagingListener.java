/*
 * Created on Jul 27, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging;

import java.util.Map;

/**
 * @author Andrew M. Finkbeiner
 */
public abstract class OseeMessagingListener {

	private Class<?> clazz;
	
	public OseeMessagingListener() {
		this.clazz = null;
	}

	public OseeMessagingListener(Class<?> clazz) {
		this.clazz = clazz;
	}

	public Class<?> getClazz(){
		return clazz;
	}
	
	public abstract void process(Object message, Map<String, Object> headers, ReplyConnection replyConnection);
}
