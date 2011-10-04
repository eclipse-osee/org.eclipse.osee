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
public abstract class ArtifactQueryOptions {

   public abstract void setIncludeCache(boolean include);

   public abstract void setCountEstimate(int count);

   public abstract void setTransactionRecord(TransactionRecord transactionRecord);

   public abstract void setIncludeDeleted(boolean include);

   public abstract void setIncludeInheritedArtifactTypes(boolean include);

   public abstract void setLoadLevel(LoadLevel level);

   public abstract ArtifactQueryResult setOptions(int countEstimate, LoadLevel loadLevel, QueryOption... queryOptions);

   public abstract ArtifactQueryResult setOptions(LoadLevel loadLevel, QueryOption... queryOptions);

   public abstract ArtifactQueryResult setOptionsHistorical(TransactionRecord transactionId, LoadLevel loadLevel, QueryOption... queryOptions);

   public abstract ArtifactQueryResult setOptionsHistorical(int countEstimate, TransactionRecord transactionId, LoadLevel loadLevel, QueryOption... queryOptions);

}