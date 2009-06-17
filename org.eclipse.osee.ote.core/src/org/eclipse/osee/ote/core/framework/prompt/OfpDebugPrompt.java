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
import org.eclipse.osee.ote.core.TestScript;

/**
 * @author Ken J. Aguilar
 */
public class OfpDebugPrompt extends ScriptPausePromptImpl {

   /**
    * @param script
    * @param id
    * @param message
    * @throws UnknownHostException
    */
   public OfpDebugPrompt(IServiceConnector connector, TestScript script, String id, String message) throws UnknownHostException {
      super(connector, script, id, message);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.core.framework.prompt.ScriptPausePromptImpl#resume()
    */
   @Override
   public void resume() throws RemoteException {
      super.resume();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.core.framework.prompt.AbstractInteractivePrompt#waitForResponse(org.eclipse.osee.ote.core.TestScript, boolean)
    */
   @Override
   protected String waitForResponse(TestScript script, boolean executionUnitPause) throws InterruptedException, Exception {
      synchronized (script) {
         script.getTestEnvironment().getScriptCtrl().setScriptPause(true);
         script.getTestEnvironment().getScriptCtrl().setExecutionUnitPause(executionUnitPause);
         script.getTestEnvironment().getScriptCtrl().unlock();
            script.wait();
         if (exception != null) {
            throw exception;
         }
         return response;
      }
   }
   
   

}
