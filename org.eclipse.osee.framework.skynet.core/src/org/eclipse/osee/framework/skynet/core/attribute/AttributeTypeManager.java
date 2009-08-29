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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.ConnectionHandlerStatement;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.OseeTypeManager;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author Ryan D. Brooks
 */
public class AttributeTypeManager {

   private static final String SELECT_ATTRIBUTE_MOD_TYPE =
         "SELECT mod_type FROM osee_txs txs, osee_tx_details txd WHERE txs.gamma_id = ? and txs.transaction_id = txd.transaction_id AND branch_id = ? AND tx_current IN (1,2)";

   private static final AttributeTypeManager instance = new AttributeTypeManager();

   private AttributeTypeManager() {
   }

   private static synchronized void ensurePopulated() throws OseeCoreException {
      OseeTypeManager.getCache().ensureAttributeTypePopulated();
   }

   /**
    * Temporary method to access modification type from database to support remote event service for pre 0.8.2
    * development where modification type was allowed to be null before and not now. This allows production code to
    * propagate null mod types and clients and RCs to self-heal.
    * 
    * @param attribute
    * @throws OseeCoreException
    * @deprecated After 0.8.2 release, modType should never be null and this method should be removed
    */
   @Deprecated
   public static ModificationType internalGetModificationTypeFromDb(Attribute<?> attribute) throws OseeCoreException {
      int branchId = attribute.getArtifact().getBranch().getBranchId();
      int gammaId = attribute.getGammaId();
      return internalGetModificationTypeFromDb(branchId, gammaId);
   }

   /**
    * Temporary method to access modification type from database to support remote event service for pre 0.8.2
    * development where modification type was allowed to be null before and not now. This allows production code to
    * propagate null mod types and clients and RCs to self-heal.
    * 
    * @param attribute
    * @throws OseeCoreException
    * @deprecated After 0.8.2 release, modType should never be null and this method should be removed
    */
   @Deprecated
   public static ModificationType internalGetModificationTypeFromDb(int branchId, int gammaId) throws OseeCoreException {
      OseeLog.log(Activator.class, Level.INFO, "Internal db loading of attribute mod type");
      ModificationType modType = null;
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      try {
         chStmt.runPreparedQuery(SELECT_ATTRIBUTE_MOD_TYPE, gammaId, branchId);
         if (chStmt.next()) {
            modType = ModificationType.getMod(chStmt.getInt("mod_type"));
         }
      } finally {
         chStmt.close();
      }
      return modType;
   }

   public static Collection<AttributeType> getValidAttributeTypes(Branch branch) throws OseeCoreException {
      // TODO Filter by Branch
      return getAllTypes();
   }

   public static Collection<AttributeType> getAllTypes() throws OseeCoreException {
      ensurePopulated();
      return OseeTypeManager.getCache().getAllAttributeTypes();
   }

   public static Collection<AttributeType> getTaggableTypes() throws OseeCoreException {
      Collection<AttributeType> taggableTypes = new ArrayList<AttributeType>();
      for (AttributeType type : getAllTypes()) {
         if (type.isTaggable()) {
            taggableTypes.add(type);
         }
      }
      return taggableTypes;
   }

   public static boolean typeExists(String name) throws OseeCoreException {
      ensurePopulated();
      return OseeTypeManager.getCache().getAttributeTypeByName(name) != null;
   }

   /**
    * @param name
    * @return the attribute type with the given name or throws an OseeTypeDoesNotExist if it does not
    *         exist.
    * @throws OseeCoreException
    */
   public static AttributeType getType(String name) throws OseeCoreException {
      ensurePopulated();
      AttributeType attributeType = OseeTypeManager.getCache().getAttributeTypeByName(name);
      if (attributeType == null) {
         throw new OseeTypeDoesNotExist("Attribute Type with name [" + name + "] does not exist.");
      }
      return attributeType;
   }

   /**
    * Returns the attribute type with the given type id or throws an IllegalArgumentException if it does not exist.
    * 
    * @param attrTypeId
    * @throws OseeCoreException
    */
   public static AttributeType getType(int attrTypeId) throws OseeCoreException {
      ensurePopulated();
      AttributeType attributeType = OseeTypeManager.getCache().getAttributeTypeById(attrTypeId);
      if (attributeType == null) {
         throw new OseeTypeDoesNotExist("Attribute type: " + attrTypeId + " is not available.");
      }

      return attributeType;
   }

   public static AttributeType createType(String attributeBaseType, String attributeProviderTypeName, String fileTypeExtension, String attributeTypeName, String defaultValue, String validityXml, int minOccurrences, int maxOccurrences, String tipText, String taggerId) throws OseeCoreException {
      //      if (minOccurrences > 0 && defaultValue == null) {
      //         throw new OseeArgumentException(
      //               "DefaultValue must be set for attribute [" + attributeTypeName + "] with minOccurrences " + minOccurrences);
      //      }
      //      if (typeExists(attributeTypeName)) {
      //         return getType(attributeTypeName);
      //      }
      //
      //      Class<? extends Attribute<?>> baseAttributeClass =
      //            AttributeExtensionManager.getAttributeClassFor(attributeBaseType);
      //      Class<? extends IAttributeDataProvider> providerAttributeClass =
      //            AttributeExtensionManager.getAttributeProviderClassFor(attributeProviderTypeName);
      //
      //      int attrTypeId = SequenceManager.getNextAttributeTypeId();
      //      int attrBaseTypeId = instance.getOrCreateAttributeBaseType(attributeBaseType);
      //      int attrProviderTypeId = instance.getOrCreateAttributeProviderType(attributeProviderTypeName);
      //
      //      int enumTypeId;
      //      if (EnumeratedAttribute.class.isAssignableFrom(baseAttributeClass)) {
      //         enumTypeId = OseeEnumTypeManager.createEnumTypeFromXml(attributeTypeName, validityXml).getEnumTypeId();
      //      } else {
      //         enumTypeId = OseeEnumTypeManager.getDefaultEnumTypeId();
      //      }
      //
      //      ConnectionHandler.runPreparedUpdate(INSERT_ATTRIBUTE_TYPE, attrTypeId, attrBaseTypeId, attrProviderTypeId,
      //            fileTypeExtension == null ? SQL3DataType.VARCHAR : fileTypeExtension,
      //            attributeTypeName == null ? SQL3DataType.VARCHAR : attributeTypeName,
      //            defaultValue == null ? SQL3DataType.VARCHAR : defaultValue, enumTypeId, minOccurrences, maxOccurrences,
      //            tipText == null ? SQL3DataType.VARCHAR : tipText, taggerId == null ? SQL3DataType.VARCHAR : taggerId);
      //      AttributeType attributeType =
      //            new AttributeType(attrTypeId, baseAttributeClass, providerAttributeClass, fileTypeExtension,
      //                  attributeTypeName, defaultValue, enumTypeId, minOccurrences, maxOccurrences, tipText, taggerId);
      //      instance.cache(attributeType);
      return null;
   }

   public static Set<String> getEnumerationValues(AttributeType attributeType) {
      try {
         return attributeType.getOseeEnumType().valuesAsOrderedStringSet();
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         return Collections.emptySet();
      }
   }

   public static Set<String> getEnumerationValues(String attributeName) throws OseeCoreException {
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