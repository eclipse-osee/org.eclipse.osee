/*
 * Created on Oct 13, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.orcs;

import org.eclipse.osee.framework.core.model.cache.ArtifactTypeCache;
import org.eclipse.osee.framework.core.model.cache.AttributeTypeCache;
import org.eclipse.osee.framework.core.model.cache.RelationTypeCache;

public interface DataStoreTypeCache {

   AttributeTypeCache getAttributeTypeCache();

   ArtifactTypeCache getArtifactTypeCache();

   RelationTypeCache getRelationTypeCache();

}
