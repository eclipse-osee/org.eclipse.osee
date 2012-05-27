package org.eclipse.osee.ote.core.environment.status.msg;

import java.io.IOException;

import org.eclipse.osee.ote.core.environment.status.TestStart;
import org.eclipse.osee.ote.message.event.SerializedClassMessage;

public class TestStartMessage extends SerializedClassMessage<TestStart> {

	public static final String EVENT = "ote/status/testStart";
	
	public TestStartMessage() {
		super(EVENT);
	}

	public TestStartMessage(TestStart testStart) throws IOException {
		super(EVENT, testStart);
	}

	public TestStartMessage(byte[] bytes) {
		super(bytes);
	}

}
