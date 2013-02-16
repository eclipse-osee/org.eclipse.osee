/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.jms.bridge;

import org.eclipse.osee.framework.messaging.MessageID;


public enum BridgeMessages implements MessageID {
	
	BYTE_MESSAGE("OTE_EVENT_MESSAGE");
	
	private String id;

	BridgeMessages(String id){
		this.id = id;
	}
	
	@Override
	public Class<?> getSerializationClass() {
		return null;
	}

	@Override
	public boolean isReplyRequired() {
		return false;
	}

	@Override
	public boolean isTopic() {
		return true;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getName() {
		return id;
	}

}
