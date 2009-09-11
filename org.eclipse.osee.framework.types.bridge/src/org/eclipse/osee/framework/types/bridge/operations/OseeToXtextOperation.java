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
package org.eclipse.osee.framework.types.bridge.operations;

import java.util.Collection;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.oseeTypes.OseeTypeModel;
import org.eclipse.osee.framework.oseeTypes.OseeTypesFactory;
import org.eclipse.osee.framework.oseeTypes.RelationMultiplicityEnum;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.OseeEnumType;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;
import org.eclipse.osee.framework.skynet.core.relation.order.RelationOrderBaseTypes;
import org.eclipse.osee.framework.types.bridge.internal.Activator;

/**
 * @author Roberto E. Escobar
 */
public class OseeToXtextOperation extends AbstractOperation {

   private final Map<String, OseeTypeModel> oseeModels;
   private final OseeTypesFactory factory;

   public OseeToXtextOperation(Map<String, OseeTypeModel> oseeModels) {
      super("OSEE to Text Model", Activator.PLUGIN_ID);
      this.oseeModels = oseeModels;
      this.factory = OseeTypesFactory.eINSTANCE;
   }

   private OseeTypesFactory getFactory() {
      return factory;
   }

   private OseeTypeModel getModelByNamespace(String namespace) {
      OseeTypeModel model = oseeModels.get(namespace);
      if (model == null) {
         model = factory.createOseeTypeModel();
         oseeModels.put(namespace, model);
      }
      return model;
   }

   private String getNamespace(String name) {
      String toReturn = "default";
      //      if (Strings.isValid(name)) {
      //         int index = name.lastIndexOf(".");
      //         if (index > 0) {
      //            toReturn = name.substring(0, index);
      //         }
      //      }
      return toReturn;
   }

   private String asPrimitiveType(String name) {
      return name.replace("org.eclipse.osee.framework.skynet.core.", "");
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      populateAttributeTypes(monitor);
      monitor.worked(calculateWork(0.20));

      populateArtifactTypes(monitor);
      monitor.worked(calculateWork(0.20));

      //      populateRelationTypes(monitor);
      monitor.worked(calculateWork(0.20));

      // TypeValidityManager.getAttributeTypesFromArtifactType(artifactType,
      // branch);
      monitor.worked(calculateWork(0.20));

      //
      monitor.worked(calculateWork(0.20));
   }

   private void populateAttributeTypes(IProgressMonitor monitor) throws OseeCoreException {
      Collection<AttributeType> attributeTypes = AttributeTypeManager.getAllTypes();
      for (AttributeType attributeType : attributeTypes) {
         checkForCancelledStatus(monitor);
         org.eclipse.osee.framework.oseeTypes.AttributeType modelType = getFactory().createAttributeType();

         OseeTypeModel model = getModelByNamespace(getNamespace(attributeType.getName()));
         model.getAttributeTypes().add(modelType);

         modelType.setName(attributeType.getName());
         modelType.setTypeGuid(attributeType.getGuid());
         modelType.setBaseAttributeType(asPrimitiveType(attributeType.getBaseAttributeTypeId()));
         modelType.setDataProvider(asPrimitiveType(attributeType.getAttributeProviderId()));
         modelType.setMax(String.valueOf(attributeType.getMaxOccurrences()));
         modelType.setMin(String.valueOf(attributeType.getMinOccurrences()));
         modelType.setFileExtension(attributeType.getFileTypeExtension());
         modelType.setDescription(attributeType.getDescription());
         modelType.setDefaultValue(attributeType.getDefaultValue());
         modelType.setTaggerId(attributeType.getTaggerId());

         OseeEnumType oseeEnumType = attributeType.getOseeEnumType();
         if (oseeEnumType != null) {
            org.eclipse.osee.framework.oseeTypes.OseeEnumType enumType = toModelEnumType(oseeEnumType);
            modelType.setEnumType(enumType);
            model.getEnumTypes().add(enumType);
         }
         //         modelType.setOverride();
      }
   }

   private org.eclipse.osee.framework.oseeTypes.OseeEnumType toModelEnumType(OseeEnumType oseeEnumType) {
      org.eclipse.osee.framework.oseeTypes.OseeEnumType modelType = getFactory().createOseeEnumType();

      return modelType;
   }

   private void populateRelationTypes(IProgressMonitor monitor) throws OseeCoreException {
      Collection<RelationType> relationTypes = RelationTypeManager.getAllTypes();
      for (RelationType relationType : relationTypes) {
         checkForCancelledStatus(monitor);
         org.eclipse.osee.framework.oseeTypes.RelationType modelType = getFactory().createRelationType();

         OseeTypeModel model = getModelByNamespace(getNamespace(relationType.getName()));
         model.getRelationTypes().add(modelType);
         modelType.setName(relationType.getName());
         modelType.setTypeGuid(relationType.getGuid());

         modelType.setDefaultOrderType(getRelationOrderType(relationType.getDefaultOrderTypeGuid()));
         modelType.setMultiplicity(RelationMultiplicityEnum.getByName(relationType.getMultiplicity().name()));

         modelType.setSideAName(relationType.getSideAName());
         modelType.setSideBName(relationType.getSideBName());

         modelType.setSideAArtifactType(getArtifactType(model, relationType.getArtifactTypeSideA().getGuid()));
         modelType.setSideBArtifactType(getArtifactType(model, relationType.getArtifactTypeSideB().getGuid()));
      }
   }

   private org.eclipse.osee.framework.oseeTypes.ArtifactType getArtifactType(OseeTypeModel model, String guid) throws OseeArgumentException {
      for (org.eclipse.osee.framework.oseeTypes.ArtifactType artifactType : model.getArtifactTypes()) {
         if (guid.equals(artifactType.getTypeGuid())) {
            return artifactType;
         }
      }
      throw new OseeArgumentException(String.format("ArtifactType guid:[%s] was not found", guid));
   }

   private String getRelationOrderType(String guid) throws OseeArgumentException {
      RelationOrderBaseTypes type = RelationOrderBaseTypes.getFromGuid(guid);
      return type.prettyName().replaceAll(" ", "_");
   }

   private void populateArtifactTypes(IProgressMonitor monitor) throws OseeCoreException {
      Collection<ArtifactType> artifactTypes = ArtifactTypeManager.getAllTypes();
      for (ArtifactType artifactType : artifactTypes) {
         checkForCancelledStatus(monitor);
         org.eclipse.osee.framework.oseeTypes.ArtifactType modelType = getFactory().createArtifactType();

         modelType.setName(artifactType.getName());
         modelType.setTypeGuid(artifactType.getGuid());

         OseeTypeModel model = getModelByNamespace(getNamespace(artifactType.getName()));
         model.getArtifactTypes().add(modelType);
      }
   }
}
