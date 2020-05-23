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

package org.eclipse.osee.orcs;

import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;

/**
 * @author Roberto E. Escobar
 */
public class OrcsException extends OseeCoreException {

   private static final long serialVersionUID = -7326541420696736796L;

   public OrcsException(String message, Object... args) {
      super(formatMessage(message, args));
   }

   public OrcsException(Throwable cause, String message) {
      super(formatMessage(message), cause);
   }

   public OrcsException(Throwable cause, String message, Object... args) {
      super(formatMessage(message, args), cause);
   }

   public OrcsException(Throwable cause) {
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
