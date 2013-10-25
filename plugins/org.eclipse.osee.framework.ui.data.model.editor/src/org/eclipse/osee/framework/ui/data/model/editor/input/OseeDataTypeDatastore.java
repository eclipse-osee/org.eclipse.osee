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
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;
import org.eclipse.osee.framework.ui.data.model.editor.model.ArtifactDataType;
import org.eclipse.osee.framework.ui.data.model.editor.model.AttributeDataType;
import org.eclipse.osee.framework.ui.data.model.editor.model.RelationDataType;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageManager;

/**
 * @author Roberto E. Escobar
 */
public class OseeDataTypeDatastore {

   public static List<AttributeDataType> getAttributeTypes() throws OseeCoreException {
      List<AttributeDataType> attributeDataTypes = new ArrayList<AttributeDataType>();
      for (AttributeType attributeType : AttributeTypeManager.getAllTypes()) {
         String baseClass = AttributeTypeManager.getAttributeBaseClass(attributeType).getCanonicalName();
         String providerClass = AttributeTypeManager.getAttributeProviderClass(attributeType).getCanonicalName();
         AttributeDataType attributeDataType =
            new AttributeDataType(String.valueOf(attributeType.getId()), attributeType.getName(), baseClass,
               attributeType.getDefaultValue(), attributeType.getFileTypeExtension(),
               attributeType.getMaxOccurrences(), attributeType.getMinOccurrences(), providerClass,
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
               ArtifactImageManager.getImage(artifactType));
         artifactDataTypes.add(artifactDataType);
      }
      return artifactDataTypes;
   }

   public static HashCollection<String, String> getArtifactToAttributeEntries() throws OseeCoreException {
      HashCollection<String, String> toReturn = new HashCollection<String, String>();
      for (ArtifactType artifactType : ArtifactTypeManager.getAllTypes()) {
         Collection<IAttributeType> attributeTypes =
            artifactType.getAttributeTypes(BranchManager.getBranch(CoreBranches.SYSTEM_ROOT));

         for (IAttributeType attrType : attributeTypes) {
            long typeId = AttributeTypeManager.getType(attrType).getId();
            toReturn.put(String.valueOf(artifactType.getId()), String.valueOf(typeId));
         }
      }
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
