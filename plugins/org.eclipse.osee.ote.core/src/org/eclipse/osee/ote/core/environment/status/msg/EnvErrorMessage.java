package org.eclipse.osee.ote.core.environment.status.msg;

import java.io.IOException;

import org.eclipse.osee.ote.core.environment.status.EnvironmentError;
import org.eclipse.osee.ote.message.event.SerializedClassMessage;

public class EnvErrorMessage extends SerializedClassMessage<EnvironmentError> {

	public static final String EVENT = "ote/status/envError";
	
	public EnvErrorMessage() {
		super(EVENT);
	}

	public EnvErrorMessage(EnvironmentError envError) throws IOException {
		super(EVENT, envError);
	}

	public EnvErrorMessage(byte[] bytes) {
		super(bytes);
	}

}
