package org.eclipse.osee.ote.core.environment.status.msg;

import java.io.IOException;

import org.eclipse.osee.ote.core.environment.status.TestServerCommandComplete;
import org.eclipse.osee.ote.message.event.SerializedClassMessage;

public class TestServerCommandCompleteMessage extends SerializedClassMessage<TestServerCommandComplete> {

	public static final String EVENT = "ote/status/testServerCommandComplete";
	
	public TestServerCommandCompleteMessage() {
		super(EVENT);
	}

	public TestServerCommandCompleteMessage(
			TestServerCommandComplete testServerCommandComplete) throws IOException {
		super(EVENT, testServerCommandComplete);
	}
	
	public TestServerCommandCompleteMessage(byte[] bytes){
		super(bytes);
	}

}
