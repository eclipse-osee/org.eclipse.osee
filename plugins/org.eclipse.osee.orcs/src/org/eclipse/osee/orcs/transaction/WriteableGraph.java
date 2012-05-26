/*
 * Created on May 25, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.orcs.transaction;

import org.eclipse.osee.framework.core.data.IRelationSorterId;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.data.ReadableArtifact;

public interface WriteableGraph {

   //////////////////// Relations Portion ? ////////////

   void addChild(ReadableArtifact parent, ReadableArtifact child) throws OseeCoreException;

   void addChild(IRelationSorterId sorterId, ReadableArtifact parent, ReadableArtifact child) throws OseeCoreException;

   void removeChild(ReadableArtifact parent, ReadableArtifact child) throws OseeCoreException;

   ///////////
   //   addRelation(IRelationTypeSide, Artifact)
   //   addRelation(IRelationSorterId, IRelationTypeSide, Artifact)

   void createRelation(ReadableArtifact aArt, IRelationTypeSide relationTypeSide, ReadableArtifact bArt) throws OseeCoreException;

   void createRelation(IRelationSorterId sorterId, ReadableArtifact aArt, IRelationTypeSide relationTypeSide, ReadableArtifact bArt) throws OseeCoreException;

   ///////////
   void deleteRelation(ReadableArtifact aArt, IRelationType relationTypeSide, ReadableArtifact bArt) throws OseeCoreException;

   void deleteRelations(IRelationTypeSide relationTypeSide) throws OseeCoreException;

   //   setRelations(IRelationSorterId, IRelationTypeSide, Collection<? extends Artifact>)
   //   setRelations(IRelationTypeSide, Collection<? extends Artifact>)
   //   setRelationsOfTypeUseCurrentOrder(IRelationTypeSide, Collection<? extends Artifact>, Class<?>)
   //
   /////////////////////////////////////////////////////

}
