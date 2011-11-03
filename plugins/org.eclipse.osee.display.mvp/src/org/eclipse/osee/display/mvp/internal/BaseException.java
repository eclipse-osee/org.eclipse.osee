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
package org.eclipse.osee.display.mvp.internal;

import org.eclipse.osee.framework.jdk.core.util.Collections;

/**
 * @author Roberto E. Escobar
 */
public class BaseException extends Exception {

   private static final long serialVersionUID = -7326541420696736796L;

   protected BaseException(String message, Object... args) {
      super(formatMessage(message, args));
   }

   protected BaseException(Throwable cause, String message, Object... args) {
      super(formatMessage(message, args), cause);
   }

   protected BaseException(Throwable cause) {
      super(cause);
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
}
