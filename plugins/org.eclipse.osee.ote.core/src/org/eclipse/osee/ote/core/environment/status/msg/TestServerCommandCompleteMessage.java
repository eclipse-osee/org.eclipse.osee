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
