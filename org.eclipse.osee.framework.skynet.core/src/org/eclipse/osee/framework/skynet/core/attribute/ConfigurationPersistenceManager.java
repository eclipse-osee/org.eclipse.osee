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

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeValidityCache;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;

/**
 * @author Jeff C. Phillips
 */
public class ConfigurationPersistenceManager {
   private static final String INSERT_VALID_ATTRIBUTE =
         "INSERT INTO osee_define_valid_attributes (art_type_id, attr_type_id) VALUES (?, ?)";

   private final AttributeTypeValidityCache cacheAttributeTypeValidity = new AttributeTypeValidityCache();
   private final ArtifactTypeValidityCache artifactTypeValidityCache = new ArtifactTypeValidityCache();

   private static final ConfigurationPersistenceManager instance = new ConfigurationPersistenceManager();

   private ConfigurationPersistenceManager() {
   }

   /**
    * Persists that a particular user defined attribute is valid for some artifact type.
    * 
    * @param attributeType
    * @param artifactType
    * @throws Exception
    */
   public static void persistAttributeValidity(ArtifactType artifactType, AttributeType attributeType) throws Exception {
      if (!instance.cacheAttributeTypeValidity.isValid(artifactType, attributeType)) {
         ConnectionHandler.runPreparedUpdate(INSERT_VALID_ATTRIBUTE, artifactType.getArtTypeId(),
               attributeType.getAttrTypeId());

         instance.cacheAttributeTypeValidity.add(artifactType, attributeType);
      }
   }

   public static Collection<ArtifactType> getArtifactTypesFromAttributeType(AttributeType attributeType) throws SQLException {
      return instance.cacheAttributeTypeValidity.getArtifactTypesFromAttributeType(attributeType);
   }

   public static Collection<AttributeType> getAttributeTypesFromArtifactType(String artifactTypeName, Branch branch) throws SQLException {
      return instance.cacheAttributeTypeValidity.getAttributeTypesFromArtifactType(
            ArtifactTypeManager.getType(artifactTypeName), branch);
   }

   public static Collection<AttributeType> getAttributeTypesFromArtifactType(ArtifactType descriptor, Branch branch) throws SQLException {
      return instance.cacheAttributeTypeValidity.getAttributeTypesFromArtifactType(descriptor, branch);
   }

   public static Collection<ArtifactType> getValidArtifactTypes(Branch branch) throws SQLException {
      try {
         return instance.artifactTypeValidityCache.getValidArtifactTypes(branch);
      } catch (OseeCoreException ex) {
         throw new SQLException(ex.getLocalizedMessage());
      }
   }

   public static Set<String> getValidEnumerationAttributeValues(String attributeName, Branch branch) {
      Set<String> names = new HashSet<String>();
      try {
         AttributeType dad = AttributeTypeManager.getType(attributeName);
         String str = dad.getValidityXml();
         Matcher m = Pattern.compile("<Enum>(.*?)</Enum>").matcher(str);
         while (m.find())
            names.add(m.group(1));
      } catch (Exception ex) {
         SkynetActivator.getLogger().log(Level.SEVERE, "Error getting valid enumeration values", ex);
      }
      return names;
   }
}