/*
 * Created on Mar 30, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.internal.activemq;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;

import org.eclipse.osee.framework.messaging.internal.FailoverConnectionNode;

/**
 * @author b1528444
 *
 */
public class OseeExceptionListener implements ExceptionListener {

	private FailoverConnectionNode failoverConnectionNode;

	@Override
	public void onException(JMSException ex) {
		if(failoverConnectionNode != null){
			failoverConnectionNode.onException(ex);
		}
	}

	public void setListener(FailoverConnectionNode failoverConnectionNode) {
		this.failoverConnectionNode = failoverConnectionNode;
	}

}
