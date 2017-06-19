/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.client.internal.search;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.jdk.core.type.Identity;
import org.eclipse.osee.orcs.rest.model.search.artifact.Predicate;

/**
 * @author John Misinco
 */
public interface PredicateFactory {

   Predicate createUuidSearch(List<String> ids);

   Predicate createLocalIdsSearch(Collection<? extends ArtifactId> ids);

   Predicate createIdSearch(Collection<? extends Identity<String>> ids);

   Predicate createIsOfTypeSearch(Collection<? extends ArtifactTypeId> artifactType);

   Predicate createTypeEqualsSearch(Collection<? extends ArtifactTypeId> artifactType);

   Predicate createAttributeTypeSearch(Collection<? extends AttributeTypeId> attributeTypes, String value, QueryOption... options);

   Predicate createAttributeTypeSearch(Collection<? extends AttributeTypeId> attributeTypes, Collection<String> values, QueryOption... options);

   Predicate createAttributeExistsSearch(Collection<? extends AttributeTypeId> attributeTypes);

   Predicate createAttributeNotExistsSearch(Collection<? extends AttributeTypeId> attributeTypes);

   Predicate createRelationExistsSearch(Collection<? extends IRelationType> relationTypes);

   Predicate createRelationNotExistsSearch(Collection<? extends IRelationType> relationTypes);

   Predicate createRelationTypeSideNotExistsSearch(RelationTypeSide relationTypeSide);

   Predicate createRelationTypeSideExistsSearch(RelationTypeSide relationTypeSide);

   Predicate createRelatedToSearch(RelationTypeSide relationTypeSide, Collection<ArtifactId> ids);

}