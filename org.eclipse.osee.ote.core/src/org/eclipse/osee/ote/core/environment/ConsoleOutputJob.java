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
package org.eclipse.osee.ote.core.environment;

import org.eclipse.osee.ote.core.IUserSession;

public class ConsoleOutputJob implements Runnable {

   private final IUserSession callback;
   private final String message;

   public ConsoleOutputJob(IUserSession callback, String message) {
      this.callback = callback;
      this.message = message;
   }

   public void run() {
      try {
         callback.initiateInformationalPrompt(message);
      } catch (Throwable e) {
         e.printStackTrace();
      }
   }

}
