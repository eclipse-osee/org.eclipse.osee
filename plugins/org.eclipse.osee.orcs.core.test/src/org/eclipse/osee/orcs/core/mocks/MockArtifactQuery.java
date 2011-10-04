/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.mocks;

import java.util.List;
import java.util.concurrent.Callable;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.Artifact;
import org.eclipse.osee.orcs.ArtifactQuery;
import org.eclipse.osee.orcs.QueryOption;
import org.eclipse.osee.orcs.TransactionRecord;

public class MockArtifactQuery extends ArtifactQuery {

   @Override
   public Artifact getArtifactExactlyOne(LoadLevel loadLevel, QueryOption... queryOptions) throws OseeCoreException {
      return null;
   }

   @Override
   public Artifact getArtifactExactlyOneHistorical(TransactionRecord transactionId, LoadLevel loadLevel, QueryOption... queryOptions) throws OseeCoreException {
      return null;
   }

   @Override
   public Artifact getArtifactOrNull(LoadLevel loadLevel, QueryOption... queryOptions) throws OseeCoreException {
      return null;
   }

   @Override
   public Artifact getArtifactOrNullHistorical(TransactionRecord transactionId, LoadLevel loadLevel, QueryOption... queryOptions) throws OseeCoreException {
      return null;
   }

   @Override
   public List<Integer> getArtifactUuids(int countEstimate, LoadLevel loadLevel, QueryOption... queryOptions) throws OseeCoreException {
      return null;
   }

   @Override
   public List<Integer> getArtifactUuidsHistorical(TransactionRecord transactionId, int countEstimate, LoadLevel loadLevel, QueryOption... queryOptions) throws OseeCoreException {
      return null;
   }

   @Override
   public List<Artifact> getArtifactList(int countEstimate, LoadLevel loadLevel, QueryOption... queryOptions) throws OseeCoreException {
      return null;
   }

   @Override
   public List<Artifact> getArtifactListHistorical(TransactionRecord transactionId, int countEstimate, LoadLevel loadLevel, QueryOption... queryOptions) throws OseeCoreException {
      return null;
   }

   @Override
   public int getCount() throws OseeCoreException {
      return 0;
   }

   @Override
   public Callable<?> getCallable() {
      return null;
   }

   @Override
   public Iterable<?> getIterable(int fetchSize) {
      return null;
   }

}
