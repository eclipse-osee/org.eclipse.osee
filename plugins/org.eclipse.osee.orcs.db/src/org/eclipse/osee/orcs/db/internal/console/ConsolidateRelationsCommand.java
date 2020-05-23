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
import org.eclipse.osee.orcs.db.internal.callable.ConsolidateRelationsDatabaseTxCallable;
import org.eclipse.osee.orcs.db.internal.sql.join.SqlJoinFactory;

/**
 * @author Roberto E. Escobar
 */
public class ConsolidateRelationsCommand extends AbstractDatastoreConsoleCommand {

   private SqlJoinFactory joinFactory;

   public void setSqlJoinFactory(SqlJoinFactory joinFactory) {
      this.joinFactory = joinFactory;
   }

   @Override
   public String getName() {
      return "db_consolidate_relations";
   }

   @Override
   public String getDescription() {
      return "Consolidate rows of relations";
   }

   @Override
   public String getUsage() {
      return "";
   }

   @Override
   public Callable<?> createCallable(Console console, ConsoleParameters params) {
      return new ConsolidateRelationsDatabaseTxCallable(getLogger(), getSession(), getJdbcClient(), joinFactory,
         console);
   }
}
