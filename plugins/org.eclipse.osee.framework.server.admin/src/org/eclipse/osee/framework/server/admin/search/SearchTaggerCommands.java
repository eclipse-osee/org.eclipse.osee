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

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.server.admin.internal.Activator;
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

   public Job startTagItem(CommandInterpreter ci) {
      if (!this.dropAllWorker.isRunning() && !this.tagAllWorker.isRunning()) {
         this.tagItemWorker.setCommandInterpreter(ci);
         this.tagItemWorker.setExecutionAllowed(true);
         return Operations.executeAsJob(tagItemWorker, false);
      } else {
         if (this.dropAllWorker.isRunning()) {
            ci.println("Drop All Tags is running.");
         }
         if (this.tagAllWorker.isRunning()) {
            ci.println("Tag All is running.");
         }
         return null;
      }
   }

   public void stopTagItem(CommandInterpreter ci) {
      if (this.tagItemWorker.isRunning()) {
         this.tagItemWorker.setExecutionAllowed(false);
      } else {
         ci.println("Tag Item is not running.");
      }
   }

   public Job startDropAll(CommandInterpreter ci) {
      if (!this.dropAllWorker.isRunning() && !this.tagAllWorker.isRunning() && !this.tagItemWorker.isRunning()) {
         this.dropAllWorker.setCommandInterpreter(ci);
         this.dropAllWorker.setExecutionAllowed(true);
         return Operations.executeAsJob(dropAllWorker, false);
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
         return null;
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

   public Job startTagAll(CommandInterpreter ci) {
      if (!this.dropAllWorker.isRunning() && !this.tagAllWorker.isRunning() && !this.tagItemWorker.isRunning()) {
         this.tagAllWorker.setCommandInterpreter(ci);
         this.tagAllWorker.setExecutionAllowed(true);
         return Operations.executeAsJob(tagAllWorker, false);
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
         return null;
      }
   }

   public Job getTaggerStatistics(CommandInterpreter ci) {
      TaggerStats stats = new TaggerStats();
      stats.setCommandInterpreter(ci);
      stats.setExecutionAllowed(true);
      return Operations.executeAsJob(stats, false);
   }

   public void clearTaggerStats() {
      Activator.getSearchTagger().clearStatistics();
   }

   public Job getSearchStatistics(CommandInterpreter ci) {
      SearchStats stats = new SearchStats();
      stats.setCommandInterpreter(ci);
      stats.setExecutionAllowed(true);
      return Operations.executeAsJob(stats, false);
   }

   public void clearSearchStats() {
      Activator.getSearchEngine().clearStatistics();
   }
}
