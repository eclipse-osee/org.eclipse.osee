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

import java.lang.reflect.Constructor;
import org.eclipse.osee.framework.core.data.OseeCodeVersion;

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

   public static void wrapAndThrow(Throwable ex) throws OseeCoreException {
      String value = ex != null ? ex.getMessage() : "";
      String finalMsg = String.format(MSG, OseeCodeVersion.getVersion(), value);
      if (ex instanceof RuntimeException) {
         RuntimeException exception = new RuntimeException(finalMsg, ex);
         throw exception;
      } else if (ex instanceof OseeCoreException) {
         throw appendMessage(finalMsg, (OseeCoreException) ex);
      } else {
         throw new OseeWrappedException(finalMsg, ex);
      }
   }

   private static OseeCoreException appendMessage(String message, OseeCoreException ex) {
      OseeCoreException exception;
      try {
         Constructor<? extends OseeCoreException> constructor =
            ex.getClass().getConstructor(String.class, Throwable.class);
         exception = constructor.newInstance(message, ex);
      } catch (Throwable th1) {
         try {
            Constructor<? extends OseeCoreException> constructor =
               ex.getClass().getConstructor(Throwable.class, String.class);
            exception = constructor.newInstance(message, ex);
         } catch (Throwable th2) {
            exception = new OseeCoreException(message, ex);
         }
      }
      return exception;
   }
}