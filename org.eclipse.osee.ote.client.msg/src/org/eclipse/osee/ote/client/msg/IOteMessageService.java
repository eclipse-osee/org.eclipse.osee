/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.client.msg;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.eclipse.osee.ote.client.msg.core.IMessageSubscription;
import org.eclipse.osee.ote.message.commands.RecordCommand;
import org.eclipse.osee.ote.message.tool.IFileTransferHandle;

/**
 * @author Ken J. Aguilar
 *
 */
public interface IOteMessageService {
	IMessageSubscription subscribe(String name);
	IFileTransferHandle startRecording(String fileName, List<RecordCommand.MessageRecordDetails> list) throws FileNotFoundException, IOException;
	void stopRecording() throws Exception;
}
