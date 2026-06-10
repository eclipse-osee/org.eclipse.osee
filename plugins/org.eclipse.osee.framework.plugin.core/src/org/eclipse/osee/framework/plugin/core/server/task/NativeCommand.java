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

package org.eclipse.osee.framework.plugin.core.server.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Ryan D. Brooks
 */
public class NativeCommand extends Command {
   public static final int NATIVE_CMD_ID = 0;

   /**
    * Allowlist of permitted executable names. Only commands in this set may be invoked
    * via the task server protocol to prevent arbitrary OS command injection.
    * Extend this set as needed for legitimate use cases.
    */
   private static final Set<String> ALLOWED_COMMANDS;
   static {
      Set<String> cmds = new HashSet<>();
      // Add permitted commands here as needed
      cmds.add("ls");
      cmds.add("dir");
      cmds.add("echo");
      cmds.add("ps");
      ALLOWED_COMMANDS = Collections.unmodifiableSet(cmds);
   }

   public NativeCommand() {
      super(NATIVE_CMD_ID);
   }

   public void sendNativeCommand(ObjectOutputStream toServer, String[] callAndArgs) throws IOException {
      Object[] params = new Object[callAndArgs.length];
      System.arraycopy(callAndArgs, 0, params, 0, params.length);
      sendCommand(toServer, params);
   }

   @Override
   public Object invoke(Object... parameters) throws IOException {
      if (parameters == null || parameters.length == 0) {
         throw new IOException("No command specified");
      }

      String[] callAndArgs = new String[parameters.length];
      System.arraycopy(parameters, 0, callAndArgs, 0, parameters.length);

      // Validate the executable against the allowlist to prevent OS command injection
      String executable = extractExecutableName(callAndArgs[0]);
      if (!ALLOWED_COMMANDS.contains(executable)) {
         throw new IOException("Command not permitted: " + executable);
      }

      // Validate arguments do not contain shell metacharacters
      for (int i = 1; i < callAndArgs.length; i++) {
         if (containsShellMetacharacters(callAndArgs[i])) {
            throw new IOException("Invalid characters in command argument at position " + i);
         }
      }

      ProcessBuilder processBuilder = new ProcessBuilder(callAndArgs);
      processBuilder.redirectErrorStream(true);
      Process process = processBuilder.start();

      ArrayList<String> lines = new ArrayList<>();
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
         String line;
         while ((line = reader.readLine()) != null) {
            lines.add(line);
         }
      }
      return lines.toArray(new String[lines.size()]);
   }

   /**
    * Extracts the base executable name from a potentially fully-qualified path.
    */
   private static String extractExecutableName(String command) {
      if (command == null) {
         return "";
      }
      // Strip any path prefix to get just the executable name
      int lastSlash = Math.max(command.lastIndexOf('/'), command.lastIndexOf('\\'));
      return lastSlash >= 0 ? command.substring(lastSlash + 1) : command;
   }

   /**
    * Checks for shell metacharacters that could be used for command injection.
    */
   private static boolean containsShellMetacharacters(String arg) {
      if (arg == null) {
         return false;
      }
      return arg.contains(";") || arg.contains("|") || arg.contains("&") || arg.contains("`") || arg.contains("$(")
         || arg.contains("\n") || arg.contains("\r");
   }
}
