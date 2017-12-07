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

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactType;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.data.ArtifactTypes;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactTypesImpl implements ArtifactTypes {

   public static interface ArtifactTypeIndexProvider {
      ArtifactTypeIndex getArtifactTypeIndex();
   }

   private final ArtifactTypeIndexProvider provider;

   public ArtifactTypesImpl(ArtifactTypeIndexProvider provider) {
      this.provider = provider;
   }

   private ArtifactTypeIndex getArtifactTypesIndex() {
      return provider.getArtifactTypeIndex();
   }

   private XArtifactType getType(ArtifactTypeId artType) {
      Conditions.checkNotNull(artType, "artifactType");
      return getArtifactTypesIndex().getDslTypeByToken(artType);
   }

   @Override
   public Collection<IArtifactType> getAll() {
      return getArtifactTypesIndex().getAllTokens();
   }

   @Override
   public IArtifactType get(Id id) {
      return getArtifactTypesIndex().get(id);
   }

   @Override
   public IArtifactType get(Long id) {
      return getArtifactTypesIndex().get(id);
   }

   @Override
   public boolean isAbstract(ArtifactTypeId artType) {
      XArtifactType type = getType(artType);
      return type.isAbstract();
   }

   @Override
   public boolean hasSuperArtifactTypes(ArtifactTypeId artType) {
      return !getSuperArtifactTypes(artType).isEmpty();
   }

   @Override
   public Collection<? extends ArtifactTypeId> getSuperArtifactTypes(ArtifactTypeId artType) {
      Conditions.checkNotNull(artType, "artifactType");
      return getArtifactTypesIndex().getSuperTypes(artType);
   }

   @Override
   public boolean inheritsFrom(ArtifactTypeId thisType, ArtifactTypeId... otherTypes) {
      Conditions.checkNotNull(thisType, "thisArtifactType");
      Conditions.checkNotNull(otherTypes, "otherArtifactTypes");
      return getArtifactTypesIndex().inheritsFrom(thisType, otherTypes);
   }

   @Override
   public Collection<? extends IArtifactType> getAllDescendantTypes(ArtifactTypeId artType) {
      Conditions.checkNotNull(artType, "artifactType");
      LinkedHashSet<IArtifactType> descendants = Sets.newLinkedHashSet();
      walkDescendants(artType, descendants);
      return descendants;
   }

   private void walkDescendants(ArtifactTypeId artifactType, Collection<IArtifactType> descendants) {
      Collection<IArtifactType> childTypes = getArtifactTypesIndex().getDescendantTypes(artifactType);
      if (!childTypes.isEmpty()) {
         for (IArtifactType type : childTypes) {
            walkDescendants(type, descendants);
            descendants.add(type);
         }
      }
   }

   @Override
   public boolean isValidAttributeType(ArtifactTypeId artType, BranchId branch, AttributeTypeId attributeType) {
      Collection<AttributeTypeToken> attributes = getAttributeTypes(artType, branch);
      return attributes.contains(attributeType);
   }

   @Override
   public Collection<AttributeTypeToken> getAttributeTypes(ArtifactTypeId artType, BranchId branch) {
      Conditions.checkNotNull(artType, "artifactType");
      Conditions.checkNotNull(branch, "branch");
      return getArtifactTypesIndex().getAttributeTypes(artType, branch);
   }

   @Override
   public boolean isEmpty() {
      return getArtifactTypesIndex().isEmpty();
   }

   @Override
   public int size() {
      return getArtifactTypesIndex().size();
   }

   @Override
   public boolean exists(Id id) {
      return getArtifactTypesIndex().exists(id);
   }

   @Override
   public Map<BranchId, Collection<AttributeTypeToken>> getAllAttributeTypes(ArtifactTypeId artType) {
      return getArtifactTypesIndex().getAllAttributeTypes(artType);
   }

}
