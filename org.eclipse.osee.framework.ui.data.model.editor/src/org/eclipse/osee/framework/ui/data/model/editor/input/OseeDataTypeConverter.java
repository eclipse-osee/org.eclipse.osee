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

import java.util.Collection;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.OseeEnumType;
import org.eclipse.osee.framework.skynet.core.attribute.OseeEnumTypeManager;
import org.eclipse.osee.framework.skynet.core.importing.IOseeDataTypeProcessor;
import org.eclipse.osee.framework.ui.data.model.editor.model.ArtifactDataType;
import org.eclipse.osee.framework.ui.data.model.editor.model.AttributeDataType;
import org.eclipse.osee.framework.ui.data.model.editor.model.DataTypeSource;
import org.eclipse.osee.framework.ui.data.model.editor.model.RelationDataType;

/**
 * @author Roberto E. Escobar
 */
public class OseeDataTypeConverter implements IOseeDataTypeProcessor {

   private final DataTypeSource dataTypeSource;

   public OseeDataTypeConverter(DataTypeSource dataTypeSource) {
      this.dataTypeSource = dataTypeSource;
   }

   @Override
   public boolean doesArtifactSuperTypeExist(String artifactSuperTypeName) throws OseeCoreException {
      return false;
   }

   @Override
   public void onArtifactType(boolean isAbstract, String artifactTypeName) throws OseeCoreException {
      ArtifactDataType artifactDataType = new ArtifactDataType(artifactTypeName, null);

      this.dataTypeSource.add(artifactDataType);
   }

   @Override
   public void onArtifactTypeInheritance(String ancestor, Collection<String> descendants) throws OseeCoreException {

   }

   @Override
   public void onAttributeType(String baseAttributeClass, String providerAttributeClass, String fileTypeExtension, String name, String defaultValue, String validityXml, int minOccurrence, int maxOccurrence, String toolTipText, String taggerId) throws OseeCoreException {
      OseeEnumType enumType = OseeEnumTypeManager.getType(name);
      int enumTypeId = enumType.getId();
      AttributeDataType attributeDataType =
            new AttributeDataType(name, baseAttributeClass, defaultValue, fileTypeExtension, maxOccurrence,
                  minOccurrence, providerAttributeClass, taggerId, toolTipText, enumTypeId);

      // Create attribute provider and base attribute classes here ?? 

      this.dataTypeSource.add(attributeDataType);
   }

   @Override
   public void onAttributeValidity(String attributeName, String artifactSuperTypeName, Collection<String> concreteTypes) throws OseeCoreException {
   }

   @Override
   public void onRelationType(String name, String sideAName, String sideBName, String artifactTypeSideA, String artifactTypeSideB, String multiplicity, String ordered, String defaultOrderTypeGuid) throws OseeCoreException {
      RelationDataType relationDataType =
            new RelationDataType("", name, "", "", Boolean.valueOf(ordered), "", sideAName, sideBName);

      this.dataTypeSource.add(relationDataType);
   }

   @Override
   public void onRelationValidity(String artifactTypeName, String relationTypeName, int sideAMax, int sideBMax) throws OseeCoreException {
   }

   public DataTypeSource getODMModel() {
      return dataTypeSource;
   }

   @Override
   public void onFinish() throws OseeCoreException {

   }

}
