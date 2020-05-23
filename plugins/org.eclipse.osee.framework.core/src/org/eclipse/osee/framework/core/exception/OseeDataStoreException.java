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
 * @author Ryan D. Brooks
 */
public class OseeDataStoreException extends OseeCoreException {

   public OseeDataStoreException(String message, Throwable cause) {
      super(message, cause);
   }

   public OseeDataStoreException(Throwable cause, String message, Object... args) {
      super(cause, message, args);
   }

   public OseeDataStoreException(Throwable cause) {
      super(cause);
   }

   private static final long serialVersionUID = 7339636628746394923L;

   public OseeDataStoreException(String message, Object... args) {
      super(message, args);
   }
}