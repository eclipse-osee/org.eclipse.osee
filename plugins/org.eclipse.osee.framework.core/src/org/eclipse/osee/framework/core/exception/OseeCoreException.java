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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.jdk.core.util.Collections;

/**
 * @author Ryan D. Brooks
 */
public class OseeCoreException extends CoreException {
   private static final long serialVersionUID = 1L;

   public OseeCoreException(String message, Object... args) {
      super(createSafeStatus(formatMessage(message, args), null));
   }

   public OseeCoreException(String message, Throwable cause) {
      super(createSafeStatus(message, cause));
   }

   public OseeCoreException(Throwable cause, String message, Object... args) {
      super(createSafeStatus(formatMessage(message, args), cause));
   }

   public OseeCoreException(Throwable cause) {
      super(createSafeStatus(null, cause));
   }

   public OseeCoreException(IStatus status) {
      super(safeStatus(status));
   }

   private static IStatus safeStatus(IStatus status) {
      IStatus toReturn = status;
      if (toReturn == null) {
         toReturn = createSafeStatus("Exception message unavaliable - status was null", null);
      }
      return toReturn;
   }

   private static String formatMessage(String message, Object... args) {
      try {
         return String.format(message, args);
      } catch (RuntimeException ex) {
         return String.format(
            "Exception message could not be formatted: [%s] with the following arguments [%s].  Cause [%s]", message,
            Collections.toString(",", args), ex.toString());
      }
   }

   private static IStatus createSafeStatus(String message, Throwable cause) {
      IStatus status = null;
      if (message != null && cause != null) {
         status = new Status(IStatus.ERROR, "OSEE", message, cause);
      } else if (cause != null) {
         status = new Status(IStatus.ERROR, "OSEE", cause.getMessage(), cause);
      } else if (message != null) {
         status = new Status(IStatus.ERROR, "OSEE", message);
      } else {
         status =
            new Status(IStatus.ERROR, "OSEE", "Exception message unavaliable - both exception and message were null");
      }
      return status;
   }
}