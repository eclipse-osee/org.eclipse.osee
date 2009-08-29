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
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.ConnectionHandlerStatement;
import org.eclipse.osee.framework.database.core.DbTransaction;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.OseeTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.OseeTypeCache.OseeTypeCacheData;
import org.eclipse.osee.framework.skynet.core.attribute.providers.IAttributeDataProvider;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author Ryan D. Brooks
 */
public class AttributeTypeManager {

   private static final String SELECT_ATTRIBUTE_MOD_TYPE =
         "SELECT mod_type FROM osee_txs txs, osee_tx_details txd WHERE txs.gamma_id = ? and txs.transaction_id = txd.transaction_id AND branch_id = ? AND tx_current IN (1,2)";

   private AttributeTypeManager() {
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
      return OseeTypeManager.getCache().getAttributeTypeData().getAllTypes();
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
      return OseeTypeManager.getCache().getAttributeTypeData().getTypeByName(name) != null;
   }

   /**
    * @return Returns the attribute type matching the guid
    * @param guid attribute type guid to match
    * @throws OseeDataStoreException
    * @throws OseeTypeDoesNotExist
    */
   public static AttributeType getTypeByGuid(String guid) throws OseeCoreException {
      AttributeType attributeType = OseeTypeManager.getCache().getAttributeTypeData().getTypeByGuid(guid);
      if (attributeType == null) {
         throw new OseeTypeDoesNotExist("Attribute Type [" + guid + "] is not available.");
      }
      return attributeType;
   }

   /**
    * @param name
    * @return the attribute type with the given name or throws an OseeTypeDoesNotExist if it does not
    *         exist.
    * @throws OseeCoreException
    */
   public static AttributeType getType(String name) throws OseeCoreException {
      AttributeType attributeType = OseeTypeManager.getCache().getAttributeTypeData().getTypeByName(name);
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
      AttributeType attributeType = OseeTypeManager.getCache().getAttributeTypeData().getTypeById(attrTypeId);
      if (attributeType == null) {
         throw new OseeTypeDoesNotExist("Attribute type: " + attrTypeId + " is not available.");
      }

      return attributeType;
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

   public static void purgeAttributeType(final AttributeType attributeType) throws OseeCoreException {
      int attributeCount =
            ConnectionHandler.runPreparedQueryFetchInt(0, COUNT_ATTRIBUTE_OCCURRENCE, attributeType.getTypeId());
      if (attributeCount != 0) {
         throw new OseeArgumentException(
               "Can not delete attribute type " + attributeType.getName() + " because there are " + attributeCount + " existing attributes of this type.");
      }

      new DbTransaction() {
         @Override
         protected void handleTxWork(OseeConnection connection) throws OseeCoreException {
            int attributeTypeId = attributeType.getTypeId();
            ConnectionHandler.runPreparedUpdate(connection, DELETE_VALID_ATTRIBUTE, attributeTypeId);
            ConnectionHandler.runPreparedUpdate(connection, DELETE_ATTRIBUTE_TYPE, attributeTypeId);
         }
      };
   }

   public static AttributeType createType(String guid, String typeName, String baseAttributeTypeId, String attributeProviderNameId, String fileTypeExtension, String defaultValue, OseeEnumType oseeEnumType, int minOccurrences, int maxOccurrences, String description, String taggerId) throws OseeCoreException {
      OseeTypeCacheData<AttributeType> dataCache = OseeTypeManager.getCache().getAttributeTypeData();
      AttributeType attributeType = dataCache.getTypeByGuid(guid);
      if (attributeType == null) {
         Class<? extends Attribute<?>> baseAttributeClass =
               AttributeExtensionManager.getAttributeClassFor(baseAttributeTypeId);
         Class<? extends IAttributeDataProvider> providerAttributeClass =
               AttributeExtensionManager.getAttributeProviderClassFor(attributeProviderNameId);
         attributeType =
               OseeTypeManager.getTypeFactory().createAttributeType(guid, typeName, baseAttributeTypeId,
                     attributeProviderNameId, baseAttributeClass, providerAttributeClass, fileTypeExtension,
                     defaultValue, oseeEnumType, minOccurrences, maxOccurrences, description, taggerId);
      } else {
         // UPDATE VALUES HERE
      }
      dataCache.cacheType(attributeType);
      dataCache.storeAllModified();
      return attributeType;
   }
}