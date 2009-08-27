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
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.TypeValidityManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;

/**
 * @author Roberto E. Escobar
 */
public class DbOseeDataTypeProcessor implements IOseeDataTypeProcessor {

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
   public void onArtifactType(boolean isAbstract, String artifactTypeName, String superArtifactTypeName) throws OseeCoreException {
      ArtifactTypeManager.createType(isAbstract, artifactTypeName, superArtifactTypeName);
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
         TypeValidityManager.persistAttributeValidity(artifactType, attributeType, BranchManager.getSystemRootBranch());
      }

   }

   @Override
   public void onRelationType(String relationTypeName, String sideAName, String sideBName, String abPhrasing, String baPhrasing, String shortName, String ordered, String defaultOrderIdGuid) throws OseeCoreException {
      RelationTypeManager.createRelationType(relationTypeName, sideAName, sideBName, abPhrasing, baPhrasing, shortName,
            ordered, defaultOrderIdGuid);
   }

   @Override
   public void onRelationValidity(String artifactTypeName, String relationTypeName, int sideAMax, int sideBMax) throws OseeCoreException {
      ArtifactType artifactType = ArtifactTypeManager.getType(artifactTypeName);
      RelationType relationType = RelationTypeManager.getType(relationTypeName);
      RelationTypeManager.createRelationLinkValidity(BranchManager.getSystemRootBranch(), artifactType, relationType,
            sideAMax, sideBMax);
   }
}
