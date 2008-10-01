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
package org.eclipse.osee.framework.session.management;

/**
 * @author Roberto E. Escobar
 */
public class AuthenticationException extends Exception {

   private static final long serialVersionUID = 1890728724625261131L;

   public AuthenticationException() {
      super();
   }

   /**
    * @param message
    * @param cause
    */
   public AuthenticationException(String message, Throwable cause) {
      super(message, cause);
   }

   /**
    * @param message
    */
   public AuthenticationException(String message) {
      super(message);
   }

   /**
    * @param cause
    */
   public AuthenticationException(Throwable cause) {
      super(cause);
   }

}
