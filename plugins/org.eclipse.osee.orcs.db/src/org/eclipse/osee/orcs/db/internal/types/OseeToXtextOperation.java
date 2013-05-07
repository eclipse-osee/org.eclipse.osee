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
package org.eclipse.osee.orcs.db.internal.types;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslFactory;
import org.eclipse.osee.framework.core.dsl.oseeDsl.RelationMultiplicityEnum;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactType;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeTypeRef;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeEnumEntry;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeEnumType;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationType;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.OseeEnumEntry;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.core.model.type.OseeEnumType;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.util.HexUtil;

/**
 * @author Roberto E. Escobar
 */
public class OseeToXtextOperation extends AbstractOperation {

   private final OseeDsl oseeModel;
   private final OseeDslFactory factory;
   private final OseeTypeCache cache;

   public OseeToXtextOperation(OseeTypeCache cache, OseeDslFactory factory, OseeDsl oseeModel) {
      super("OSEE to Text Model", "");
      this.oseeModel = oseeModel;
      this.factory = factory;
      this.cache = cache;
   }

   private OseeDslFactory getFactory() {
      return factory;
   }

   private OseeDsl getModelByNamespace(String namespace) {
      return oseeModel;
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
         XOseeEnumType modelType = getFactory().createXOseeEnumType();

         OseeDsl model = getModelByNamespace(getNamespace(enumType.getName()));
         model.getEnumTypes().add(modelType);

         modelType.setName(enumType.getName());
         modelType.setUuid(HexUtil.toString(enumType.getGuid()));

         for (OseeEnumEntry entry : enumType.values()) {
            checkForCancelledStatus(monitor);
            XOseeEnumEntry entryModelType = getFactory().createXOseeEnumEntry();
            modelType.getEnumEntries().add(entryModelType);

            entryModelType.setName(entry.getName());
            entryModelType.setOrdinal(String.valueOf(entry.ordinal()));
         }
      }
      monitor.worked(calculateWork(workPercentage));
   }

   private void populateAttributeTypes(IProgressMonitor monitor, double workPercentage) throws OseeCoreException {
      monitor.setTaskName("Attribute Types");
      Collection<AttributeType> attributeTypes = cache.getAttributeTypeCache().getAll();
      for (AttributeType attributeType : attributeTypes) {
         checkForCancelledStatus(monitor);
         XAttributeType modelType = getFactory().createXAttributeType();

         OseeDsl model = getModelByNamespace(getNamespace(attributeType.getName()));
         model.getAttributeTypes().add(modelType);

         modelType.setName(attributeType.getName());

         modelType.setUuid(HexUtil.toString(attributeType.getGuid()));

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
            XOseeEnumType enumType = toModelEnumType(model, oseeEnumType.getGuid());
            modelType.setEnumType(enumType);
         }
      }
      monitor.worked(calculateWork(workPercentage));
   }

   private void populateArtifactTypes(IProgressMonitor monitor, double workPercentage) throws OseeCoreException {
      monitor.setTaskName("Artifact Types");
      Collection<ArtifactType> artifactTypes = cache.getArtifactTypeCache().getAll();
      for (ArtifactType artifactType : artifactTypes) {
         checkForCancelledStatus(monitor);
         XArtifactType modelType = getFactory().createXArtifactType();

         OseeDsl model = getModelByNamespace(getNamespace(artifactType.getName()));
         model.getArtifactTypes().add(modelType);

         modelType.setName(artifactType.getName());
         modelType.setUuid(HexUtil.toString(artifactType.getGuid()));
      }
      monitor.worked(calculateWork(workPercentage));
   }

   private void populateArtifactTypeInheritance(IProgressMonitor monitor, double workPercentage) throws OseeCoreException {
      monitor.setTaskName("Artifact Type Inheritance");
      Collection<ArtifactType> artifactTypes = cache.getArtifactTypeCache().getAll();
      for (ArtifactType artifactType : artifactTypes) {
         checkForCancelledStatus(monitor);
         OseeDsl model = getModelByNamespace(getNamespace(artifactType.getName()));

         XArtifactType childType = getArtifactType(model, artifactType.getGuid());

         for (ArtifactType oseeSuperType : artifactType.getSuperArtifactTypes()) {
            XArtifactType superModelType = getArtifactType(model, oseeSuperType.getGuid());
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

         OseeDsl model = getModelByNamespace(getNamespace(artifactType.getName()));
         XArtifactType modelArtifactType = getArtifactType(model, artifactType.getGuid());

         Map<IOseeBranch, Collection<AttributeType>> types = artifactType.getLocalAttributeTypes();
         if (types != null) {
            List<XAttributeTypeRef> references = new ArrayList<XAttributeTypeRef>();
            for (Entry<IOseeBranch, Collection<AttributeType>> entry : types.entrySet()) {
               IOseeBranch branch = entry.getKey();
               Collection<AttributeType> attributeTypes = entry.getValue();
               if (attributeTypes != null) {
                  for (AttributeType attributeType : attributeTypes) {

                     XAttributeTypeRef ref = getFactory().createXAttributeTypeRef();

                     XAttributeType modelType = getAttributeType(model, attributeType.getGuid());
                     if (modelType != null) {
                        ref.setValidAttributeType(modelType);
                        if (branch != null && !branch.equals(CoreBranches.SYSTEM_ROOT)) {
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
         XRelationType modelType = getFactory().createXRelationType();

         OseeDsl model = getModelByNamespace(getNamespace(relationType.getName()));
         model.getRelationTypes().add(modelType);

         modelType.setName(relationType.getName());
         modelType.setUuid(HexUtil.toString(relationType.getGuid()));

         modelType.setDefaultOrderType(OseeUtil.getRelationOrderType(relationType.getDefaultOrderTypeGuid()));
         modelType.setMultiplicity(RelationMultiplicityEnum.getByName(relationType.getMultiplicity().name()));

         modelType.setSideAName(relationType.getSideAName());
         modelType.setSideBName(relationType.getSideBName());

         modelType.setSideAArtifactType(getArtifactType(model, relationType.getArtifactTypeSideA().getGuid()));
         modelType.setSideBArtifactType(getArtifactType(model, relationType.getArtifactTypeSideB().getGuid()));
      }
      monitor.worked(calculateWork(workPercentage));
   }

   private XArtifactType getArtifactType(OseeDsl model, Long uuid) throws OseeCoreException {
      for (XArtifactType type : model.getArtifactTypes()) {
         Long normalizedGuid = HexUtil.toLong(type.getUuid());
         if (uuid.equals(normalizedGuid)) {
            return type;
         }
      }
      return null;
   }

   private XAttributeType getAttributeType(OseeDsl model, Long uuid) throws OseeCoreException {
      for (XAttributeType type : model.getAttributeTypes()) {
         Long normalizedGuid = HexUtil.toLong(type.getUuid());
         if (uuid.equals(normalizedGuid)) {
            return type;
         }
      }
      return null;
   }

   private XOseeEnumType toModelEnumType(OseeDsl model, Long uuid) throws OseeCoreException {
      for (XOseeEnumType type : model.getEnumTypes()) {
         Long normalizedGuid = HexUtil.toLong(type.getUuid());
         if (uuid.equals(normalizedGuid)) {
            return type;
         }
      }
      return null;
   }

}
