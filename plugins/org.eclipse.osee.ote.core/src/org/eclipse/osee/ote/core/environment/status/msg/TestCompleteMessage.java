package org.eclipse.osee.ote.core.environment.status.msg;

import java.io.IOException;

import org.eclipse.osee.ote.core.environment.status.TestComplete;
import org.eclipse.osee.ote.message.event.SerializedClassMessage;

public class TestCompleteMessage extends SerializedClassMessage<TestComplete> {

	public static final String EVENT = "ote/status/testComplete";
	
	public TestCompleteMessage() {
		super(EVENT);
	}

	public TestCompleteMessage(TestComplete testComplete) throws IOException {
		super(EVENT, testComplete);
	}

	public TestCompleteMessage(byte[] bytes){
		super(bytes);
	}
	
}
