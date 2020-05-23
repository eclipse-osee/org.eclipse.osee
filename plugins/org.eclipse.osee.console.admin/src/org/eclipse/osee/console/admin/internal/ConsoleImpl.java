/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.console.admin.internal;

import java.util.Dictionary;
import org.eclipse.osee.console.admin.Console;
import org.eclipse.osgi.framework.console.CommandInterpreter;

/**
 * @author Roberto E. Escobar
 */
public class ConsoleImpl implements Console {

   private final CommandInterpreter ci;

   public ConsoleImpl(CommandInterpreter ci) {
      this.ci = ci;
   }

   @Override
   public void write(Object o) {
      ci.print(String.valueOf(o));
   }

   @Override
   public void write(String message, Object... args) {
      ci.print(safeFormat(message, args));
   }

   @Override
   public void write(Throwable throwable) {
      if (throwable != null) {
         ci.printStackTrace(throwable);
      } else {
         ci.print("Attempted to print null throwable");
      }
   }

   @Override
   public void write(String title, Dictionary<?, ?> dictionary) {
      ci.printDictionary(dictionary, title);
   }

   @Override
   public void writeln() {
      ci.println();
   }

   private static String safeFormat(String message, Object... args) {
      String toReturn;
      try {
         toReturn = String.format(message, args);
      } catch (RuntimeException ex) {
         StringBuilder builder = new StringBuilder();
         builder.append("Message could not be formatted:");
         builder.append(message);
         builder.append(" with the following arguments [");
         builder.append(org.eclipse.osee.framework.jdk.core.util.Collections.toString(",", args));
         builder.append("].  Cause [");
         builder.append(ex.toString());
         builder.append("]");
         toReturn = builder.toString();
      }
      return toReturn;
   }

   @Override
   public Object execute(String command) {
      return ci.execute(command);
   }

   @Override
   public void writeln(Object o) {
      write(o);
      writeln();
   }

   @Override
   public void writeln(String message, Object... args) {
      write(message, args);
      writeln();
   }

   @Override
   public void writeln(Throwable throwable) {
      write(throwable);
      writeln();
   }

   @Override
   public void writeln(String title, Dictionary<?, ?> dictionary) {
      write(title, dictionary);
      writeln();
   }

}
