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

import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.server.admin.Activator;
import org.eclipse.osgi.framework.console.CommandInterpreter;

/**
 * @author Roberto E. Escobar
 */
public class SearchTaggerCommands {

   private final TaggerAllWorker tagAllWorker;
   private final TaggerDropAllWorker dropAllWorker;
   private final TagItemWorker tagItemWorker;

   public SearchTaggerCommands() {
      this.tagAllWorker = new TaggerAllWorker();
      this.tagAllWorker.setExecutionAllowed(true);

      this.dropAllWorker = new TaggerDropAllWorker();
      this.dropAllWorker.setExecutionAllowed(true);

      this.tagItemWorker = new TagItemWorker();
      this.tagItemWorker.setExecutionAllowed(true);
   }

   public void startTagItem(CommandInterpreter ci) {
      if (!this.dropAllWorker.isRunning() && !this.tagAllWorker.isRunning()) {
         this.tagItemWorker.setCommandInterpreter(ci);
         this.tagItemWorker.setExecutionAllowed(true);
      } else {
         if (this.dropAllWorker.isRunning()) {
            ci.println("Drop All Tags is running.");
         }
         if (this.tagAllWorker.isRunning()) {
            ci.println("Tag All is running.");
         }
      }
   }

   public void stopTagItem(CommandInterpreter ci) {
      if (this.tagItemWorker.isRunning()) {
         this.tagItemWorker.setExecutionAllowed(false);
      } else {
         ci.println("Tag Item is not running.");
      }
   }

   public void startDropAll(CommandInterpreter ci) {
      if (!this.dropAllWorker.isRunning() && !this.tagAllWorker.isRunning() && !this.tagItemWorker.isRunning()) {
         this.dropAllWorker.setCommandInterpreter(ci);
         this.dropAllWorker.setExecutionAllowed(true);
         Operations.executeAsJob(dropAllWorker, false);
      } else {
         if (this.dropAllWorker.isRunning()) {
            ci.println("Drop All Tags is running.");
         }
         if (this.tagAllWorker.isRunning()) {
            ci.println("Tag All is running.");
         }
         if (this.tagItemWorker.isRunning()) {
            ci.println("Tag Item is running.");
         }
      }
   }

   public void stopDropAll(CommandInterpreter ci) {
      if (this.dropAllWorker.isRunning()) {
         this.dropAllWorker.setExecutionAllowed(false);
      } else {
         ci.println("Drop All Tags is not running.");
      }
   }

   public void stopTagAll(CommandInterpreter ci) {
      if (this.tagAllWorker.isRunning()) {
         this.tagAllWorker.setExecutionAllowed(false);
      } else {
         ci.println("Tag All is not running.");
      }
   }

   public void startTagAll(CommandInterpreter ci) {
      if (!this.dropAllWorker.isRunning() && !this.tagAllWorker.isRunning() && !this.tagItemWorker.isRunning()) {
         this.tagAllWorker.setCommandInterpreter(ci);
         this.tagAllWorker.setExecutionAllowed(true);
         Operations.executeAsJob(tagAllWorker, false);
      } else {
         if (this.dropAllWorker.isRunning()) {
            ci.println("Drop All Tags is running.");
         }
         if (this.tagAllWorker.isRunning()) {
            ci.println("Tag All is running.");
         }
         if (this.tagItemWorker.isRunning()) {
            ci.println("Tag Item is running.");
         }
      }
   }

   public void getTaggerStatistics(CommandInterpreter ci) {
      TaggerStats stats = new TaggerStats();
      stats.setCommandInterpreter(ci);
      stats.setExecutionAllowed(true);
      Operations.executeAsJob(stats, false);
   }

   public void clearTaggerStats() {
      Activator.getInstance().getSearchTagger().clearStatistics();
   }

   public void getSearchStatistics(CommandInterpreter ci) {
      SearchStats stats = new SearchStats();
      stats.setCommandInterpreter(ci);
      stats.setExecutionAllowed(true);
      Operations.executeAsJob(stats, false);
   }

   public void clearSearchStats() {
      Activator.getInstance().getSearchEngine().clearStatistics();
   }
}
