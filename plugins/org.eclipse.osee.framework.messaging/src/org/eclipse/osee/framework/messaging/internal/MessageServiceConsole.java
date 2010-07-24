/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
