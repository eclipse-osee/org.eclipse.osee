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
package org.eclipse.osee.ote.core;

import org.eclipse.osee.framework.messaging.Message;
import org.eclipse.osee.ote.core.framework.prompt.IPassFailPromptResponse;
import org.eclipse.osee.ote.core.framework.prompt.IResumeResponse;
import org.eclipse.osee.ote.core.framework.prompt.IUserInputPromptResponse;

public interface IUserSession {

   public OSEEPerson1_4 getUser() throws Exception;

   public String getAddress() throws Exception;

   /**
    * returns the contents of the requested file on the client session
    * 
    * @param workspacePath
    * @return the byte contents of the requested file or null if it does not exist
    * @throws Exception
    */
   public byte[] getFile(String workspacePath) throws Exception;

   public long getFileDate(String workspacePath) throws Exception;

   public String getFileVersion(String workspacePath) throws Exception;

   public void initiateInformationalPrompt(String message) throws Exception;

   public void initiatePassFailPrompt(IPassFailPromptResponse prompt) throws Exception;

   public void initiateUserInputPrompt(IUserInputPromptResponse prompt) throws Exception;

   public void initiateResumePrompt(IResumeResponse prompt) throws Exception;

   public void sendMessageToClient(Message message)  throws Exception;
   
   public boolean isAlive() throws Exception;

}
