/*
 * Created on Oct 13, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.orcs;

import java.util.List;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.cache.ArtifactTypeCache;
import org.eclipse.osee.framework.core.model.cache.AttributeTypeCache;
import org.eclipse.osee.framework.core.model.cache.RelationTypeCache;
import org.eclipse.osee.framework.core.model.type.RelationType;

public interface DataStoreTypeCache {

   AttributeTypeCache getAttributeTypeCache();

   ArtifactTypeCache getArtifactTypeCache();

   RelationTypeCache getRelationTypeCache();

   List<RelationType> getValidRelationTypes(IArtifactType artifactType, IOseeBranch branch) throws OseeCoreException;

   int getRelationSideMax(RelationType relationType, IArtifactType artifactType, RelationSide relationSide) throws OseeCoreException;
}
