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
import org.eclipse.osee.console.admin.ConsoleCommand;
import org.eclipse.osee.console.admin.ConsoleParameters;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.orcs.db.internal.exchange.TxCurrentsAndModTypesCommand;
import org.eclipse.osee.orcs.db.internal.util.OperationCallableAdapter;
import org.eclipse.osee.orcs.db.internal.util.OperationLoggerAdapter;

/**
 * @author Roberto E. Escobar
 */
public class TxCurrentsCommand implements ConsoleCommand {

   private IOseeDatabaseService dbService;

   public IOseeDatabaseService getDatabaseService() {
      return dbService;
   }

   public void setDatabaseService(IOseeDatabaseService dbService) {
      this.dbService = dbService;
   }

   @Override
   public String getName() {
      return "db_tx_currents";
   }

   @Override
   public String getDescription() {
      return "Detect and fix tx current and mod types inconsistencies on archive txs or txs";
   }

   @Override
   public String getUsage() {
      return "[onTxsArchived=<TRUE|FALSE>]";
   }

   @Override
   public Callable<?> createCallable(Console console, ConsoleParameters params) {
      boolean isArchivedTable = params.getBoolean("onTxsArchived");
      OperationLogger logger = new OperationLoggerAdapter(console);
      IOperation operation = new TxCurrentsAndModTypesCommand(getDatabaseService(), logger, isArchivedTable);
      return new OperationCallableAdapter(operation);
   }
}
