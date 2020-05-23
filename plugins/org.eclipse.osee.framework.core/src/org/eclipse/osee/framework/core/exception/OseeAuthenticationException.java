/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.core.exception;

import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public class OseeAuthenticationException extends OseeCoreException {

   private static final long serialVersionUID = 1890728724625261131L;

   public static enum AuthenticationErrorCode {
      Success,
      UserNotFound,
      InvalidPassword,
      NoResponse,
      Unknown;
   }

   private final AuthenticationErrorCode errorCode;

   public OseeAuthenticationException(String message, Object... args) {
      super(message, args);
      this.errorCode = null;
   }

   public OseeAuthenticationException(String message, Throwable cause) {
      super(message, cause);
      this.errorCode = null;
   }

   public OseeAuthenticationException(Throwable cause, String message, Object... args) {
      super(cause, message, args);
      this.errorCode = null;
   }

   public OseeAuthenticationException(Throwable cause) {
      super(cause);
      this.errorCode = null;
   }

   public AuthenticationErrorCode getCode() {
      return errorCode;
   }
}
