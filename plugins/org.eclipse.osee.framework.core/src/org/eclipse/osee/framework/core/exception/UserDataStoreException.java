/*******************************************************************************
 * Copyright (c) 2013 Boeing.
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
public class UserDataStoreException extends OseeCoreException {

   private static final long serialVersionUID = 6332029869706688372L;

   public UserDataStoreException(String message, Throwable cause) {
      super(message, cause);
   }

   public UserDataStoreException(Throwable cause, String message, Object... args) {
      super(cause, message, args);
   }

   public UserDataStoreException(Throwable cause) {
      super(cause);
   }

   public UserDataStoreException(String message, Object... args) {
      super(message, args);
   }
}