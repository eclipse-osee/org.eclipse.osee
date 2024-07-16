/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.framework.core.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.enums.CoreTypeTokenProvider;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.jdk.core.type.NamedId;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;

/**
 * @author Ryan D. Brooks
 */
public final class OrcsTokenServiceImpl implements OrcsTokenService {
   private final Map<String, Class<?>> tokenClasses = new ConcurrentHashMap<>();
   private final Map<Long, ArtifactTypeToken> artifactTypes = new ConcurrentHashMap<>();
   private final Map<Long, AttributeTypeGeneric<?>> attributeTypes = new ConcurrentHashMap<>();
   private final Map<Long, RelationTypeToken> relationTypes = new ConcurrentHashMap<>();

   private final Map<Long, ArtifactTypeJoin> artifactTypeJoins = new ConcurrentHashMap<>();
   private final Map<Long, AttributeTypeJoin> attributeTypeJoins = new ConcurrentHashMap<>();
   private final Map<Long, RelationTypeJoin> relationTypeJoins = new ConcurrentHashMap<>();

   private final Map<Long, Tuple2Type<?, ?>> tuple2Types = new ConcurrentHashMap<>();
   private final Map<Long, Tuple3Type<?, ?, ?>> tuple3Types = new ConcurrentHashMap<>();
   private final Map<Long, Tuple4Type<?, ?, ?, ?>> tuple4Types = new ConcurrentHashMap<>();

   /**
    * Register core types first to prevent their ids from being registered by mistaken or malicious code
    */
   public OrcsTokenServiceImpl() {
      artifactTypes.put(ArtifactTypeToken.SENTINEL.getId(), ArtifactTypeToken.SENTINEL);
      attributeTypes.put(AttributeTypeToken.SENTINEL.getId(), AttributeTypeToken.SENTINEL);
      relationTypes.put(RelationTypeToken.SENTINEL.getId(), RelationTypeToken.SENTINEL);
      new CoreTypeTokenProvider().registerTypes(this);
   }

   public void addTypeTokenProvider(OrcsTypeTokenProvider typeProvider) {
      typeProvider.registerTypes(this);
   }

   @Override
   public BranchToken getBranch(BranchId branch) {
      return BranchToken.valueOf(branch);
   }

   @Override
   public ArtifactTypeToken getArtifactType(Long id) {
      ArtifactTypeToken artifactType = artifactTypes.get(id);
      if (artifactType == null) {
         throw new OseeTypeDoesNotExist("Artifact type [%s] is not available.", id);
      }
      return artifactType;
   }

   @Override
   public ArtifactTypeToken getArtifactType(String name) {
      for (ArtifactTypeToken artifactType : artifactTypes.values()) {
         if (artifactType.getName().equals(name)) {
            return artifactType;
         }
      }
      throw new OseeTypeDoesNotExist("Artifact type [%s] is not available.", name);
   }

   @Override
   public AttributeTypeGeneric<?> getAttributeType(Long id) {
      AttributeTypeGeneric<?> attributeType = attributeTypes.get(id);
      if (attributeType == null) {
         throw new OseeTypeDoesNotExist("Attribute type [%s] is not available.", id);
      }
      return attributeType;
   }

   @Override
   public AttributeTypeGeneric<?> getAttributeType(AttributeTypeId id) {
      return getAttributeType(id.getId());
   }

   @Override
   public AttributeTypeGeneric<?> getAttributeType(String name) {
      for (AttributeTypeGeneric<?> attributeType : attributeTypes.values()) {
         if (attributeType.getName().equals(name)) {
            return attributeType;
         }
      }
      throw new OseeTypeDoesNotExist("Attribute type [%s] is not available.", name);
   }

   @Override
   public RelationTypeToken getRelationType(Long id) {
      RelationTypeToken relationType = relationTypes.get(id);
      if (relationType == null) {
         throw new OseeTypeDoesNotExist("Relation type [%s] is not available.", id);
      }
      return relationType;
   }

   @Override
   public RelationTypeToken getRelationType(String name) {
      for (RelationTypeToken relationType : relationTypes.values()) {
         if (relationType.getName().equals(name)) {
            return relationType;
         }
      }
      throw new OseeTypeDoesNotExist("Relation type [%s] is not available.", name);
   }

   @Override
   public <E1, E2> Tuple2Type<E1, E2> getTuple2Type(Long id) {
      Tuple2Type<E1, E2> tuple = (Tuple2Type<E1, E2>) tuple2Types.get(id);
      if (tuple == null) {
         return tuple;
      }
      throw new OseeTypeDoesNotExist("Tuple 2 type [%s] is not available.", id);
   }

   @Override
   public <E1, E2, E3> Tuple3Type<E1, E2, E3> getTuple3Type(Long id) {
      Tuple3Type<E1, E2, E3> tuple = (Tuple3Type<E1, E2, E3>) tuple3Types.get(id);
      if (tuple == null) {
         return tuple;
      }
      throw new OseeTypeDoesNotExist("Tuple 3 type [%s] is not available.", id);
   }

   @Override
   public <E1, E2, E3, E4> Tuple4Type<E1, E2, E3, E4> getTuple4Type(Long id) {
      Tuple4Type<E1, E2, E3, E4> tuple = (Tuple4Type<E1, E2, E3, E4>) tuple4Types.get(id);
      if (tuple == null) {
         return tuple;
      }
      throw new OseeTypeDoesNotExist("Tuple 4 type [%s] is not available.", id);
   }

   @Override
   public ArtifactTypeToken getArtifactTypeOrSentinel(Long id) {
      return getXTypeOrSentinel(artifactTypes, id, ArtifactTypeToken.SENTINEL);
   }

   @Override
   public AttributeTypeGeneric<?> getAttributeTypeOrSentinel(Long id) {
      return getXTypeOrSentinel(attributeTypes, id, AttributeTypeGeneric.SENTINEL);
   }

   @Override
   public RelationTypeToken getRelationTypeOrSentinel(Long id) {
      return getXTypeOrSentinel(relationTypes, id, RelationTypeToken.SENTINEL);
   }

   private <T extends NamedId> T getXTypeOrSentinel(Map<Long, T> types, Long id, T sentinel) {
      if (id == null) {
         return sentinel;
      }
      T type = types.get(id);
      if (type == null) {
         return sentinel;
      }
      return type;
   }

   @Override
   public ArtifactTypeToken getArtifactTypeOrCreate(Long id) {
      ArtifactTypeToken artifactType = getArtifactTypeOrSentinel(id);
      if (artifactType.isInvalid()) {
         artifactType = ArtifactTypeToken.valueOf(id, "Mising Artifact Type " + id);
         registerArtifactType(artifactType);
      }
      return artifactType;
   }

   @Override
   public AttributeTypeGeneric<?> getAttributeTypeOrCreate(Long id) {
      AttributeTypeGeneric<?> attributeType = getAttributeTypeOrSentinel(id);
      if (attributeType.isInvalid()) {
         String missing = AttributeTypeToken.MISSING_TYPE + id;
         attributeType = AttributeTypeToken.valueOf(id, missing, missing);
         registerAttributeType(attributeType);
      }
      return attributeType;
   }

   @Override
   public RelationTypeToken getRelationTypeOrCreate(Long id) {
      RelationTypeToken relationType = getRelationTypeOrSentinel(id);
      if (relationType.isInvalid()) {
         relationType = RelationTypeToken.create(id, "Missing Artifact Type " + id, null, null, null, null, null, null);
         registerRelationType(relationType);
      }
      return relationType;
   }

   @Override
   public void registerTokenClasses(Class<?>[] tokenClasses) {
      for (Class<?> tokenClass : tokenClasses) {
         this.tokenClasses.put(tokenClass.getCanonicalName(), tokenClass);
      }
   }

   @Override
   public Class<?> getTokenClass(String canonicalClassName) {
      return tokenClasses.get(canonicalClassName);
   }

   @Override
   public void registerArtifactType(ArtifactTypeToken artifactType) {
      ArtifactTypeToken existingType = artifactTypes.putIfAbsent(artifactType.getId(), artifactType);
      if (existingType != null) {
         throw new OseeArgumentException("An artifact type %s with the same id as %s has already been registered.",
            existingType, artifactType);
      }
   }

   @Override
   public void registerAttributeType(AttributeTypeGeneric<?> attributeType) {
      AttributeTypeGeneric<?> existingType = attributeTypes.get(attributeType.getId());
      if (existingType == null) {
         attributeTypes.put(attributeType.getId(), attributeType);
      } else {
         if (existingType instanceof AttributeTypeEnum && attributeType instanceof AttributeTypeEnum && existingType.getId().equals(
            attributeType.getId())) {
            // Update the existing attribute type's enum values with the child attribute type's enum values
            existingType.toEnum().appendEnumValues(attributeType.toEnum());
         } else {
            throw new OseeArgumentException(
               "Cannot register the attribute type %s with ID %s. Existing attribute type %s with ID %s already exists",
               attributeType, attributeType.getId(), existingType, existingType.getId());
         }
      }
   }

   @Override
   public void registerRelationType(RelationTypeToken relationType) {
      RelationTypeToken existingType = relationTypes.putIfAbsent(relationType.getId(), relationType);
      if (existingType != null) {
         throw new OseeArgumentException("The relation type %s with the same id as %s has already been registered.",
            existingType, relationType);
      }
   }

   @Override
   public void registerArtifactTypeJoin(ArtifactTypeJoin typeJoin) {
      registerOrcsTypeJoin(typeJoin, artifactTypeJoins);
   }

   @Override
   public void registerAttributeTypeJoin(AttributeTypeJoin typeJoin) {
      registerOrcsTypeJoin(typeJoin, attributeTypeJoins);
   }

   @Override
   public void registerRelationTypeJoin(RelationTypeJoin typeJoin) {
      registerOrcsTypeJoin(typeJoin, relationTypeJoins);
   }

   @Override
   public ArtifactTypeJoin getArtifactTypeJoin(Long id) {
      ArtifactTypeJoin typeJoin = artifactTypeJoins.get(id);
      if (typeJoin == null) {
         throw new OseeTypeDoesNotExist("Artifact type join [%s] is not available.", id);
      }
      return typeJoin;
   }

   @Override
   public AttributeTypeJoin getAttributeTypeJoin(Long id) {
      AttributeTypeJoin typeJoin = attributeTypeJoins.get(id);
      if (typeJoin == null) {
         throw new OseeTypeDoesNotExist("Attribute type join [%s] is not available.", id);
      }
      return typeJoin;
   }

   @Override
   public RelationTypeJoin getRelationTypeJoin(Long id) {
      RelationTypeJoin typeJoin = relationTypeJoins.get(id);
      if (typeJoin == null) {
         throw new OseeTypeDoesNotExist("Relation type join [%s] is not available.", id);
      }
      return typeJoin;
   }

   @Override
   public Collection<ArtifactTypeJoin> getArtifactTypeJoins() {
      return artifactTypeJoins.values();
   }

   @Override
   public Collection<AttributeTypeJoin> getAttributeTypeJoins() {
      return attributeTypeJoins.values();
   }

   @Override
   public Collection<RelationTypeJoin> getRelationTypeJoins() {
      return relationTypeJoins.values();
   }

   private <T extends NamedId> void registerOrcsTypeJoin(T orcsTypeJoin, Map<Long, T> orcsTypeJoins) {
      T existingType = orcsTypeJoins.putIfAbsent(orcsTypeJoin.getId(), orcsTypeJoin);
      if (existingType != null) {
         throw new OseeArgumentException("The join type %s with the same id as %s has already been registered.",
            existingType, orcsTypeJoin);
      }
   }

   @Override
   public Iterable<AttributeTypeGeneric<?>> getTaggedAttrs() {
      Set<AttributeTypeGeneric<?>> attrTypeIds = new HashSet<>();
      for (Map.Entry<Long, AttributeTypeGeneric<?>> entry : attributeTypes.entrySet()) {
         AttributeTypeGeneric<?> attrType = entry.getValue();
         if (attrType.isTaggable()) {
            attrTypeIds.add(attrType);
         }
      }
      return attrTypeIds;
   }

   @Override
   public Collection<ArtifactTypeToken> getArtifactTypes() {
      return Collections.unmodifiableCollection(artifactTypes.values());
   }

   @Override
   public Collection<RelationTypeToken> getRelationTypes() {
      return Collections.unmodifiableCollection(relationTypes.values());
   }

   @Override
   public Collection<AttributeTypeGeneric<?>> getAttributeTypes() {
      return Collections.unmodifiableCollection(attributeTypes.values());
   }

   @Override
   public Collection<Tuple2Type<?, ?>> getTuple2Types() {
      return Collections.unmodifiableCollection(tuple2Types.values());
   }

   @Override
   public Collection<Tuple3Type<?, ?, ?>> getTuple3Types() {
      return Collections.unmodifiableCollection(tuple3Types.values());
   }

   @Override
   public Collection<Tuple4Type<?, ?, ?, ?>> getTuple4Types() {
      return Collections.unmodifiableCollection(tuple4Types.values());
   }

   @Override
   public List<ArtifactTypeToken> getConcreteArtifactTypes() {
      List<ArtifactTypeToken> concreteArtifactTypes = new ArrayList<>();
      for (ArtifactTypeToken artifactType : getArtifactTypes()) {
         if (!artifactType.isAbstract()) {
            concreteArtifactTypes.add(artifactType);
         }
      }
      return concreteArtifactTypes;
   }

   @Override
   public Set<ArtifactTypeToken> getValidArtifactTypes(AttributeTypeToken attributeType) {
      Set<ArtifactTypeToken> artifactTypes = new HashSet<>();
      for (ArtifactTypeToken artifactType : getArtifactTypes()) {
         if (artifactType.isValidAttributeType(attributeType)) {
            artifactTypes.add(artifactType);
         }
      }
      return artifactTypes;
   }

   @Override
   public List<RelationTypeToken> getValidRelationTypes(ArtifactTypeToken artifactType) {
      Collection<RelationTypeToken> relationTypes = getRelationTypes();
      List<RelationTypeToken> validRelationTypes = new ArrayList<>();
      for (RelationTypeToken relationType : relationTypes) {
         boolean onSideA = relationType.getRelationSideMax(artifactType, RelationSide.SIDE_A) > 0;
         boolean onSideB = relationType.getRelationSideMax(artifactType, RelationSide.SIDE_B) > 0;
         if (onSideA || onSideB) {
            validRelationTypes.add(relationType);
         }
      }
      return validRelationTypes;
   }

   @Override
   public boolean attributeTypeExists(String name) {
      for (AttributeTypeGeneric<?> attributeType : attributeTypes.values()) {
         if (attributeType.getName().equals(name)) {
            return true;
         }
      }
      return false;
   }

   @Override
   public Set<AttributeTypeToken> getSingletonAttributeTypes() {
      Set<AttributeTypeToken> attributeTypeTokens = new HashSet<>();
      for (ArtifactTypeToken artifactType : getArtifactTypes()) {
         artifactType.getSingletonAttributeTypes(attributeTypeTokens);
      }
      return attributeTypeTokens;
   }

   @Override
   public void registerTuple2Type(Tuple2Type<?, ?> tupleType) {
      Tuple2Type<?, ?> existingType = tuple2Types.putIfAbsent(tupleType.getId(), tupleType);
      if (existingType != null) {
         throw new OseeArgumentException("The tuple2 type %s with the same id as %s has already been registered.",
            existingType, tupleType);
      }
   }

   @Override
   public void registerTuple3Type(Tuple3Type<?, ?, ?> tupleType) {
      Tuple3Type<?, ?, ?> existingType = tuple3Types.putIfAbsent(tupleType.getId(), tupleType);
      if (existingType != null) {
         throw new OseeArgumentException("The tuple3 type %s with the same id as %s has already been registered.",
            existingType, tupleType);
      }
   }

   @Override
   public void registerTuple4Type(Tuple4Type<?, ?, ?, ?> tupleType) {
      Tuple4Type<?, ?, ?, ?> existingType = tuple4Types.putIfAbsent(tupleType.getId(), tupleType);
      if (existingType != null) {
         throw new OseeArgumentException("The tuple4 type %s with the same id as %s has already been registered.",
            existingType, tupleType);
      }
   }

}