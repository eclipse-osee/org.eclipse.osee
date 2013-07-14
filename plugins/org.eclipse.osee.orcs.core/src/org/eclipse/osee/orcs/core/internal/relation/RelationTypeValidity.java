/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.relation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.orcs.core.internal.util.MultiplicityState;
import org.eclipse.osee.orcs.data.RelationTypes;

/**
 * @author Roberto E. Escobar
 * @author Megumi Telles
 */
public class RelationTypeValidity {

   private final RelationTypes relationTypes;

   public RelationTypeValidity(RelationTypes relationTypes) {
      super();
      this.relationTypes = relationTypes;
   }

   public void checkRelationTypeMultiplicity(RelationNode node, IRelationTypeSide typeAndSide, int count) throws OseeCoreException {
      MultiplicityState state = getRelationMultiplicityState(typeAndSide, count);
      switch (state) {
         case MAX_VIOLATION:
            throw new OseeStateException("Relation type [%s] exceeds max occurrence rule on [%s]", typeAndSide,
               node.getExceptionString());
         case MIN_VIOLATION:
            throw new OseeStateException("Relation type [%s] is less than min occurrence rule on [%s]", typeAndSide,
               node.getExceptionString());
         default:
            break;
      }
   }

   public boolean isRelationTypeValid(IArtifactType artifactType, IRelationTypeSide relationTypeSide) throws OseeCoreException {
      IRelationType relationType = relationTypes.getByUuid(relationTypeSide.getGuid());
      RelationSide relationSide = relationTypeSide.getSide();
      return isRelationTypeValid(artifactType, relationType, relationSide);
   }

   public void checkRelationTypeValid(RelationNode node, IRelationTypeSide relationTypeSide) throws OseeCoreException {
      IRelationType relationType = relationTypes.getByUuid(relationTypeSide.getGuid());
      RelationSide relationSide = relationTypeSide.getSide();
      IArtifactType artifactType = node.getArtifactType();
      boolean isValid = isRelationTypeValid(artifactType, relationType, relationSide);
      Conditions.checkExpressionFailOnTrue(
         !isValid,
         "Relation validity error for [%s] - ArtifactType [%s] does not belong on side [%s] of relation [%s] - only items of type [%s] are allowed",
         node.getExceptionString(), artifactType, relationSide.name(), relationType,
         relationTypes.getArtifactType(relationType, relationSide));
   }

   public int getMaximumRelationsAllowed(IArtifactType artifactType, IRelationTypeSide relationTypeSide) throws OseeCoreException {
      Conditions.checkNotNull(artifactType, "artifactType");
      Conditions.checkNotNull(relationTypeSide, "relationTypeSide");
      int toReturn = 0;
      IRelationType relationType = relationTypes.getByUuid(relationTypeSide.getGuid());
      RelationSide relationSide = relationTypeSide.getSide();
      if (relationTypes.isArtifactTypeAllowed(relationType, relationSide, artifactType)) {
         toReturn = relationTypes.getMultiplicity(relationType).getLimit(relationSide);
      }
      return toReturn;
   }

   public MultiplicityState getRelationMultiplicityState(IRelationTypeSide relationTypeSide, int count) throws OseeCoreException {
      IRelationType relationType = relationTypes.getByUuid(relationTypeSide.getGuid());
      RelationSide relationSide = relationTypeSide.getSide();
      RelationTypeMultiplicity multiplicity = relationTypes.getMultiplicity(relationType);

      MultiplicityState toReturn = MultiplicityState.IS_VALID;
      int limit = multiplicity.getLimit(relationSide);
      if (count > limit) {
         toReturn = MultiplicityState.MAX_VIOLATION;
      }
      return toReturn;
   }

   public List<IRelationType> getValidRelationTypes(IArtifactType artifactType) throws OseeCoreException {
      Conditions.checkNotNull(artifactType, "artifactType");
      Collection<? extends IRelationType> types = relationTypes.getAll();
      List<IRelationType> toReturn = new ArrayList<IRelationType>();
      for (IRelationType relationType : types) {
         if (isTypeAllowed(artifactType, relationType)) {
            toReturn.add(relationType);
         }
      }
      return toReturn;
   }

   private boolean isRelationTypeValid(IArtifactType artifactType, IRelationType relationType, RelationSide relationSide) throws OseeCoreException {
      return getRelationSideMax(artifactType, relationType, relationSide) > 0;
   }

   private boolean isTypeAllowed(IArtifactType artifactType, IRelationType relationType) throws OseeCoreException {
      boolean result = false;
      for (RelationSide side : RelationSide.values()) {
         int sideMax = getRelationSideMax(artifactType, relationType, side);
         if (sideMax > 0) {
            result = true;
            break;
         }
      }
      return result;
   }

   private int getRelationSideMax(IArtifactType artifactType, IRelationType relationType, RelationSide relationSide) throws OseeCoreException {
      int toReturn = 0;
      if (relationTypes.isArtifactTypeAllowed(relationType, relationSide, artifactType)) {
         toReturn = relationTypes.getMultiplicity(relationType).getLimit(relationSide);
      }
      return toReturn;
   }

}