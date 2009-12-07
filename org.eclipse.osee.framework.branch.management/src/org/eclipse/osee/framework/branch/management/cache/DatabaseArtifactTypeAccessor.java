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
package org.eclipse.osee.framework.branch.management.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Level;
import org.eclipse.osee.framework.branch.management.internal.Activator;
import org.eclipse.osee.framework.core.cache.ArtifactTypeCache;
import org.eclipse.osee.framework.core.cache.AttributeTypeCache;
import org.eclipse.osee.framework.core.cache.BranchCache;
import org.eclipse.osee.framework.core.cache.IOseeCache;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.exception.OseeInvalidInheritanceException;
import org.eclipse.osee.framework.core.model.AbstractOseeType;
import org.eclipse.osee.framework.core.model.ArtifactType;
import org.eclipse.osee.framework.core.model.ArtifactTypeFactory;
import org.eclipse.osee.framework.core.model.AttributeType;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryServiceProvider;
import org.eclipse.osee.framework.database.IOseeDatabaseServiceProvider;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public class DatabaseArtifactTypeAccessor extends AbstractDatabaseAccessor<ArtifactType> {
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

   private static final String SELECT_ARTIFACT_TYPE_ATTRIBUTES =
         "select * from osee_artifact_type_attributes order by art_type_id, branch_id, attr_type_id";
   private static final String INSERT_ARTIFACT_TYPE_ATTRIBUTES =
         "insert into osee_artifact_type_attributes (art_type_id, attr_type_id, branch_id) VALUES (?, ?, ?)";
   private static final String DELETE_ARTIFACT_TYPE_ATTRIBUTES =
         "delete from osee_artifact_type_attributes where art_type_id = ?";

   private final AttributeTypeCache attributeCache;
   private final BranchCache branchCache;

   public DatabaseArtifactTypeAccessor(IOseeDatabaseServiceProvider databaseProvider, IOseeModelFactoryServiceProvider factoryProvider, BranchCache branchCache, AttributeTypeCache attributeCache) {
      super(databaseProvider, factoryProvider);
      this.attributeCache = attributeCache;
      this.branchCache = branchCache;
   }

   @Override
   public void load(IOseeCache<ArtifactType> cache) throws OseeCoreException {
      attributeCache.ensurePopulated();
      Set<ArtifactType> loadedTypes = new HashSet<ArtifactType>();
      ArtifactTypeCache artCache = (ArtifactTypeCache) cache;
      loadArtifactTypes(artCache, loadedTypes);
      loadTypeInheritance(artCache);
      loadAllTypeValidity(artCache);
      for (ArtifactType type : loadedTypes) {
         type.clearDirty();
      }
   }

   private void loadArtifactTypes(ArtifactTypeCache cache, Set<ArtifactType> loadedTypes) throws OseeCoreException {
      ArtifactTypeFactory factory = getFactoryService().getArtifactTypeFactory();

      IOseeStatement chStmt = getDatabaseService().getStatement();
      try {
         chStmt.runPreparedQuery(SELECT_ARTIFACT_TYPES);

         while (chStmt.next()) {
            try {
               int artTypeId = chStmt.getInt("art_type_id");
               boolean isAbstract = chStmt.getInt("is_abstract") == ABSTRACT_TYPE_INDICATOR;
               String artifactTypeName = chStmt.getString("name");
               String guid = chStmt.getString("art_type_guid");

               ArtifactType artifactType =
                     factory.createOrUpdate(cache, artTypeId, ModificationType.MODIFIED, guid, isAbstract,
                           artifactTypeName);
               loadedTypes.add(artifactType);
            } catch (OseeDataStoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      } finally {
         chStmt.close();
      }
   }

   private void loadTypeInheritance(ArtifactTypeCache cache) throws OseeCoreException {
      IOseeStatement chStmt2 = getDatabaseService().getStatement();
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
            Set<ArtifactType> superTypes = (Set<ArtifactType>) baseToSuperTypes.getValues(artifactType);
            if (superTypes != null) {
               artifactType.setSuperType(superTypes);
            }
         }
      } finally {
         chStmt2.close();
      }
   }

   private void loadAllTypeValidity(ArtifactTypeCache cache) throws OseeCoreException {
      CompositeKeyHashMap<ArtifactType, Branch, Collection<AttributeType>> typeValidity =
            new CompositeKeyHashMap<ArtifactType, Branch, Collection<AttributeType>>();
      IOseeStatement chStmt = getDatabaseService().getStatement();
      try {
         chStmt.runPreparedQuery(2000, SELECT_ARTIFACT_TYPE_ATTRIBUTES);
         while (chStmt.next()) {
            try {
               ArtifactType artifactType = cache.getById(chStmt.getInt("art_type_id"));
               Branch branch = branchCache.getById(chStmt.getInt("branch_id"));
               AttributeType attributeType = attributeCache.getById(chStmt.getInt("attr_type_id"));
               Collection<AttributeType> attributes = typeValidity.get(artifactType, branch);
               if (attributes == null) {
                  attributes = new HashSet<AttributeType>();
                  typeValidity.put(artifactType, branch, attributes);
               }
               attributes.add(attributeType);
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      } finally {
         chStmt.close();
      }
      for (Entry<Pair<ArtifactType, Branch>, Collection<AttributeType>> entry : typeValidity.entrySet()) {
         try {
            Pair<ArtifactType, Branch> key = entry.getKey();
            key.getFirst().setAttributeTypes(entry.getValue(), key.getSecond());
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
   }

   @Override
   public void store(Collection<ArtifactType> types) throws OseeCoreException {
      Set<ArtifactType> typeInheritanceChanges = new HashSet<ArtifactType>();
      Set<ArtifactType> typeValidityChanges = new HashSet<ArtifactType>();
      List<Object[]> insertData = new ArrayList<Object[]>();
      List<Object[]> updateData = new ArrayList<Object[]>();

      for (ArtifactType type : types) {
         if (isDataDirty(type)) {
            int abstractValue = type.isAbstract() ? ABSTRACT_TYPE_INDICATOR : CONCRETE_TYPE_INDICATOR;
            switch (type.getModificationType()) {
               case NEW:
                  type.setId(getSequence().getNextArtifactTypeId());
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
      getDatabaseService().runBatchUpdate(INSERT_ARTIFACT_TYPE, insertData);
      getDatabaseService().runBatchUpdate(UPDATE_ARTIFACT_TYPE, updateData);

      storeArtifactTypeInheritance(typeInheritanceChanges);
      storeAttributeTypeValidity(types);

      for (ArtifactType type : types) {
         if (type.getModificationType() == ModificationType.NEW) {
            type.setModificationType(ModificationType.MODIFIED);
         }
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
      getDatabaseService().runBatchUpdate(DELETE_ARTIFACT_TYPE_INHERITANCE, deleteInheritanceData);
      getDatabaseService().runBatchUpdate(INSERT_ARTIFACT_TYPE_INHERITANCE, insertInheritanceData);
   }

   private void storeAttributeTypeValidity(Collection<ArtifactType> types) throws OseeCoreException {
      List<Object[]> insertData = new ArrayList<Object[]>();
      List<Object[]> deleteData = new ArrayList<Object[]>();
      for (ArtifactType artifactType : types) {
         deleteData.add(new Object[] {artifactType.getId()});
         Map<Branch, Collection<AttributeType>> entries = artifactType.getLocalAttributeTypes();
         if (entries != null) {
            for (Entry<Branch, Collection<AttributeType>> entry : entries.entrySet()) {
               Branch branch = entry.getKey();
               for (AttributeType attributeType : entry.getValue()) {
                  insertData.add(new Object[] {artifactType.getId(), attributeType.getId(), branch.getId()});
               }
            }
         }
      }
      getDatabaseService().runBatchUpdate(DELETE_ARTIFACT_TYPE_ATTRIBUTES, deleteData);
      getDatabaseService().runBatchUpdate(INSERT_ARTIFACT_TYPE_ATTRIBUTES, insertData);
   }

}
