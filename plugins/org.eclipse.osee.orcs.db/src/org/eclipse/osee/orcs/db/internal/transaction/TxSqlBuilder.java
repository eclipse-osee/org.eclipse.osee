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
package org.eclipse.osee.orcs.db.internal.transaction;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.database.core.ArtifactJoinQuery;
import org.eclipse.osee.orcs.core.ds.OrcsChangeSet;
import org.eclipse.osee.orcs.db.internal.transaction.TransactionWriter.SqlOrderEnum;

/**
 * @author Roberto E. Escobar
 */
public interface TxSqlBuilder {

   void clear();

   void accept(TransactionRecord tx, OrcsChangeSet txData) throws OseeCoreException;

   Set<Entry<SqlOrderEnum, ArtifactJoinQuery>> getTxNotCurrents();

   List<Object[]> getInsertData(SqlOrderEnum key);

   List<DaoToSql> getBinaryStores();

   void updateAfterBinaryStorePersist() throws OseeCoreException;

}