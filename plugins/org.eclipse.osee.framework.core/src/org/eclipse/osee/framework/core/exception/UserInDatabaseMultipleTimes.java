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

/**
 * @author Ryan D. Brooks
 * @author Donald G. Dunne
 */
public class UserInDatabaseMultipleTimes extends UserDataStoreException {

   private static final long serialVersionUID = 5497730793552605424L;

   public UserInDatabaseMultipleTimes(String message, Throwable cause) {
      super(message, cause);
   }

   public UserInDatabaseMultipleTimes(Throwable cause, String message, Object... args) {
      super(cause, message, args);
   }

   public UserInDatabaseMultipleTimes(Throwable cause) {
      super(cause);
   }

   public UserInDatabaseMultipleTimes(String message, Object... args) {
      super(message, args);
   }
}