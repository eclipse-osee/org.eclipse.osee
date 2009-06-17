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
package org.eclipse.osee.ote.core.environment.command;

import java.io.Serializable;
import org.eclipse.osee.ote.core.OSEEPerson1_4;
import org.eclipse.osee.ote.core.TestException;
import org.eclipse.osee.ote.core.environment.TestEnvironment;
import org.eclipse.osee.ote.core.environment.UserTestSessionKey;
import org.eclipse.osee.ote.core.environment.interfaces.IServiceCommand;

/**
 * @author Ryan D. Brooks
 * @author Andrew M. Finkbeiner
 */
public abstract class TestEnvironmentCommand implements Serializable, IServiceCommand {
   private final UserTestSessionKey key;
   private final CommandDescription commandDescription;

   /**
    * @param callback
    */
   public TestEnvironmentCommand(UserTestSessionKey key, CommandDescription commandDescription) {
      this.key = key;
      this.commandDescription = commandDescription;
   }

   public UserTestSessionKey getUserKey() {
      return key;
   }

   public void executeBase(TestEnvironment environment) {
      execute(environment);
   }

   /* (non-Javadoc)
    * @see net.jini.service.interfaces.IServiceCommand#execute(net.jini.service.interfaces.IService)
    */
   public abstract void execute(TestEnvironment environment) throws TestException;

   /**
    *  
    */
   public CommandDescription getDescription() {
      return commandDescription;
   }

   public OSEEPerson1_4 getUser() {
      return key.getUser();
   }

}