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
package org.eclipse.osee.orcs.core.internal.console;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import org.eclipse.osee.console.admin.Console;
import org.eclipse.osee.console.admin.ConsoleCommand;
import org.eclipse.osee.console.admin.ConsoleParameters;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.transaction.TransactionFactory;

/**
 * @author Roberto E. Escobar
 */
public class PurgeTransactionCommand implements ConsoleCommand {

   private OrcsApi orcsApi;

   public void setOrcsApi(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   public OrcsApi getOrcsApi() {
      return orcsApi;
   }

   @Override
   public String getName() {
      return "purge_transaction";
   }

   @Override
   public String getDescription() {
      return "Purges transactions from the datastore";
   }

   @Override
   public String getUsage() {
      return "txIds=<TX_IDS,...>";
   }

   @Override
   public Callable<?> createCallable(final Console console, final ConsoleParameters params) {
      final TransactionFactory txFactory = getOrcsApi().getTransactionFactory();
      return new Callable<Object>() {

         @Override
         public Object call() throws Exception {
            String[] stringIds = params.getArray("txIds");
            final List<TransactionId> transactions = new ArrayList<>();
            for (String arg : stringIds) {
               TransactionId tx = TransactionId.valueOf(arg);
               transactions.add(tx);
            }
            console.writeln();
            console.writeln("Purging transactions: [%s]", Arrays.deepToString(stringIds));
            return txFactory.purgeTransaction(transactions).call();
         }

      };
   }
}
