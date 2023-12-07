/*********************************************************************
 * Copyright (c) 2023 Boeing
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
 * @author Luciano T. Vaglienti
 */
public class RelTableInvalidException extends OseeCoreException {

   private static final long serialVersionUID = 1L;

   public RelTableInvalidException(String message, Object... args) {
      super(message, args);
   }

   public RelTableInvalidException(String message, Throwable cause) {
      super(message, cause);
   }

   public RelTableInvalidException(Throwable cause, String message, Object... args) {
      super(cause, message, args);
   }

   public RelTableInvalidException(Throwable cause) {
      super(cause);
   }
}