/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.console;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import org.eclipse.osee.console.admin.Console;
import org.eclipse.osee.console.admin.ConsoleParameters;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.orcs.db.internal.callable.PurgeAttributesDatabaseTxCallable;
import org.eclipse.osee.orcs.db.internal.sql.join.SqlJoinFactory;

/**
 * @author John Misinco
 */
public class PurgeAttributeCommand extends AbstractDatastoreConsoleCommand {

   private SqlJoinFactory joinFactory;

   public void setSqlJoinFactory(SqlJoinFactory joinFactory) {
      this.joinFactory = joinFactory;
   }

   @Override
   public String getName() {
      return "purge_attribute";
   }

   @Override
   public String getDescription() {
      return "Purges attribute instances from datastore";
   }

   @Override
   public String getUsage() {
      return "[force=<TRUE|FALSE>] attr_id=<ATTRIBUTE_ID,...>";
   }

   @Override
   public Callable<Void> createCallable(final Console console, final ConsoleParameters params) {
      String[] attrIds = params.getArray("attr_id");
      Set<AttributeId> longIds = new HashSet<>();
      for (String id : attrIds) {
         longIds.add(AttributeId.valueOf(id));
      }

      boolean force = params.getBoolean("force");
      JdbcClient jdbcClient = getJdbcClient();
      if (force) {

         return new PurgeAttributesDatabaseTxCallable(getLogger(), getSession(), jdbcClient, joinFactory, longIds,
            console);
      } else {
         return new Callable<Void>() {

            @Override
            public Void call() throws Exception {
               console.writeln("Attribute IDs: ");
               console.writeln(Collections.toString(", ", longIds));
               console.writeln("Re-run with the force option to execute the command");
               return null;
            }
         };
      }
   }

}
