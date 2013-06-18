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

import org.junit.Assert;

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
