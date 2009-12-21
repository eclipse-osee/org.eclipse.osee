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
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.core.cache.AbstractOseeCache;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.core.model.ArtifactType;
import org.eclipse.osee.framework.core.model.AttributeType;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.DbTransaction;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.providers.IAttributeDataProvider;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author Ryan D. Brooks
 */
public class AttributeTypeManager {

   private AttributeTypeManager() {
   }

   public static AbstractOseeCache<AttributeType> getCache() {
      return Activator.getInstance().getOseeCacheService().getAttributeTypeCache();
   }

   public static Collection<AttributeType> getValidAttributeTypes(Branch branch) throws OseeCoreException {
      Set<AttributeType> attributeTypes = new HashSet<AttributeType>(100);
      for (ArtifactType artifactType : ArtifactTypeManager.getAllTypes()) {
         attributeTypes.addAll(artifactType.getAttributeTypes(branch));
      }
      return attributeTypes;
   }

   public static Collection<AttributeType> getAllTypes() throws OseeCoreException {
      return getCache().getAll();
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
      return !getCache().getByName(name).isEmpty();
   }

   /**
    * @return Returns the attribute type matching the guid
    * @param guid attribute type guid to match
    * @throws OseeDataStoreException
    * @throws OseeTypeDoesNotExist
    */
   private static AttributeType getTypeByGuid(String guid) throws OseeCoreException {
      if (!GUID.isValid(guid)) {
         throw new OseeArgumentException(String.format("[%s] is not a valid guid", guid));
      }
      AttributeType attributeType = getCache().getByGuid(guid);
      if (attributeType == null) {
         throw new OseeTypeDoesNotExist("Attribute Type [" + guid + "] is not available.");
      }
      return attributeType;
   }

   public static AttributeType getType(IAttributeType type) throws OseeCoreException {
      return getTypeByGuid(type.getGuid());
   }

   /**
    * @param name
    * @return the attribute type with the given name or throws an OseeTypeDoesNotExist if it does not exist.
    * @throws OseeCoreException
    */
   public static AttributeType getType(String name) throws OseeCoreException {
      AttributeType attributeType = getCache().getUniqueByName(name);
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
      AttributeType attributeType = getCache().getById(attrTypeId);
      if (attributeType == null) {
         throw new OseeTypeDoesNotExist("Attribute type: " + attrTypeId + " is not available.");
      }

      return attributeType;
   }

   public static Set<String> getEnumerationValues(AttributeType attributeType) throws OseeCoreException {
      return attributeType.getOseeEnumType().valuesAsOrderedStringSet();
   }

   public static Set<String> getEnumerationValues(IAttributeType attributeType) throws OseeCoreException {
      return getEnumerationValues(getType(attributeType));
   }

   public static Set<String> getEnumerationValues(String attributeName) throws OseeCoreException {
      return getEnumerationValues(getType(attributeName));
   }

   private static final String DELETE_VALID_ATTRIBUTE =
         "delete from osee_artifact_type_attributes where attr_type_id = ?";
   private static final String COUNT_ATTRIBUTE_OCCURRENCE =
         "select count(1) FROM osee_attribute where attr_type_id = ?";
   private static final String DELETE_ATTRIBUTE_TYPE = "delete from osee_attribute_type where attr_type_id = ?";

   public static void purgeAttributeType(final AttributeType attributeType) throws OseeCoreException {
      int attributeCount =
            ConnectionHandler.runPreparedQueryFetchInt(0, COUNT_ATTRIBUTE_OCCURRENCE, attributeType.getId());
      if (attributeCount != 0) {
         throw new OseeArgumentException(
               "Can not delete attribute type " + attributeType.getName() + " because there are " + attributeCount + " existing attributes of this type.");
      }

      new DbTransaction() {
         @Override
         protected void handleTxWork(OseeConnection connection) throws OseeCoreException {
            int attributeTypeId = attributeType.getId();
            ConnectionHandler.runPreparedUpdate(connection, DELETE_VALID_ATTRIBUTE, attributeTypeId);
            ConnectionHandler.runPreparedUpdate(connection, DELETE_ATTRIBUTE_TYPE, attributeTypeId);
         }
      };
   }

   public static void persist() throws OseeCoreException {
      getCache().storeAllModified();
   }

   public static int getTypeId(IAttributeType typeToken) throws OseeCoreException {
      return getType(typeToken).getId();
   }

   @SuppressWarnings("unchecked")
   public static boolean isBaseTypeCompatible(Class<? extends Attribute> baseType, String attributeTypeName) throws OseeCoreException {
      return baseType.isAssignableFrom(getAttributeBaseClass(getType(attributeTypeName)));
   }

   public static boolean isBaseTypeCompatible(Class<? extends Attribute> baseType, IAttributeType attributeType) throws OseeCoreException {
      return baseType.isAssignableFrom(getAttributeBaseClass(getType(attributeType)));
   }

   public static Class<? extends Attribute<?>> getAttributeBaseClass(AttributeType attributeType) throws OseeCoreException {
      return AttributeExtensionManager.getAttributeClassFor(attributeType.getBaseAttributeTypeId());
   }

   public static Class<? extends IAttributeDataProvider> getAttributeProviderClass(AttributeType attributeType) throws OseeCoreException {
      return AttributeExtensionManager.getAttributeProviderClassFor(attributeType.getAttributeProviderId());
   }
}