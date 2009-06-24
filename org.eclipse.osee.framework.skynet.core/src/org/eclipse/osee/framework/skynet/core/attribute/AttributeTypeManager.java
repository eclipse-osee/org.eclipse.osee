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
package org.eclipse.osee.framework.skynet.core.attribute;

import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.ATTRIBUTE_BASE_TYPE_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.ATTRIBUTE_PROVIDER_TYPE_TABLE;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.core.SequenceManager;
import org.eclipse.osee.framework.db.connection.exception.OseeArgumentException;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.attribute.providers.IAttributeDataProvider;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author Ryan D. Brooks
 */
public class AttributeTypeManager {

   private static final String SELECT_ATTRIBUTE_TYPES =
         "SELECT * FROM osee_attribute_type aty1, osee_attribute_base_type aby1, osee_attribute_provider_type apy1 WHERE aty1.attr_base_type_id = aby1.attr_base_type_id AND aty1.attr_provider_type_id = apy1.attr_provider_type_id";
   private static final String INSERT_ATTRIBUTE_TYPE =
         "INSERT INTO osee_attribute_type (attr_type_id, attr_base_type_id, attr_provider_type_id, file_type_extension, namespace, name, default_value, enum_type_id, min_occurence, max_occurence, tip_text, tagger_id) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
   private static final String INSERT_BASE_ATTRIBUTE_TYPE =
         "INSERT INTO osee_attribute_base_type (attr_base_type_id, attribute_class) VALUES (?, ?)";
   private static final String INSERT_ATTRIBUTE_PROVIDER_TYPE =
         "INSERT INTO osee_attribute_provider_type (attr_provider_type_id, attribute_provider_class) VALUES (?, ?)";
   private static final String SELECT_ATTRIBUTE_BASE_TYPE =
         "SELECT attr_base_type_id FROM " + ATTRIBUTE_BASE_TYPE_TABLE + " WHERE attribute_class = ?";
   private static final String SELECT_ATTRIBUTE_PROVIDER_TYPE =
         "SELECT attr_provider_type_id FROM " + ATTRIBUTE_PROVIDER_TYPE_TABLE + " WHERE attribute_provider_class = ?";

   private final HashMap<String, AttributeType> nameToTypeMap;
   private final HashMap<Integer, AttributeType> idToTypeMap;
   private static final AttributeTypeManager instance = new AttributeTypeManager();

   private AttributeTypeManager() {
      this.nameToTypeMap = new HashMap<String, AttributeType>();
      this.idToTypeMap = new HashMap<Integer, AttributeType>();
   }

   private static synchronized void ensurePopulated() throws OseeDataStoreException {
      if (instance.idToTypeMap.size() == 0) {
         instance.populateCache();
      }
   }

   private void populateCache() throws OseeDataStoreException {
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();

      try {
         chStmt.runPreparedQuery(SELECT_ATTRIBUTE_TYPES);

         while (chStmt.next()) {
            String baseClassString = chStmt.getString("attribute_class");
            String baseProviderClassString = chStmt.getString("attribute_provider_class");
            try {
               Class<? extends Attribute<?>> baseAttributeClass =
                     AttributeExtensionManager.getAttributeClassFor(baseClassString);
               Class<? extends IAttributeDataProvider> providerAttributeClass =
                     AttributeExtensionManager.getAttributeProviderClassFor(baseProviderClassString);
               AttributeType type =
                     new AttributeType(chStmt.getInt("attr_type_id"), baseAttributeClass, providerAttributeClass,
                           chStmt.getString("file_type_extension"), chStmt.getString("namespace"),
                           chStmt.getString("name"), chStmt.getString("default_value"), chStmt.getInt("enum_type_id"),
                           chStmt.getInt("min_occurence"), chStmt.getInt("max_occurence"),
                           chStmt.getString("tip_text"), chStmt.getString("tagger_id"));
               cache(type);
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      } finally {
         chStmt.close();
      }
   }

   public static Collection<AttributeType> getAllTypes() throws OseeDataStoreException {
      ensurePopulated();
      return instance.idToTypeMap.values();
   }

   public static Collection<AttributeType> getTaggableTypes() throws OseeDataStoreException {
      ensurePopulated();
      Collection<AttributeType> taggableTypes = new ArrayList<AttributeType>();
      for (AttributeType type : instance.idToTypeMap.values()) {
         if (type.isTaggable()) {
            taggableTypes.add(type);
         }
      }
      return taggableTypes;
   }

   public static boolean typeExists(String namespace, String name) throws OseeDataStoreException {
      ensurePopulated();
      return instance.nameToTypeMap.get(namespace + name) != null;
   }

   /**
    * @param namespace
    * @param name
    * @return the attribute type with the given name and namespace or throws an OseeTypeDoesNotExist if it does not
    *         exist.
    * @throws OseeDataStoreException
    * @throws OseeTypeDoesNotExist
    */
   public static AttributeType getType(String namespace, String name) throws OseeDataStoreException, OseeTypeDoesNotExist {
      ensurePopulated();
      AttributeType attributeType = instance.nameToTypeMap.get(namespace + name);
      if (attributeType == null) {
         throw new OseeTypeDoesNotExist(
               "Attribute Type with namespace \"" + namespace + "\" and name \"" + name + "\" does not exist.");
      }
      return attributeType;
   }

   /**
    * Returns the attribute type with the given type id or throws an IllegalArgumentException if it does not exist.
    * 
    * @param attrTypeId
    * @throws OseeTypeDoesNotExist
    * @throws OseeDataStoreException
    */
   public static AttributeType getType(int attrTypeId) throws OseeTypeDoesNotExist, OseeDataStoreException {
      ensurePopulated();
      AttributeType attributeType = instance.idToTypeMap.get(attrTypeId);
      if (attributeType == null) {
         throw new OseeTypeDoesNotExist("Attribute type: " + attrTypeId + " is not available.");
      }

      return attributeType;
   }

   /**
    * @param attrTypeId
    * @return the attribute type with the given name or throws an IllegalArgumentException if it does not exist.
    * @throws OseeTypeDoesNotExist
    */
   public static AttributeType getType(String name) throws OseeDataStoreException, OseeTypeDoesNotExist {
      return getType("", name);
   }

   /**
    * Cache a newly created type.
    * 
    * @param attributeType
    */
   public void cache(AttributeType attributeType) {
      nameToTypeMap.put(attributeType.getNamespace() + attributeType.getName(), attributeType);
      idToTypeMap.put(attributeType.getAttrTypeId(), attributeType);
   }

   public static AttributeType createType(String attributeBaseType, String attributeProviderTypeName, String fileTypeExtension, String namespace, String name, String defaultValue, String validityXml, int minOccurrences, int maxOccurrences, String tipText, String taggerId) throws OseeCoreException {
      if (minOccurrences > 0 && defaultValue == null) throw new OseeArgumentException(
            "DefaultValue must be set for attribute namespace \"" + namespace + "\" and name \"" + name + "\" with minOccurrences " + minOccurrences);
      if (typeExists(namespace, name)) {
         return getType(namespace, name);
      }

      Class<? extends Attribute<?>> baseAttributeClass =
            AttributeExtensionManager.getAttributeClassFor(attributeBaseType);
      Class<? extends IAttributeDataProvider> providerAttributeClass =
            AttributeExtensionManager.getAttributeProviderClassFor(attributeProviderTypeName);

      int attrTypeId = SequenceManager.getNextAttributeTypeId();
      int attrBaseTypeId = instance.getOrCreateAttributeBaseType(attributeBaseType);
      int attrProviderTypeId = instance.getOrCreateAttributeProviderType(attributeProviderTypeName);

      int enumTypeId;
      if (EnumeratedAttribute.class.isAssignableFrom(baseAttributeClass)) {
         enumTypeId = OseeEnumTypeManager.createEnumTypeFromXml(namespace + name, validityXml).getEnumTypeId();
      } else {
         enumTypeId = OseeEnumTypeManager.getDefaultEnumTypeId();
      }

      ConnectionHandler.runPreparedUpdate(INSERT_ATTRIBUTE_TYPE, attrTypeId, attrBaseTypeId, attrProviderTypeId,
            fileTypeExtension == null ? SQL3DataType.VARCHAR : fileTypeExtension,
            namespace == null ? SQL3DataType.VARCHAR : namespace, name,
            defaultValue == null ? SQL3DataType.VARCHAR : defaultValue, enumTypeId, minOccurrences, maxOccurrences,
            tipText == null ? SQL3DataType.VARCHAR : tipText, taggerId == null ? SQL3DataType.VARCHAR : taggerId);
      AttributeType attributeType =
            new AttributeType(attrTypeId, baseAttributeClass, providerAttributeClass, fileTypeExtension, namespace,
                  name, defaultValue, enumTypeId, minOccurrences, maxOccurrences, tipText, taggerId);
      instance.cache(attributeType);
      return attributeType;
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

   public static Set<String> getEnumerationValues(AttributeType attributeType) {
      try {
         int oseeEnumTypeId = attributeType.getOseeEnumTypeId();
         OseeEnumType enumType = OseeEnumTypeManager.getType(oseeEnumTypeId);
         return enumType.valuesAsOrderedStringSet();
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         return Collections.emptySet();
      }
   }

   public static Set<String> getEnumerationValues(String attributeName) throws OseeDataStoreException, OseeTypeDoesNotExist {
      return getEnumerationValues(getType(attributeName));
   }

   private static final String DELETE_VALID_ATTRIBUTE = "delete from osee_valid_attributes where attr_type_id = ?";
   private static final String COUNT_ATTRIBUTE_OCCURRENCE =
         "select count(1) FROM osee_attribute where attr_type_id = ?";
   private static final String DELETE_ATTRIBUTE_TYPE = "delete from osee_attribute_type where attr_type_id = ?";

   public static void purgeAttributeType(AttributeType attributeType) throws OseeCoreException {
      int attributeTypeId = attributeType.getAttrTypeId();
      int attributeCount = ConnectionHandler.runPreparedQueryFetchInt(0, COUNT_ATTRIBUTE_OCCURRENCE, attributeTypeId);

      if (attributeCount != 0) {
         throw new OseeArgumentException(
               "Can not delete attribute type " + attributeType.getName() + " because there are " + attributeCount + " existing attributes of this type.");
      }

      ConnectionHandler.runPreparedUpdate(DELETE_VALID_ATTRIBUTE, attributeTypeId);
      ConnectionHandler.runPreparedUpdate(DELETE_ATTRIBUTE_TYPE, attributeTypeId);
   }
}