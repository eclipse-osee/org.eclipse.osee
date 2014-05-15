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
package org.eclipse.osee.ote.remote.messages;

import java.io.IOException;

import org.eclipse.osee.ote.message.commands.SubscribeToMessage;
import org.eclipse.osee.ote.message.event.SerializedClassMessage;

public class SerializedSubscribeToMessage extends SerializedClassMessage<SubscribeToMessage> {

	public static final String EVENT = "ote/message/subscriptiondetails";
	
	public SerializedSubscribeToMessage() {
		super(EVENT);
	}
	
	public SerializedSubscribeToMessage(SubscribeToMessage commandAdded) throws IOException {
		super(EVENT, commandAdded);
	}
	
	public SerializedSubscribeToMessage(byte[] bytes){
		super(bytes);
	}
}
