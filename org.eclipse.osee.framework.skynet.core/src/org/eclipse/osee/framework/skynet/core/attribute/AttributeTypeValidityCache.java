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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.TreeSet;
import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.ui.plugin.util.db.ConnectionHandler;
import org.eclipse.osee.framework.ui.plugin.util.db.ConnectionHandlerStatement;
import org.eclipse.osee.framework.ui.plugin.util.db.DbUtil;

/**
 * Caches the mapping of valid attribute types to artifact types for which they are valid
 * 
 * @see org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor
 * @author Ryan D. Brooks
 */
public class AttributeTypeValidityCache {
   private static final String attributeValiditySql =
         "SELECT art_type_id, attr_type_id FROM osee_define_valid_attributes";
   private final HashCollection<ArtifactSubtypeDescriptor, DynamicAttributeDescriptor> artifactToAttributeMap;
   private final HashCollection<DynamicAttributeDescriptor, ArtifactSubtypeDescriptor> attributeToartifactMap;

   public AttributeTypeValidityCache() {
      artifactToAttributeMap =
            new HashCollection<ArtifactSubtypeDescriptor, DynamicAttributeDescriptor>(false, TreeSet.class);
      attributeToartifactMap =
            new HashCollection<DynamicAttributeDescriptor, ArtifactSubtypeDescriptor>(false, TreeSet.class);
   }

   public Collection<DynamicAttributeDescriptor> getAttributeTypesFromArtifactType(ArtifactSubtypeDescriptor artifactType, Branch branch) throws SQLException {
      ensurePopulated();

      Collection<DynamicAttributeDescriptor> attributeTypes = artifactToAttributeMap.getValues(artifactType);
      if (attributeTypes == null) {
         throw new IllegalArgumentException(
               "There are no valid attribute types available for the artifact type \"" + artifactType + "\"");
      }
      //Partition attribute type id = 107, CSCI attribute type id = 695,
      int rootBranchId = branch.getRootBranch().getBranchId();
      if (rootBranchId == 2) {
         removeAttributeType(attributeTypes, 695);
      } else if (rootBranchId == 221) {
         removeAttributeType(attributeTypes, 107);
      }

      return attributeTypes;
   }

   private void removeAttributeType(Collection<DynamicAttributeDescriptor> attributeTypes, int attributeTypeId) {
      for (DynamicAttributeDescriptor attributeType : attributeTypes) {
         if (attributeType.getAttrTypeId() == attributeTypeId) {
            attributeTypes.remove(attributeType);
            return;
         }
      }
   }

   private synchronized void ensurePopulated() throws SQLException {
      if (artifactToAttributeMap.size() == 0) {
         populateCache();
      }
   }

   private void populateCache() throws SQLException {
      ConnectionHandlerStatement chStmt = null;
      try {
         ConfigurationPersistenceManager configurationManager = ConfigurationPersistenceManager.getInstance();

         chStmt = ConnectionHandler.runPreparedQuery(attributeValiditySql);
         ResultSet rSet = chStmt.getRset();

         while (rSet.next()) {
            try {
               ArtifactSubtypeDescriptor artifactType =
                     configurationManager.getArtifactSubtypeDescriptor(rSet.getInt("art_type_id"));
               DynamicAttributeDescriptor attributeType =
                     configurationManager.getDynamicAttributeType(rSet.getInt("attr_type_id"));

               artifactToAttributeMap.put(artifactType, attributeType);
               attributeToartifactMap.put(attributeType, artifactType);
            } catch (IllegalArgumentException ex) {
               SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
            }
         }
      } finally {
         DbUtil.close(chStmt);
      }
   }

   public Collection<ArtifactSubtypeDescriptor> getArtifactTypesFromAttributeType(DynamicAttributeDescriptor requestedAttributeType) throws SQLException {
      ensurePopulated();

      Collection<ArtifactSubtypeDescriptor> artifactTypes = attributeToartifactMap.getValues(requestedAttributeType);
      if (artifactTypes == null) {
         throw new IllegalArgumentException(
               "There are no valid artifact types available for the attribute type " + requestedAttributeType);
      }

      return artifactTypes;
   }

   public boolean isValid(ArtifactSubtypeDescriptor artifactType, DynamicAttributeDescriptor attributeType) throws SQLException {
      ensurePopulated();
      Collection<DynamicAttributeDescriptor> attributeTypes = artifactToAttributeMap.getValues(artifactType);
      if (attributeTypes != null) {
         for (DynamicAttributeDescriptor otherAttributeType : attributeTypes) {
            if (attributeType.equals(otherAttributeType)) {
               return true;
            }
         }
      }
      return false;
   }

   /**
    * @param attributeType
    * @param artifactType
    * @throws SQLException
    */
   public void add(ArtifactSubtypeDescriptor artifactType, DynamicAttributeDescriptor attributeType) throws SQLException {
      ensurePopulated();
      artifactToAttributeMap.put(artifactType, attributeType);
      attributeToartifactMap.put(attributeType, artifactType);
   }
}