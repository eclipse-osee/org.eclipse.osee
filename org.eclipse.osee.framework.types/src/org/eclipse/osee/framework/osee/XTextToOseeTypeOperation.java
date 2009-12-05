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
package org.eclipse.osee.framework.osee;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.cache.AttributeTypeCache;
import org.eclipse.osee.framework.core.cache.BranchCache;
import org.eclipse.osee.framework.core.enums.RelationOrderBaseTypes;
import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryService;
import org.eclipse.osee.framework.internal.InternalTypesActivator;
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

/**
 * @author Ryan D. Brooks
 * @author Roberto E. Escobar
 */
public class XTextToOseeTypeOperation extends AbstractOperation {
   private final IOseeModelFactoryService provider;
   private final OseeTypeModel model;
   private final OseeTypeCache typeCache;
   private final BranchCache branchCache;

   public XTextToOseeTypeOperation(IOseeModelFactoryService provider, OseeTypeCache typeCache, BranchCache branchCache, OseeTypeModel model) {
      super("OSEE Text Model to OSEE", InternalTypesActivator.PLUGIN_ID);
      this.provider = provider;
      this.typeCache = typeCache;
      this.branchCache = branchCache;
      this.model = model;
   }

   private OseeTypeCache getCache() {
      return typeCache;
   }

   private BranchCache getBranchCache() {
      return branchCache;
   }

   private IOseeModelFactoryService getFactory() throws OseeCoreException {
      return provider;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      double workAmount = 1.0;

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

   private void handleArtifactTypeCrossRef(ArtifactType artifactType) throws OseeCoreException {
      Set<org.eclipse.osee.framework.core.model.ArtifactType> superTypes =
            new HashSet<org.eclipse.osee.framework.core.model.ArtifactType>();

      org.eclipse.osee.framework.core.model.ArtifactType targetArtifactType =
            getCache().getArtifactTypeCache().getByGuid(artifactType.getTypeGuid());

      for (ArtifactType superType : artifactType.getSuperArtifactTypes()) {
         superTypes.add(getCache().getArtifactTypeCache().getUniqueByName(removeQuotes(superType.getName())));
      }
      if (!superTypes.isEmpty()) {
         targetArtifactType.setSuperType(superTypes);
      }

      BranchCache branchCache = getBranchCache();
      Branch systemRoot = branchCache.getSystemRootBranch();
      HashCollection<Branch, org.eclipse.osee.framework.core.model.AttributeType> items =
            new HashCollection<Branch, org.eclipse.osee.framework.core.model.AttributeType>(false, HashSet.class);
      for (AttributeTypeRef attributeTypeRef : artifactType.getValidAttributeTypes()) {
         AttributeType attributeType = attributeTypeRef.getValidAttributeType();
         //         handleAttributeType(attributeType);
         Branch branch;
         String branchGuid = attributeTypeRef.getBranchGuid();
         if (branchGuid == null) {
            branch = systemRoot;
         } else {
            branch = branchCache.getByGuid(branchGuid);
         }
         org.eclipse.osee.framework.core.model.AttributeType type =
               getCache().getAttributeTypeCache().getByGuid(attributeType.getTypeGuid());
         if (type != null) {
            items.put(branch, type);
         } else {
            System.out.println("Type was null for: " + artifactType.getName());
         }
      }

      for (Branch branch : items.keySet()) {
         List<org.eclipse.osee.framework.core.model.AttributeType> attributeTypes = items.getValues();
         if (attributeTypes != null) {
            targetArtifactType.setAttributeTypes(attributeTypes, branch);
         } else {
            System.out.println("Types were null for: " + artifactType.getName());
         }
      }
   }

   private String removeQuotes(String nameReference) {
      return nameReference != null ? nameReference.substring(1, nameReference.length() - 1) : nameReference; // strip off enclosing quotes
   }

   private void handleArtifactType(ArtifactType artifactType) throws OseeCoreException {
      String artifactTypeName = removeQuotes(artifactType.getName());

      org.eclipse.osee.framework.core.model.ArtifactType type =
            getFactory().getArtifactTypeFactory().createOrUpdate(getCache().getArtifactTypeCache(),
                  artifactType.getTypeGuid(), artifactType.isAbstract(), artifactTypeName);
      artifactType.setTypeGuid(type.getGuid());
   }

   private void handleOseeEnumType(OseeEnumType modelEnumType) throws OseeCoreException {
      String enumTypeName = removeQuotes(modelEnumType.getName());

      org.eclipse.osee.framework.core.model.OseeEnumType oseeEnumType =
            getFactory().getOseeEnumTypeFactory().createOrUpdate(getCache().getEnumTypeCache(),
                  modelEnumType.getTypeGuid(), enumTypeName);

      int lastOrdinal = 0;
      List<org.eclipse.osee.framework.core.model.OseeEnumEntry> modelEntries =
            new ArrayList<org.eclipse.osee.framework.core.model.OseeEnumEntry>();
      for (OseeEnumEntry enumEntry : modelEnumType.getEnumEntries()) {
         String entryName = removeQuotes(enumEntry.getName());
         String ordinal = enumEntry.getOrdinal();
         if (Strings.isValid(ordinal)) {
            lastOrdinal = Integer.parseInt(ordinal);
         }

         String entryGuid = enumEntry.getEntryGuid();
         modelEntries.add(getFactory().getOseeEnumTypeFactory().createEnumEntry(entryGuid, entryName, lastOrdinal));
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
            String entryGuid = ((AddEnum) overrideOption).getEntryGuid();
            OseeEnumEntry enumEntry = factory.createOseeEnumEntry();
            enumEntry.setName(entryName);
            enumEntry.setEntryGuid(entryGuid);
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
      org.eclipse.osee.framework.core.model.OseeEnumType oseeEnumType = null;
      OseeEnumType enumType = attributeType.getEnumType();
      if (enumType != null) {
         oseeEnumType = getCache().getEnumTypeCache().getByGuid(enumType.getTypeGuid());
      }

      AttributeTypeCache cache = getCache().getAttributeTypeCache();
      org.eclipse.osee.framework.core.model.AttributeType type =
            getFactory().getAttributeTypeFactory().createOrUpdate(cache, attributeType.getTypeGuid(), //
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
            );
      attributeType.setTypeGuid(type.getGuid());
   }

   private void handleRelationType(RelationType relationType) throws OseeCoreException {
      RelationTypeMultiplicity multiplicity =
            RelationTypeMultiplicity.getFromString(relationType.getMultiplicity().name());

      org.eclipse.osee.framework.core.model.RelationType type =
            getFactory().getRelationTypeFactory().createOrUpdate(
                  getCache().getRelationTypeCache(),
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
            );

      relationType.setTypeGuid(//
      type.getGuid());
   }

   private String convertOrderTypeNameToGuid(String orderTypeName) throws OseeArgumentException {
      return RelationOrderBaseTypes.getFromOrderTypeName(orderTypeName.replaceAll("_", " ")).getGuid();
   }

}
