/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.orcs;

import org.eclipse.osee.framework.core.enums.LoadLevel;

/**
 * @author Ryan D. Brooks
 * @author Donald G. Dunne
 */
public interface IArtifactQueryNew {

   public QueryResult setOptions(int countEstimate, LoadLevel loadLevel, QueryOption... queryOptions);

   public QueryResult setOptions(LoadLevel loadLevel, QueryOption... queryOptions);

   public QueryResult setOptionsHistorical(TransactionRecord transactionId, LoadLevel loadLevel, QueryOption... queryOptions);

   public QueryResult setOptionsHistorical(int countEstimate, TransactionRecord transactionId, LoadLevel loadLevel, QueryOption... queryOptions);

}