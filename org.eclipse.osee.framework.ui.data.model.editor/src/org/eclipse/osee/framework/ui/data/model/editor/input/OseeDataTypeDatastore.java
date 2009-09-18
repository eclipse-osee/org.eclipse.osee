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
package org.eclipse.osee.framework.ui.data.model.editor.input;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.database.core.ConnectionHandlerStatement;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;
import org.eclipse.osee.framework.ui.data.model.editor.model.ArtifactDataType;
import org.eclipse.osee.framework.ui.data.model.editor.model.AttributeDataType;
import org.eclipse.osee.framework.ui.data.model.editor.model.RelationDataType;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;

/**
 * @author Roberto E. Escobar
 */
public class OseeDataTypeDatastore {
   private static final String SELECT_ATTRIBUTE_VALIDITY = "SELECT * FROM osee_artifact_type_attributes";

   private OseeDataTypeDatastore() {
   }

   public static List<AttributeDataType> getAttributeTypes() throws OseeCoreException {
      List<AttributeDataType> attributeDataTypes = new ArrayList<AttributeDataType>();
      for (AttributeType attributeType : AttributeTypeManager.getAllTypes()) {
         AttributeDataType attributeDataType =
               new AttributeDataType(String.valueOf(attributeType.getId()), attributeType.getName(),
                     attributeType.getBaseAttributeClass().getCanonicalName(), attributeType.getDefaultValue(),
                     attributeType.getFileTypeExtension(), attributeType.getMaxOccurrences(),
                     attributeType.getMinOccurrences(), attributeType.getProviderAttributeClass().getCanonicalName(),
                     attributeType.getTaggerId(), attributeType.getDescription(), attributeType.getOseeEnumTypeId());
         attributeDataTypes.add(attributeDataType);
      }
      return attributeDataTypes;
   }

   public static List<RelationDataType> getRelationDataTypes() throws OseeCoreException {
      List<RelationDataType> relationDataTypes = new ArrayList<RelationDataType>();
      for (RelationType relationType : RelationTypeManager.getAllTypes()) {
         RelationDataType relationDataType =
               new RelationDataType(String.valueOf(relationType.getId()), relationType.getName(), "", "",
                     relationType.isOrdered(), "", relationType.getSideAName(), relationType.getSideBName());
         relationDataTypes.add(relationDataType);
      }
      return relationDataTypes;
   }

   public static List<ArtifactDataType> getArtifactDataTypes() throws OseeCoreException {
      List<ArtifactDataType> artifactDataTypes = new ArrayList<ArtifactDataType>();
      for (ArtifactType artifactType : ArtifactTypeManager.getAllTypes()) {
         ArtifactDataType artifactDataType =
               new ArtifactDataType(String.valueOf(artifactType.getId()), artifactType.getName(),
                     ImageManager.getImage(artifactType));
         artifactDataTypes.add(artifactDataType);
      }
      return artifactDataTypes;
   }

   public static HashCollection<String, String> getArtifactToAttributeEntries() throws OseeCoreException {
      HashCollection<String, String> toReturn = new HashCollection<String, String>();
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      try {
         chStmt.runPreparedQuery(2000, SELECT_ATTRIBUTE_VALIDITY);
         while (chStmt.next()) {
            try {
               toReturn.put(chStmt.getString("art_type_id"), chStmt.getString("attr_type_id"));
            } catch (OseeCoreException ex) {
               OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
            }
         }
      } finally {
         chStmt.close();
      }
      return toReturn;
   }

   public static CompositeKeyHashMap<String, String, Pair<Integer, Integer>> getArtifactToRelationEntries() throws OseeCoreException {
      CompositeKeyHashMap<String, String, Pair<Integer, Integer>> toReturn =
            new CompositeKeyHashMap<String, String, Pair<Integer, Integer>>();
      //      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      //      try {
      //         chStmt.runPreparedQuery(2000, SELECT_RELATION_VALIDITY);
      //         while (chStmt.next()) {
      //            try {
      //               Pair<String, String> key =
      //                     new Pair<String, String>(chStmt.getString("art_type_id"), chStmt.getString("rel_link_type_id"));
      //
      //               Pair<Integer, Integer> multiplicity =
      //                     new Pair<Integer, Integer>(chStmt.getInt("side_a_max"), chStmt.getInt("side_b_max"));
      //
      //               toReturn.put(key, multiplicity);
      //            } catch (OseeCoreException ex) {
      //               OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      //            }
      //         }
      //      } finally {
      //         chStmt.close();
      //      }
      return toReturn;
   }

   public static HashCollection<String, String> getArtifactInheritance() throws OseeCoreException {
      HashCollection<String, String> toReturn = new HashCollection<String, String>();
      for (ArtifactType artifactType : ArtifactTypeManager.getAllTypes()) {
         if (artifactType.hasSuperArtifactTypes()) {
            for (ArtifactType superType : artifactType.getSuperArtifactTypes()) {
               toReturn.put(String.valueOf(superType.getId()), String.valueOf(artifactType.getId()));
            }
         }
      }
      return toReturn;
   }
}
