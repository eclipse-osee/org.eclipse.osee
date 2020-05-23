/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.framework.messaging.internal;

import org.eclipse.osee.framework.messaging.ConnectionNode;
import org.eclipse.osee.framework.messaging.MessageService;
import org.eclipse.osee.framework.messaging.NodeInfo;
import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

/**
 * @author Andrew M. Finkbeiner
 */
public class MessageServiceConsole implements CommandProvider {

   private MessageService messageService;

   public void setMessageService(MessageService messageService) {
      this.messageService = messageService;
   }

   public void _msgPrintSummary(CommandInterpreter ci) throws Exception {
      for (NodeInfo info : messageService.getAvailableConnections()) {
         ConnectionNode node = messageService.get(info);
         ci.println(node.getSummary());
      }
   }

   public void _msgPrintSend(CommandInterpreter ci) throws Exception {
      ConsoleDebugSupport support = ServiceUtility.getConsoleDebugSupport();
      if (support != null) {
         support.setPrintSends(!support.getPrintSends());
         ci.println("printSends " + support.getPrintSends());
      } else {
         ci.println("ConsoleDebugSupport service not found, unable to show sends.");
      }
   }

   public void _msgPrintReceive(CommandInterpreter ci) throws Exception {
      ConsoleDebugSupport support = ServiceUtility.getConsoleDebugSupport();
      if (support != null) {
         support.setPrintReceives(!support.getPrintReceives());
         ci.println("printReceives " + support.getPrintReceives());
      } else {
         ci.println("ConsoleDebugSupport service not found, unable to show receives.");
      }
   }

   public void _msgPrintStats(CommandInterpreter ci) throws Exception {
      ConsoleDebugSupport support = ServiceUtility.getConsoleDebugSupport();
      if (support != null) {
         String arg = ci.nextArgument();
         if (arg == null) {
            support.printAllStats(ci);
         } else if (arg.equals("tx")) {
            support.printTxStats(ci);
         } else if (arg.equals("rx")) {
            support.printRxStats(ci);
         }
      } else {
         ci.println("ConsoleDebugSupport service not found, unable to show receives.");
      }

   }

   @Override
   public String getHelp() {
      StringBuilder sb = new StringBuilder();
      sb.append("---Message Service Commands---\n");
      sb.append("\tmsgPrintSummary - prints a Summary of all ConnectionNodes.\n");
      sb.append("\tmsgPrintReceive - print out a receive when it happens to std.out.\n");
      sb.append("\tmsgPrintSend - print out a receive when it happens.to std.out.\n");
      sb.append("\tmsgPrintStats [tx|rx]- print out a stats collected on sends and/or receives.\n");
      return sb.toString();
   }

}
