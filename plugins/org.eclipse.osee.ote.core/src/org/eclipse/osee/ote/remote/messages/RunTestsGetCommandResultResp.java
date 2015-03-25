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
package org.eclipse.osee.ote.remote.messages;

import java.io.IOException;

import org.eclipse.osee.ote.core.framework.command.ITestCommandResult;
import org.eclipse.osee.ote.message.event.SerializedClassMessage;

public class RunTestsGetCommandResultResp extends SerializedClassMessage<ITestCommandResult> {

	public static final String TOPIC = "ote/message/runtests/commandresultresp";
	
	public RunTestsGetCommandResultResp() {
		super(TOPIC);
	}
	
	public RunTestsGetCommandResultResp(ITestCommandResult commandAdded) throws IOException {
		super(TOPIC, commandAdded);
	}
	
	public RunTestsGetCommandResultResp(byte[] bytes){
		super(bytes);
	}
}
