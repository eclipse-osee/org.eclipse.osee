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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.dsl.oseeDsl.AddAttribute;
import org.eclipse.osee.framework.core.dsl.oseeDsl.AddEnum;
import org.eclipse.osee.framework.core.dsl.oseeDsl.AttributeOverrideOption;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslFactory;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OverrideOption;
import org.eclipse.osee.framework.core.dsl.oseeDsl.RemoveAttribute;
import org.eclipse.osee.framework.core.dsl.oseeDsl.RemoveEnum;
import org.eclipse.osee.framework.core.dsl.oseeDsl.UpdateAttribute;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactType;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeTypeRef;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeArtifactTypeOverride;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeEnumEntry;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeEnumOverride;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeEnumType;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationType;
import org.eclipse.osee.framework.core.dsl.oseeDsl.util.OseeDslSwitch;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.OseeEnumEntry;
import org.eclipse.osee.framework.core.model.cache.AttributeTypeCache;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.core.model.type.OseeEnumType;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryService;
import org.eclipse.osee.framework.core.util.HexUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Ryan D. Brooks
 * @author Roberto E. Escobar
 */
public class XTextToOseeTypeOperation extends AbstractOperation {
   private final IOseeModelFactoryService provider;
   private final OseeDsl model;
   private final OseeTypeCache typeCache;
   private final BranchCache branchCache;

   public XTextToOseeTypeOperation(IOseeModelFactoryService provider, OseeTypeCache typeCache, BranchCache branchCache, OseeDsl model) {
      super("OSEE Text Model to OSEE", "");
      this.provider = provider;
      this.typeCache = typeCache;
      this.branchCache = branchCache;
      this.model = model;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      double workAmount = 1.0;

      int workTotal = model.getArtifactTypes().size();
      workTotal += model.getArtifactTypeOverrides().size();
      workTotal += model.getAttributeTypes().size();
      workTotal += model.getRelationTypes().size();
      workTotal += model.getEnumTypes().size();
      workTotal += model.getEnumOverrides().size();

      if (workTotal > 0) {
         int amount = calculateWork(workAmount / workTotal);

         for (XOseeArtifactTypeOverride xArtifactTypeOverride : model.getArtifactTypeOverrides()) {
            translateXArtifactTypeOverride(xArtifactTypeOverride);
            monitor.worked(amount);
         }

         for (XArtifactType xArtifactType : model.getArtifactTypes()) {
            translateXArtifactType(xArtifactType);
            monitor.worked(amount);
         }

         for (XOseeEnumOverride xEnumOverride : model.getEnumOverrides()) {
            translateXEnumOverride(xEnumOverride);
            monitor.worked(amount);
         }

         for (XOseeEnumType xEnumType : model.getEnumTypes()) {
            translateXEnumType(xEnumType);
            monitor.worked(amount);
         }

         for (XAttributeType xAttributeType : model.getAttributeTypes()) {
            translateXAttributeType(xAttributeType);
            monitor.worked(amount);
         }

         for (XArtifactType xArtifactType : model.getArtifactTypes()) {
            handleXArtifactTypeCrossRef(xArtifactType);
            monitor.worked(amount);
         }

         for (XRelationType xRelationType : model.getRelationTypes()) {
            translateXRelationType(xRelationType);
            monitor.worked(amount);
         }
      }
   }

   private void handleXArtifactTypeCrossRef(XArtifactType xArtifactType) throws OseeCoreException {
      ArtifactType targetArtifactType =
         typeCache.getArtifactTypeCache().getByGuid(HexUtil.toLong(xArtifactType.getUuid()));
      translateSuperTypes(targetArtifactType, xArtifactType);
      Map<IOseeBranch, Collection<AttributeType>> validAttributesPerBranch = getOseeAttributes(xArtifactType);
      targetArtifactType.setAllAttributeTypes(validAttributesPerBranch);
   }

   private void translateSuperTypes(ArtifactType targetArtifactType, XArtifactType xArtifactType) throws OseeCoreException {
      Set<ArtifactType> oseeSuperTypes = new HashSet<ArtifactType>();
      for (XArtifactType xSuperType : xArtifactType.getSuperArtifactTypes()) {
         String superTypeName = xSuperType.getName();
         ArtifactType oseeSuperType = typeCache.getArtifactTypeCache().getUniqueByName(superTypeName);
         oseeSuperTypes.add(oseeSuperType);
      }

      if (!oseeSuperTypes.isEmpty()) {
         targetArtifactType.setSuperTypes(oseeSuperTypes);
      }
   }

   private Map<IOseeBranch, Collection<AttributeType>> getOseeAttributes(XArtifactType xArtifactType) throws OseeCoreException {
      Map<IOseeBranch, Collection<AttributeType>> validAttributes =
         new HashMap<IOseeBranch, Collection<AttributeType>>();
      for (XAttributeTypeRef xAttributeTypeRef : xArtifactType.getValidAttributeTypes()) {
         XAttributeType xAttributeType = xAttributeTypeRef.getValidAttributeType();
         IOseeBranch branch = getAttributeBranch(xAttributeTypeRef);
         AttributeType oseeAttributeType =
            typeCache.getAttributeTypeCache().getByGuid(HexUtil.toLong(xAttributeType.getUuid()));
         if (oseeAttributeType != null) {
            Collection<AttributeType> listOfAllowedAttributes = validAttributes.get(branch);
            if (listOfAllowedAttributes == null) {
               listOfAllowedAttributes = new HashSet<AttributeType>();
               validAttributes.put(branch, listOfAllowedAttributes);
            }
            listOfAllowedAttributes.add(oseeAttributeType);
         } else {
            System.out.println(String.format("Type was null for \"%s\"", xArtifactType.getName()));
         }
      }
      return validAttributes;
   }

   private IOseeBranch getAttributeBranch(XAttributeTypeRef xAttributeTypeRef) throws OseeCoreException {
      String branchGuid = xAttributeTypeRef.getBranchGuid();
      if (branchGuid == null) {
         return CoreBranches.SYSTEM_ROOT;
      } else {
         IOseeBranch branch = branchCache.getByGuid(branchGuid);
         if (branch == null) {
            branch = TokenFactory.createBranch(branchGuid, branchGuid);
         }
         return branch;
      }
   }

   private void translateXArtifactTypeOverride(XOseeArtifactTypeOverride xArtTypeOverride) {
      XArtifactType xArtifactType = xArtTypeOverride.getOverridenArtifactType();
      final EList<XAttributeTypeRef> validAttributeTypes = xArtifactType.getValidAttributeTypes();
      if (!xArtTypeOverride.isInheritAll()) {
         validAttributeTypes.clear();
      }

      OseeDslSwitch<Void> overrideVisitor = new OseeDslSwitch<Void>() {

         @Override
         public Void caseAddAttribute(AddAttribute addOption) {
            XAttributeTypeRef attributeRef = addOption.getAttribute();
            validAttributeTypes.add(attributeRef);
            return super.caseAddAttribute(addOption);
         }

         @Override
         public Void caseRemoveAttribute(RemoveAttribute removeOption) {
            XAttributeType attribute = removeOption.getAttribute();
            String guidToMatch = attribute.getUuid();
            List<XAttributeTypeRef> toRemove = new LinkedList<XAttributeTypeRef>();
            for (XAttributeTypeRef xAttributeTypeRef : validAttributeTypes) {
               String itemGuid = xAttributeTypeRef.getValidAttributeType().getUuid();
               if (guidToMatch.equals(itemGuid)) {
                  toRemove.add(xAttributeTypeRef);
               }
            }
            validAttributeTypes.removeAll(toRemove);
            return super.caseRemoveAttribute(removeOption);
         }

         @Override
         public Void caseUpdateAttribute(UpdateAttribute updateAttribute) {
            XAttributeTypeRef refToUpdate = updateAttribute.getAttribute();
            String guidToMatch = refToUpdate.getValidAttributeType().getUuid();
            List<XAttributeTypeRef> toRemove = new LinkedList<XAttributeTypeRef>();
            for (XAttributeTypeRef xAttributeTypeRef : validAttributeTypes) {
               String itemGuid = xAttributeTypeRef.getValidAttributeType().getUuid();
               if (guidToMatch.equals(itemGuid)) {
                  toRemove.add(xAttributeTypeRef);
               }
            }
            validAttributeTypes.removeAll(toRemove);
            validAttributeTypes.add(refToUpdate);
            return super.caseUpdateAttribute(updateAttribute);
         }

      };

      for (AttributeOverrideOption xOverrideOption : xArtTypeOverride.getOverrideOptions()) {
         overrideVisitor.doSwitch(xOverrideOption);
      }
   }

   private void translateXArtifactType(XArtifactType xArtifactType) throws OseeCoreException {
      String artifactTypeName = xArtifactType.getName();

      provider.getArtifactTypeFactory().createOrUpdate(typeCache.getArtifactTypeCache(),
         HexUtil.toLong(xArtifactType.getUuid()), xArtifactType.isAbstract(), artifactTypeName);
   }

   private void translateXEnumType(XOseeEnumType xEnumType) throws OseeCoreException {
      String enumTypeName = xEnumType.getName();

      OseeEnumType oseeEnumType =
         provider.getOseeEnumTypeFactory().createOrUpdate(typeCache.getEnumTypeCache(),
            HexUtil.toLong(xEnumType.getUuid()), enumTypeName);

      int lastOrdinal = 0;
      List<OseeEnumEntry> oseeEnumEntries = new ArrayList<OseeEnumEntry>();
      for (XOseeEnumEntry xEnumEntry : xEnumType.getEnumEntries()) {
         String entryName = xEnumEntry.getName();
         String ordinal = xEnumEntry.getOrdinal();
         if (Strings.isValid(ordinal)) {
            lastOrdinal = Integer.parseInt(ordinal);
         }

         String entryGuid = xEnumEntry.getEntryGuid();
         oseeEnumEntries.add(provider.getOseeEnumTypeFactory().createEnumEntry(entryGuid, entryName, lastOrdinal,
            xEnumEntry.getDescription()));
         lastOrdinal++;
      }
      oseeEnumType.setEntries(oseeEnumEntries);
   }

   private void translateXEnumOverride(XOseeEnumOverride xEnumOverride) {
      XOseeEnumType xEnumType = xEnumOverride.getOverridenEnumType();
      final EList<XOseeEnumEntry> enumEntries = xEnumType.getEnumEntries();
      if (!xEnumOverride.isInheritAll()) {
         enumEntries.clear();
      }

      OseeDslSwitch<Void> overrideVisitor = new OseeDslSwitch<Void>() {

         @Override
         public Void caseAddEnum(AddEnum addEnum) {
            String entryName = addEnum.getEnumEntry();
            String entryGuid = addEnum.getEntryGuid();
            String description = addEnum.getDescription();
            XOseeEnumEntry xEnumEntry = OseeDslFactory.eINSTANCE.createXOseeEnumEntry();
            xEnumEntry.setName(entryName);
            xEnumEntry.setEntryGuid(entryGuid);
            xEnumEntry.setDescription(description);
            enumEntries.add(xEnumEntry);
            return super.caseAddEnum(addEnum);
         }

         @Override
         public Void caseRemoveEnum(RemoveEnum removeEnum) {
            XOseeEnumEntry enumEntry = removeEnum.getEnumEntry();
            String guidToMatch = enumEntry.getEntryGuid();
            List<XOseeEnumEntry> toRemove = new LinkedList<XOseeEnumEntry>();
            for (XOseeEnumEntry item : enumEntries) {
               String itemGuid = item.getEntryGuid();
               if (guidToMatch.equals(itemGuid)) {
                  toRemove.add(item);
               }
            }
            enumEntries.removeAll(toRemove);
            return super.caseRemoveEnum(removeEnum);
         }

      };

      for (OverrideOption xOverrideOption : xEnumOverride.getOverrideOptions()) {
         overrideVisitor.doSwitch(xOverrideOption);
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
         oseeEnumType = typeCache.getEnumTypeCache().getByGuid(HexUtil.toLong(xEnumType.getUuid()));
      }

      AttributeTypeCache cache = typeCache.getAttributeTypeCache();
      provider.getAttributeTypeFactory().createOrUpdate(cache, //
         HexUtil.toLong(xAttributeType.getUuid()), //
         xAttributeType.getName(), //
         getQualifiedTypeName(xAttributeType.getBaseAttributeType()), //
         getQualifiedTypeName(xAttributeType.getDataProvider()), //
         xAttributeType.getFileExtension(), //
         xAttributeType.getDefaultValue(), //
         oseeEnumType, //
         min, //
         max, //
         xAttributeType.getDescription(), //
         xAttributeType.getTaggerId(),//
         xAttributeType.getMediaType());
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

      String sideATypeName = xRelationType.getSideAArtifactType().getName();
      String sideBTypeName = xRelationType.getSideBArtifactType().getName();

      ArtifactType sideAType = typeCache.getArtifactTypeCache().getUniqueByName(sideATypeName);
      ArtifactType sideBType = typeCache.getArtifactTypeCache().getUniqueByName(sideBTypeName);

      provider.getRelationTypeFactory().createOrUpdate(typeCache.getRelationTypeCache(), //
         HexUtil.toLong(xRelationType.getUuid()), //
         xRelationType.getName(), //
         xRelationType.getSideAName(), //
         xRelationType.getSideBName(), //
         sideAType, //
         sideBType, //
         multiplicity, //
         OseeUtil.orderTypeNameToGuid(xRelationType.getDefaultOrderType()) //
      );
   }
}
