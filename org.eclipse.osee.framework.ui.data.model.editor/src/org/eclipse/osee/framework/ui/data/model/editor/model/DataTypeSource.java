/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.data.model.editor.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.db.connection.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;

/**
 * @author Roberto E. Escobar
 */
public class DataTypeSource {

   private final TypeManager<ArtifactDataType> artifactTypeManager;
   private final TypeManager<AttributeDataType> attributeTypeManager;
   private final TypeManager<RelationDataType> relationTypeManager;

   private final HashCollection<ArtifactDataType, AttributeDataType> artifactToAttributes;
   private final HashCollection<ArtifactDataType, RelationDataType> artifactToRelations;

   private final String sourceId;

   public DataTypeSource(String sourceId) {
      this.sourceId = sourceId;

      artifactTypeManager = new TypeManager<ArtifactDataType>();
      attributeTypeManager = new TypeManager<AttributeDataType>();
      relationTypeManager = new TypeManager<RelationDataType>();

      artifactToAttributes = new HashCollection<ArtifactDataType, AttributeDataType>();
      artifactToRelations = new HashCollection<ArtifactDataType, RelationDataType>();
   }

   public String getSourceId() {
      return sourceId;
   }

   public void add(DataType dataType) {
      if (dataType instanceof ArtifactDataType) {
         artifactTypeManager.add((ArtifactDataType) dataType);
      } else if (dataType instanceof AttributeDataType) {
         attributeTypeManager.add((AttributeDataType) dataType);
      } else if (dataType instanceof RelationDataType) {
         relationTypeManager.add((RelationDataType) dataType);
      }
   }

   public void addAll(Collection<? extends DataType> dataTypes) {
      for (DataType dataType : dataTypes) {
         add(dataType);
      }
   }

   public TypeManager<ArtifactDataType> getArtifactTypeManager() {
      return artifactTypeManager;
   }

   public TypeManager<AttributeDataType> getAttributeTypeManager() {
      return attributeTypeManager;
   }

   public TypeManager<RelationDataType> getRelationTypeManager() {
      return relationTypeManager;
   }

   public void addAttributeToArtifact(String artifactUniqueId, String attributeUniqueId) throws OseeTypeDoesNotExist {
      ArtifactDataType artifactDataType = getArtifactTypeManager().getById(artifactUniqueId);
      AttributeDataType attributeDataType = getAttributeTypeManager().getById(attributeUniqueId);
      if (artifactDataType == null) {
         throw new OseeTypeDoesNotExist(String.format("Artifact type with unique id [%s] was null", artifactUniqueId));
      }
      if (attributeDataType == null) {
         throw new OseeTypeDoesNotExist(String.format("Attribute type with unique id [%s] was null", attributeUniqueId));
      }
      addAttributeToArtifact(artifactDataType, attributeDataType);
   }

   public void addAttributeToArtifact(ArtifactDataType artifactDataType, AttributeDataType attributeDataType) {
      artifactToAttributes.put(artifactDataType, attributeDataType);
   }

   public void addRelationToArtifact(String artifactUniqueId, String relationUniqueId) throws OseeTypeDoesNotExist {
      ArtifactDataType artifactDataType = getArtifactTypeManager().getById(artifactUniqueId);
      RelationDataType relationDataType = getRelationTypeManager().getById(relationUniqueId);
      if (artifactDataType == null) {
         throw new OseeTypeDoesNotExist(String.format("Artifact type with unique id [%s] was null", artifactUniqueId));
      }
      if (relationDataType == null) {
         throw new OseeTypeDoesNotExist(String.format("Relation type with unique id [%s] was null", relationUniqueId));
      }
      addAttributeToArtifact(artifactDataType, relationDataType);
   }

   public void addAttributeToArtifact(ArtifactDataType artifactDataType, RelationDataType relationDataType) {
      artifactToRelations.put(artifactDataType, relationDataType);
   }

   public void addRelationToArtifact(String artifactUniqueId, String relationUniqueId, int aMax, int bMax) {

   }

   public Collection<AttributeDataType> getAttributesForArtifact(ArtifactDataType artifactDataType) {
      return artifactToAttributes.getValues(artifactDataType);
   }

   public Collection<RelationDataType> getRelationsForArtifact(ArtifactDataType artifactDataType) {
      return artifactToRelations.getValues(artifactDataType);
   }

   public final class TypeManager<T extends DataType> {
      private final Map<String, T> types;

      public TypeManager() {
         this.types = new HashMap<String, T>();
      }

      public Set<String> getIds() {
         return types.keySet();
      }

      public List<T> getAll() {
         return new ArrayList<T>(types.values());
      }

      public T getById(String uniqueId) {
         return types.get(uniqueId);
      }

      public void add(T type) {
         types.put(type.getUniqueId(), type);
      }

      public void addAll(Collection<T> types) {
         for (T type : types) {
            add(type);
         }
      }

      public int size() {
         return types.size();
      }

      public T getFirst() {
         return types.isEmpty() ? null : types.values().iterator().next();
      }

      public boolean removeById(String uniqueId) {
         return types.remove(uniqueId) != null;
      }
   }
}
