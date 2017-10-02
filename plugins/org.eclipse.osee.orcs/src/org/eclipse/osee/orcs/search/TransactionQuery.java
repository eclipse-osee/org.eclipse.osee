/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.search;

import org.eclipse.osee.executor.admin.CancellableCallable;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.orcs.data.TransactionReadable;

/**
 * @author Ryan D. Brooks
 * @author Roberto E. Escobar
 */
public interface TransactionQuery extends TxQueryBuilder<TransactionQuery>, Query {

   ResultSet<TransactionReadable> getResults() ;

   ResultSet<TransactionToken> getTokens() ;

   ResultSet<Long> getResultsAsIds() ;

   @Override
   int getCount() ;

   @Override
   CancellableCallable<Integer> createCount() ;

   CancellableCallable<ResultSet<TransactionReadable>> createSearch() ;

   CancellableCallable<ResultSet<Long>> createSearchResultsAsIds() ;

}
