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
package org.eclipse.osee.framework.ui.data.model.editor.input;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IPath;
import org.eclipse.osee.framework.core.data.OseeInfo;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.CompositeKey;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.ObjectPair;
import org.eclipse.osee.framework.ui.data.model.editor.model.ArtifactDataType;
import org.eclipse.osee.framework.ui.data.model.editor.model.AttributeDataType;
import org.eclipse.osee.framework.ui.data.model.editor.model.DataTypeCache;
import org.eclipse.osee.framework.ui.data.model.editor.model.DataTypeSource;
import org.eclipse.osee.framework.ui.data.model.editor.model.RelationDataType;
import org.eclipse.osee.framework.ui.data.model.editor.model.TypeManager;

/**
 * @author Roberto E. Escobar
 */
public class OseeDataTypeFactory {
   private static OseeDataTypeFactory instance = new OseeDataTypeFactory();

   private List<IOseeDataTypeHandler> fileHandlers;

   private OseeDataTypeFactory() {
      if (fileHandlers == null) {
         fileHandlers = new ArrayList<IOseeDataTypeHandler>();
         fileHandlers.add(new ExcelXmlODMFileHandler());
      }
   }

   public static DataTypeSource loadFromFile(IPath file) throws OseeCoreException {
      DataTypeSource dataTypeSource = null;
      for (IOseeDataTypeHandler handler : instance.fileHandlers) {
         if (handler.isValid(file)) {
            dataTypeSource = handler.toODMDataTypeSource(file);
            if (dataTypeSource != null) {
               break;
            }
         }
      }
      return dataTypeSource;
   }

   public static void addTypesFromDataStore(DataTypeCache cache) throws OseeCoreException {
      String sourceId = OseeInfo.getValue("osee.db.guid");
      DataTypeSource dataTypeSource = new DataTypeSource(sourceId, true);
      dataTypeSource.addAll(OseeDataTypeDatastore.getArtifactDataTypes());
      dataTypeSource.addAll(OseeDataTypeDatastore.getAttributeTypes());
      dataTypeSource.addAll(OseeDataTypeDatastore.getRelationDataTypes());
      HashCollection<String, String> entries = OseeDataTypeDatastore.getArtifactToAttributeEntries();

      TypeManager<ArtifactDataType> artifactDataType = dataTypeSource.getArtifactTypeManager();
      TypeManager<AttributeDataType> attributeDataType = dataTypeSource.getAttributeTypeManager();

      for (String artifactKey : entries.keySet()) {
         ArtifactDataType artifact = artifactDataType.getById(artifactKey);
         if (artifact != null) {
            Collection<String> attrKeys = entries.getValues(artifactKey);
            if (attrKeys != null) {
               for (String attrKey : attrKeys) {
                  AttributeDataType attribute = attributeDataType.getById(attrKey);
                  if (attribute != null) {
                     artifact.add(attribute);
                  }
               }
            }
         }
      }
      CompositeKeyHashMap<String, String, ObjectPair<Integer, Integer>> relationsMap =
            OseeDataTypeDatastore.getArtifactToRelationEntries();
      TypeManager<RelationDataType> relationDataType = dataTypeSource.getRelationTypeManager();
      for (CompositeKey<String, String> key : relationsMap.keySet()) {
         ArtifactDataType artifact = artifactDataType.getById(key.getKey1());
         if (artifact != null) {
            Collection<String> relKeys = entries.getValues(key.getKey1());
            if (relKeys != null) {
               for (String relKey : relKeys) {
                  RelationDataType relation = relationDataType.getById(relKey);
                  if (relation != null) {
                     artifact.add(relation);
                  }
               }
            }
         }
      }

      HashCollection<String, String> parentChildTable = OseeDataTypeDatastore.getArtifactInheritance();
      for (String parentKey : parentChildTable.keySet()) {
         ArtifactDataType parent = artifactDataType.getById(parentKey);
         if (parent != null) {
            Collection<String> children = parentChildTable.getValues(parentKey);
            for (String childKey : children) {
               ArtifactDataType child = artifactDataType.getById(childKey);
               if (child != null) {
                  child.setParent(parent);
               }
            }
         }
      }
      cache.addDataTypeSource(dataTypeSource);
   }
}
