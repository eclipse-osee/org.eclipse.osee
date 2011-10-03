/*
 * Created on Sep 30, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.orcs.core.internal.relation;

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
