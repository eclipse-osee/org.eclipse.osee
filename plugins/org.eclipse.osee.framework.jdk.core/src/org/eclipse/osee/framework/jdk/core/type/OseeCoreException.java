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

package org.eclipse.osee.framework.jdk.core.type;

import java.util.Arrays;

/**
 * @author Ryan D. Brooks
 */
public class OseeCoreException extends RuntimeException {
   private static final long serialVersionUID = 1L;

   public OseeCoreException(String message, Object... args) {
      super(formatMessage(message, args));
   }

   public OseeCoreException(String message, Throwable cause) {
      super(getMessage(message, cause), cause);
   }

   public OseeCoreException(Throwable cause, String message, Object... args) {
      super(formatMessage(message, args), cause);
   }

   public OseeCoreException(Throwable cause) {
      super(cause);
   }

   private static String formatMessage(String message, Object... args) {
      try {
         return String.format(message, args);
      } catch (RuntimeException ex) {
         return String.format(
            "Exception message could not be formatted: [%s] with the following arguments %s.  Cause [%s]", message,
            Arrays.deepToString(args), ex.toString());
      }
   }

   private static final String getMessage(String message, Throwable cause) {
      if (message == null) {
         if (cause == null) {
            message = "Exception message unavaliable - both exception and message were null";
         } else {
            message = cause.getLocalizedMessage();
         }
      }
      return message;
   }

   public static RuntimeException wrap(Throwable throwable) {
      if (throwable instanceof RuntimeException) {
         return (RuntimeException) throwable;
      }
      return new OseeCoreException(throwable);
   }

   public static void wrapAndThrow(Throwable throwable) {
      throw wrap(throwable);
   }
}