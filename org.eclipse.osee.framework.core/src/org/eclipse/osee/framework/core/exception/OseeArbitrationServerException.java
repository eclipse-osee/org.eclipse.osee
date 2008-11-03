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

import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;

/**
 * @author Roberto E. Escobar
 */
public class OseeArbitrationServerException extends OseeDataStoreException {

   private static final long serialVersionUID = -1622659773564525533L;

   /**
    * @param message
    */
   public OseeArbitrationServerException(String message) {
      super(message);
   }

   /**
    * @param message
    * @param cause
    */
   public OseeArbitrationServerException(String message, Throwable cause) {
      super(message, cause);
   }

   /**
    * @param cause
    */
   public OseeArbitrationServerException(Throwable cause) {
      super(cause);
   }
}
