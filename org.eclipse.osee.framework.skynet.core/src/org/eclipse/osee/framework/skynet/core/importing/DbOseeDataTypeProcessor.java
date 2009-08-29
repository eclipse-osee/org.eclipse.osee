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
package org.eclipse.osee.framework.skynet.core.importing;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;

/**
 * @author Roberto E. Escobar
 */
public class DbOseeDataTypeProcessor implements IOseeDataTypeProcessor {

   private final HashCollection<ArtifactType, AttributeType> artifactTypeContainment =
         new HashCollection<ArtifactType, AttributeType>(false, HashSet.class);
   private final Map<String, RelationTypeDefinition> relationTypeDefinitions =
         new HashMap<String, RelationTypeDefinition>();

   @Override
   public boolean doesArtifactSuperTypeExist(String artifactSuperTypeName) throws OseeCoreException {
      boolean result = false;
      if (ArtifactTypeManager.typeExists(artifactSuperTypeName)) { // artifactSuperTypeName might also be a concrete type
         ArtifactTypeManager.getType(artifactSuperTypeName); // ensure existence
         result = true;
      }
      return result;
   }

   @Override
   public void onArtifactType(boolean isAbstract, String artifactTypeName) throws OseeCoreException {
      ArtifactTypeManager.createType(isAbstract, artifactTypeName);
   }

   @Override
   public void onArtifactTypeInheritance(String ancestor, Collection<String> descendants) throws OseeCoreException {
      ArtifactTypeManager.createInheritance(ancestor, descendants);
   }

   @Override
   public void onAttributeType(String attributeBaseType, String attributeProviderTypeName, String fileTypeExtension, String attributeName, String defaultValue, String validityXml, int minOccurrence, int maxOccurrence, String tipText, String taggerId) throws OseeCoreException {
      AttributeTypeManager.createType(attributeBaseType, attributeProviderTypeName, fileTypeExtension, attributeName,
            defaultValue, validityXml, minOccurrence, maxOccurrence, tipText, taggerId);
   }

   @Override
   public void onAttributeValidity(String attributeName, String artifactSuperTypeName, Collection<String> concreteArtifactTypes) throws OseeCoreException {
      AttributeType attributeType = AttributeTypeManager.getType(attributeName);

      for (String artifactTypeName : concreteArtifactTypes) {
         ArtifactType artifactType = ArtifactTypeManager.getType(artifactTypeName);
         artifactTypeContainment.put(artifactType, attributeType);
      }
   }

   @Override
   public void onRelationType(String relationTypeName, String sideAName, String sideBName, String artifactTypeSideA, String artifactTypeSideB, String multiplicity, String ordered, String defaultOrderIdGuid) throws OseeCoreException {
      RelationTypeDefinition relationTypeDef = new RelationTypeDefinition();
      relationTypeDef.typeName = relationTypeName;
      relationTypeDef.defaultOrderTypeGuid = defaultOrderIdGuid;
      relationTypeDef.ordered = ordered;
      relationTypeDef.sideAName = sideAName;
      relationTypeDef.sideBName = sideBName;
      relationTypeDef.multiplicity = RelationTypeMultiplicity.MANY_TO_MANY;
      relationTypeDefinitions.put(relationTypeName, relationTypeDef);
   }

   @Override
   public void onRelationValidity(String artifactTypeName, String relationTypeName, int sideAMax, int sideBMax) throws OseeCoreException {
      ArtifactType artifactType = ArtifactTypeManager.getType(artifactTypeName);
      //      RelationType relationType = RelationTypeManager.getType(relationTypeName);
      //      RelationTypeManager.createRelationLinkValidity(BranchManager.getSystemRootBranch(), artifactType, relationType,
      //            sideAMax, sideBMax);

      RelationTypeDefinition relationType = relationTypeDefinitions.get(relationTypeName);
      if (sideAMax > 0) {
         relationType.artifactTypeSideA = artifactType.getName();
      }
      if (sideBMax > 0) {
         relationType.artifactTypeSideB = artifactType.getName();
      }

      RelationTypeMultiplicity multiplicity = relationType.multiplicity;
      if (sideAMax == Integer.MAX_VALUE && sideBMax == 1) {
         multiplicity = RelationTypeMultiplicity.ONE_TO_MANY;

      } else if (sideAMax == 1 && sideBMax == Integer.MAX_VALUE) {
         multiplicity = RelationTypeMultiplicity.MANY_TO_ONE;

      } else if (sideAMax == Integer.MAX_VALUE && sideBMax == Integer.MAX_VALUE) {
         multiplicity = RelationTypeMultiplicity.MANY_TO_MANY;
      } else if (sideAMax == 1 && sideBMax == 1) {
         multiplicity = RelationTypeMultiplicity.ONE_TO_ONE;
      } else {
         System.out.println("None detected - " + relationTypeName);
      }

      if (multiplicity != null && !multiplicity.equals(relationType.multiplicity)) {
         relationType.multiplicity = multiplicity;
      }
   }

   @Override
   public void onFinish() throws OseeCoreException {
      for (ArtifactType artifactType : artifactTypeContainment.keySet()) {
         Collection<AttributeType> innerTypes = artifactTypeContainment.getValues(artifactType);
         if (innerTypes != null) {
            ArtifactTypeManager.setAttributeTypes(artifactType, innerTypes, BranchManager.getSystemRootBranch());
         }
      }
      for (RelationTypeDefinition relationType : relationTypeDefinitions.values()) {
         RelationTypeManager.createRelationType(relationType.typeName, relationType.sideAName, relationType.sideBName,
               relationType.artifactTypeSideA, relationType.artifactTypeSideB, relationType.multiplicity.toString(),
               relationType.ordered, relationType.defaultOrderTypeGuid);
      }
   }

   private static class RelationTypeDefinition {
      protected String typeName;
      protected String sideAName;
      protected String sideBName;
      protected RelationTypeMultiplicity multiplicity;
      protected String artifactTypeSideA;
      protected String artifactTypeSideB;
      protected String ordered;
      protected String defaultOrderTypeGuid;
   }

}
