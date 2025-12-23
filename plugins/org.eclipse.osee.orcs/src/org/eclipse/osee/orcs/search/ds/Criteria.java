/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.orcs.search.ds;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.eclipse.osee.orcs.search.ds.criteria.CriteriaArtifactIds;
import org.eclipse.osee.orcs.search.ds.criteria.CriteriaArtifactType;
import org.eclipse.osee.orcs.search.ds.criteria.CriteriaAttributeKeywords;
import org.eclipse.osee.orcs.search.ds.criteria.CriteriaAttributeTypeExists;
import org.eclipse.osee.orcs.search.ds.criteria.CriteriaAttributeTypeNotExists;
import org.eclipse.osee.orcs.search.ds.criteria.CriteriaRelatedTo;
import org.eclipse.osee.orcs.search.ds.criteria.CriteriaRelatedToThroughRels;
import org.eclipse.osee.orcs.search.ds.criteria.CriteriaRelationTypeExists;
import org.eclipse.osee.orcs.search.ds.criteria.CriteriaRelationTypeFollow;
import org.eclipse.osee.orcs.search.ds.criteria.CriteriaRelationTypeFollowFork;
import org.eclipse.osee.orcs.search.ds.criteria.CriteriaRelationTypeNotExists;
import org.eclipse.osee.orcs.search.ds.criteria.CriteriaRelationTypeSideExists;
import org.eclipse.osee.orcs.search.ds.criteria.CriteriaRelationTypeSideNotExists;

/**
 * @author Roberto E. Escobar
 * @author Donald G. Dunne
 */
@JsonTypeInfo( //
   use = JsonTypeInfo.Id.NAME, // embed a type name
   include = JsonTypeInfo.As.PROPERTY, // as a property in JSON
   property = "type" // property name to carry the type
)
@JsonSubTypes({ //
   @JsonSubTypes.Type(value = CriteriaAttributeKeywords.class, name = "CriteriaAttributeKeywords"), //
   @JsonSubTypes.Type(value = CriteriaArtifactType.class, name = "CriteriaArtifactType"), //
   @JsonSubTypes.Type(value = CriteriaArtifactIds.class, name = "CriteriaArtifactIds"), //
   @JsonSubTypes.Type(value = CriteriaRelatedTo.class, name = "CriteriaRelatedTo"), //
   @JsonSubTypes.Type(value = CriteriaRelatedToThroughRels.class, name = "CriteriaRelatedToThroughRels"), //
   @JsonSubTypes.Type(value = CriteriaRelationTypeExists.class, name = "CriteriaRelationTypeExists"), //
   @JsonSubTypes.Type(value = CriteriaRelationTypeFollow.class, name = "CriteriaRelationTypeFollow"), //
   @JsonSubTypes.Type(value = CriteriaRelationTypeFollowFork.class, name = "CriteriaRelationTypeFollowFork"), //
   @JsonSubTypes.Type(value = CriteriaRelationTypeNotExists.class, name = "CriteriaRelationTypeNotExists"), //
   @JsonSubTypes.Type(value = CriteriaRelationTypeSideExists.class, name = "CriteriaRelationTypeSideExists"), //
   @JsonSubTypes.Type(value = CriteriaRelationTypeSideNotExists.class, name = "CriteriaRelationTypeSideNotExists"), //
   @JsonSubTypes.Type(value = CriteriaAttributeTypeExists.class, name = "CriteriaAttributeTypeExists"), //
   @JsonSubTypes.Type(value = CriteriaAttributeTypeNotExists.class, name = "CriteriaAttributeTypeNotExists"), //
})
public class Criteria {

   public void checkValid(Options options) {
      // For subclasses to implement
   }

   public boolean isReferenceHandler() {
      return false;
   }

   public String getName() {
      return getClass().getSimpleName();
   }

   @Override
   public String toString() {
      return getClass().getSimpleName();
   }
}
