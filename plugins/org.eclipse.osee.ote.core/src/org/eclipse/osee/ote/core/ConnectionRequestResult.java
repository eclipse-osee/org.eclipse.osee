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

import java.io.Serializable;
import org.eclipse.osee.ote.core.environment.UserTestSessionKey;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironment;

/**
 * @author Ken J. Aguilar
 */
public class ConnectionRequestResult implements Serializable {
   private final ITestEnvironment environment;
   private final UserTestSessionKey sessionKey;
   private final ReturnStatus status;

   /**
    * @param environment
    * @param sessionKey
    * @param status
    */
   public ConnectionRequestResult(ITestEnvironment environment, UserTestSessionKey sessionKey, ReturnStatus status) {
      this.environment = environment;
      this.sessionKey = sessionKey;
      this.status = status;
   }

   /**
    * @return the environment
    */
   public ITestEnvironment getEnvironment() {
      return environment;
   }

   /**
    * @return the sessionKey
    */
   public UserTestSessionKey getSessionKey() {
      return sessionKey;
   }

   /**
    * @return the status
    */
   public ReturnStatus getStatus() {
      return status;
   }

}
