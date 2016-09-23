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
import org.eclipse.osee.framework.core.data.RelationSorter;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationType;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.data.RelationTypes;

/**
 * @author Roberto E. Escobar
 */
public class RelationTypesImpl implements RelationTypes {

   public static interface RelationTypeIndexProvider {
      RelationTypeIndex getRelationTypeIndex() throws OseeCoreException;
   }

   private final RelationTypeIndexProvider provider;

   public RelationTypesImpl(RelationTypeIndexProvider provider) {
      this.provider = provider;
   }

   private XRelationType getType(IRelationType type) throws OseeCoreException {
      Conditions.checkNotNull(type, "relationType");
      return provider.getRelationTypeIndex().getDslTypeByToken(type);
   }

   @Override
   public Collection<? extends IRelationType> getAll() throws OseeCoreException {
      return provider.getRelationTypeIndex().getAllTokens();
   }

   @Override
   public IRelationType getByUuid(Long uuid) throws OseeCoreException {
      Conditions.checkNotNull(uuid, "uuid");
      return provider.getRelationTypeIndex().getTokenByUuid(uuid);
   }

   @Override
   public RelationTypeMultiplicity getMultiplicity(IRelationType relation) throws OseeCoreException {
      XRelationType type = getType(relation);
      String multiplicityId = type.getMultiplicity().getName();
      RelationTypeMultiplicity multiplicity = RelationTypeMultiplicity.getFromString(multiplicityId);
      return multiplicity;
   }

   @Override
   public String getSideName(IRelationType relation, RelationSide relationSide) throws OseeCoreException {
      Conditions.checkNotNull(relationSide, "relationSide");
      return relationSide == RelationSide.SIDE_A ? getSideAName(relation) : getSideBName(relation);
   }

   @Override
   public String getSideAName(IRelationType relation) throws OseeCoreException {
      XRelationType type = getType(relation);
      return type.getSideAName();
   }

   @Override
   public String getSideBName(IRelationType relation) throws OseeCoreException {
      XRelationType type = getType(relation);
      return type.getSideBName();
   }

   @Override
   public boolean isSideAName(IRelationType relation, String sideName) throws OseeCoreException {
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
   public RelationSorter getDefaultOrderTypeGuid(IRelationType relation) throws OseeCoreException {
      XRelationType type = getType(relation);
      return RelationSorter.valueOfName(type.getDefaultOrderType());
   }

   @Override
   public IArtifactType getArtifactTypeSideA(IRelationType relation) throws OseeCoreException {
      return getArtifactType(relation, RelationSide.SIDE_A);
   }

   @Override
   public IArtifactType getArtifactTypeSideB(IRelationType relation) throws OseeCoreException {
      return getArtifactType(relation, RelationSide.SIDE_B);
   }

   @Override
   public IArtifactType getArtifactType(IRelationType relation, RelationSide relationSide) throws OseeCoreException {
      Conditions.checkNotNull(relation, "relationType");
      Conditions.checkNotNull(relationSide, "relationSide");
      return provider.getRelationTypeIndex().getArtifactType(relation, relationSide);
   }

   @Override
   public boolean isArtifactTypeAllowed(IRelationType relation, RelationSide relationSide, IArtifactType artifactType) throws OseeCoreException {
      Conditions.checkNotNull(relation, "relationType");
      Conditions.checkNotNull(relationSide, "relationSide");
      Conditions.checkNotNull(artifactType, "artifactType");
      return provider.getRelationTypeIndex().isArtifactTypeAllowed(relation, relationSide, artifactType);
   }

   @Override
   public boolean isEmpty() throws OseeCoreException {
      return provider.getRelationTypeIndex().isEmpty();
   }

   @Override
   public int size() throws OseeCoreException {
      return provider.getRelationTypeIndex().size();
   }

   @Override
   public boolean exists(IRelationType item) throws OseeCoreException {
      return provider.getRelationTypeIndex().existsByUuid(item.getId());
   }
}
