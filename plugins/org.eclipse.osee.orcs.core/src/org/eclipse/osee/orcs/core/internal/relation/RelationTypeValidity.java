/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.orcs.core.internal.relation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.core.internal.artifact.Artifact;
import org.eclipse.osee.orcs.core.internal.util.MultiplicityState;

/**
 * @author Roberto E. Escobar
 * @author Megumi Telles
 */
public class RelationTypeValidity {

   private final OrcsTokenService tokenService;

   public RelationTypeValidity(OrcsTokenService tokenService) {
      super();
      this.tokenService = tokenService;
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
            node.getExceptionString(), artifactType, side.name(), type.getName(), type.getArtifactType(side));
      }
   }

   public int getMaximumRelationsAllowed(RelationTypeToken type, ArtifactTypeToken artifactType, RelationSide side) {
      Conditions.checkNotNull(type, "relationType");
      Conditions.checkNotNull(artifactType, "artifactType");
      Conditions.checkNotNull(side, "relationSide");

      int toReturn = 0;
      if (type.isArtifactTypeAllowed(side, artifactType)) {
         toReturn = type.getMultiplicity().getLimit(side);
      }
      return toReturn;
   }

   public MultiplicityState getRelationMultiplicityState(RelationTypeToken type, RelationSide side, int count) {
      Conditions.checkNotNull(type, "type");
      Conditions.checkNotNull(side, "relationSide");
      //checkTypeExists(type);

      RelationTypeMultiplicity multiplicity = type.getMultiplicity();

      MultiplicityState toReturn = MultiplicityState.IS_VALID;
      int limit = multiplicity.getLimit(side);
      if (count > limit) {
         toReturn = MultiplicityState.MAX_VIOLATION;
      }
      return toReturn;
   }

   public boolean isRelationTypeValid(RelationTypeToken relationType, ArtifactTypeToken artifactType,
      RelationSide relationSide) {
      Conditions.checkNotNull(artifactType, "artifactType");
      Conditions.checkNotNull(relationSide, "relationSide");
      return getRelationSideMax(relationType, artifactType, relationSide) > 0;
   }

   public List<RelationTypeToken> getValidRelationTypes(ArtifactTypeToken artifactType) {
      Conditions.checkNotNull(artifactType, "artifactType");
      Collection<? extends RelationTypeToken> types = tokenService.getValidRelationTypes();
      List<RelationTypeToken> toReturn = new ArrayList<>();
      for (RelationTypeToken relationType : types) {
         if (isTypeAllowed(artifactType, relationType)) {
            toReturn.add(relationType);
         }
      }
      return toReturn;
   }

   private boolean isTypeAllowed(ArtifactTypeToken artifactType, RelationTypeToken relationType) {
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

   private int getRelationSideMax(RelationTypeToken relationType, ArtifactTypeToken artifactType,
      RelationSide relationSide) {
      int toReturn = 0;
      if (relationType.isArtifactTypeAllowed(relationSide, artifactType)) {
         toReturn = relationType.getMultiplicity().getLimit(relationSide);
      }
      return toReturn;
   }

}