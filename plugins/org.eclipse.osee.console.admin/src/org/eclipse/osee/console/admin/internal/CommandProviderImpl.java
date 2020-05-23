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

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Future;
import org.eclipse.osee.console.admin.Console;
import org.eclipse.osee.console.admin.ConsoleCommand;
import org.eclipse.osee.console.admin.ConsoleParameters;
import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

/**
 * @author Roberto E. Escobar
 */
public class CommandProviderImpl implements CommandProvider {

   private static final String CONSOLE_HEADER = "OSEE Console Commands";
   private static final String NEW_LINE = "\r\n";
   private static final String TAB = "\t";
   private static final String PREFIX = "osee";
   private static final String HELP_COMMAND = "help";

   private ConsoleAdmin consoleAdmin;

   public void setConsoleAdmin(ConsoleAdmin consoleAdmin) {
      this.consoleAdmin = consoleAdmin;
   }

   public CommandDispatcher getDispatcher() {
      return consoleAdmin.getDispatcher();
   }

   private ConsoleParameters getParameters(CommandInterpreter ci) {
      return ConsoleAdminUtils.parse(ci);
   }

   private Console getConsole(CommandInterpreter ci) {
      return new ConsoleImpl(ci);
   }

   public Future<?> _osee(CommandInterpreter ci) throws Exception {
      ConsoleParameters parameters = getParameters(ci);
      Console console = getConsole(ci);

      Future<?> toReturn = null;
      if (HELP_COMMAND.equalsIgnoreCase(parameters.getCommandName())) {
         help(console, parameters);
      } else {
         toReturn = getDispatcher().dispatch(console, parameters);
      }
      return toReturn;
   }

   private String getHelpSubCommand(ConsoleParameters parameters) {
      String subCommandName = null;
      String[] tokens = parameters.getRawString().split(" ");
      if (tokens.length > 1) {
         subCommandName = tokens[1];
      }
      return subCommandName;
   }

   private void help(Console console, ConsoleParameters parameters) throws Exception {
      String subCommandName = getHelpSubCommand(parameters);
      writeSubCommandHelp(console, subCommandName);
   }

   private void writeSubCommandHelp(Console console, String subCommandName) throws Exception {
      ConsoleCommand command = getDispatcher().getCommandByName(subCommandName);
      ConsoleAdminUtils.checkNotNull(command, "command", "Unable to find help for subCommand:[%s]", subCommandName);
      console.writeln(command.getUsage());
   }

   @Override
   public String getHelp() {
      StringBuilder buffer = new StringBuilder();
      addHeader(buffer, CONSOLE_HEADER);
      addSyntax(buffer);
      addSubCommandHeader(buffer);
      addHelp(buffer);

      Collection<ConsoleCommand> commands = getDispatcher().getRegistered();
      List<ConsoleCommand> sorted = ConsoleAdminUtils.sort(commands);
      for (ConsoleCommand command : sorted) {
         addCommand(buffer, command);
      }

      return buffer.toString();
   }

   private void addHeader(StringBuilder help, String header) {
      help.append("---");
      help.append(header);
      help.append("---");
      help.append(NEW_LINE);
   }

   private void addCommand(StringBuilder help, ConsoleCommand command) {
      help.append(TAB);
      help.append(PREFIX);
      help.append(" ");
      help.append(command.getName());
      help.append(" - ");
      help.append(command.getDescription());
      help.append(NEW_LINE);
   }

   private void addSyntax(StringBuilder help) {
      help.append("Commands: osee [sub-command]");
      help.append(NEW_LINE);
   }

   private void addSubCommandHeader(StringBuilder help) {
      help.append("Sub-Commands:");
      help.append(NEW_LINE);
   }

   private void addHelp(StringBuilder help) {
      help.append(TAB);
      help.append("osee help [sub-command]");
      help.append(NEW_LINE);
   }
}
