/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.types.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.ConnectionHandlerStatement;
import org.eclipse.osee.framework.database.core.SQL3DataType;
import org.eclipse.osee.framework.database.core.SequenceManager;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeExtensionManager;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.OseeEnumType;
import org.eclipse.osee.framework.skynet.core.attribute.providers.IAttributeDataProvider;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.types.AbstractOseeCache;
import org.eclipse.osee.framework.skynet.core.types.IOseeDataAccessor;
import org.eclipse.osee.framework.skynet.core.types.IOseeTypeFactory;

/**
 * @author Roberto E. Escobar
 */
public class DatabaseAttributeTypeAccessor implements IOseeDataAccessor<AttributeType> {

   private static final String SELECT_ATTRIBUTE_TYPES =
         "SELECT * FROM osee_attribute_type aty1, osee_attribute_base_type aby1, osee_attribute_provider_type apy1 WHERE aty1.attr_base_type_id = aby1.attr_base_type_id AND aty1.attr_provider_type_id = apy1.attr_provider_type_id";
   private static final String INSERT_ATTRIBUTE_TYPE =
         "INSERT INTO osee_attribute_type (attr_type_id, attr_type_guid, attr_base_type_id, attr_provider_type_id, file_type_extension, name, default_value, enum_type_id, min_occurence, max_occurence, tip_text, tagger_id) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
   private static final String UPDATE_ATTRIBUTE_TYPE =
         "update osee_attribute_type SET attr_base_type_id=?, attr_provider_type_id=?, file_type_extension=?, name=?, default_value=?, enum_type_id=?, min_occurence=?, max_occurence=?, tip_text=?, tagger_id=? where attr_type_id = ?";

   private static final String INSERT_BASE_ATTRIBUTE_TYPE =
         "INSERT INTO osee_attribute_base_type (attr_base_type_id, attribute_class) VALUES (?, ?)";
   private static final String INSERT_ATTRIBUTE_PROVIDER_TYPE =
         "INSERT INTO osee_attribute_provider_type (attr_provider_type_id, attribute_provider_class) VALUES (?, ?)";
   private static final String SELECT_ATTRIBUTE_BASE_TYPE =
         "SELECT attr_base_type_id FROM osee_attribute_base_type WHERE attribute_class = ?";
   private static final String SELECT_ATTRIBUTE_PROVIDER_TYPE =
         "SELECT attr_provider_type_id FROM osee_attribute_provider_type WHERE attribute_provider_class = ?";

   private final AbstractOseeCache<OseeEnumType> enumCache;

   public DatabaseAttributeTypeAccessor(AbstractOseeCache<OseeEnumType> enumCache) {
      this.enumCache = enumCache;
   }

   @Override
   public void load(AbstractOseeCache<AttributeType> cache, IOseeTypeFactory factory) throws OseeCoreException {
      enumCache.ensurePopulated();
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();

      try {
         chStmt.runPreparedQuery(SELECT_ATTRIBUTE_TYPES);

         while (chStmt.next()) {
            int attributeTypeId = chStmt.getInt("attr_type_id");
            String baseClassString = chStmt.getString("attribute_class");
            String baseProviderClassString = chStmt.getString("attribute_provider_class");
            try {
               Class<? extends Attribute<?>> baseAttributeClass =
                     AttributeExtensionManager.getAttributeClassFor(baseClassString);
               Class<? extends IAttributeDataProvider> providerAttributeClass =
                     AttributeExtensionManager.getAttributeProviderClassFor(baseProviderClassString);
               int enumTypeId = chStmt.getInt("enum_type_id");
               OseeEnumType oseeEnumType = enumCache.getById(enumTypeId);

               AttributeType attributeType = cache.getById(attributeTypeId);
               if (attributeType == null) {
                  attributeType =
                        factory.createAttributeType(cache, chStmt.getString("attr_type_guid"),
                              chStmt.getString("name"), baseClassString, baseProviderClassString, baseAttributeClass,
                              providerAttributeClass, chStmt.getString("file_type_extension"),
                              chStmt.getString("default_value"), oseeEnumType, chStmt.getInt("min_occurence"),
                              chStmt.getInt("max_occurence"), chStmt.getString("tip_text"),
                              chStmt.getString("tagger_id"));
                  attributeType.setId(attributeTypeId);
                  attributeType.setModificationType(ModificationType.MODIFIED);
                  cache.cache(attributeType);
               } else {
                  attributeType.setFields(chStmt.getString("name"), baseClassString, baseProviderClassString,
                        baseAttributeClass, providerAttributeClass, chStmt.getString("file_type_extension"),
                        chStmt.getString("default_value"), oseeEnumType, chStmt.getInt("min_occurence"),
                        chStmt.getInt("max_occurence"), chStmt.getString("tip_text"), chStmt.getString("tagger_id"));
               }
               attributeType.clearDirty();
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      } finally {
         chStmt.close();
      }
   }

   @Override
   public void store(AbstractOseeCache<AttributeType> cache, Collection<AttributeType> types) throws OseeCoreException {
      List<Object[]> insertData = new ArrayList<Object[]>();
      List<Object[]> updateData = new ArrayList<Object[]>();
      for (AttributeType type : types) {
         if (type.isDirty()) {
            switch (type.getModificationType()) {
               case NEW:
                  type.setId(SequenceManager.getNextAttributeTypeId());
                  insertData.add(toInsertValues(type));
                  break;
               case MODIFIED:
                  updateData.add(toUpdateValues(type));
                  break;
               default:
                  break;
            }
         }
      }
      ConnectionHandler.runBatchUpdate(INSERT_ATTRIBUTE_TYPE, insertData);
      ConnectionHandler.runBatchUpdate(UPDATE_ATTRIBUTE_TYPE, updateData);
      for (AttributeType type : types) {
         type.clearDirty();
      }
   }

   private Object[] toInsertValues(AttributeType type) throws OseeDataStoreException {
      int attrBaseTypeId = getOrCreateAttributeBaseType(type.getBaseAttributeTypeId());
      int attrProviderTypeId = getOrCreateAttributeProviderType(type.getAttributeProviderId());
      return new Object[] {type.getId(), type.getGuid(), attrBaseTypeId, attrProviderTypeId,
            type.getFileTypeExtension() == null ? SQL3DataType.VARCHAR : type.getFileTypeExtension(),
            type.getName() == null ? SQL3DataType.VARCHAR : type.getName(),
            type.getDefaultValue() == null ? SQL3DataType.VARCHAR : type.getDefaultValue(), type.getOseeEnumTypeId(),
            type.getMinOccurrences(), type.getMaxOccurrences(),
            type.getDescription() == null ? SQL3DataType.VARCHAR : type.getDescription(),
            type.getTaggerId() == null ? SQL3DataType.VARCHAR : type.getTaggerId()};
   }

   private Object[] toUpdateValues(AttributeType type) throws OseeDataStoreException {
      int attrBaseTypeId = getOrCreateAttributeBaseType(type.getBaseAttributeTypeId());
      int attrProviderTypeId = getOrCreateAttributeProviderType(type.getAttributeProviderId());
      return new Object[] {attrBaseTypeId, attrProviderTypeId,
            type.getFileTypeExtension() == null ? SQL3DataType.VARCHAR : type.getFileTypeExtension(),
            type.getName() == null ? SQL3DataType.VARCHAR : type.getName(),
            type.getDefaultValue() == null ? SQL3DataType.VARCHAR : type.getDefaultValue(), type.getOseeEnumTypeId(),
            type.getMinOccurrences(), type.getMaxOccurrences(),
            type.getDescription() == null ? SQL3DataType.VARCHAR : type.getDescription(),
            type.getTaggerId() == null ? SQL3DataType.VARCHAR : type.getTaggerId(), type.getId()};
   }

   private int getOrCreateAttributeProviderType(String attrProviderExtension) throws OseeDataStoreException {
      int attrBaseTypeId = -1;
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      try {
         chStmt.runPreparedQuery(SELECT_ATTRIBUTE_PROVIDER_TYPE, attrProviderExtension);
         if (chStmt.next()) {
            attrBaseTypeId = chStmt.getInt("attr_provider_type_id");
         } else {
            attrBaseTypeId = SequenceManager.getNextAttributeProviderTypeId();
            ConnectionHandler.runPreparedUpdate(INSERT_ATTRIBUTE_PROVIDER_TYPE, attrBaseTypeId, attrProviderExtension);
         }
      } finally {
         chStmt.close();
      }
      return attrBaseTypeId;
   }

   private int getOrCreateAttributeBaseType(String attrBaseExtension) throws OseeDataStoreException {
      int attrBaseTypeId = -1;
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      try {
         chStmt.runPreparedQuery(SELECT_ATTRIBUTE_BASE_TYPE, attrBaseExtension);
         if (chStmt.next()) {
            attrBaseTypeId = chStmt.getInt("attr_base_type_id");
         } else {
            attrBaseTypeId = SequenceManager.getNextAttributeBaseTypeId();
            ConnectionHandler.runPreparedUpdate(INSERT_BASE_ATTRIBUTE_TYPE, attrBaseTypeId, attrBaseExtension);
         }
      } finally {
         chStmt.close();
      }
      return attrBaseTypeId;
   }
}
