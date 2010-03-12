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
package org.eclipse.osee.ote.service;

import org.eclipse.osee.ote.core.environment.interfaces.IHostTestEnvironment;
import org.eclipse.osee.ote.core.framework.prompt.IPassFailPromptResponse;
import org.eclipse.osee.ote.core.framework.prompt.IResumeResponse;
import org.eclipse.osee.ote.core.framework.prompt.IUserInputPromptResponse;

/**
 * Test sessions will delegate responsibilities to sub classes of this interface.
 * 
 * @author Ken J. Aguilar
 */
public interface SessionDelegate {
   void handlePassFail(IPassFailPromptResponse prompt) throws Exception;

   void handlePause(IResumeResponse prompt) throws Exception;

   void handleInformationPrompt(String message) throws Exception;

   void handleUserInput(final IUserInputPromptResponse prompt) throws Exception;

   /**
    * When connected to a {@link IHostTestEnvironment}, it is possible for the environment to request files from the
    * client. This method will be called by the {@link IHostTestEnvironment} with a path that should make sense to the
    * client.
    * 
    * @param path
    * @return an array bytes that represent the content of the requested file
    * @throws Exception
    */
   byte[] getFile(String path) throws Exception;
   long getFileDate(String path) throws Exception;
   String getFileVersion(String path) throws Exception;
}
