/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.account.admin;

import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public class AccountLoginException extends OseeCoreException {

   private static final long serialVersionUID = 4197083803994404451L;

   public AccountLoginException(String message, Object... args) {
      super(message, args);
   }

   public AccountLoginException(String message, Throwable cause) {
      super(message, cause);
   }

   public AccountLoginException(Throwable cause, String message, Object... args) {
      super(cause, message, args);
   }

   public AccountLoginException(Throwable cause) {
      super(cause);
   }

}
