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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.oseeTypes.AttributeTypeRef;
import org.eclipse.osee.framework.oseeTypes.OseeTypeModel;
import org.eclipse.osee.framework.oseeTypes.OseeTypesFactory;
import org.eclipse.osee.framework.oseeTypes.RelationMultiplicityEnum;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.OseeEnumEntry;
import org.eclipse.osee.framework.skynet.core.attribute.OseeEnumType;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;
import org.eclipse.osee.framework.skynet.core.relation.order.RelationOrderBaseTypes;
import org.eclipse.osee.framework.skynet.core.types.OseeTypeCache;
import org.eclipse.osee.framework.types.bridge.internal.Activator;

/**
 * @author Roberto E. Escobar
 */
public class OseeToXtextOperation extends AbstractOperation {

   private final Map<String, OseeTypeModel> oseeModels;
   private final OseeTypesFactory factory;
   private final OseeTypeCache cache;

   public OseeToXtextOperation(OseeTypeCache cache, Map<String, OseeTypeModel> oseeModels) {
      super("OSEE to Text Model", Activator.PLUGIN_ID);
      this.oseeModels = oseeModels;
      this.factory = OseeTypesFactory.eINSTANCE;
      this.cache = cache;
   }

   private OseeTypesFactory getFactory() {
      return factory;
   }

   private OseeTypeModel getModelByNamespace(String namespace) {
      OseeTypeModel model = oseeModels.get(namespace);
      if (model == null) {
         model = factory.createOseeTypeModel();
         model.getImports();
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
      double workPercentage = 1.0 / 6.0;
      populateEnumTypes(monitor, workPercentage);
      populateAttributeTypes(monitor, workPercentage);
      populateArtifactTypes(monitor, workPercentage);
      populateArtifactTypeInheritance(monitor, workPercentage);
      populateArtifactTypeAttributeTypes(monitor, workPercentage);
      populateRelationTypes(monitor, workPercentage);
   }

   private void populateEnumTypes(IProgressMonitor monitor, double workPercentage) throws OseeCoreException {
      Collection<OseeEnumType> enumTypes = cache.getEnumTypeCache().getAll();
      for (OseeEnumType enumType : enumTypes) {
         checkForCancelledStatus(monitor);
         org.eclipse.osee.framework.oseeTypes.OseeEnumType modelType = getFactory().createOseeEnumType();

         OseeTypeModel model = getModelByNamespace(getNamespace(enumType.getName()));
         model.getEnumTypes().add(modelType);

         modelType.setName(enumType.getName());
         modelType.setTypeGuid(enumType.getGuid());

         for (OseeEnumEntry entry : enumType.values()) {
            checkForCancelledStatus(monitor);
            org.eclipse.osee.framework.oseeTypes.OseeEnumEntry entryModelType = getFactory().createOseeEnumEntry();

            entryModelType.setName(entry.getName());
            entryModelType.setOrdinal(String.valueOf(entry.ordinal()));
            modelType.getEnumEntries().add(entryModelType);
         }
      }
      monitor.worked(calculateWork(workPercentage));
   }

   private void populateAttributeTypes(IProgressMonitor monitor, double workPercentage) throws OseeCoreException {
      monitor.setTaskName("Attribute Types");
      Collection<AttributeType> attributeTypes = cache.getAttributeTypeCache().getAll();
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
            org.eclipse.osee.framework.oseeTypes.OseeEnumType enumType = toModelEnumType(model, oseeEnumType);
            modelType.setEnumType(enumType);
         }
      }
      monitor.worked(calculateWork(workPercentage));
   }

   private org.eclipse.osee.framework.oseeTypes.OseeEnumType toModelEnumType(OseeTypeModel model, OseeEnumType oseeEnumType) {
      String guid = oseeEnumType.getGuid();
      for (org.eclipse.osee.framework.oseeTypes.OseeEnumType type : model.getEnumTypes()) {
         if (guid.equals(type.getTypeGuid())) {
            return type;
         }
      }
      return null;
   }

   private void populateArtifactTypes(IProgressMonitor monitor, double workPercentage) throws OseeCoreException {
      monitor.setTaskName("Artifact Types");
      Collection<ArtifactType> artifactTypes = cache.getArtifactTypeCache().getAll();
      for (ArtifactType artifactType : artifactTypes) {
         checkForCancelledStatus(monitor);
         org.eclipse.osee.framework.oseeTypes.ArtifactType modelType = getFactory().createArtifactType();

         OseeTypeModel model = getModelByNamespace(getNamespace(artifactType.getName()));
         model.getArtifactTypes().add(modelType);

         modelType.setName(artifactType.getName());
         modelType.setTypeGuid(artifactType.getGuid());

      }
      monitor.worked(calculateWork(workPercentage));
   }

   private void populateArtifactTypeInheritance(IProgressMonitor monitor, double workPercentage) throws OseeCoreException {
      monitor.setTaskName("Artifact Type Inheritance");
      Collection<ArtifactType> artifactTypes = cache.getArtifactTypeCache().getAll();
      for (ArtifactType artifactType : artifactTypes) {
         checkForCancelledStatus(monitor);
         OseeTypeModel model = getModelByNamespace(getNamespace(artifactType.getName()));

         org.eclipse.osee.framework.oseeTypes.ArtifactType childType = getArtifactType(model, artifactType.getGuid());

         for (ArtifactType oseeSuperType : artifactType.getSuperArtifactTypes()) {
            org.eclipse.osee.framework.oseeTypes.ArtifactType superModelType =
                  getArtifactType(model, oseeSuperType.getGuid());
            childType.getSuperArtifactTypes().add(superModelType);
         }
      }
      monitor.worked(calculateWork(workPercentage));
   }

   private void populateArtifactTypeAttributeTypes(IProgressMonitor monitor, double workPercentage) throws OseeCoreException {
      monitor.setTaskName("Artifact Type to Attribute Types");
      Collection<ArtifactType> artifactTypes = cache.getArtifactTypeCache().getAll();
      for (ArtifactType artifactType : artifactTypes) {
         checkForCancelledStatus(monitor);

         OseeTypeModel model = getModelByNamespace(getNamespace(artifactType.getName()));
         org.eclipse.osee.framework.oseeTypes.ArtifactType modelArtifactType =
               getArtifactType(model, artifactType.getGuid());

         Map<Branch, Collection<AttributeType>> types =
               cache.getArtifactTypeCache().getLocalAttributeTypes(artifactType);
         if (types != null) {
            List<AttributeTypeRef> references = new ArrayList<AttributeTypeRef>();
            for (Entry<Branch, Collection<AttributeType>> entry : types.entrySet()) {
               Branch branch = entry.getKey();
               Collection<AttributeType> attributeTypes = entry.getValue();
               if (attributeTypes != null) {
                  for (AttributeType attributeType : attributeTypes) {

                     AttributeTypeRef ref = getFactory().createAttributeTypeRef();

                     org.eclipse.osee.framework.oseeTypes.AttributeType modelType =
                           getAttributeType(model, attributeType.getGuid());
                     if (modelType != null) {
                        ref.setValidAttributeType(modelType);
                        if (branch != null && !branch.getBranchType().isSystemRootBranch()) {
                           ref.setBranchGuid(branch.getGuid());
                        }
                        references.add(ref);
                     }
                  }
               }
            }
            modelArtifactType.getValidAttributeTypes().addAll(references);
         }
      }
      monitor.worked(calculateWork(workPercentage));
   }

   private void populateRelationTypes(IProgressMonitor monitor, double workPercentage) throws OseeCoreException {
      monitor.setTaskName("Relation Types");
      Collection<RelationType> relationTypes = cache.getRelationTypeCache().getAll();
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
      monitor.worked(calculateWork(workPercentage));
   }

   private org.eclipse.osee.framework.oseeTypes.ArtifactType getArtifactType(OseeTypeModel model, String guid) throws OseeArgumentException {
      for (org.eclipse.osee.framework.oseeTypes.ArtifactType artifactType : model.getArtifactTypes()) {
         if (guid.equals(artifactType.getTypeGuid())) {
            return artifactType;
         }
      }
      return null;
   }

   private org.eclipse.osee.framework.oseeTypes.AttributeType getAttributeType(OseeTypeModel model, String guid) throws OseeArgumentException {
      for (org.eclipse.osee.framework.oseeTypes.AttributeType attributeType : model.getAttributeTypes()) {
         if (guid.equals(attributeType.getTypeGuid())) {
            return attributeType;
         }
      }
      return null;
   }

   private String getRelationOrderType(String guid) throws OseeArgumentException {
      RelationOrderBaseTypes type = RelationOrderBaseTypes.getFromGuid(guid);
      return type.prettyName().replaceAll(" ", "_");
   }

}
