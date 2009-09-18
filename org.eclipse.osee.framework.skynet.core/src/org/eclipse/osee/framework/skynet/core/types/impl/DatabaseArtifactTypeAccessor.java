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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.exception.OseeInvalidInheritanceException;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.ConnectionHandlerStatement;
import org.eclipse.osee.framework.database.core.SequenceManager;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType.DirtyStateDetail;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.types.ArtifactTypeCache;
import org.eclipse.osee.framework.skynet.core.types.IOseeTypeDataAccessor;
import org.eclipse.osee.framework.skynet.core.types.IOseeTypeFactory;
import org.eclipse.osee.framework.skynet.core.types.OseeTypeCache;

/**
 * @author Roberto E. Escobar
 */
public class DatabaseArtifactTypeAccessor implements IOseeTypeDataAccessor<ArtifactType> {
   protected static final int ABSTRACT_TYPE_INDICATOR = 1;
   protected static final int CONCRETE_TYPE_INDICATOR = 0;

   private static final String SELECT_ARTIFACT_TYPES = "select * from osee_artifact_type";
   private static final String INSERT_ARTIFACT_TYPE =
         "insert into osee_artifact_type (art_type_id, art_type_guid, name, is_abstract) VALUES (?,?,?,?)";

   private static final String UPDATE_ARTIFACT_TYPE =
         "update osee_artifact_type SET name = ?, is_abstract = ? where art_type_id = ?";

   private static final String SELECT_ARTIFACT_TYPE_INHERITANCE =
         "select * from osee_artifact_type_inheritance order by super_art_type_id, art_type_id";
   private static final String INSERT_ARTIFACT_TYPE_INHERITANCE =
         "insert into osee_artifact_type_inheritance (art_type_id, super_art_type_id) VALUES (?,?)";
   private static final String DELETE_ARTIFACT_TYPE_INHERITANCE =
         "delete from osee_artifact_type_inheritance where art_type_id = ?";

   private static final String SELECT_ARTIFACT_TYPE_ATTRIBUTES = "SELECT * FROM osee_artifact_type_attributes";
   private static final String INSERT_ARTIFACT_TYPE_ATTRIBUTES =
         "INSERT INTO osee_artifact_type_attributes (art_type_id, attr_type_id, branch_id) VALUES (?, ?, ?)";
   private static final String DELETE_ARTIFACT_TYPE_ATTRIBUTES =
         "delete from osee_artifact_type_attributes where art_type_id = ?";

   @Override
   public void load(OseeTypeCache cache, IOseeTypeFactory factory) throws OseeCoreException {
      ArtifactTypeCache cacheData = cache.getArtifactTypeCache();
      loadArtifactTypes(cacheData, factory);
      loadTypeInheritance(cacheData);
      loadAllTypeValidity(cacheData, factory);
      for (ArtifactType type : cacheData.getAllTypes()) {
         type.clearDirty();
      }
   }

   private void loadArtifactTypes(ArtifactTypeCache cache, IOseeTypeFactory factory) throws OseeCoreException {
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      try {
         chStmt.runPreparedQuery(SELECT_ARTIFACT_TYPES);

         while (chStmt.next()) {
            try {
               boolean isAbstract = chStmt.getInt("is_abstract") == ABSTRACT_TYPE_INDICATOR;
               ArtifactType artifactType =
                     factory.createArtifactType(cache, chStmt.getString("art_type_guid"), isAbstract,
                           chStmt.getString("name"));
               artifactType.setId(chStmt.getInt("art_type_id"));
               artifactType.setModificationType(ModificationType.MODIFIED);
               cache.cacheType(artifactType);
            } catch (OseeDataStoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      } finally {
         chStmt.close();
      }
   }

   private void loadTypeInheritance(ArtifactTypeCache cache) throws OseeCoreException {
      ConnectionHandlerStatement chStmt2 = new ConnectionHandlerStatement();
      try {
         chStmt2.runPreparedQuery(SELECT_ARTIFACT_TYPE_INHERITANCE);
         int previousBaseId = -1;
         Set<ArtifactType> superTypes = new HashSet<ArtifactType>();
         while (chStmt2.next()) {
            int artTypeId = chStmt2.getInt("art_type_id");
            int superArtTypeId = chStmt2.getInt("super_art_type_id");
            if (artTypeId == superArtTypeId) {
               throw new OseeInvalidInheritanceException(String.format(
                     "Circular inheritance detected artifact type [%s] inherits from [%s]", artTypeId, superArtTypeId));
            }
            superTypes.add(cache.getTypeById(superArtTypeId));

            if (previousBaseId != artTypeId) {
               ArtifactType artifactType = cache.getTypeById(artTypeId);
               if (artifactType == null) {
                  throw new OseeInvalidInheritanceException(String.format(
                        "ArtifactType [%s] inherit from [%s] is null", artTypeId, superArtTypeId));
               }
               cache.cacheArtifactTypeInheritance(artifactType, superTypes);
               superTypes.clear();
               previousBaseId = artTypeId;
            }
         }
      } finally {
         chStmt2.close();
      }
   }

   private void loadAllTypeValidity(ArtifactTypeCache cache, IOseeTypeFactory factory) throws OseeCoreException {
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      try {
         chStmt.runPreparedQuery(2000, SELECT_ARTIFACT_TYPE_ATTRIBUTES);
         while (chStmt.next()) {
            try {
               ArtifactType artifactType = ArtifactTypeManager.getType(chStmt.getInt("art_type_id"));
               AttributeType attributeType = AttributeTypeManager.getType(chStmt.getInt("attr_type_id"));
               Branch branch = BranchManager.getBranch(chStmt.getInt("branch_id"));
               cache.cacheTypeValidity(artifactType, attributeType, branch);
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      } finally {
         chStmt.close();
      }
   }

   @Override
   public void store(OseeTypeCache cache, Collection<ArtifactType> types) throws OseeCoreException {
      Set<ArtifactType> typeInheritanceChanges = new HashSet<ArtifactType>();
      Set<ArtifactType> typeValidityChanges = new HashSet<ArtifactType>();
      List<Object[]> insertData = new ArrayList<Object[]>();
      List<Object[]> updateData = new ArrayList<Object[]>();

      for (ArtifactType type : types) {
         DirtyStateDetail dirtyDetails = type.getDirtyDetails();
         if (dirtyDetails.isNameDirty() || dirtyDetails.isAbstractDirty()) {
            int abstractValue = type.isAbstract() ? ABSTRACT_TYPE_INDICATOR : CONCRETE_TYPE_INDICATOR;
            switch (type.getModificationType()) {
               case NEW:
                  type.setId(SequenceManager.getNextArtifactTypeId());
                  insertData.add(new Object[] {type.getId(), type.getGuid(), type.getName(), abstractValue});
                  break;
               case MODIFIED:
                  updateData.add(new Object[] {type.getName(), abstractValue, type.getId()});
                  break;
               default:
                  break;
            }
         }
         if (dirtyDetails.isInheritanceDirty()) {
            typeInheritanceChanges.add(type);

         }
         if (dirtyDetails.isAttributeTypeValidityDirty()) {
            typeValidityChanges.add(type);
         }
      }
      ConnectionHandler.runBatchUpdate(INSERT_ARTIFACT_TYPE, insertData);
      ConnectionHandler.runBatchUpdate(UPDATE_ARTIFACT_TYPE, updateData);

      storeArtifactTypeInheritance(typeInheritanceChanges);
      storeAttributeTypeValidity(cache, types);

      for (ArtifactType type : types) {
         type.clearDirty();
      }
   }

   private void storeArtifactTypeInheritance(Collection<ArtifactType> types) throws OseeDataStoreException {
      List<Object[]> insertInheritanceData = new ArrayList<Object[]>();
      List<Object[]> deleteInheritanceData = new ArrayList<Object[]>();
      for (ArtifactType type : types) {
         deleteInheritanceData.add(new Object[] {type.getId()});
         for (ArtifactType superType : type.getSuperArtifactTypes()) {
            insertInheritanceData.add(new Object[] {type.getId(), superType.getId()});
         }
      }
      ConnectionHandler.runBatchUpdate(DELETE_ARTIFACT_TYPE_INHERITANCE, deleteInheritanceData);
      ConnectionHandler.runBatchUpdate(INSERT_ARTIFACT_TYPE_INHERITANCE, insertInheritanceData);
   }

   private void storeAttributeTypeValidity(OseeTypeCache cache, Collection<ArtifactType> types) throws OseeDataStoreException {
      List<Object[]> insertData = new ArrayList<Object[]>();
      List<Object[]> deleteData = new ArrayList<Object[]>();
      for (ArtifactType artifactType : types) {
         deleteData.add(new Object[] {artifactType.getId()});
         Map<Branch, Collection<AttributeType>> entries =
               cache.getArtifactTypeCache().getLocalAttributeTypes(artifactType);
         if (entries != null) {
            for (Entry<Branch, Collection<AttributeType>> entry : entries.entrySet()) {
               Branch branch = entry.getKey();
               for (AttributeType attributeType : entry.getValue()) {
                  insertData.add(new Object[] {artifactType.getId(), attributeType.getId(),
                        branch.getBranchId()});
               }
            }
         }
      }
      ConnectionHandler.runBatchUpdate(DELETE_ARTIFACT_TYPE_ATTRIBUTES, deleteData);
      ConnectionHandler.runBatchUpdate(INSERT_ARTIFACT_TYPE_ATTRIBUTES, insertData);
   }
}
