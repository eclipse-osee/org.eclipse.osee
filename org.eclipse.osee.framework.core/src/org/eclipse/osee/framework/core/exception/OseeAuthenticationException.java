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
package org.eclipse.osee.framework.core.exception;


/**
 * @author Roberto E. Escobar
 */
public class OseeAuthenticationException extends OseeCoreException {

   private static final long serialVersionUID = 1890728724625261131L;

   public static enum AuthenticationErrorCode {
      Success, UserNotFound, InvalidPassword, NoResponse, Unknown;
   }

   private final AuthenticationErrorCode errorCode;

   /**
    * @param message
    * @param cause
    */
   public OseeAuthenticationException(String message, Throwable cause) {
      super(message, cause);
      this.errorCode = null;
   }

   /**
    * @param message
    */
   public OseeAuthenticationException(String message) {
      super(message);
      this.errorCode = null;
   }

   /**
    * @param cause
    */
   public OseeAuthenticationException(Throwable cause) {
      super(cause);
      this.errorCode = null;
   }

   public AuthenticationErrorCode getCode() {
      return errorCode;
   }
}
