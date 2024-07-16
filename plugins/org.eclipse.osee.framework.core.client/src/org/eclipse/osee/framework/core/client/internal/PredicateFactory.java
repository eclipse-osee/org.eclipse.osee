/*********************************************************************
 * Copyright (c) 2012 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.core.client.internal;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.orcs.rest.model.search.artifact.Predicate;

/**
 * @author John Misinco
 */
public interface PredicateFactory {

   Predicate createGuidSearch(List<String> ids);

   Predicate createArtifactIdsSearch(Collection<? extends ArtifactId> ids);

   Predicate createIsOfTypeSearch(Collection<? extends ArtifactTypeId> artifactType);

   Predicate createTypeEqualsSearch(Collection<? extends ArtifactTypeId> artifactType);

   Predicate createAttributeTypeSearch(Collection<? extends AttributeTypeId> attributeTypes, String value, QueryOption... options);

   Predicate createAttributeTypeSearch(Collection<? extends AttributeTypeId> attributeTypes, Collection<String> values, QueryOption... options);

   Predicate createAttributeExistsSearch(Collection<? extends AttributeTypeId> attributeTypes);

   Predicate createAttributeNotExistsSearch(Collection<? extends AttributeTypeId> attributeTypes);

   Predicate createRelationExistsSearch(Collection<? extends RelationTypeToken> relationTypes);

   Predicate createRelationNotExistsSearch(Collection<? extends RelationTypeToken> relationTypes);

   Predicate createRelationTypeSideNotExistsSearch(RelationTypeSide relationTypeSide);

   Predicate createRelationTypeSideExistsSearch(RelationTypeSide relationTypeSide);

   Predicate createRelatedToSearch(RelationTypeSide relationTypeSide, Collection<ArtifactId> ids);

}