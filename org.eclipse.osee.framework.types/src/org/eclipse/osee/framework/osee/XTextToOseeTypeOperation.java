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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.cache.AttributeTypeCache;
import org.eclipse.osee.framework.core.cache.BranchCache;
import org.eclipse.osee.framework.core.enums.RelationOrderBaseTypes;
import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.model.ArtifactType;
import org.eclipse.osee.framework.core.model.AttributeType;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.OseeEnumEntry;
import org.eclipse.osee.framework.core.model.OseeEnumType;
import org.eclipse.osee.framework.core.model.RelationType;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryService;
import org.eclipse.osee.framework.internal.InternalTypesActivator;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.oseeTypes.AddEnum;
import org.eclipse.osee.framework.oseeTypes.OseeTypeModel;
import org.eclipse.osee.framework.oseeTypes.OseeTypesFactory;
import org.eclipse.osee.framework.oseeTypes.OverrideOption;
import org.eclipse.osee.framework.oseeTypes.RemoveEnum;
import org.eclipse.osee.framework.oseeTypes.XArtifactType;
import org.eclipse.osee.framework.oseeTypes.XAttributeType;
import org.eclipse.osee.framework.oseeTypes.XAttributeTypeRef;
import org.eclipse.osee.framework.oseeTypes.XOseeEnumEntry;
import org.eclipse.osee.framework.oseeTypes.XOseeEnumOverride;
import org.eclipse.osee.framework.oseeTypes.XOseeEnumType;
import org.eclipse.osee.framework.oseeTypes.XRelationType;

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

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      double workAmount = 1.0;

      int workTotal = model.getArtifactTypes().size();
      workTotal += model.getAttributeTypes().size();
      workTotal += model.getRelationTypes().size();
      workTotal += model.getEnumTypes().size();
      workTotal += model.getEnumOverrides().size();

      if (workTotal > 0) {
         double workPercentage = workAmount / workTotal;

         for (XArtifactType xArtifactType : model.getArtifactTypes()) {
            translateXArtifactType(xArtifactType);
            monitor.worked(calculateWork(workPercentage));
         }

         for (XOseeEnumOverride xEnumOverride : model.getEnumOverrides()) {
            translateXEnumOverride(xEnumOverride);
            monitor.worked(calculateWork(workPercentage));
         }

         for (XOseeEnumType xEnumType : model.getEnumTypes()) {
            translateXEnumType(xEnumType);
            monitor.worked(calculateWork(workPercentage));
         }

         for (XAttributeType xAttributeType : model.getAttributeTypes()) {
            translateXAttributeType(xAttributeType);
            monitor.worked(calculateWork(workPercentage));
         }

         for (XArtifactType xArtifactType : model.getArtifactTypes()) {
            handleXArtifactTypeCrossRef(xArtifactType);
            monitor.worked(calculateWork(workPercentage));
         }

         for (XRelationType xRelationType : model.getRelationTypes()) {
            translateXRelationType(xRelationType);
            monitor.worked(calculateWork(workPercentage));
         }
      }
   }

   private void handleXArtifactTypeCrossRef(XArtifactType xArtifactType) throws OseeCoreException {
      ArtifactType targetArtifactType = typeCache.getArtifactTypeCache().getByGuid(xArtifactType.getTypeGuid());
      translateSuperTypes(targetArtifactType, xArtifactType);
      Map<Branch, Collection<AttributeType>> validAttributesPerBranch = getOseeAttributes(xArtifactType);
      targetArtifactType.setAllAttributeTypes(validAttributesPerBranch);
   }

   private void translateSuperTypes(ArtifactType targetArtifactType, XArtifactType xArtifactType) throws OseeCoreException {
      Set<ArtifactType> oseeSuperTypes = new HashSet<ArtifactType>();
      for (XArtifactType xSuperType : xArtifactType.getSuperArtifactTypes()) {
         String superTypeName = removeQuotes(xSuperType.getName());
         ArtifactType oseeSuperType = typeCache.getArtifactTypeCache().getUniqueByName(superTypeName);
         oseeSuperTypes.add(oseeSuperType);
      }

      if (!oseeSuperTypes.isEmpty()) {
         targetArtifactType.setSuperTypes(oseeSuperTypes);
      }
   }

   private Map<Branch, Collection<AttributeType>> getOseeAttributes(XArtifactType xArtifactType) throws OseeCoreException {
      Map<Branch, Collection<AttributeType>> validAttributes = new HashMap<Branch, Collection<AttributeType>>();
      for (XAttributeTypeRef xAttributeTypeRef : xArtifactType.getValidAttributeTypes()) {
         XAttributeType xAttributeType = xAttributeTypeRef.getValidAttributeType();
         Branch branch = getAttributeBranch(xAttributeTypeRef);
         AttributeType oseeAttributeType = typeCache.getAttributeTypeCache().getByGuid(xAttributeType.getTypeGuid());
         if (oseeAttributeType != null) {
            if (validAttributes.get(branch) == null) {
               validAttributes.put(branch, new HashSet<AttributeType>());
            }
            validAttributes.get(branch).add(oseeAttributeType);
         } else {
            System.out.println(String.format("Type was null for \"%s\"", xArtifactType.getName()));
         }
      }
      return validAttributes;
   }

   private Branch getAttributeBranch(XAttributeTypeRef xAttributeTypeRef) throws OseeCoreException {
      String branchGuid = xAttributeTypeRef.getBranchGuid();
      if (branchGuid == null) {
         return branchCache.getSystemRootBranch();
      } else {
         return branchCache.getByGuid(branchGuid);
      }
   }

   private String removeQuotes(String nameReference) {
      return nameReference != null ? nameReference.substring(1, nameReference.length() - 1) : nameReference;
   }

   private void translateXArtifactType(XArtifactType xArtifactType) throws OseeCoreException {
      String artifactTypeName = removeQuotes(xArtifactType.getName());

      ArtifactType oseeArtifactType =
            provider.getArtifactTypeFactory().createOrUpdate(typeCache.getArtifactTypeCache(),
                  xArtifactType.getTypeGuid(), xArtifactType.isAbstract(), artifactTypeName);
      xArtifactType.setTypeGuid(oseeArtifactType.getGuid());
   }

   private void translateXEnumType(XOseeEnumType xEnumType) throws OseeCoreException {
      String enumTypeName = removeQuotes(xEnumType.getName());

      OseeEnumType oseeEnumType =
            provider.getOseeEnumTypeFactory().createOrUpdate(typeCache.getEnumTypeCache(), xEnumType.getTypeGuid(),
                  enumTypeName);

      int lastOrdinal = 0;
      List<OseeEnumEntry> oseeEnumEntries = new ArrayList<OseeEnumEntry>();
      for (XOseeEnumEntry xEnumEntry : xEnumType.getEnumEntries()) {
         String entryName = removeQuotes(xEnumEntry.getName());
         String ordinal = xEnumEntry.getOrdinal();
         if (Strings.isValid(ordinal)) {
            lastOrdinal = Integer.parseInt(ordinal);
         }

         String entryGuid = xEnumEntry.getEntryGuid();
         oseeEnumEntries.add(provider.getOseeEnumTypeFactory().createEnumEntry(entryGuid, entryName, lastOrdinal));
         lastOrdinal++;
      }
      oseeEnumType.setEntries(oseeEnumEntries);
   }

   private void translateXEnumOverride(XOseeEnumOverride xEnumOverride) throws OseeCoreException {
      XOseeEnumType xEnumType = xEnumOverride.getOverridenEnumType();
      if (!xEnumOverride.isInheritAll()) {
         xEnumType.getEnumEntries().clear();
      }
      OseeTypesFactory factory = OseeTypesFactory.eINSTANCE;
      for (OverrideOption xOverrideOption : xEnumOverride.getOverrideOptions()) {
         if (xOverrideOption instanceof AddEnum) {
            String entryName = ((AddEnum) xOverrideOption).getEnumEntry();
            String entryGuid = ((AddEnum) xOverrideOption).getEntryGuid();
            XOseeEnumEntry xEnumEntry = factory.createXOseeEnumEntry();
            xEnumEntry.setName(entryName);
            xEnumEntry.setEntryGuid(entryGuid);
            xEnumType.getEnumEntries().add(xEnumEntry);
         } else if (xOverrideOption instanceof RemoveEnum) {
            XOseeEnumEntry enumEntry = ((RemoveEnum) xOverrideOption).getEnumEntry();
            xEnumType.getEnumEntries().remove(enumEntry);
         } else {
            throw new OseeStateException("Unsupported Override Operation");
         }
      }
   }

   private void translateXAttributeType(XAttributeType xAttributeType) throws OseeCoreException {
      int min = Integer.parseInt(xAttributeType.getMin());
      int max = Integer.MAX_VALUE;
      if (!xAttributeType.getMax().equals("unlimited")) {
         max = Integer.parseInt(xAttributeType.getMax());
      }
      XOseeEnumType xEnumType = xAttributeType.getEnumType();
      OseeEnumType oseeEnumType = null;
      if (xEnumType != null) {
         oseeEnumType = typeCache.getEnumTypeCache().getByGuid(xEnumType.getTypeGuid());
      }

      AttributeTypeCache cache = typeCache.getAttributeTypeCache();
      AttributeType oseeAttributeType = provider.getAttributeTypeFactory().createOrUpdate(cache, //
            xAttributeType.getTypeGuid(), //
            removeQuotes(xAttributeType.getName()), //
            getQualifiedTypeName(xAttributeType.getBaseAttributeType()), //
            getQualifiedTypeName(xAttributeType.getDataProvider()), //
            xAttributeType.getFileExtension(), //
            xAttributeType.getDefaultValue(), //
            oseeEnumType, //
            min, //
            max, //
            xAttributeType.getDescription(), //
            xAttributeType.getTaggerId()//
      );
      xAttributeType.setTypeGuid(oseeAttributeType.getGuid());
   }

   private String getQualifiedTypeName(String id) {
      String value = id;
      if (!value.contains(".")) {
         value = "org.eclipse.osee.framework.skynet.core." + id;
      }
      return value;
   }

   private void translateXRelationType(XRelationType xRelationType) throws OseeCoreException {
      RelationTypeMultiplicity multiplicity =
            RelationTypeMultiplicity.getFromString(xRelationType.getMultiplicity().name());

      String sideATypeName = removeQuotes(xRelationType.getSideAArtifactType().getName());
      String sideBTypeName = removeQuotes(xRelationType.getSideBArtifactType().getName());

      ArtifactType sideAType = typeCache.getArtifactTypeCache().getUniqueByName(sideATypeName);
      ArtifactType sideBType = typeCache.getArtifactTypeCache().getUniqueByName(sideBTypeName);

      RelationType oseeRelationType =
            provider.getRelationTypeFactory().createOrUpdate(typeCache.getRelationTypeCache(), //
                  xRelationType.getTypeGuid(), //
                  removeQuotes(xRelationType.getName()), //
                  xRelationType.getSideAName(), //
                  xRelationType.getSideBName(), //
                  sideAType, //
                  sideBType, //
                  multiplicity, //
                  convertOrderTypeNameToGuid(xRelationType.getDefaultOrderType()) //
            );

      xRelationType.setTypeGuid(oseeRelationType.getGuid());
   }

   private String convertOrderTypeNameToGuid(String orderTypeName) throws OseeArgumentException {
      return RelationOrderBaseTypes.getFromOrderTypeName(orderTypeName.replaceAll("_", " ")).getGuid();
   }

}
