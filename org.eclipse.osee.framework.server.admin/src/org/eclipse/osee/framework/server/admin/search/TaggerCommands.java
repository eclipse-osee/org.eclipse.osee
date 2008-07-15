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

import org.eclipse.osgi.framework.console.CommandInterpreter;

/**
 * @author Roberto E. Escobar
 */
public class TaggerCommands {

   private static TaggerCommands instance = null;
   private TaggerAllWorker worker;

   public static TaggerCommands getInstance() {
      if (instance == null) {
         instance = new TaggerCommands();
      }
      return instance;
   }

   private TaggerCommands() {
      this.worker = new TaggerAllWorker();
      this.worker.setExecutionAllowed(true);
   }

   public void executeStop(CommandInterpreter ci) {
      if (this.worker.isRunning()) {
         this.worker.setExecutionAllowed(false);
      }
   }

   public void execute(CommandInterpreter ci) {
      if (!this.worker.isRunning()) {
         this.worker.setCommandInterpreter(ci);
         this.worker.setExecutionAllowed(true);
         Thread th = new Thread(worker);
         th.setName("Tagg All Attributes");
         th.start();
      }
   }
}
