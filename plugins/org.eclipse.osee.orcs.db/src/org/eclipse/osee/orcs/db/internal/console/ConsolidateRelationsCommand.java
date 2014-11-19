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
      return new ConsolidateRelationsDatabaseTxCallable(getLogger(), getSession(), getDatabaseService(), joinFactory,
         console);
   }
}
