/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.server.admin.search;

import org.eclipse.osee.framework.server.admin.Activator;
import org.eclipse.osgi.framework.console.CommandInterpreter;

/**
 * @author Roberto E. Escobar
 */
public class TaggerCommands {

   private static TaggerCommands instance = null;
   private TaggerAllWorker tagAllWorker;
   private TaggerDropAllWorker dropAllWorker;

   public static TaggerCommands getInstance() {
      if (instance == null) {
         instance = new TaggerCommands();
      }
      return instance;
   }

   private TaggerCommands() {
      this.tagAllWorker = new TaggerAllWorker();
      this.tagAllWorker.setExecutionAllowed(true);

      this.dropAllWorker = new TaggerDropAllWorker();
      this.dropAllWorker.setExecutionAllowed(true);
   }

   public void startDropAll(CommandInterpreter ci) {
      if (!this.dropAllWorker.isRunning() && !this.tagAllWorker.isRunning()) {
         this.dropAllWorker.setCommandInterpreter(ci);
         this.dropAllWorker.setExecutionAllowed(true);
         Thread th = new Thread(dropAllWorker);
         th.setName("Drop All Tags");
         th.start();
      }
   }

   public void stopDropAll(CommandInterpreter ci) {
      if (this.dropAllWorker.isRunning()) {
         this.dropAllWorker.setExecutionAllowed(false);
      }
   }

   public void stopTagAll(CommandInterpreter ci) {
      if (this.tagAllWorker.isRunning()) {
         this.tagAllWorker.setExecutionAllowed(false);
      }
   }

   public void startTagAll(CommandInterpreter ci) {
      if (!this.dropAllWorker.isRunning() && !this.tagAllWorker.isRunning()) {
         this.tagAllWorker.setCommandInterpreter(ci);
         this.tagAllWorker.setExecutionAllowed(true);
         Thread th = new Thread(tagAllWorker);
         th.setName("Tagger All Attributes");
         th.start();
      }
   }

   public void getStatistics(CommandInterpreter ci) {
      TaggerStats stats = new TaggerStats();
      stats.setCommandInterpreter(ci);
      stats.setExecutionAllowed(true);
      Thread th = new Thread(stats);
      th.setName("Tagger Statistics");
      th.start();
   }

   public void clearStats() {
      Activator.getInstance().getSearchTagger().clearStatistics();
   }
}
