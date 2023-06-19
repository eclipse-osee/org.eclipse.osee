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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.console.admin.ConsoleCommand;
import org.eclipse.osee.console.admin.ConsoleParameters;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osgi.framework.console.CommandInterpreter;

/**
 * @author Roberto E. Escobar
 */
public final class ConsoleAdminUtils {

   private ConsoleAdminUtils() {
      // Utility class
   }

   public static String getContextName(Map<String, String> props) {
      String contextName = props.get("context.name");
      if (!isValid(contextName)) {
         contextName = getComponentName(props);
      }
      return normalize(contextName);
   }

   public static String getComponentName(Map<String, String> props) {
      return props.get("component.name");
   }

   private static String normalize(String contextName) {
      return contextName != null && !contextName.startsWith("/") ? "/" + contextName : contextName;
   }

   private static boolean isValid(String value) {
      return value != null && value.length() > 0;
   }

   public static Map<String, String> toMap(String componentName, String contextName) {
      Map<String, String> data = new HashMap<>();
      data.put("component.name", componentName);
      data.put("context.name", contextName);
      return data;
   }

   public static List<ConsoleCommand> sort(Collection<ConsoleCommand> origCommands) {
      List<ConsoleCommand> cmds = new ArrayList<>(origCommands);
      Collections.sort(cmds, new Comparator<ConsoleCommand>() {
         @Override
         public int compare(ConsoleCommand o1, ConsoleCommand o2) {
            return o1.getName().compareTo(o2.getName());
         }
      });
      return cmds;
   }

   private static String removeQuotes(String value) {
      String toReturn = value;
      int stringLength = toReturn.length();
      if (stringLength > 1) {
         if (toReturn.startsWith("\"") && toReturn.endsWith("\"") || toReturn.startsWith("'") && toReturn.endsWith(
            "'")) {
            toReturn = toReturn.substring(1, stringLength - 1);
         }
      }
      return toReturn;
   }

   public static ConsoleParameters parse(CommandInterpreter ci) {
      String commandName = "";
      StringBuilder rawString = new StringBuilder();
      PropertyStore store = new PropertyStore();
      Set<String> options = new HashSet<>();

      int count = 0;
      String arg = ci.nextArgument();
      while (arg != null) {
         if (Strings.isValid(arg)) {
            rawString.append(arg);
            if (count == 0) {
               commandName = arg.toLowerCase();
            } else {
               String[] entries = arg.split("=");
               if (entries.length == 2) {
                  String key = entries[0];
                  String value = entries[1];
                  if (Strings.isValid(key) && Strings.isValid(value)) {
                     value = removeQuotes(value);
                     store.put(key, value);

                     String[] arrayVal = value.split("[,;& ]\\s*");
                     if (arrayVal != null && arrayVal.length > 0) {
                        store.put(key, arrayVal);
                     }
                  }
               } else if (arg.startsWith("-")) {
                  options.add(arg.substring(1));
               }
            }
         }
         arg = ci.nextArgument();
         count++;
         if (Strings.isValid(arg)) {
            rawString.append(" ");
         }
      }
      return new ConsoleParametersImpl(commandName, rawString.toString(), store, options);
   }

   public static void checkNotNull(Object object, String objectName) throws IllegalArgumentException {
      if (object == null) {
         throw new IllegalArgumentException(String.format("%s cannot be null", objectName));
      }
   }

   public static void checkNotNull(Object object, String objectName, String details, Object... data)
      throws IllegalArgumentException {
      if (object == null) {
         String message = String.format(details, data);
         throw new IllegalArgumentException(String.format("%s cannot be null - %s", objectName, message));
      }
   }

   public static void checkNotNullOrEmpty(String object, String objectName) throws IllegalArgumentException {
      checkNotNull(object, objectName);
      if (object.length() == 0) {
         throw new IllegalArgumentException(String.format("%s cannot be empty", objectName));
      }
   }
}
