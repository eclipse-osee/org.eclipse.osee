/*
 * Created on Sep 27, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.orcs.prototype;

import java.util.List;
import java.util.concurrent.Callable;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

public abstract class ArtifactQueryResult {

   public abstract Artifact getOneOrNull() throws OseeCoreException;

   public abstract Artifact getExactlyOne() throws OseeCoreException;

   public abstract List<Artifact> getList() throws OseeCoreException;

   public abstract int getCount() throws OseeCoreException;

   public abstract Iterable<?> getIterable(int fetchSize) throws OseeCoreException;

   /**
    * Provide callable to allow applications to embed in larger operation and provide for cancel
    */
   public abstract Callable<?> getOneOrNullCallable();

   public abstract Callable<?> getExactlyOneCallable();

   public abstract Callable<?> getListCallable();

   public abstract Callable<?> getSizeCallable();

   public void tryIt() {
      //      ArtifactQueryService.getFromName("WPN_PAGE", null).getArtifactList(LoadLevel.FULL, QueryOption.IncludeDeleted).getCount();
      //
      //      ArtifactQueryService.getFromName("WPN_PAGE", null).setOptions(LoadLevel.FULL, QueryOption.IncludeDeleted).getCount();
      //
      //      ArtifactQueryService.getFromName("WPN_PAGE", null).fullLoadWithDeleted().getCount();\
      //
      //      ArtifactQueryService.getFromName("WPN_PAGE", null).setOptions(new OptionsObject(LoadLevel.FULL, ).getCount();
      //
      //      ArtifactQueryService.getFromName("WPN_PAGE", null).setOptions(new FullLoadwithDeletedAndSomething()).getCount();
      //
      //      ArtifactQueryService.getFromName("WPN_PAGE", null, ).getCount();
      //
      //   
      //   QueryFactory queryFactory = orcs.createQuery(); // Create Composes the services
      //
      //   QueryBuilder query = queryFactory.getFromName("WPN_PAGE", null);
      //   query.includeDeleted().excludeSomething();
      //
      //   Result result = query.build(); 
      //   result.getCount();
      //   result.getList();
      // etc.....
      //
      //   orcs.createQuery().getFromName("WPN_PAGE", null).includeDeleted()...QueryBuilder...//Result .build().getCount();
      //
      //

   }
}
