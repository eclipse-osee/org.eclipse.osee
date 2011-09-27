/*
 * Created on Sep 27, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.orcs;

import java.util.List;
import java.util.concurrent.Callable;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

public abstract class QueryResult {

   public abstract Artifact getOneOrNull() throws OseeCoreException;

   public abstract Artifact getExactlyOne() throws OseeCoreException;

   public abstract List<Artifact> getList();

   public abstract int getSize();

   /**
    * Provide callable to allow applications to embed in larger operation and provide for cancel
    */
   public abstract Callable<?> getOneOrNullCallable();

   public abstract Callable<?> getExactlyOneCallable();

   public abstract Callable<?> getListCallable();

   public abstract Callable<?> getSizeCallable();

   public abstract Iterable<?> getIterable(int fetchSize);

   public void tryIt() {
      //      ArtifactQueryService.getFromName("WPN_PAGE", null).getArtifactList(LoadLevel.FULL, QueryOption.IncludeDeleted).getCount();
      //
      //      ArtifactQueryService.getFromName("WPN_PAGE", null).setOptions(LoadLevel.FULL, QueryOption.IncludeDeleted).getCount();
   }
}
