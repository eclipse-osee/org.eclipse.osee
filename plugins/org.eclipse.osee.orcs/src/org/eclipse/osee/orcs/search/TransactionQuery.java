/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.orcs.search;

import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.orcs.data.TransactionReadable;

/**
 * @author Ryan D. Brooks
 * @author Roberto E. Escobar
 */
public interface TransactionQuery extends TxQueryBuilder<TransactionQuery>, Query {

   ResultSet<TransactionReadable> getResults();

   ResultSet<TransactionToken> getTokens();

   ResultSet<TransactionId> getResultsAsIds();
}