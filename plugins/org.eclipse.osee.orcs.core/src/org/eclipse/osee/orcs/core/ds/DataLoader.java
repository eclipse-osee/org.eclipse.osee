/*
 * Created on Oct 11, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.orcs.core.ds;

import org.eclipse.osee.framework.core.exception.OseeCoreException;

public interface DataLoader {

   //   void loadArtifacts(ArtifactRowHandler handler, LoadOptions options, int fetchSize, int queryId) throws OseeCoreException;

   //   void loadAttributes(AttributeRowHandler handler, Object dataStoreContext, LoadOptions options) throws OseeCoreException;
   //
   //   void loadRelations(RelationRowHandler handler, Object dataStoreContext, LoadOptions options) throws OseeCoreException;

   void loadArtifacts(ArtifactRowHandler handler, Object dataStoreContext, LoadOptions loadOptions, RelationRowHandlerFactory relationRowHandlerFactory, AttributeRowHandlerFactory attributeRowHandlerFactory) throws OseeCoreException;

}
