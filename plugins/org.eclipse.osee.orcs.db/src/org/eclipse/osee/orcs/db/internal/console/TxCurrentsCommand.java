/*********************************************************************
 * Copyright (c) 2012 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.orcs.db.internal.console;

import java.util.concurrent.Callable;
import org.eclipse.osee.console.admin.Console;
import org.eclipse.osee.console.admin.ConsoleParameters;
import org.eclipse.osee.orcs.db.internal.exchange.TxCurrentsOpFactory;

/**
 * @author Roberto E. Escobar
 */
public class TxCurrentsCommand extends AbstractDatastoreConsoleCommand {

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
      return TxCurrentsOpFactory.createTxCurrentsAndModTypesOp(getLogger(), getSession(), getJdbcClient(),
         isArchivedTable);
   }
}
