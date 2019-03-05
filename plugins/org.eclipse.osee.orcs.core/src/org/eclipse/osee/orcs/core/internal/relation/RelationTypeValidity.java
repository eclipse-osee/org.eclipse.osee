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
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.RelationTypeId;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.core.internal.artifact.Artifact;
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

   public void checkRelationTypeMultiplicity(RelationTypeToken type, Artifact node, RelationSide side, int count) {
      MultiplicityState state = getRelationMultiplicityState(type, side, count);
      switch (state) {
         case MAX_VIOLATION:
            throw new OseeStateException("Relation type [%s] on [%s] exceeds max occurrence rule on [%s]", type, side,
               node.getExceptionString());
         case MIN_VIOLATION:
            throw new OseeStateException("Relation type [%s] on [%s] is less than min occurrence rule on [%s]", type,
               side, node.getExceptionString());
         default:
            break;
      }
   }

   public void checkRelationTypeValid(RelationTypeToken type, Artifact node, RelationSide side) {
      Conditions.checkNotNull(type, "type");
      Conditions.checkNotNull(node, "node");
      Conditions.checkNotNull(side, "relationSide");

      ArtifactTypeToken artifactType = node.getArtifactType();
      boolean isValid = isRelationTypeValid(type, artifactType, side);
      if (!isValid) {
         throw new OseeArgumentException(
            "Relation validity error for [%s] - ArtifactType [%s] does not belong on side [%s] of relation [%s] - only items of type [%s] are allowed",
            node.getExceptionString(), artifactType, side.name(), type.getName(),
            relationTypes.getArtifactType(type, side));
      }
   }

   public int getMaximumRelationsAllowed(RelationTypeId type, ArtifactTypeToken artifactType, RelationSide side) {
      Conditions.checkNotNull(type, "relationType");
      Conditions.checkNotNull(artifactType, "artifactType");
      Conditions.checkNotNull(side, "relationSide");
      checkTypeExists(type);

      int toReturn = 0;
      if (relationTypes.isArtifactTypeAllowed(type, side, artifactType)) {
         toReturn = relationTypes.getMultiplicity(type).getLimit(side);
      }
      return toReturn;
   }

   public MultiplicityState getRelationMultiplicityState(RelationTypeToken type, RelationSide side, int count) {
      Conditions.checkNotNull(type, "type");
      Conditions.checkNotNull(side, "relationSide");
      checkTypeExists(type);

      RelationTypeMultiplicity multiplicity = relationTypes.getMultiplicity(type);

      MultiplicityState toReturn = MultiplicityState.IS_VALID;
      int limit = multiplicity.getLimit(side);
      if (count > limit) {
         toReturn = MultiplicityState.MAX_VIOLATION;
      }
      return toReturn;
   }

   public boolean isRelationTypeValid(RelationTypeId relationType, ArtifactTypeToken artifactType, RelationSide relationSide) {
      checkTypeExists(relationType);
      Conditions.checkNotNull(artifactType, "artifactType");
      Conditions.checkNotNull(relationSide, "relationSide");
      return getRelationSideMax(relationType, artifactType, relationSide) > 0;
   }

   public List<RelationTypeId> getValidRelationTypes(ArtifactTypeToken artifactType) {
      Conditions.checkNotNull(artifactType, "artifactType");
      Collection<? extends IRelationType> types = relationTypes.getAll();
      List<RelationTypeId> toReturn = new ArrayList<>();
      for (IRelationType relationType : types) {
         if (isTypeAllowed(artifactType, relationType)) {
            toReturn.add(relationType);
         }
      }
      return toReturn;
   }

   private boolean isTypeAllowed(ArtifactTypeToken artifactType, IRelationType relationType) {
      boolean result = false;
      for (RelationSide side : RelationSide.values()) {
         int sideMax = getRelationSideMax(relationType, artifactType, side);
         if (sideMax > 0) {
            result = true;
            break;
         }
      }
      return result;
   }

   private void checkTypeExists(RelationTypeId type) {
      boolean exists = relationTypes.exists(type);
      Conditions.checkExpressionFailOnTrue(!exists, "relationType [%s] does not exist", type);
   }

   private int getRelationSideMax(RelationTypeId relationType, ArtifactTypeToken artifactType, RelationSide relationSide) {
      int toReturn = 0;
      if (relationTypes.isArtifactTypeAllowed(relationType, relationSide, artifactType)) {
         toReturn = relationTypes.getMultiplicity(relationType).getLimit(relationSide);
      }
      return toReturn;
   }

}