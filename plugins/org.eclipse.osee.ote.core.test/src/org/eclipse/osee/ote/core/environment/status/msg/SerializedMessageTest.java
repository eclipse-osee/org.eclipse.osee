package org.eclipse.osee.ote.core.environment.status.msg;

import java.io.IOException;

import junit.framework.Assert;

import org.eclipse.osee.ote.core.environment.status.EnvironmentError;
import org.junit.Test;

public class SerializedMessageTest {

	@Test
	public void testErrorMessage() throws IOException, ClassNotFoundException {
		String exMessage = "MY EX TEST";
		EnvironmentError error = new EnvironmentError(new Exception(exMessage));
		EnvErrorMessage msg = new EnvErrorMessage();
		msg.setObject(error);
		EnvironmentError errorBack = msg.getObject();
		Assert.assertEquals(errorBack.getErr().getMessage(), exMessage);
	}

}
