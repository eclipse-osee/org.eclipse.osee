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
package org.eclipse.osee.orcs.core.internal.types.impl;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.RelationTypeId;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationType;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.enums.RelationSorter;
import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.data.RelationTypes;

/**
 * @author Roberto E. Escobar
 */
public class RelationTypesImpl implements RelationTypes {

   public static interface RelationTypeIndexProvider {
      RelationTypeIndex getRelationTypeIndex();
   }

   private final RelationTypeIndexProvider provider;

   public RelationTypesImpl(RelationTypeIndexProvider provider) {
      this.provider = provider;
   }

   private XRelationType getType(RelationTypeId type) {
      Conditions.checkNotNull(type, "relationType");
      return provider.getRelationTypeIndex().getDslTypeByToken(type);
   }

   @Override
   public Collection<RelationTypeToken> getAll() {
      return provider.getRelationTypeIndex().getAllTokens();
   }

   @Override
   public RelationTypeToken get(Id id) {
      if (id instanceof RelationTypeToken) {
         return (RelationTypeToken) id;
      }
      return provider.getRelationTypeIndex().get(id);
   }

   @Override
   public RelationTypeToken get(Long id) {
      return provider.getRelationTypeIndex().get(id);
   }

   @Override
   public RelationTypeMultiplicity getMultiplicity(RelationTypeId relation) {
      XRelationType type = getType(relation);
      String multiplicityId = type.getMultiplicity().getName();
      RelationTypeMultiplicity multiplicity = RelationTypeMultiplicity.getFromString(multiplicityId);
      return multiplicity;
   }

   @Override
   public String getSideName(IRelationType relation, RelationSide relationSide) {
      Conditions.checkNotNull(relationSide, "relationSide");
      return relationSide == RelationSide.SIDE_A ? getSideAName(relation) : getSideBName(relation);
   }

   @Override
   public String getSideAName(IRelationType relation) {
      XRelationType type = getType(relation);
      return type.getSideAName();
   }

   @Override
   public String getSideBName(IRelationType relation) {
      XRelationType type = getType(relation);
      return type.getSideBName();
   }

   @Override
   public boolean isSideAName(IRelationType relation, String sideName) {
      XRelationType type = getType(relation);
      boolean isSideA = type.getSideAName().equals(sideName);
      if (!isSideA && !type.getSideBName().equals(sideName)) {
         throw new OseeArgumentException("sideName does not match either of the available side names");
      }
      return isSideA;
   }

   @Override
   public boolean isOrdered(IRelationType relation) {
      return !RelationSorter.UNORDERED.equals(getDefaultOrderTypeGuid(relation));
   }

   @Override
   public RelationSorter getDefaultOrderTypeGuid(IRelationType relation) {
      XRelationType type = getType(relation);
      return RelationSorter.valueOfName(type.getDefaultOrderType());
   }

   @Override
   public IArtifactType getArtifactTypeSideA(IRelationType relation) {
      return getArtifactType(relation, RelationSide.SIDE_A);
   }

   @Override
   public IArtifactType getArtifactTypeSideB(IRelationType relation) {
      return getArtifactType(relation, RelationSide.SIDE_B);
   }

   @Override
   public IArtifactType getArtifactType(RelationTypeId relation, RelationSide relationSide) {
      Conditions.checkNotNull(relation, "relationType");
      Conditions.checkNotNull(relationSide, "relationSide");
      return provider.getRelationTypeIndex().getArtifactType(relation, relationSide);
   }

   @Override
   public boolean isArtifactTypeAllowed(RelationTypeId relation, RelationSide relationSide, IArtifactType artifactType) {
      Conditions.checkNotNull(relation, "relationType");
      Conditions.checkNotNull(relationSide, "relationSide");
      Conditions.checkNotNull(artifactType, "artifactType");
      return provider.getRelationTypeIndex().isArtifactTypeAllowed(relation, relationSide, artifactType);
   }

   @Override
   public boolean isEmpty() {
      return provider.getRelationTypeIndex().isEmpty();
   }

   @Override
   public int size() {
      return provider.getRelationTypeIndex().size();
   }

   @Override
   public boolean exists(Id id) {
      return provider.getRelationTypeIndex().exists(id);
   }
}