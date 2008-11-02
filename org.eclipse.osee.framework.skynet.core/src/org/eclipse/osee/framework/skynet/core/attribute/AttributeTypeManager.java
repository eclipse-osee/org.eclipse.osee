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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.core.SequenceManager;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.attribute.providers.IAttributeDataProvider;

/**
 * @author Ryan D. Brooks
 */
public class AttributeTypeManager {

   private static final String SELECT_ATTRIBUTE_TYPES =
         "SELECT * FROM osee_attribute_type aty1, osee_attribute_base_type aby1, osee_attribute_provider_type apy1 WHERE aty1.attr_base_type_id = aby1.attr_base_type_id AND aty1.attr_provider_type_id = apy1.attr_provider_type_id";
   private static final String INSERT_ATTRIBUTE_TYPE =
         "INSERT INTO osee_attribute_type (attr_type_id, attr_base_type_id, attr_provider_type_id, file_type_extension, namespace, name, default_value, validity_xml, min_occurence, max_occurence, tip_text, tagger_id) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
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
                           chStmt.getString("name"), chStmt.getString("default_value"),
                           chStmt.getString("validity_xml"), chStmt.getInt("min_occurence"),
                           chStmt.getInt("max_occurence"), chStmt.getString("tip_text"), chStmt.getString("tagger_id"));
               cache(type);
            } catch (ClassNotFoundException ex) {
               OseeLog.log(SkynetActivator.class, Level.SEVERE, ex);
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

   public static boolean typeExists(String namespace, String name) throws Exception {
      ensurePopulated();
      return instance.nameToTypeMap.get(namespace + name) != null;
   }

   /**
    * @param namespace
    * @param name
    * @return the attribute type with the given name and namespace or throws an IllegalArgumentException if it does not
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

   public static AttributeType createType(String attributeBaseType, String attributeProviderTypeName, String fileTypeExtension, String namespace, String name, String defaultValue, String validityXml, int minOccurrences, int maxOccurrences, String tipText, String taggerId) throws Exception {
      if (minOccurrences > 0 && defaultValue == null) throw new IllegalArgumentException(
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
      ConnectionHandler.runPreparedUpdate(INSERT_ATTRIBUTE_TYPE, attrTypeId, attrBaseTypeId, attrProviderTypeId,
            fileTypeExtension == null ? SQL3DataType.VARCHAR : fileTypeExtension,
            namespace == null ? SQL3DataType.VARCHAR : namespace, name,
            defaultValue == null ? SQL3DataType.VARCHAR : defaultValue,
            validityXml == null ? SQL3DataType.VARCHAR : validityXml, minOccurrences, maxOccurrences,
            tipText == null ? SQL3DataType.VARCHAR : tipText, taggerId == null ? SQL3DataType.VARCHAR : taggerId);
      AttributeType descriptor =
            new AttributeType(attrTypeId, baseAttributeClass, providerAttributeClass, fileTypeExtension, namespace,
                  name, defaultValue, validityXml, minOccurrences, maxOccurrences, tipText, taggerId);
      instance.cache(descriptor);
      return descriptor;
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

   public static Set<String> getValidEnumerationAttributeValues(String attributeName, Branch branch) {
      Set<String> names = new HashSet<String>();
      try {
         AttributeType dad = getType(attributeName);
         String str = dad.getValidityXml();
         Matcher m = Pattern.compile("<Enum>(.*?)</Enum>").matcher(str);
         while (m.find())
            names.add(m.group(1));
      } catch (Exception ex) {
         OseeLog.log(SkynetActivator.class, Level.SEVERE, "Error getting valid enumeration values", ex);
      }
      return names;
   }
}