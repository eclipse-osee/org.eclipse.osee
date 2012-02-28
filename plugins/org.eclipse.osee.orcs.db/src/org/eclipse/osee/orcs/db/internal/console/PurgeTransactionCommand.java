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
package org.eclipse.osee.orcs.db.internal.console;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import org.eclipse.osee.console.admin.Console;
import org.eclipse.osee.console.admin.ConsoleCommand;
import org.eclipse.osee.console.admin.ConsoleParameters;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.operation.PurgeTransactionOperation;
import org.eclipse.osee.orcs.db.internal.util.OperationCallableAdapter;
import org.eclipse.osee.orcs.db.internal.util.OperationLoggerAdapter;

/**
 * @author Roberto E. Escobar
 */
public class PurgeTransactionCommand implements ConsoleCommand {

   private IOseeDatabaseService dbService;

   public IOseeDatabaseService getDatabaseService() {
      return dbService;
   }

   public void setDatabaseService(IOseeDatabaseService dbService) {
      this.dbService = dbService;
   }

   @Override
   public String getName() {
      return "db_purge_transaction";
   }

   @Override
   public String getDescription() {
      return "Purges transactions from the database";
   }

   @Override
   public String getUsage() {
      return "txIds=<TX_IDS,...>";
   }

   @Override
   public Callable<?> createCallable(Console console, ConsoleParameters params) {
      String[] stringIds = params.getArray("txIds");

      final List<Integer> transactions = new ArrayList<Integer>();
      for (String arg : stringIds) {
         transactions.add(Integer.parseInt(arg));
      }

      OperationLogger logger = new OperationLoggerAdapter(console);
      IOperation operation = new PurgeTransactionOperation(getDatabaseService(), logger, transactions);
      return new OperationCallableAdapter(operation);
   }

}
