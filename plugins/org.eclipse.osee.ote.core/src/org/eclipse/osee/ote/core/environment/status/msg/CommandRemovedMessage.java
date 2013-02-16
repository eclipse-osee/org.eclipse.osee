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
package org.eclipse.osee.ote.core.environment.status.msg;

import java.io.IOException;

import org.eclipse.osee.ote.core.environment.status.CommandRemoved;
import org.eclipse.osee.ote.message.event.SerializedClassMessage;

public class CommandRemovedMessage extends SerializedClassMessage<CommandRemoved> {

	public static final String EVENT = "ote/status/commandRemoved";
	
	public CommandRemovedMessage() {
		super(EVENT);
	}

	public CommandRemovedMessage(CommandRemoved cmdRemoved) throws IOException {
		super("ote/status/commandRemoved", cmdRemoved);
	}
	
	public CommandRemovedMessage(byte[] bytes){
		super(bytes);
	}

}
