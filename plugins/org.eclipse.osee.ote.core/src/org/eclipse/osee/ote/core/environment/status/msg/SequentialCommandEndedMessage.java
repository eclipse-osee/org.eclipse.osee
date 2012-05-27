package org.eclipse.osee.ote.core.environment.status.msg;

import java.io.IOException;

import org.eclipse.osee.ote.core.environment.status.SequentialCommandEnded;
import org.eclipse.osee.ote.message.event.SerializedClassMessage;

public class SequentialCommandEndedMessage extends SerializedClassMessage<SequentialCommandEnded> {

	public static final String EVENT = "ote/status/sequentialCommandEnded";
	
	public SequentialCommandEndedMessage() {
		super(EVENT);
	}

	public SequentialCommandEndedMessage(SequentialCommandEnded seqCmdEnded) throws IOException {
		super(EVENT, seqCmdEnded);
	}

	public SequentialCommandEndedMessage(byte[] bytes){
		super(bytes);
	}
	
}
