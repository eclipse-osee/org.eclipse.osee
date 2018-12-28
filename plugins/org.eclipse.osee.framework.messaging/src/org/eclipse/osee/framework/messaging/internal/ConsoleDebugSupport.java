/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.messaging.internal;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.jms.JMSException;
import javax.jms.Message;
import org.eclipse.osee.framework.messaging.MessageID;
import org.eclipse.osgi.framework.console.CommandInterpreter;

/**
 * @author Roberto E. Escobar
 */
public class ConsoleDebugSupport {

   private boolean printSends;
   private boolean printReceives;
   private final Map<MessageID, Stats> sends;
   private final Map<String, Stats> receives;

   public ConsoleDebugSupport() {
      sends = new ConcurrentHashMap<>();
      receives = new ConcurrentHashMap<>();
   }

   protected void setPrintSends(boolean printSends) {
      this.printSends = printSends;
   }

   public boolean getPrintSends() {
      return printSends;
   }

   public boolean getPrintReceives() {
      return printReceives;
   }

   public void setPrintReceives(boolean printReceives) {
      this.printReceives = printReceives;
   }

   public void addSend(MessageID messageId) {
      Stats stats = sends.get(messageId);
      if (stats == null) {
         stats = new Stats();
         sends.put(messageId, stats);
      }
      stats.add(messageId);
   }

   public void addReceive(Message jmsMessage) {
      String id = null;
      try {
         id = jmsMessage.getJMSMessageID();
      } catch (JMSException ex) {
         ex.printStackTrace();
      }
      if (id != null) {
         Stats stats = receives.get(id);
         if (stats == null) {
            stats = new Stats();
            receives.put(id, stats);
         }
         stats.add(jmsMessage);
      }
   }

   /**
    * @author Roberto E. Escobar
    */
   private class Stats {
      private int count = 0;
      private Date lastReceipt;

      public void add(MessageID messageId) {
         lastReceipt = new Date();
         count++;
      }

      public void add(Message jmsMessage) {
         lastReceipt = new Date();
         count++;
      }

      @Override
      public String toString() {
         StringBuilder sb = new StringBuilder();
         sb.append(count);
         sb.append(" : ");
         sb.append(lastReceipt);
         return sb.toString();
      }
   }

   public void printAllStats(CommandInterpreter ci) {
      printTxStats(ci);
      printRxStats(ci);
   }

   public void printTxStats(CommandInterpreter ci) {
      ci.println("TxStats:");
      for (MessageID id : sends.keySet()) {
         Stats status = sends.get(id);
         ci.println(id);
         ci.println(status);
         ci.println("------------------------------");
      }
   }

   public void printRxStats(CommandInterpreter ci) {
      ci.println("RxStats:");
      for (String id : receives.keySet()) {
         Stats status = sends.get(id);
         ci.println(id);
         ci.println(status);
         ci.println("------------------------------");
      }
   }

}
