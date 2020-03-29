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

package org.eclipse.osee.orcs.db.internal.transaction;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import org.eclipse.osee.orcs.core.ds.DataProxy;
import org.eclipse.osee.orcs.core.ds.OrcsChangeSet;
import org.eclipse.osee.orcs.data.TransactionReadable;
import org.eclipse.osee.orcs.db.internal.sql.join.Id4JoinQuery;
import org.eclipse.osee.orcs.db.internal.sql.join.IdJoinQuery;
import org.eclipse.osee.orcs.db.internal.transaction.TransactionWriter.SqlOrderEnum;

/**
 * @author Roberto E. Escobar
 */
public interface TxSqlBuilder {

   void clear();

   void accept(TransactionReadable tx, OrcsChangeSet txData);

   Set<Entry<SqlOrderEnum, IdJoinQuery>> getTxNotCurrents();

   Set<Entry<SqlOrderEnum, Id4JoinQuery>> getTxNotCurrents4();

   List<Object[]> getInsertData(SqlOrderEnum key);

   List<DataProxy<?>> getBinaryStores();

   void updateAfterBinaryStorePersist();

}