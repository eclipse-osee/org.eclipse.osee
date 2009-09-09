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
package org.eclipse.osee.framework.skynet.core.types;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.exception.OseeInvalidInheritanceException;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.ConnectionHandlerStatement;
import org.eclipse.osee.framework.database.core.SQL3DataType;
import org.eclipse.osee.framework.database.core.SequenceManager;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeExtensionManager;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.OseeEnumEntry;
import org.eclipse.osee.framework.skynet.core.attribute.OseeEnumType;
import org.eclipse.osee.framework.skynet.core.attribute.providers.IAttributeDataProvider;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;

/**
 * @author Roberto E. Escobar
 */
final class OseeTypeDatabaseAccessor implements IOseeTypeDataAccessor {
   private static final int ABSTRACT_TYPE_INDICATOR = 1;
   private static final int CONCRETE_TYPE_INDICATOR = 0;
   private static final String USER_ORDERED = "Yes";
   private static final String NOT_USER_ORDERED = "No";

   private static final String SELECT_ARTIFACT_TYPES = "select * from osee_artifact_type";
   private static final String INSERT_ARTIFACT_TYPE =
         "insert into osee_artifact_type (art_type_id, art_type_guid, name, is_abstract) VALUES (?,?,?,?)";

   private static final String UPDATE_ARTIFACT_TYPE =
         "update osee_artifact_type SET name = ?, is_abstract = ? where art_type_id = ?";

   private static final String SELECT_ARTIFACT_TYPE_INHERITANCE =
         "select * from osee_artifact_type_inheritance order by super_art_type_id, art_type_id";
   private static final String INSERT_ARTIFACT_TYPE_INHERITANCE =
         "insert into osee_artifact_type_inheritance (art_type_id, super_art_type_id) VALUES (?,?)";

   private static final String SELECT_ATTRIBUTE_VALIDITY = "SELECT * FROM osee_valid_attributes";
   private static final String INSERT_VALID_ATTRIBUTE =
         "INSERT INTO osee_valid_attributes (art_type_id, attr_type_id, branch_id) VALUES (?, ?, ?)";

   private static final String SELECT_ATTRIBUTE_TYPES =
         "SELECT * FROM osee_attribute_type aty1, osee_attribute_base_type aby1, osee_attribute_provider_type apy1 WHERE aty1.attr_base_type_id = aby1.attr_base_type_id AND aty1.attr_provider_type_id = apy1.attr_provider_type_id";
   private static final String INSERT_ATTRIBUTE_TYPE =
         "INSERT INTO osee_attribute_type (attr_type_id, attr_type_guid, attr_base_type_id, attr_provider_type_id, file_type_extension, name, default_value, enum_type_id, min_occurence, max_occurence, tip_text, tagger_id) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
   private static final String UPDATE_ATTRIBUTE_TYPE =
         "update osee_attribute_type SET attr_base_type_id=?, attr_provider_type_id=?, file_type_extension=?, name=?, default_value=?, enum_type_id=?, min_occurence=?, max_occurence=?, tip_text=?, tagger_id=? where attr_type_id = ?";

   private static final String INSERT_BASE_ATTRIBUTE_TYPE =
         "INSERT INTO osee_attribute_base_type (attr_base_type_id, attribute_class) VALUES (?, ?)";
   private static final String INSERT_ATTRIBUTE_PROVIDER_TYPE =
         "INSERT INTO osee_attribute_provider_type (attr_provider_type_id, attribute_provider_class) VALUES (?, ?)";
   private static final String SELECT_ATTRIBUTE_BASE_TYPE =
         "SELECT attr_base_type_id FROM osee_attribute_base_type WHERE attribute_class = ?";
   private static final String SELECT_ATTRIBUTE_PROVIDER_TYPE =
         "SELECT attr_provider_type_id FROM osee_attribute_provider_type WHERE attribute_provider_class = ?";

   private static final String SELECT_LINK_TYPES = "SELECT * FROM osee_relation_link_type";
   private static final String INSERT_RELATION_TYPE =
         "INSERT INTO osee_relation_link_type (rel_link_type_id, rel_link_type_guid, type_name, a_name, b_name, a_art_type_id, b_art_type_id, multiplicity, user_ordered, default_order_type_guid) VALUES (?,?,?,?,?,?,?,?,?,?)";
   private static final String UPDATE_RELATION_TYPE =
         "update osee_relation_link_type SET type_name=?, a_name=?, b_name=?, a_art_type_id=?, b_art_type_id=?, multiplicity=?, default_order_type_guid=? where rel_link_type_id = ?";

   private static final String SELECT_OSEE_ENUM_TYPES =
         "select oet.enum_type_name, oet.enum_type_guid, oetd.* from osee_enum_type oet, osee_enum_type_def oetd where oet.enum_type_id = oetd.enum_type_id order by oetd.enum_type_id, oetd.ordinal";
   private static final String INSERT_ENUM_TYPE =
         "insert into osee_enum_type (enum_type_id, enum_type_guid, enum_type_name) values (?,?,?)";
   private static final String UPDATE_ENUM_TYPE = "update osee_enum_type set enum_type_name=? where enum_type_id=?";
   private static final String DELETE_ENUM_TYPE = "delete from osee_enum_type oet where enum_type_id = ?";

   private static final String INSERT_ENUM_TYPE_DEF =
         "insert into osee_enum_type_def (enum_type_id, enum_entry_guid, name, ordinal) values (?,?,?,?)";
   private static final String UPDATE_ENUM_TYPE_DEF =
         "update osee_enum_type_def set name=?, ordinal=? where enum_entry_guid=?";
   private static final String DELETE_ENUM_TYPE_DEF = "delete from osee_enum_type_def where enum_entry_guid = ?";

   public OseeTypeDatabaseAccessor() {
   }

   @Override
   public void loadAllTypeValidity(OseeTypeCache cache, IOseeTypeFactory factory) throws OseeCoreException {
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      try {
         chStmt.runPreparedQuery(2000, SELECT_ATTRIBUTE_VALIDITY);
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

   public void loadAllArtifactTypes(OseeTypeCache cache, IOseeTypeFactory factory) throws OseeCoreException {
      ArtifactTypeCache cacheData = cache.getArtifactTypeCache();
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      try {
         chStmt.runPreparedQuery(SELECT_ARTIFACT_TYPES);

         while (chStmt.next()) {
            try {
               boolean isAbstract = chStmt.getInt("is_abstract") == ABSTRACT_TYPE_INDICATOR;
               ArtifactType artifactType =
                     factory.createArtifactType(chStmt.getString("art_type_guid"), isAbstract,
                           chStmt.getString("name"), cache);
               artifactType.setTypeId(chStmt.getInt("art_type_id"));
               cacheData.cacheType(artifactType);
            } catch (OseeDataStoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      } finally {
         chStmt.close();
      }

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
            superTypes.add(cacheData.getTypeById(superArtTypeId));

            if (previousBaseId != artTypeId) {
               ArtifactType artifactType = cacheData.getTypeById(artTypeId);
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

   public void loadAllAttributeTypes(OseeTypeCache cache, IOseeTypeFactory factory) throws OseeCoreException {
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

               int enumTypeId = chStmt.getInt("enum_type_id");
               OseeEnumType enumType = cache.getEnumTypeCache().getTypeById(enumTypeId);
               AttributeType attributeType =
                     factory.createAttributeType(chStmt.getString("attr_type_guid"), chStmt.getString("name"),
                           baseClassString, baseProviderClassString, baseAttributeClass, providerAttributeClass,
                           chStmt.getString("file_type_extension"), chStmt.getString("default_value"), enumType,
                           chStmt.getInt("min_occurence"), chStmt.getInt("max_occurence"),
                           chStmt.getString("tip_text"), chStmt.getString("tagger_id"));
               attributeType.setTypeId(chStmt.getInt("attr_type_id"));
               cache.getAttributeTypeCache().cacheType(attributeType);
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      } finally {
         chStmt.close();
      }
   }

   @Override
   public void loadAllRelationTypes(OseeTypeCache cache, IOseeTypeFactory factory) throws OseeCoreException {
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();

      try {
         chStmt.runPreparedQuery(SELECT_LINK_TYPES);

         while (chStmt.next()) {
            try {
               String relationTypeName = chStmt.getString("type_name");
               int relationTypeId = chStmt.getInt("rel_link_type_id");
               ArtifactType artifactTypeSideA = ArtifactTypeManager.getType(chStmt.getInt("a_art_type_id"));
               ArtifactType artifactTypeSideB = ArtifactTypeManager.getType(chStmt.getInt("b_art_type_id"));
               RelationTypeMultiplicity multiplicity =
                     RelationTypeMultiplicity.getRelationMultiplicity(chStmt.getInt("multiplicity"));
               if (multiplicity != null) {
                  boolean isUserOrdered = USER_ORDERED.equalsIgnoreCase(chStmt.getString("user_ordered"));
                  RelationType relationType =
                        factory.createRelationType(chStmt.getString("rel_link_type_guid"), relationTypeName,
                              chStmt.getString("a_name"), chStmt.getString("b_name"), artifactTypeSideA,
                              artifactTypeSideB, multiplicity, isUserOrdered,
                              chStmt.getString("default_order_type_guid"));
                  relationType.setTypeId(chStmt.getInt("rel_link_type_id"));
                  cache.getRelationTypeCache().cacheType(relationType);
               } else {
                  OseeLog.log(Activator.class, Level.SEVERE, String.format("Multiplicity was null for [%s][%s]",
                        relationTypeName, relationTypeId));
               }
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      } finally {
         chStmt.close();
      }
   }

   @Override
   public void storeArtifactType(OseeTypeCache cache, Collection<ArtifactType> types) throws OseeCoreException {
      List<Object[]> insertData = new ArrayList<Object[]>();
      List<Object[]> updateData = new ArrayList<Object[]>();
      for (ArtifactType type : types) {
         switch (type.getModificationType()) {
            case NEW:
               type.setTypeId(SequenceManager.getNextArtifactTypeId());
               insertData.add(new Object[] {type.getTypeId(), type.getGuid(), type.getName(),
                     type.isAbstract() ? ABSTRACT_TYPE_INDICATOR : CONCRETE_TYPE_INDICATOR});
               break;
            case MODIFIED:
               updateData.add(new Object[] {type.getName(), type.isAbstract(), type.getTypeId()});
               break;
            default:
               break;
         }
      }
      ConnectionHandler.runBatchUpdate(INSERT_ARTIFACT_TYPE, insertData);
      ConnectionHandler.runBatchUpdate(UPDATE_ARTIFACT_TYPE, updateData);

      storeArtifactTypeInheritance(types);
      storeAttributeTypeValidity(cache, types);
   }

   private void storeArtifactTypeInheritance(Collection<ArtifactType> types) throws OseeDataStoreException {
      List<Object[]> insertData = new ArrayList<Object[]>();
      for (ArtifactType type : types) {
         for (ArtifactType superType : type.getSuperArtifactTypes()) {
            insertData.add(new Object[] {type.getTypeId(), superType.getTypeId()});
         }
      }
      ConnectionHandler.runBatchUpdate(INSERT_ARTIFACT_TYPE_INHERITANCE, insertData);
   }

   private void storeAttributeTypeValidity(OseeTypeCache cache, Collection<ArtifactType> types) throws OseeDataStoreException {
      List<Object[]> insertData = new ArrayList<Object[]>();
      for (Entry<Pair<ArtifactType, Branch>, Collection<AttributeType>> entry : cache.getArtifactToAttributeMap().entrySet()) {
         ArtifactType artifactType = entry.getKey().getFirst();
         if (types.contains(artifactType)) {
            Branch branch = entry.getKey().getSecond();
            for (AttributeType attributeType : entry.getValue()) {
               insertData.add(new Object[] {artifactType.getTypeId(), attributeType.getTypeId(), branch.getBranchId()});
            }
         }
      }
      ConnectionHandler.runBatchUpdate(INSERT_VALID_ATTRIBUTE, insertData);
   }

   @Override
   public void storeRelationType(Collection<RelationType> types) throws OseeCoreException {
      List<Object[]> insertData = new ArrayList<Object[]>();
      List<Object[]> updateData = new ArrayList<Object[]>();
      for (RelationType type : types) {
         switch (type.getModificationType()) {
            case NEW:
               type.setTypeId(SequenceManager.getNextRelationTypeId());
               insertData.add(toInsertValues(type));
               break;
            case MODIFIED:
               updateData.add(toUpdateValues(type));
               break;
            default:
               break;
         }
      }
      ConnectionHandler.runBatchUpdate(INSERT_RELATION_TYPE, insertData);
      ConnectionHandler.runBatchUpdate(UPDATE_RELATION_TYPE, updateData);
   }

   @Override
   public void storeAttributeType(Collection<AttributeType> types) throws OseeCoreException {
      List<Object[]> insertData = new ArrayList<Object[]>();
      List<Object[]> updateData = new ArrayList<Object[]>();
      for (AttributeType type : types) {
         switch (type.getModificationType()) {
            case NEW:
               type.setTypeId(SequenceManager.getNextAttributeTypeId());
               insertData.add(toInsertValues(type));
               break;
            case MODIFIED:
               updateData.add(toUpdateValues(type));
               break;
            default:
               break;
         }
      }
      ConnectionHandler.runBatchUpdate(INSERT_ATTRIBUTE_TYPE, insertData);
      ConnectionHandler.runBatchUpdate(UPDATE_ATTRIBUTE_TYPE, updateData);
   }

   private Object[] toInsertValues(RelationType type) throws OseeDataStoreException {
      return new Object[] {type.getTypeId(), type.getGuid(), type.getName(), type.getSideAName(), type.getSideBName(),
            type.getArtifactTypeSideA().getTypeId(), type.getArtifactTypeSideB().getTypeId(),
            type.getMultiplicity().getValue(), type.isOrdered() ? USER_ORDERED : NOT_USER_ORDERED,
            type.getDefaultOrderTypeGuid()};
   }

   private Object[] toUpdateValues(RelationType type) throws OseeDataStoreException {
      return new Object[] {type.getName(), type.getSideAName(), type.getSideBName(),
            type.getArtifactTypeSideA().getTypeId(), type.getArtifactTypeSideB().getTypeId(),
            type.getMultiplicity().getValue(), type.getDefaultOrderTypeGuid(), type.getTypeId()};
   }

   private Object[] toInsertValues(AttributeType type) throws OseeDataStoreException {
      int attrBaseTypeId = getOrCreateAttributeBaseType(type.getBaseAttributeTypeId());
      int attrProviderTypeId = getOrCreateAttributeProviderType(type.getAttributeProviderId());
      return new Object[] {type.getTypeId(), type.getGuid(), attrBaseTypeId, attrProviderTypeId,
            type.getFileTypeExtension() == null ? SQL3DataType.VARCHAR : type.getFileTypeExtension(),
            type.getName() == null ? SQL3DataType.VARCHAR : type.getName(),
            type.getDefaultValue() == null ? SQL3DataType.VARCHAR : type.getDefaultValue(), type.getOseeEnumTypeId(),
            type.getMinOccurrences(), type.getMaxOccurrences(),
            type.getDescription() == null ? SQL3DataType.VARCHAR : type.getDescription(),
            type.getTaggerId() == null ? SQL3DataType.VARCHAR : type.getTaggerId()};
   }

   private Object[] toUpdateValues(AttributeType type) throws OseeDataStoreException {
      int attrBaseTypeId = getOrCreateAttributeBaseType(type.getBaseAttributeTypeId());
      int attrProviderTypeId = getOrCreateAttributeProviderType(type.getAttributeProviderId());
      return new Object[] {attrBaseTypeId, attrProviderTypeId,
            type.getFileTypeExtension() == null ? SQL3DataType.VARCHAR : type.getFileTypeExtension(),
            type.getName() == null ? SQL3DataType.VARCHAR : type.getName(),
            type.getDefaultValue() == null ? SQL3DataType.VARCHAR : type.getDefaultValue(), type.getOseeEnumTypeId(),
            type.getMinOccurrences(), type.getMaxOccurrences(),
            type.getDescription() == null ? SQL3DataType.VARCHAR : type.getDescription(),
            type.getTaggerId() == null ? SQL3DataType.VARCHAR : type.getTaggerId(), type.getTypeId()};
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

   @Override
   public void loadAllOseeEnumTypes(OseeTypeCache cache, IOseeTypeFactory factory) throws OseeCoreException {
      HashCollection<OseeEnumType, OseeEnumEntry> types = new HashCollection<OseeEnumType, OseeEnumEntry>();
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      try {
         chStmt.runPreparedQuery(SELECT_OSEE_ENUM_TYPES);
         OseeEnumType oseeEnumType = null;
         int lastEnumTypeId = -1;
         while (chStmt.next()) {
            try {
               int currentEnumTypeId = chStmt.getInt("enum_type_id");
               String currentEnumTypeGuid = chStmt.getString("enum_type_guid");
               if (lastEnumTypeId != currentEnumTypeId) {
                  oseeEnumType = factory.createEnumType(currentEnumTypeGuid, chStmt.getString("enum_type_name"), cache);
                  oseeEnumType.setTypeId(currentEnumTypeId);
                  cache.getEnumTypeCache().cacheType(oseeEnumType);
                  lastEnumTypeId = currentEnumTypeId;
               }
               types.put(oseeEnumType, factory.createEnumEntry(chStmt.getString("enum_entry_guid"),
                     chStmt.getString("name"), chStmt.getInt("ordinal"), cache));
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      } finally {
         chStmt.close();
      }
      for (OseeEnumType oseeEnumType : types.keySet()) {
         Collection<OseeEnumEntry> oseeEnumEntries = types.getValues(oseeEnumType);
         if (oseeEnumEntries != null) {
            oseeEnumType.setEntries(oseeEnumEntries);
         }
      }
   }

   private Object[] toInsertValues(OseeEnumType type) throws OseeDataStoreException {
      return new Object[] {type.getTypeId(), type.getGuid(), type.getName()};
   }

   private Object[] toUpdateValues(OseeEnumType type) throws OseeDataStoreException {
      return new Object[] {type.getName(), type.getTypeId()};
   }

   private Object[] toDeleteValues(OseeEnumType type) throws OseeDataStoreException {
      return new Object[] {type.getTypeId()};
   }

   private Object[] toInsertValues(OseeEnumEntry type) throws OseeDataStoreException {
      return new Object[] {type.getTypeId(), type.getGuid(), type.getName(), type.ordinal()};
   }

   private Object[] toUpdateValues(OseeEnumEntry type) throws OseeDataStoreException {
      return new Object[] {type.getName(), type.ordinal(), type.getGuid()};
   }

   private Object[] toDeleteValues(OseeEnumEntry type) throws OseeDataStoreException {
      return new Object[] {type.getGuid()};
   }

   @Override
   public void storeOseeEnumType(Collection<OseeEnumType> oseeEnumTypes) throws OseeCoreException {
      List<Object[]> insertData = new ArrayList<Object[]>();
      List<Object[]> updateData = new ArrayList<Object[]>();
      List<Object[]> deleteData = new ArrayList<Object[]>();
      for (OseeEnumType oseeEnumType : oseeEnumTypes) {
         switch (oseeEnumType.getModificationType()) {
            case NEW:
               oseeEnumType.setTypeId(SequenceManager.getNextOseeEnumTypeId());
               insertData.add(toInsertValues(oseeEnumType));
               break;
            case MODIFIED:
               updateData.add(toUpdateValues(oseeEnumType));
               break;
            case DELETED:
               deleteData.add(toDeleteValues(oseeEnumType));
               break;
            default:
               break;
         }
      }
      ConnectionHandler.runBatchUpdate(INSERT_ENUM_TYPE, insertData);
      ConnectionHandler.runBatchUpdate(UPDATE_ENUM_TYPE, updateData);
      ConnectionHandler.runBatchUpdate(DELETE_ENUM_TYPE, deleteData);

      insertData.clear();
      updateData.clear();
      deleteData.clear();
      for (OseeEnumType oseeEnumType : oseeEnumTypes) {
         for (OseeEnumEntry entry : oseeEnumType.values()) {
            switch (entry.getModificationType()) {
               case NEW:
                  entry.setTypeId(oseeEnumType.getTypeId());
                  insertData.add(toInsertValues(entry));
                  break;
               case MODIFIED:
                  updateData.add(toUpdateValues(entry));
                  break;
               case DELETED:
                  deleteData.add(toDeleteValues(entry));
                  break;
               default:
                  break;
            }
         }
      }
      ConnectionHandler.runBatchUpdate(INSERT_ENUM_TYPE_DEF, insertData);
      ConnectionHandler.runBatchUpdate(UPDATE_ENUM_TYPE_DEF, updateData);
      ConnectionHandler.runBatchUpdate(DELETE_ENUM_TYPE_DEF, deleteData);
   }
}
