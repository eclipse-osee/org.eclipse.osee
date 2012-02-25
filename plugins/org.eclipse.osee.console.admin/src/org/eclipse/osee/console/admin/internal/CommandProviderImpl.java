/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.console.admin.internal;

import java.util.Collection;
import java.util.List;
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

   public void _osee(CommandInterpreter ci) throws Exception {
      ConsoleParameters parameters = getParameters(ci);
      Console console = getConsole(ci);
      getDispatcher().dispatch(console, parameters);
   }

   @Override
   public String getHelp() {
      StringBuilder help = new StringBuilder();
      addHeader(help, CONSOLE_HEADER);
      addSyntax(help);
      addSubCommandHeader(help);

      Collection<ConsoleCommand> commands = getDispatcher().getRegistered();
      List<ConsoleCommand> sorted = ConsoleAdminUtils.sort(commands);
      for (ConsoleCommand command : sorted) {
         addCommand(help, command);
      }
      return help.toString();
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

}
