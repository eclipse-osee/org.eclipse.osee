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
package org.eclipse.osee.ote.core.cmd;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Andrew M. Finkbeiner
 *
 */
public class CommandDistributerImpl implements CommandDistributer {
   private final ExecutorService executor = Executors.newSingleThreadExecutor();
   private final Set<CommandHandler> handlers = new CopyOnWriteArraySet<CommandHandler>();

   public CommandDistributerImpl(){

   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.core.cmd.CommandDistributer#distribute(org.eclipse.osee.ote.core.cmd.Command)
    */
   public void distribute(final Command command) {
      executor.submit(new Runnable() {

         public void run() {
            for(CommandHandler handler:handlers){
               try {
                  if(handler.canHandle(command.getId())){
                     handler.handle(command);
                  }
               } catch (RuntimeException e) {
                  OseeLog.log(CommandDistributerImpl.class, Level.SEVERE, "Eception in handler for " + command.getId().toString(), e);
               }
            }
         }
      });
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.core.cmd.CommandDistributer#registerHandler(org.eclipse.osee.ote.core.cmd.CommandHandler)
    */
   public void registerHandler(CommandHandler commandHandler) {
      handlers.add(commandHandler);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.core.cmd.CommandDistributer#unregisterHandler(org.eclipse.osee.ote.core.cmd.CommandHandler)
    */
   public void unregisterHandler(CommandHandler commandHandler) {
      handlers.remove(commandHandler);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.core.cmd.CommandDistributer#shutdown()
    */
   public void shutdown() {
      OseeLog.log(CommandDistributerImpl.class, Level.INFO, "Command distributor shutting down...");
      handlers.clear();
      executor.shutdown();
      try {
         executor.awaitTermination(30000, TimeUnit.MILLISECONDS);
      } catch (InterruptedException ex) {
         OseeLog.log(CommandDistributerImpl.class, Level.WARNING, "Interrupted while shutting down command distributor", ex);
      }
   }

}
