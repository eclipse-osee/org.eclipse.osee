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
