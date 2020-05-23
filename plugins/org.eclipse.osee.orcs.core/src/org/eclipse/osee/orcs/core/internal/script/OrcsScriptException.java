/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.orcs.core.internal.script;

import javax.script.ScriptException;
import org.eclipse.osee.framework.jdk.core.util.Collections;

/**
 * @author Roberto E. Escobar
 */
public class OrcsScriptException extends ScriptException {

   private static final long serialVersionUID = 2686802729964260972L;

   private OrcsScriptException(Exception e) {
      super(e);
   }

   public OrcsScriptException(String s) {
      super(s);
   }

   public OrcsScriptException(String s, String filename) {
      super(s, filename, -1);
   }

   public static OrcsScriptException newException(String filename, String message, Object... args) {
      String msg = formatMessage(message, args);
      return new OrcsScriptException(filename, msg);
   }

   public static OrcsScriptException newException(Exception cause) {
      return new OrcsScriptException(cause);
   }

   public static OrcsScriptException newException(String message, Object... args) {
      return new OrcsScriptException(formatMessage(message, args));
   }

   public static OrcsScriptException newException(String filename, Iterable<String> errors) {
      String errorString = Collections.toString("\n", errors);
      return new OrcsScriptException(errorString, filename);
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
