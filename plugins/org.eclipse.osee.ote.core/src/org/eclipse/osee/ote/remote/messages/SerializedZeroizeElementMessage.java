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

import org.eclipse.osee.ote.message.commands.ZeroizeElement;
import org.eclipse.osee.ote.message.event.SerializedClassMessage;

public class SerializedZeroizeElementMessage extends SerializedClassMessage<ZeroizeElement> {

	public static final String EVENT = "ote/message/zeroizeelement";
	
	public SerializedZeroizeElementMessage() {
		super(EVENT);
	}
	
	public SerializedZeroizeElementMessage(ZeroizeElement commandAdded) throws IOException {
		super(EVENT, commandAdded);
	}
	
	public SerializedZeroizeElementMessage(byte[] bytes){
		super(bytes);
	}
}
