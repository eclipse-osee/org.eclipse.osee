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
package org.eclipse.osee.orcs.db.internal.console;

import java.util.concurrent.Callable;
import org.eclipse.osee.console.admin.Console;
import org.eclipse.osee.console.admin.ConsoleParameters;
import org.eclipse.osee.orcs.db.internal.callable.PurgeUnusedBackingDataAndTransactions;

/**
 * @author Roberto E. Escobar
 */
public class TxPruneCommand extends AbstractDatastoreConsoleCommand {

   @Override
   public String getName() {
      return "db_tx_prune";
   }

   @Override
   public String getDescription() {
      return "Purge artifact, attribute, and relation versions that are not addressed or non-existent and purge empty transactions";
   }

   @Override
   public String getUsage() {
      return "";
   }

   @Override
   public Callable<?> createCallable(Console console, ConsoleParameters params) {
      return new Callable<Void>() {
         @Override
         public Void call() throws Exception {
            new PurgeUnusedBackingDataAndTransactions(getJdbcClient());
            return null;
         }
      };
   }
}
