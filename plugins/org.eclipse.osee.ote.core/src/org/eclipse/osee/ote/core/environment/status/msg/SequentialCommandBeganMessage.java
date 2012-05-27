package org.eclipse.osee.ote.core.environment.status.msg;

import java.io.IOException;

import org.eclipse.osee.ote.core.environment.status.SequentialCommandBegan;
import org.eclipse.osee.ote.message.event.SerializedClassMessage;

public class SequentialCommandBeganMessage extends SerializedClassMessage<SequentialCommandBegan> {

	public static final String EVENT = "ote/status/sequentialCommandBegan";
	
	public SequentialCommandBeganMessage() {
		super(EVENT);
	}

	public SequentialCommandBeganMessage(SequentialCommandBegan seqCmdBegan) throws IOException {
		super(EVENT, seqCmdBegan);
	}
	
	public SequentialCommandBeganMessage(byte[] bytes){
		super(bytes);
	}

}
