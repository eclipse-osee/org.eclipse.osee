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

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.TreeSet;
import java.util.logging.Level;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.db.connection.exception.OseeArgumentException;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;

/**
 * Caches the mapping of valid attribute types to artifact types for which they are valid
 * 
 * @see org.eclipse.osee.framework.skynet.core.artifact.ArtifactType
 * @author Ryan D. Brooks
 */
public class TypeValidityManager {
   private static final String SELECT_ATTRIBUTE_VALIDITY = "SELECT * FROM osee_valid_attributes";
   private static final String INSERT_VALID_ATTRIBUTE =
         "INSERT INTO osee_valid_attributes (art_type_id, attr_type_id, branch_id) VALUES (?, ?, ?)";
   private static final TypeValidityManager instance = new TypeValidityManager();

   private final CompositeKeyHashMap<Branch, ArtifactType, Collection<AttributeType>> artifactToAttributeMap =
         new CompositeKeyHashMap<Branch, ArtifactType, Collection<AttributeType>>();
   private final CompositeKeyHashMap<Branch, AttributeType, Collection<ArtifactType>> attributeToartifactMap =
         new CompositeKeyHashMap<Branch, AttributeType, Collection<ArtifactType>>();

   private final HashCollection<Branch, ArtifactType> branchToartifactTypeMap =
         new HashCollection<Branch, ArtifactType>(false, TreeSet.class);

   private TypeValidityManager() {
   }

   private static synchronized void ensurePopulated() throws OseeCoreException {
      if (instance.artifactToAttributeMap.isEmpty()) {
         instance.populateCache();
      }
   }

   private void populateCache() throws OseeCoreException {
      branchToartifactTypeMap.put(BranchManager.getSystemRootBranch(), ArtifactTypeManager.getAllTypes());

      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      try {
         chStmt.runPreparedQuery(2000, SELECT_ATTRIBUTE_VALIDITY);
         while (chStmt.next()) {
            try {
               ArtifactType artifactType = ArtifactTypeManager.getType(chStmt.getInt("art_type_id"));
               AttributeType attributeType = AttributeTypeManager.getType(chStmt.getInt("attr_type_id"));
               Branch branch = BranchManager.getBranch(chStmt.getInt("branch_id"));

               cacheAttributeValidity(artifactType, attributeType, branch);
            } catch (OseeCoreException ex) {
               OseeLog.log(SkynetActivator.class, Level.SEVERE, ex);
            }
         }
      } finally {
         chStmt.close();
      }
   }

   public static Collection<ArtifactType> getArtifactTypesFromAttributeType(String requestedAttributeType, Branch branch) throws OseeCoreException {
      return getArtifactTypesFromAttributeType(AttributeTypeManager.getType(requestedAttributeType), branch);
   }

   /**
    * Update type data to associate attributeType with artifactType on the given branch
    * 
    * @param artifactType
    * @param attributeType
    * @param branch
    * @throws OseeDataStoreException
    * @throws BranchDoesNotExist
    */
   public static void persistAttributeValidity(ArtifactType artifactType, AttributeType attributeType, Branch branch) throws OseeCoreException {
      if (!isValid(artifactType, attributeType, branch)) {
         ConnectionHandler.runPreparedUpdate(INSERT_VALID_ATTRIBUTE, artifactType.getArtTypeId(),
               attributeType.getAttrTypeId(), branch.getBranchId());

         instance.cacheAttributeValidity(artifactType, attributeType, branch);
      }
   }

   public static Collection<ArtifactType> getArtifactTypesFromAttributeType(AttributeType requestedAttributeType, Branch branch) throws OseeCoreException {
      ensurePopulated();

      Collection<ArtifactType> inheritedArtifactTypes = new HashSet<ArtifactType>();
      Branch branchCursor = branch;
      boolean notDone = true;
      while (notDone) {
         Collection<ArtifactType> artifactTypes =
               instance.attributeToartifactMap.get(branchCursor, requestedAttributeType);
         if (artifactTypes != null) {
            inheritedArtifactTypes.addAll(artifactTypes);
         }

         if (branchCursor.isSystemRootBranch()) {
            notDone = false;
         } else {
            branchCursor = branchCursor.getParentBranch();
         }
      }

      if (inheritedArtifactTypes.isEmpty()) {
         throw new OseeTypeDoesNotExist(
               "There are no valid artifact types available for the attribute type " + requestedAttributeType);
      }

      return inheritedArtifactTypes;
   }

   public static Collection<AttributeType> getAttributeTypesFromArtifactType(ArtifactType artifactType, Branch branch) throws OseeCoreException {
      ensurePopulated();

      Collection<AttributeType> inhieritedAttributeTypes = new HashSet<AttributeType>();
      Branch branchCursor = branch;
      boolean notDone = true;
      while (notDone) {
         Collection<AttributeType> attributeTypes = instance.artifactToAttributeMap.get(branchCursor, artifactType);
         if (attributeTypes != null) {
            inhieritedAttributeTypes.addAll(attributeTypes);
         }

         if (branchCursor.isSystemRootBranch()) {
            notDone = false;
         } else {
            branchCursor = branchCursor.getParentBranch();
         }
      }

      if (inhieritedAttributeTypes.isEmpty()) {
         throw new OseeTypeDoesNotExist(
               "There are no valid attribute types available for the artifact type \"" + artifactType + "\"");
      }
      return inhieritedAttributeTypes;
   }

   public static boolean isValid(ArtifactType artifactType, AttributeType attributeType, Branch branch) throws OseeCoreException {
      ensurePopulated();
      Collection<AttributeType> attributeTypes = instance.artifactToAttributeMap.get(branch, artifactType);
      if (attributeTypes != null) {
         for (AttributeType otherAttributeType : attributeTypes) {
            if (attributeType.equals(otherAttributeType)) {
               return true;
            }
         }
      }
      return false;
   }

   private void cacheAttributeValidity(ArtifactType artifactType, AttributeType attributeType, Branch branch) throws OseeDataStoreException {
      Collection<AttributeType> attributeTypes = artifactToAttributeMap.get(branch, artifactType);
      if (attributeTypes == null) {
         attributeTypes = new LinkedList<AttributeType>();
         artifactToAttributeMap.put(branch, artifactType, attributeTypes);
      }
      attributeTypes.add(attributeType);

      Collection<ArtifactType> artifactTypes = attributeToartifactMap.get(branch, attributeType);
      if (artifactTypes == null) {
         artifactTypes = new LinkedList<ArtifactType>();
         attributeToartifactMap.put(branch, attributeType, artifactTypes);
      }
      artifactTypes.add(artifactType);
   }

   public static Collection<AttributeType> getValidAttributeTypes(Branch branch) throws OseeCoreException {
      return AttributeTypeManager.getAllTypes();
   }

   public static Collection<ArtifactType> getValidArtifactTypes(Branch branch) throws OseeCoreException {
      if (false) { // TODO: Filter Types By Branch
         ensurePopulated();
         Branch topLevelBranch = branch.getTopLevelBranch();
         Collection<ArtifactType> artifactTypes = instance.branchToartifactTypeMap.getValues(topLevelBranch);
         if (artifactTypes == null) {
            throw new OseeArgumentException(
                  "There are no valid artifact types available for the branch " + topLevelBranch);
         }
         return artifactTypes;
      } else {
         return ArtifactTypeManager.getAllTypes();
      }
   }
}