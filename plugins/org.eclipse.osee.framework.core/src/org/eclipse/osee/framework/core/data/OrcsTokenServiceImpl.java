/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;

/**
 * @author Ryan D. Brooks
 */
public final class OrcsTokenServiceImpl implements OrcsTokenService {
   private final Map<Long, ArtifactTypeToken> artifactTypes = new ConcurrentHashMap<>();
   private final Map<Long, AttributeTypeToken> attributeTypes = new ConcurrentHashMap<>();
   private final Map<Long, RelationTypeToken> relationTypes = new ConcurrentHashMap<>();

   /**
    * Register core types first to prevent their ids from being registered by mistaken or malicious code
    */
   public OrcsTokenServiceImpl() {
      new CoreAttributeTypes().registerTypes(this);
   }

   public void addTypeTokenProvider(OrcsTypeTokenProvider typeProvider) {
      typeProvider.registerTypes(this);
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
   public AttributeTypeToken getAttributeType(Long id) {
      AttributeTypeToken attributeType = attributeTypes.get(id);
      if (attributeType == null) {
         throw new OseeTypeDoesNotExist("Attribute type [%s] is not available.", id);
      }
      return attributeType;
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
   public ArtifactTypeToken getArtifactTypeOrSentinel(Long id) {
      ArtifactTypeToken artifactType = artifactTypes.get(id);
      if (artifactType == null) {
         return ArtifactTypeToken.SENTINEL;
      }
      return artifactType;
   }

   @Override
   public AttributeTypeToken getAttributeTypeOrSentinel(Long id) {
      AttributeTypeToken attributeType = attributeTypes.get(id);
      if (attributeType == null) {
         return AttributeTypeToken.SENTINEL;
      }
      return attributeType;
   }

   @Override
   public RelationTypeToken getRelationTypeOrSentinel(Long id) {
      RelationTypeToken relationType = relationTypes.get(id);
      if (relationType == null) {
         return RelationTypeToken.SENTINEL;
      }
      return relationType;
   }

   @Override
   public void registerArtifactType(ArtifactTypeToken artifactType) {
      if (artifactTypes.putIfAbsent(artifactType.getId(), artifactType) != null) {
         throw new OseeArgumentException("An artifact type with the id %s has already been registered.", artifactType);
      }
      artifactTypes.put(artifactType.getId(), artifactType);
   }

   @Override
   public void registerAttributeType(AttributeTypeToken attributeType) {
      if (attributeTypes.putIfAbsent(attributeType.getId(), attributeType) != null) {
         throw new OseeArgumentException("An attribute type with the id %s has already been registered.",
            attributeType);
      }
      attributeTypes.put(attributeType.getId(), attributeType);
   }

   @Override
   public void registerRelationType(RelationTypeToken relationType) {
      if (relationTypes.putIfAbsent(relationType.getId(), relationType) != null) {
         throw new OseeArgumentException("A relation type with the id %s has already been registered.", relationType);
      }
      relationTypes.put(relationType.getId(), relationType);
   }

   public void start() {
   }
}