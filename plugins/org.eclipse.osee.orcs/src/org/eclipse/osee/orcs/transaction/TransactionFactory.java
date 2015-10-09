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
package org.eclipse.osee.orcs.transaction;

import java.util.Collection;
import java.util.concurrent.Callable;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.ITransaction;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.TransactionReadable;

/**
 * @author Roberto E. Escobar
 */
public interface TransactionFactory {

   TransactionBuilder createTransaction(IOseeBranch branch, ArtifactReadable userArtifact, String comment) throws OseeCoreException;

   TransactionBuilder createTransaction(Long branchId, ArtifactReadable userArtifact, String comment) throws OseeCoreException;

   Callable<Integer> purgeTransaction(Collection<? extends ITransaction> transactions);

   Callable<Void> setTransactionComment(ITransaction transaction, String comment);

   CompareResults compareTxs(int txId1, int txId2);

   ResultSet<TransactionReadable> getAllTxs();

   TransactionReadable getTx(int txId);

   TransactionReadable getTxById(int txId);

   boolean setTxComment(int txId, String comment);

   boolean replaceWithBaselineTxVersion(String userId, Long branchId, int txId, int artId, String comment);

   boolean purgeTxs(String txIds);
}
