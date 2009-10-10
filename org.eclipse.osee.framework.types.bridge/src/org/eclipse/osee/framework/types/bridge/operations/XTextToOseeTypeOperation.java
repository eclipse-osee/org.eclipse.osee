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

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.oseeTypes.AddEnum;
import org.eclipse.osee.framework.oseeTypes.ArtifactType;
import org.eclipse.osee.framework.oseeTypes.AttributeType;
import org.eclipse.osee.framework.oseeTypes.AttributeTypeRef;
import org.eclipse.osee.framework.oseeTypes.OseeEnumEntry;
import org.eclipse.osee.framework.oseeTypes.OseeEnumOverride;
import org.eclipse.osee.framework.oseeTypes.OseeEnumType;
import org.eclipse.osee.framework.oseeTypes.OseeTypeModel;
import org.eclipse.osee.framework.oseeTypes.OseeTypesFactory;
import org.eclipse.osee.framework.oseeTypes.OverrideOption;
import org.eclipse.osee.framework.oseeTypes.RelationType;
import org.eclipse.osee.framework.oseeTypes.RemoveEnum;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.relation.order.RelationOrderBaseTypes;
import org.eclipse.osee.framework.skynet.core.types.OseeTypeCache;
import org.eclipse.osee.framework.types.bridge.internal.Activator;

/**
 * @author Ryan D. Brooks
 * @author Roberto E. Escobar
 */
public class XTextToOseeTypeOperation extends AbstractOperation {
   private final java.net.URI resource;
   private final Object context;
   private final OseeTypeCache typeCache;
   private final boolean isPersistAllowed;

   public XTextToOseeTypeOperation(OseeTypeCache typeCache, boolean isPersistAllowed, Object context, java.net.URI resource) {
      super("OSEE Text Model to OSEE", Activator.PLUGIN_ID);
      this.typeCache = typeCache;
      this.resource = resource;
      this.context = context;
      this.isPersistAllowed = isPersistAllowed;
   }

   private OseeTypeCache getCache() {
      return typeCache;
   }

   private void loadDependencies(OseeTypeModel baseModel, List<OseeTypeModel> models) throws OseeCoreException, URISyntaxException {
      //      for (Import dependant : baseModel.getImports()) {
      //         OseeTypeModel childModel = OseeTypeModelUtil.loadModel(context, new URI(depenant.getImportURI()));
      //         loadDependencies(childModel, models);
      //         //         System.out.println("depends on: " + depenant.getImportURI());
      //      }
      //      System.out.println("Added on: " + baseModel.eResource().getURI());
      models.add(baseModel);
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      List<OseeTypeModel> models = new ArrayList<OseeTypeModel>();
      OseeTypeModel targetModel = null;
      try {
         targetModel = OseeTypeModelUtil.loadModel(context, resource);
      } catch (OseeCoreException ex) {
         throw new OseeWrappedException(String.format("Error loading: [%s]", resource), ex);
      }
      loadDependencies(targetModel, models);

      if (!models.isEmpty()) {
         double workAmount = 1.0 / models.size();
         for (OseeTypeModel model : models) {

            int count = model.getArtifactTypes().size();
            count += model.getAttributeTypes().size();
            count += model.getRelationTypes().size();
            count += model.getEnumTypes().size();
            count += model.getEnumOverrides().size();

            if (count > 0) {
               double workPercentage = workAmount / count;

               for (ArtifactType type : model.getArtifactTypes()) {
                  handleArtifactType(type);
                  monitor.worked(calculateWork(workPercentage));
               }

               for (OseeEnumOverride enumOverride : model.getEnumOverrides()) {
                  handleEnumOverride(enumOverride);
                  monitor.worked(calculateWork(workPercentage));
               }

               for (OseeEnumType enumType : model.getEnumTypes()) {
                  handleOseeEnumType(enumType);
                  monitor.worked(calculateWork(workPercentage));
               }

               for (AttributeType type : model.getAttributeTypes()) {
                  handleAttributeType(type);
                  monitor.worked(calculateWork(workPercentage));
               }

               for (ArtifactType type : model.getArtifactTypes()) {
                  handleArtifactTypeCrossRef(type);
                  monitor.worked(calculateWork(workPercentage));
               }

               for (RelationType type : model.getRelationTypes()) {
                  handleRelationType(type);
                  monitor.worked(calculateWork(workPercentage));
               }
            }
         }
         if (isPersistAllowed) {
            getCache().storeAllModified();
         }
      }
   }

   /**
    * @param type
    * @throws OseeCoreException
    */
   private void handleArtifactTypeCrossRef(ArtifactType artifactType) throws OseeCoreException {
      Set<org.eclipse.osee.framework.skynet.core.artifact.ArtifactType> superTypes =
            new HashSet<org.eclipse.osee.framework.skynet.core.artifact.ArtifactType>();
      org.eclipse.osee.framework.skynet.core.artifact.ArtifactType targetArtifactType =
            getCache().getArtifactTypeCache().getByGuid(artifactType.getTypeGuid());

      for (ArtifactType superType : artifactType.getSuperArtifactTypes()) {
         superTypes.add(getCache().getArtifactTypeCache().getUniqueByName(removeQuotes(superType.getName())));
      }
      if (!superTypes.isEmpty()) {
         targetArtifactType.setSuperType(superTypes);
      }
      HashCollection<Branch, org.eclipse.osee.framework.skynet.core.attribute.AttributeType> items =
            new HashCollection<Branch, org.eclipse.osee.framework.skynet.core.attribute.AttributeType>();
      for (AttributeTypeRef attributeTypeRef : artifactType.getValidAttributeTypes()) {
         AttributeType attributeType = attributeTypeRef.getValidAttributeType();
         //         handleAttributeType(attributeType);
         Branch branch;
         String branchGuid = attributeTypeRef.getBranchGuid();
         if (branchGuid == null) {
            branch = BranchManager.getSystemRootBranch();
         } else {
            branch = BranchManager.getBranchByGuid(branchGuid);
         }
         items.put(branch, getCache().getAttributeTypeCache().getByGuid(attributeType.getTypeGuid()));
      }

      for (Branch branch : items.keySet()) {
         targetArtifactType.setAttributeTypeValidity(items.getValues(), branch);
      }
   }

   private String removeQuotes(String nameReference) {
      return nameReference != null ? nameReference.substring(1, nameReference.length() - 1) : nameReference; // strip off enclosing quotes
   }

   private void handleArtifactType(ArtifactType artifactType) throws OseeCoreException {
      String artifactTypeName = removeQuotes(artifactType.getName());
      artifactType.setTypeGuid(getCache().getArtifactTypeCache().createType(artifactType.getTypeGuid(),
            artifactType.isAbstract(), artifactTypeName).getGuid());
   }

   private void handleOseeEnumType(OseeEnumType modelEnumType) throws OseeCoreException {
      String enumTypeName = removeQuotes(modelEnumType.getName());

      org.eclipse.osee.framework.skynet.core.attribute.OseeEnumType oseeEnumType =
            getCache().getEnumTypeCache().createType(modelEnumType.getTypeGuid(), enumTypeName);

      int lastOrdinal = 0;
      List<org.eclipse.osee.framework.skynet.core.attribute.OseeEnumEntry> modelEntries =
            new ArrayList<org.eclipse.osee.framework.skynet.core.attribute.OseeEnumEntry>();
      for (OseeEnumEntry enumEntry : modelEnumType.getEnumEntries()) {
         String entryName = removeQuotes(enumEntry.getName());
         String ordinal = enumEntry.getOrdinal();
         if (Strings.isValid(ordinal)) {
            lastOrdinal = Integer.parseInt(ordinal);
         }
         // enumEntry guid set to null but if we had we could modify an existing entry
         modelEntries.add(getCache().getEnumTypeCache().createEntry(null, entryName, lastOrdinal));
         lastOrdinal++;
      }
      oseeEnumType.setEntries(modelEntries);
   }

   private void handleEnumOverride(OseeEnumOverride enumOverride) throws OseeCoreException {
      OseeEnumType oseeEnumType = enumOverride.getOverridenEnumType();
      if (!enumOverride.isInheritAll()) {
         oseeEnumType.getEnumEntries().clear();
      }
      OseeTypesFactory factory = OseeTypesFactory.eINSTANCE;
      for (OverrideOption overrideOption : enumOverride.getOverrideOptions()) {
         if (overrideOption instanceof AddEnum) {
            String entryName = ((AddEnum) overrideOption).getEnumEntry();
            OseeEnumEntry enumEntry = factory.createOseeEnumEntry();
            enumEntry.setName(entryName);
            oseeEnumType.getEnumEntries().add(enumEntry);
         } else if (overrideOption instanceof RemoveEnum) {
            OseeEnumEntry enumEntry = ((RemoveEnum) overrideOption).getEnumEntry();
            oseeEnumType.getEnumEntries().remove(enumEntry);
         } else {
            throw new OseeStateException("Unsupported Override Operation");
         }
      }
   }

   private void handleAttributeType(AttributeType attributeType) throws OseeCoreException {
      int max = Integer.MAX_VALUE;
      if (!attributeType.getMax().equals("unlimited")) {
         max = Integer.parseInt(attributeType.getMax());
      }
      org.eclipse.osee.framework.skynet.core.attribute.OseeEnumType oseeEnumType = null;
      OseeEnumType enumType = attributeType.getEnumType();
      if (enumType != null) {
         oseeEnumType = getCache().getEnumTypeCache().getByGuid(enumType.getTypeGuid());
      }
      attributeType.setTypeGuid(getCache().getAttributeTypeCache().createType(attributeType.getTypeGuid(), //
            removeQuotes(attributeType.getName()), //
            attributeType.getBaseAttributeType(), // 
            attributeType.getDataProvider(), // 
            attributeType.getFileExtension(), //
            attributeType.getDefaultValue(), //
            oseeEnumType, //
            Integer.parseInt(attributeType.getMin()), //
            max, //
            attributeType.getDescription(), //
            attributeType.getTaggerId()//
      ).getGuid());
   }

   private void handleRelationType(RelationType relationType) throws OseeCoreException {
      RelationTypeMultiplicity multiplicity =
            RelationTypeMultiplicity.getFromString(relationType.getMultiplicity().name());

      relationType.setTypeGuid(//
      getCache().getRelationTypeCache().createType(
            relationType.getTypeGuid(),
            removeQuotes(relationType.getName()), //
            relationType.getSideAName(), //
            relationType.getSideBName(), //
            getCache().getArtifactTypeCache().getUniqueByName(
                  removeQuotes(relationType.getSideAArtifactType().getName())), //
            getCache().getArtifactTypeCache().getUniqueByName(
                  removeQuotes(relationType.getSideBArtifactType().getName())), //
            multiplicity, //
            convertOrderTypeNameToGuid(relationType.getDefaultOrderType())//
      ).getGuid());
   }

   private String convertOrderTypeNameToGuid(String orderTypeName) throws OseeArgumentException {
      return RelationOrderBaseTypes.getFromOrderTypeName(orderTypeName.replaceAll("_", " ")).getGuid();
   }
}
