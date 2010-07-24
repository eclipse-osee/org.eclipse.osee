/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.messaging.internal.activemq;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;

import org.eclipse.osee.framework.messaging.internal.FailoverConnectionNode;

/**
 * @author Andrew M. Finkbeiner
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
