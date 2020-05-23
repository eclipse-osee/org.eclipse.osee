/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.orcs.db.internal.exchange;

import java.util.concurrent.Callable;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.db.internal.callable.AbstractDatastoreTxCallable;
import org.eclipse.osee.orcs.db.internal.callable.CompositeDatastoreTxCallable;
import org.eclipse.osee.orcs.db.internal.callable.InvalidTxCurrentsAndModTypesCallable;

/**
 * @author Ryan D. Brooks
 */
public class TxCurrentsOpFactory {

   private TxCurrentsOpFactory() {
      //Static utility
   }

   public static Callable<?> createTxCurrentsAndModTypesOp(Log logger, OrcsSession session, JdbcClient db, boolean archived) {
      return new CompositeDatastoreTxCallable(logger, session, db, //
         buildFixOperation(logger, session, db, archived, "1/3 ", "osee_artifact", "art_id"), //
         buildFixOperation(logger, session, db, archived, "2/3 ", "osee_attribute", "attr_id"), //
         buildFixOperation(logger, session, db, archived, "3/3 ", "osee_relation_link", "rel_link_id"));
   }

   private static AbstractDatastoreTxCallable<?> buildFixOperation(Log logger, OrcsSession session, JdbcClient db, boolean archived, String operationName, String tableName, String columnName) {
      return new InvalidTxCurrentsAndModTypesCallable(logger, session, db, operationName, tableName, columnName, true,
         archived);
   }
}