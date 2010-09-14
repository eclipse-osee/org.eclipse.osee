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
      super(new Status(IStatus.ERROR, "OSEE", formatMessage(message, args)));
   }

   private static String formatMessage(String message, Object... args) {
      try {
         return String.format(message, args);
      } catch (RuntimeException ex) {
         return String.format("exception message could not be formatted: [%s] with the following arguments [%s]",
            message, Collections.toString(",", args));
      }
   }

   public OseeCoreException(String message, Throwable cause) {
      super(new Status(IStatus.ERROR, "OSEE", message, cause));
   }

   public OseeCoreException(Throwable cause) {
      super(new Status(IStatus.ERROR, "OSEE", cause.getMessage(), cause));
   }

   public OseeCoreException(IStatus status) {
      super(status);
   }
}