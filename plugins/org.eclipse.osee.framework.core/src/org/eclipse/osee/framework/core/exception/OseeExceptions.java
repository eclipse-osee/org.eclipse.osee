/*******************************************************************************
 * Copyright (c) 2010 Boeing.
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
 * {@link OseeExceptionsTest}
 * 
 * @author Roberto E. Escobar
 */
public final class OseeExceptions {

   private static final String MSG = "OSEE Version: [%s]\nException message: [%s]";

   private OseeExceptions() {
      // private empty constructor is to prevent external instantiation
   }

   public static void wrapAndThrow(Throwable throwable) throws OseeCoreException {
      if (throwable instanceof RuntimeException) {
         throw (RuntimeException) throwable;
      }
      throw new OseeCoreException(throwable);
   }
}