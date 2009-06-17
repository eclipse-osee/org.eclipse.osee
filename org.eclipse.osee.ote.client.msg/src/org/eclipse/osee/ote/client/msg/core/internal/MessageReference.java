/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.client.msg.core.internal;

import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.enums.MemType;
import org.eclipse.osee.ote.message.tool.MessageMode;

/**
 * @author Ken J. Aguilar
 *
 */
public class MessageReference {

	private final MemType type;
	private final MessageMode mode;
	private final String msgClassName;

	public MessageReference(MemType type, MessageMode mode, String msgClassName) {
		this.type = type;
		this.mode = mode;
		this.msgClassName = msgClassName;
	}
	
	public MessageReference(Message msg) {
		this.type = msg.getDefaultMessageData().getType();
		this.mode = MessageMode.READER;
		this.msgClassName = msg.getMessageName();
	}

	public String getMsgClass() {
		return msgClassName;
	}

	public MemType getType() {
		return type;
	}

	public MessageMode getMode() {
		return mode;
	}
	
	@Override
	public boolean equals(Object obj) {
		MessageReference otherRef = (MessageReference) obj;
		return msgClassName.equals(otherRef.msgClassName) && type == otherRef.type && mode == otherRef.mode;
	}

	@Override
	public int hashCode() {
		return msgClassName.hashCode() ^ mode.hashCode() ^ type.hashCode();
	}
	
}
