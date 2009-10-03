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
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.types.AbstractOseeCache;
import org.eclipse.osee.framework.skynet.core.types.AbstractOseeType;
import org.eclipse.osee.framework.skynet.core.types.ArtifactTypeCache;
import org.eclipse.osee.framework.skynet.core.types.AttributeTypeCache;
import org.eclipse.osee.framework.skynet.core.types.IOseeDataAccessor;
import org.eclipse.osee.framework.skynet.core.types.IOseeTypeFactory;

/**
 * @author Roberto E. Escobar
 */
public class DatabaseArtifactTypeAccessor implements IOseeDataAccessor<ArtifactType> {
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

   private final AttributeTypeCache attributeCache;

   public DatabaseArtifactTypeAccessor(AttributeTypeCache attributeCache) {
      this.attributeCache = attributeCache;
   }

   private ArtifactTypeCache getCastedObject(AbstractOseeCache<ArtifactType> cache) {
      return (ArtifactTypeCache) cache;
   }

   @Override
   public void load(AbstractOseeCache<ArtifactType> cache, IOseeTypeFactory factory) throws OseeCoreException {
      attributeCache.ensurePopulated();
      loadArtifactTypes(cache, factory);
      loadTypeInheritance(getCastedObject(cache));
      loadAllTypeValidity(getCastedObject(cache), factory);
      for (ArtifactType type : cache.getAll()) {
         type.clearDirty();
      }
   }

   private void loadArtifactTypes(AbstractOseeCache<ArtifactType> cache, IOseeTypeFactory factory) throws OseeCoreException {
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      try {
         chStmt.runPreparedQuery(SELECT_ARTIFACT_TYPES);

         while (chStmt.next()) {
            try {
               int artTypeId = chStmt.getInt("art_type_id");
               boolean isAbstract = chStmt.getInt("is_abstract") == ABSTRACT_TYPE_INDICATOR;
               String artifactTypeName = chStmt.getString("name");

               ArtifactType artifactType = cache.getById(artTypeId);
               if (artifactType == null) {
                  artifactType =
                        factory.createArtifactType(cache, chStmt.getString("art_type_guid"), isAbstract,
                              artifactTypeName);
                  artifactType.setId(artTypeId);
                  artifactType.setModificationType(ModificationType.MODIFIED);
                  cache.cache(artifactType);
               } else {
                  artifactType.setName(artifactTypeName);
                  artifactType.setAbstract(isAbstract);
               }
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
         HashCollection<ArtifactType, ArtifactType> baseToSuperTypes =
               new HashCollection<ArtifactType, ArtifactType>(false, HashSet.class);

         while (chStmt2.next()) {
            int artTypeId = chStmt2.getInt("art_type_id");
            int superArtTypeId = chStmt2.getInt("super_art_type_id");
            ArtifactType baseArtType = cache.getById(artTypeId);
            ArtifactType superArtType = cache.getById(superArtTypeId);

            if (baseArtType == null) {
               throw new OseeInvalidInheritanceException(String.format(
                     "ArtifactType [%s] which inherits from [%s] is null", artTypeId, superArtType));
            }
            if (superArtType == null) {
               throw new OseeInvalidInheritanceException(String.format(
                     "ArtifactType [%s] which inherits from null artifact [%s]", artTypeId, superArtType));
            }
            baseToSuperTypes.put(baseArtType, superArtType);
         }
         for (ArtifactType artifactType : baseToSuperTypes.keySet()) {
            Collection<ArtifactType> superTypes = baseToSuperTypes.getValues(artifactType);
            if (superTypes != null) {
               cache.cacheArtifactSuperType(artifactType, superTypes);
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
               ArtifactType artifactType = cache.getById(chStmt.getInt("art_type_id"));
               AttributeType attributeType = attributeCache.getById(chStmt.getInt("attr_type_id"));

               // TODO remove dependency on Managers
               Branch branch = BranchManager.getBranch(chStmt.getInt("branch_id")); // Use Branch Cache
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
   public void store(AbstractOseeCache<ArtifactType> cache, Collection<ArtifactType> types) throws OseeCoreException {
      Set<ArtifactType> typeInheritanceChanges = new HashSet<ArtifactType>();
      Set<ArtifactType> typeValidityChanges = new HashSet<ArtifactType>();
      List<Object[]> insertData = new ArrayList<Object[]>();
      List<Object[]> updateData = new ArrayList<Object[]>();

      for (ArtifactType type : types) {
         if (isDataDirty(type)) {
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
         if (type.isFieldDirty(ArtifactType.ARTIFACT_INHERITANCE_FIELD_KEY)) {
            typeInheritanceChanges.add(type);

         }
         if (type.isFieldDirty(ArtifactType.ARTIFACT_TYPE_ATTRIBUTES_FIELD_KEY)) {
            typeValidityChanges.add(type);
         }
      }
      ConnectionHandler.runBatchUpdate(INSERT_ARTIFACT_TYPE, insertData);
      ConnectionHandler.runBatchUpdate(UPDATE_ARTIFACT_TYPE, updateData);

      storeArtifactTypeInheritance(typeInheritanceChanges);
      storeAttributeTypeValidity(getCastedObject(cache), types);

      for (ArtifactType type : types) {
         type.clearDirty();
      }
   }

   private boolean isDataDirty(ArtifactType type) throws OseeCoreException {
      return type.areFieldsDirty(AbstractOseeType.NAME_FIELD_KEY, AbstractOseeType.UNIQUE_ID_FIELD_KEY,
            ArtifactType.ARTIFACT_IS_ABSTRACT_FIELD_KEY);
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

   private void storeAttributeTypeValidity(ArtifactTypeCache cache, Collection<ArtifactType> types) throws OseeCoreException {
      List<Object[]> insertData = new ArrayList<Object[]>();
      List<Object[]> deleteData = new ArrayList<Object[]>();
      for (ArtifactType artifactType : types) {
         deleteData.add(new Object[] {artifactType.getId()});
         Map<Branch, Collection<AttributeType>> entries = cache.getLocalAttributeTypes(artifactType);
         if (entries != null) {
            for (Entry<Branch, Collection<AttributeType>> entry : entries.entrySet()) {
               Branch branch = entry.getKey();
               for (AttributeType attributeType : entry.getValue()) {
                  insertData.add(new Object[] {artifactType.getId(), attributeType.getId(), branch.getId()});
               }
            }
         }
      }
      ConnectionHandler.runBatchUpdate(DELETE_ARTIFACT_TYPE_ATTRIBUTES, deleteData);
      ConnectionHandler.runBatchUpdate(INSERT_ARTIFACT_TYPE_ATTRIBUTES, insertData);
   }
}
