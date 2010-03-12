/*
 * Created on Feb 24, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.internal;

import org.eclipse.osee.framework.messaging.ConnectionNode;
import org.eclipse.osee.framework.messaging.MessageService;
import org.eclipse.osee.framework.messaging.NodeInfo;
import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

/**
 * @author b1528444
 */
public class MessageServiceConsole implements CommandProvider {

   private MessageService messageService;

   MessageServiceConsole(MessageService messageService) {
      this.messageService = messageService;
   }

   public void _printSummary(CommandInterpreter ci) throws Exception {
      for (NodeInfo info : messageService.getAvailableConnections()) {
         ConnectionNode node = messageService.get(info);
         ci.println(node.getSummary());
      }
   }

   @Override
   public String getHelp() {
      StringBuilder sb = new StringBuilder();
      sb.append("---Message Service Commands---\n");
      sb.append("\tprintSummary - prints a Summary of all ConnectionNodes.\n");
      return sb.toString();
   }

}
