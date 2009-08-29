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
package org.eclipse.osee.framework.skynet.core.artifact;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.exception.OseeInvalidInheritanceException;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.ConnectionHandlerStatement;
import org.eclipse.osee.framework.database.core.SequenceManager;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeExtensionManager;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.providers.IAttributeDataProvider;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;

/**
 * @author Roberto E. Escobar
 */
final class OseeTypeDatabaseAccessor implements IOseeTypeDataAccessor {
   private static final int ABSTRACT_TYPE_INDICATOR = 1;
   private static final int CONCRETE_TYPE_INDICATOR = 0;
   private static final int NULL_SUPER_ARTIFACT_TYPE = -1;
   private static final String USER_ORDERED = "Yes";
   private static final String NOT_USER_ORDERED = "No";

   private static final String SELECT_ARTIFACT_TYPES = "select * from osee_artifact_type";
   private static final String INSERT_ARTIFACT_TYPE =
         "insert into osee_artifact_type (art_type_id, art_type_guid, name, is_abstract) VALUES (?,?,?,?)";

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
   private static final String INSERT_BASE_ATTRIBUTE_TYPE =
         "INSERT INTO osee_attribute_base_type (attr_base_type_id, attribute_class) VALUES (?, ?)";
   private static final String INSERT_ATTRIBUTE_PROVIDER_TYPE =
         "INSERT INTO osee_attribute_provider_type (attr_provider_type_id, attribute_provider_class) VALUES (?, ?)";
   private static final String SELECT_ATTRIBUTE_BASE_TYPE =
         "SELECT attr_base_type_id FROM osee_attribute_base_type WHERE attribute_class = ?";
   private static final String SELECT_ATTRIBUTE_PROVIDER_TYPE =
         "SELECT attr_provider_type_id FROM osee_attribute_provider_type WHERE attribute_provider_class = ?";

   private static final String SELECT_LINK_TYPES = "SELECT * FROM osee_relation_link_type";
   private static final String INSERT_RELATION_LINK_TYPE =
         "INSERT INTO osee_relation_link_type (rel_link_type_id, rel_link_type_guid, type_name, a_name, b_name, a_art_type_id, b_art_type_id, multiplicity, user_ordered, default_order_type_guid) VALUES (?,?,?,?,?,?,?,?,?,?)";

   public OseeTypeDatabaseAccessor() {
   }

   @Override
   public void storeTypeInheritance(List<Object[]> datas) throws OseeCoreException {
      ConnectionHandler.runBatchUpdate(INSERT_ARTIFACT_TYPE_INHERITANCE, datas);
   }

   @Override
   public void storeValidity(List<Object[]> datas) throws OseeCoreException {
      ConnectionHandler.runBatchUpdate(INSERT_VALID_ATTRIBUTE, datas);
   }

   @Override
   public void loadAllTypeValidity(OseeTypeCache cache, IOseeTypeFactory artifactTypeFactory) throws OseeCoreException {
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

   public void loadAllArtifactTypes(OseeTypeCache cache, IOseeTypeFactory artifactTypeFactory) throws OseeCoreException {
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      try {
         chStmt.runPreparedQuery(SELECT_ARTIFACT_TYPES);

         while (chStmt.next()) {
            try {
               boolean isAbstract = chStmt.getInt("is_abstract") == ABSTRACT_TYPE_INDICATOR;
               ArtifactType artifactType =
                     artifactTypeFactory.createArtifactType(chStmt.getString("art_type_guid"), isAbstract,
                           chStmt.getString("name"));
               artifactType.setArtTypeId(chStmt.getInt("art_type_id"));
               cache.cacheArtifactType(artifactType);
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
         while (chStmt2.next()) {
            int artTypeId = chStmt.getInt("art_type_id");
            int superArtTypeId = chStmt.getInt("super_art_type_id");

            ArtifactType superArtifactType = null;
            if (superArtTypeId != NULL_SUPER_ARTIFACT_TYPE) {
               superArtifactType = cache.getArtifactTypeById(artTypeId);
            }
            ArtifactType artifactType = cache.getArtifactTypeById(artTypeId);
            if (artifactType == null) {
               throw new OseeInvalidInheritanceException(String.format("ArtifactType [%s] inherit from [%s] is null",
                     artTypeId, superArtTypeId));
            }
            if (artTypeId == superArtTypeId) {
               throw new OseeInvalidInheritanceException(String.format(
                     "Circular inheritance detected artifact type [%s] inherits from [%s]", artTypeId, superArtTypeId));
            }
            cache.cacheArtifactTypeInheritance(artifactType, superArtifactType);
         }
      } finally {
         chStmt2.close();
      }
   }

   public void loadAllAttributeTypes(OseeTypeCache cache, IOseeTypeFactory artifactTypeFactory) throws OseeCoreException {
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

               AttributeType attributeType =
                     artifactTypeFactory.createAttributeType(chStmt.getString("attr_type_guid"),
                           chStmt.getString("name"), baseAttributeClass, providerAttributeClass,
                           chStmt.getString("file_type_extension"), chStmt.getString("default_value"),
                           chStmt.getInt("enum_type_id"), chStmt.getInt("min_occurence"),
                           chStmt.getInt("max_occurence"), chStmt.getString("tip_text"), chStmt.getString("tagger_id"));
               attributeType.setAttrTypeId(chStmt.getInt("attr_type_id"));
               cache.cacheAttributeType(attributeType);
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      } finally {
         chStmt.close();
      }
   }

   @Override
   public void loadAllRelationTypes(OseeTypeCache cache, IOseeTypeFactory artifactTypeFactory) throws OseeCoreException {
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
                        artifactTypeFactory.createRelationType(chStmt.getString("rel_link_type_guid_id"),
                              relationTypeName, chStmt.getString("a_name"), chStmt.getString("b_name"),
                              artifactTypeSideA, artifactTypeSideB, multiplicity, isUserOrdered,
                              chStmt.getString("default_order_type_guid"));
                  relationType.setRelationTypeId(chStmt.getInt("rel_link_type_id"));
                  cache.cacheRelationType(relationType);
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
   public void storeArtifactType(ArtifactType... artifactTypes) throws OseeCoreException {
      if (artifactTypes != null) {
         if (artifactTypes.length == 1) {
            ArtifactType type = artifactTypes[0];
            ConnectionHandler.runPreparedUpdate(INSERT_ARTIFACT_TYPE, type.getArtTypeId(), type.getName(),
                  type.isAbstract() ? ABSTRACT_TYPE_INDICATOR : CONCRETE_TYPE_INDICATOR);
         } else {
            List<Object[]> types = new ArrayList<Object[]>();
            for (ArtifactType type : artifactTypes) {
               types.add(new Object[] {type.getArtTypeId(), type.getName(),
                     type.isAbstract() ? ABSTRACT_TYPE_INDICATOR : CONCRETE_TYPE_INDICATOR});
            }
            ConnectionHandler.runBatchUpdate(INSERT_ARTIFACT_TYPE, types);
         }
      }
   }

   @Override
   public void storeRelationType(RelationType... relationTypes) throws OseeCoreException {
      if (relationTypes != null) {
         if (relationTypes.length == 1) {
            RelationType type = relationTypes[0];
            ConnectionHandler.runPreparedUpdate(INSERT_RELATION_LINK_TYPE, type.getRelationTypeId(),
                  type.getTypeName(), type.getSideAName(), type.getSideBName(),
                  type.getArtifactTypeSideA().getArtTypeId(), type.getArtifactTypeSideB().getArtTypeId(),
                  type.getMultiplicity().getValue(), type.isOrdered() ? USER_ORDERED : NOT_USER_ORDERED,
                  type.getDefaultOrderTypeGuid());
         } else {
            List<Object[]> types = new ArrayList<Object[]>();
            for (RelationType type : relationTypes) {
               types.add(new Object[] {type.getRelationTypeId(), type.getTypeName(), type.getSideAName(),
                     type.getSideBName(), type.getArtifactTypeSideA().getArtTypeId(),
                     type.getArtifactTypeSideB().getArtTypeId(), type.getMultiplicity().getValue(),
                     type.isOrdered() ? USER_ORDERED : NOT_USER_ORDERED, type.getDefaultOrderTypeGuid()});
            }
            ConnectionHandler.runBatchUpdate(INSERT_RELATION_LINK_TYPE, types);
         }
      }
   }

   @Override
   public void storeAttributeType(AttributeType... artifactTypes) throws OseeCoreException {
      if (artifactTypes != null) {
         if (artifactTypes.length == 1) {
            AttributeType type = artifactTypes[0];
            type.setAttrTypeId(SequenceManager.getNextArtifactTypeId());
            //            ConnectionHandler.runPreparedUpdate(INSERT_ATTRIBUTE_TYPE, type.getAttrTypeId(), type.getName(),
            //                  type.isAbstract() ? ABSTRACT_TYPE_INDICATOR : CONCRETE_TYPE_INDICATOR);
         } else {
            List<Object[]> types = new ArrayList<Object[]>();
            //            for (AttributeType type : artifactTypes) {
            //               types.add(new Object[] {type.getArtTypeId(), type.getName(),
            //                     type.isAbstract() ? ABSTRACT_TYPE_INDICATOR : CONCRETE_TYPE_INDICATOR});
            //            }
            ConnectionHandler.runBatchUpdate(INSERT_ATTRIBUTE_TYPE, types);
         }
      }

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
      //      int attrBaseTypeId = getOrCreateAttributeBaseType(attributeBaseType);
      //      int attrProviderTypeId = getOrCreateAttributeProviderType(attributeProviderTypeName);
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
      //      return attributeType;
      return null;
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

}
