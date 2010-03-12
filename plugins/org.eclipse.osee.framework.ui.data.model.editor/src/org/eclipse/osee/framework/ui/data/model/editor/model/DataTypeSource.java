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

import java.util.Collection;

/**
 * @author Roberto E. Escobar
 */
public class DataTypeSource {

   private final TypeManager<ArtifactDataType> artifactTypeManager;
   private final TypeManager<AttributeDataType> attributeTypeManager;
   private final TypeManager<RelationDataType> relationTypeManager;

   private final String sourceId;
   private final boolean isFromDataStore;

   public DataTypeSource(String sourceId) {
      this(sourceId, false);
   }

   public DataTypeSource(String sourceId, boolean isFromDataStore) {
      this.sourceId = sourceId;
      this.isFromDataStore = isFromDataStore;
      artifactTypeManager = new TypeManager<ArtifactDataType>();
      attributeTypeManager = new TypeManager<AttributeDataType>();
      relationTypeManager = new TypeManager<RelationDataType>();
   }

   public String getSourceId() {
      return sourceId;
   }

   public boolean isFromDataStore() {
      return isFromDataStore;
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
}
