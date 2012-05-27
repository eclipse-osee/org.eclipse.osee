package org.eclipse.osee.ote.core.environment.status.msg;

import java.io.IOException;

import org.eclipse.osee.ote.core.environment.status.CommandAdded;
import org.eclipse.osee.ote.message.event.SerializedClassMessage;

public class CommandAddedMessage extends SerializedClassMessage<CommandAdded> {

	public static final String EVENT = "ote/status/commandAdded";
	
	public CommandAddedMessage() {
		super(EVENT);
	}
	
	public CommandAddedMessage(CommandAdded commandAdded) throws IOException {
		super(EVENT, commandAdded);
	}
	
	public CommandAddedMessage(byte[] bytes){
		super(bytes);
	}
}
