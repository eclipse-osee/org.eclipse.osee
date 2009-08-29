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

import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.factory.ArtifactFactoryManager;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.providers.IAttributeDataProvider;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;

/**
 * @author Roberto E. Escobar
 */
public class OseeTypeFactory implements IOseeTypeFactory {
   private final ArtifactFactoryManager factoryManager;
   private final IArtifactTypeDataAccess artifactTypeDataAccess;

   public OseeTypeFactory() {
      this.factoryManager = new ArtifactFactoryManager();
      this.artifactTypeDataAccess = null;
   }

   @Override
   public ArtifactType createArtifactType(String guid, boolean isAbstract, String artifactTypeName) throws OseeCoreException {
      return new ArtifactType(guid, artifactTypeName, isAbstract, factoryManager, artifactTypeDataAccess);
   }

   @Override
   public AttributeType createAttributeType(String guid, String name, Class<? extends Attribute<?>> baseAttributeClass, Class<? extends IAttributeDataProvider> providerAttributeClass, String fileTypeExtension, String defaultValue, int oseeEnumTypeId, int minOccurrences, int maxOccurrences, String tipText, String taggerId) throws OseeCoreException {
      return new AttributeType(guid, name, baseAttributeClass, providerAttributeClass, fileTypeExtension, defaultValue,
            oseeEnumTypeId, minOccurrences, maxOccurrences, tipText, taggerId);
   }

   @Override
   public RelationType createRelationType(String guid, String relationTypeName, String sideAName, String sideBName, ArtifactType artifactTypeSideA, ArtifactType artifactTypeSideB, RelationTypeMultiplicity multiplicity, boolean isUserOrdered, String defaultOrderTypeGuid) throws OseeCoreException {
      if (!Strings.isValid(relationTypeName)) {
         throw new IllegalArgumentException("The relationName can not be null or empty");
      }
      if (!Strings.isValid(sideAName)) {
         throw new IllegalArgumentException("The sideAName can not be null or empty");
      }
      if (!Strings.isValid(sideBName)) {
         throw new IllegalArgumentException("The sideBName can not be null or empty");
      }
      //      if (!Strings.isValid(artifactTypeSideA)) {
      //         throw new IllegalArgumentException("The artifactTypeSideA can not be null or empty");
      //      }
      //      if (!Strings.isValid(artifactTypeSideB)) {
      //         throw new IllegalArgumentException("The artifactTypeSideB can not be null or empty");
      //      }
      //      RelationTypeMultiplicity multiplicityEnum = RelationTypeMultiplicity.getFromString(multiplicity);
      //      if (multiplicityEnum == null) {
      //         throw new IllegalArgumentException("The multiplicity can not be null or empty");
      //      }
      //      ArtifactType artTypeIdA = ArtifactTypeManager.getType(artifactTypeSideA);
      //      ArtifactType artTypeIdB = ArtifactTypeManager.getType(artifactTypeSideB);

      return new RelationType(guid, relationTypeName, sideAName, sideBName, artifactTypeSideA, artifactTypeSideB,
            multiplicity, isUserOrdered, defaultOrderTypeGuid);
   }
}
