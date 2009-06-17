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
package org.eclipse.osee.ote.core.framework.prompt;

import java.net.UnknownHostException;
import java.rmi.RemoteException;
import org.eclipse.osee.connection.service.IServiceConnector;
import org.eclipse.osee.ote.core.IUserSession;

/**
 * @author Ken J. Aguilar
 */
public class NonBlockingMessagePrompt extends AbstractRemotePrompt implements IResumeResponse {

   /**
    * @param id
    * @param message
    * @throws UnknownHostException
    */
   public NonBlockingMessagePrompt(IServiceConnector connector, String id, String message) throws UnknownHostException {
      super(connector, id, message);
   }

   /**
    * @param session
    * @param timeout
    * @return true if the user did not respond within the time specified and false if the user responded in time
    * @throws Exception
    */
   public void open(IUserSession session) throws Exception {

      session.initiateResumePrompt(this);

   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.core.framework.prompt.IResumeResponse#resume()
    */
   public void resume() throws RemoteException {

   }

}
