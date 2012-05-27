package org.eclipse.osee.ote.core.environment.status.msg;

import java.io.IOException;

import org.eclipse.osee.ote.core.environment.status.TestPointUpdate;
import org.eclipse.osee.ote.message.event.SerializedClassMessage;

public class TestPointUpdateMessage extends SerializedClassMessage<TestPointUpdate> {

	public static final String EVENT = "ote/status/testPointUpdate";
	
	public TestPointUpdateMessage() {
		super(EVENT);
	}

	public TestPointUpdateMessage(TestPointUpdate testPointUpdate) throws IOException {
		super(EVENT, testPointUpdate);
	}

	public TestPointUpdateMessage(byte[] bytes){
		super(bytes);
	}
}
