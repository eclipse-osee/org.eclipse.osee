/*********************************************************************
 * Copyright (c) 2016 Boeing
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
 * @author Morgan E. Cook
 */
public class OperationTimedoutException extends OseeCoreException {

   public OperationTimedoutException(String message, Throwable cause) {
      super(message, cause);
   }

   public OperationTimedoutException(Throwable cause, String message, Object... args) {
      super(cause, message, args);
   }

   public OperationTimedoutException(Throwable cause) {
      super(cause);
   }

   private static final long serialVersionUID = -6697324585250125614L;

   public OperationTimedoutException(String message, Object... args) {
      super(message, args);
   }
}
