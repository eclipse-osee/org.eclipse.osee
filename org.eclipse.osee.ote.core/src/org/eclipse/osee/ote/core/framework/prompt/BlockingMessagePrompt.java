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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import org.eclipse.osee.connection.service.IServiceConnector;
import org.eclipse.osee.ote.core.IUserSession;

/**
 * @author Ken J. Aguilar
 */
public class BlockingMessagePrompt extends AbstractRemotePrompt implements IResumeResponse {

   private final ReentrantLock lock = new ReentrantLock();
   private final Condition responseAvailable = lock.newCondition();
   private boolean responded;

   /**
    * @param id
    * @param message
    * @throws UnknownHostException
    */
   public BlockingMessagePrompt(IServiceConnector connector, String id, String message) throws UnknownHostException {
      super(connector, id, message);
   }

   /**
    * @param session
    * @param timeout
    * @return true if the user did not respond within the time specified and false if the user responded in time
    * @throws Exception
    */
   public boolean open(IUserSession session, int timeout) throws Exception {
      lock.lock();
      try {
         responded = false;
         session.initiateResumePrompt(this);
         long nanos = TimeUnit.SECONDS.toNanos(timeout);

         while (!responded) {
            if (nanos > 0) {
               nanos = responseAvailable.awaitNanos(0);
            } else {
               return true;
            }
         }
         return false;
      } finally {
         lock.unlock();
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.core.framework.prompt.IResumeResponse#resume()
    */
   public void resume() throws RemoteException {
      lock.lock();
      try {
         responded = true;
         responseAvailable.notifyAll();
      } finally {
         lock.unlock();
      }
   }

}
