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

package org.eclipse.osee.orcs.db.internal.callable;

import static org.eclipse.osee.framework.jdk.core.util.Conditions.checkNotNull;
import static org.eclipse.osee.framework.jdk.core.util.Conditions.checkNotNullOrEmpty;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcConnection;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;

/**
 * @author Roberto E. Escobar
 */
public class SetTransactionTxCallable extends AbstractDatastoreTxCallable<Void> {

   private static final String UPDATE_TRANSACTION_COMMENT =
      "UPDATE osee_tx_details SET osee_comment = ? WHERE transaction_id = ?";

   private final TransactionId txs;
   private final String comment;

   public SetTransactionTxCallable(Log logger, OrcsSession session, JdbcClient jdbcClient, TransactionId txs, String comment) {
      super(logger, session, jdbcClient);
      this.txs = txs;
      this.comment = comment;
   }

   @Override
   protected Void handleTxWork(JdbcConnection connection)  {
      checkNotNull(txs, "transaction");
      checkNotNullOrEmpty(comment, "comment");

      getJdbcClient().runPreparedUpdate(connection, UPDATE_TRANSACTION_COMMENT, comment, txs);

      return null;
   }

}