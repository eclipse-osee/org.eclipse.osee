/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.internal;

import com.google.common.io.ByteSource;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.emf.common.util.EList;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.dsl.OseeDslResource;
import org.eclipse.osee.framework.core.dsl.OseeDslResourceUtil;
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
import org.eclipse.osee.framework.core.enums.RelationSorter;
import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;
import org.eclipse.osee.framework.core.model.IOseeStorable;
import org.eclipse.osee.framework.core.model.OseeEnumEntry;
import org.eclipse.osee.framework.core.model.cache.ArtifactTypeCache;
import org.eclipse.osee.framework.core.model.cache.AttributeTypeCache;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.model.cache.IOseeCache;
import org.eclipse.osee.framework.core.model.cache.OseeEnumTypeCache;
import org.eclipse.osee.framework.core.model.cache.RelationTypeCache;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.core.model.type.ArtifactTypeFactory;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.core.model.type.AttributeTypeFactory;
import org.eclipse.osee.framework.core.model.type.OseeEnumType;
import org.eclipse.osee.framework.core.model.type.OseeEnumTypeFactory;
import org.eclipse.osee.framework.core.model.type.RelationTypeFactory;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.internal.ClientCachingServiceProxy.TypesLoader;

/**
 * @author Roberto E. Escobar
 */
public class DslToTypeLoader implements TypesLoader {

   private final ArtifactTypeFactory artTypeFactory = new ArtifactTypeFactory();
   private final AttributeTypeFactory attrTypeFactory = new AttributeTypeFactory();
   private final OseeEnumTypeFactory enumTypeFactory = new OseeEnumTypeFactory();
   private final RelationTypeFactory relTypeFactory = new RelationTypeFactory();

   private final BranchCache branchCache;

   public DslToTypeLoader(BranchCache branchCache) {
      super();
      this.branchCache = branchCache;
   }

   @Override
   public void loadTypes(IOseeCachingService caches, ByteSource supplier) {
      OseeDslResource loadModel;
      InputStream inputStream = null;
      try {
         inputStream = supplier.openStream();
         loadModel = OseeDslResourceUtil.loadModel("osee:/text.osee", inputStream);
      } catch (Exception ex) {
         throw OseeCoreException.wrap(ex);
      } finally {
         Lib.close(inputStream);
      }

      TypeBuffer buffer = new TypeBuffer();

      OseeDsl model = loadModel.getModel();
      if (model != null) {
         loadTypes(buffer, branchCache, model);
      }

      buffer.copyEnumTypes(caches.getEnumTypeCache());
      buffer.copyAttrTypes(caches.getAttributeTypeCache());
      buffer.copyArtTypes(caches.getArtifactTypeCache());
      buffer.copyRelTypes(caches.getRelationTypeCache());
   }

   private void loadTypes(TypeBuffer buffer, BranchCache branchCache, OseeDsl model) {
      for (XOseeArtifactTypeOverride xArtifactTypeOverride : model.getArtifactTypeOverrides()) {
         translateXArtifactTypeOverride(xArtifactTypeOverride);
      }

      for (XArtifactType xArtifactType : model.getArtifactTypes()) {
         translateXArtifactType(buffer, xArtifactType);
      }

      for (XOseeEnumOverride xEnumOverride : model.getEnumOverrides()) {
         translateXEnumOverride(xEnumOverride);
      }

      for (XOseeEnumType xEnumType : model.getEnumTypes()) {
         translateXEnumType(buffer, xEnumType);
      }

      for (XAttributeType xAttributeType : model.getAttributeTypes()) {
         translateXAttributeType(buffer, xAttributeType);
      }

      for (XArtifactType xArtifactType : model.getArtifactTypes()) {
         handleXArtifactTypeCrossRef(buffer, branchCache, xArtifactType);
      }

      for (XRelationType xRelationType : model.getRelationTypes()) {
         translateXRelationType(buffer, xRelationType);
      }
   }

   private void handleXArtifactTypeCrossRef(TypeBuffer buffer, BranchCache branchCache, XArtifactType xArtifactType) {
      ArtifactType targetArtifactType = buffer.getArtTypes().getByGuid(Long.valueOf(xArtifactType.getId()));
      translateSuperTypes(buffer, targetArtifactType, xArtifactType);
      Map<BranchId, Collection<AttributeType>> validAttributesPerBranch =
         getOseeAttributes(buffer, branchCache, xArtifactType);
      targetArtifactType.setAllAttributeTypes(validAttributesPerBranch);
   }

   private void translateSuperTypes(TypeBuffer buffer, ArtifactType targetArtifactType, XArtifactType xArtifactType) {
      Set<ArtifactType> oseeSuperTypes = new HashSet<>();
      for (XArtifactType xSuperType : xArtifactType.getSuperArtifactTypes()) {
         String superTypeName = xSuperType.getName();
         ArtifactType oseeSuperType = buffer.getArtTypes().getByName(superTypeName);
         oseeSuperTypes.add(oseeSuperType);
      }

      if (!oseeSuperTypes.isEmpty()) {
         targetArtifactType.setSuperTypes(oseeSuperTypes);
      }
   }

   private Map<BranchId, Collection<AttributeType>> getOseeAttributes(TypeBuffer buffer, BranchCache branchCache, XArtifactType xArtifactType) {
      Map<BranchId, Collection<AttributeType>> validAttributes = new HashMap<>();
      for (XAttributeTypeRef xAttributeTypeRef : xArtifactType.getValidAttributeTypes()) {
         XAttributeType xAttributeType = xAttributeTypeRef.getValidAttributeType();
         BranchId branch = getAttributeBranch(branchCache, xAttributeTypeRef);
         Long attrUuid = Long.valueOf(xAttributeType.getId());
         AttributeType oseeAttributeType = buffer.getAttrTypes().getByGuid(attrUuid);
         if (oseeAttributeType != null) {
            Collection<AttributeType> listOfAllowedAttributes = validAttributes.get(branch);
            if (listOfAllowedAttributes == null) {
               listOfAllowedAttributes = new HashSet<>();
               validAttributes.put(branch, listOfAllowedAttributes);
            }
            listOfAllowedAttributes.add(oseeAttributeType);
         } else {
            OseeLog.logf(Activator.class, Level.WARNING, "Type was null for \"%s\"", xArtifactType.getName());
         }
      }
      return validAttributes;
   }

   private BranchId getAttributeBranch(BranchCache branchCache, XAttributeTypeRef xAttributeTypeRef) {
      String branchIdStr = xAttributeTypeRef.getBranchUuid();
      if (branchIdStr == null) {
         return CoreBranches.SYSTEM_ROOT;
      } else {
         return BranchId.valueOf(branchIdStr);
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
            String guidToMatch = attribute.getId();
            List<XAttributeTypeRef> toRemove = new LinkedList<>();
            for (XAttributeTypeRef xAttributeTypeRef : validAttributeTypes) {
               String itemGuid = xAttributeTypeRef.getValidAttributeType().getId();
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
            String guidToMatch = refToUpdate.getValidAttributeType().getId();
            List<XAttributeTypeRef> toRemove = new LinkedList<>();
            for (XAttributeTypeRef xAttributeTypeRef : validAttributeTypes) {
               String itemGuid = xAttributeTypeRef.getValidAttributeType().getId();
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

   private void translateXArtifactType(TypeBuffer buffer, XArtifactType xArtifactType) {
      String artifactTypeName = xArtifactType.getName();
      Long artUuid = Long.valueOf(xArtifactType.getId());
      artTypeFactory.createOrUpdate(buffer.getArtTypes(), artUuid, xArtifactType.isAbstract(), artifactTypeName);
   }

   private void translateXEnumType(TypeBuffer buffer, XOseeEnumType xEnumType) {
      String enumTypeName = xEnumType.getName();
      Long enumUuid = Long.valueOf(xEnumType.getId());
      OseeEnumType oseeEnumType = enumTypeFactory.createOrUpdate(buffer.getEnumTypes(), enumUuid, enumTypeName);

      int lastOrdinal = 0;
      List<OseeEnumEntry> oseeEnumEntries = new ArrayList<>();
      for (XOseeEnumEntry xEnumEntry : xEnumType.getEnumEntries()) {
         String entryName = xEnumEntry.getName();
         String ordinal = xEnumEntry.getOrdinal();
         if (Strings.isValid(ordinal)) {
            lastOrdinal = Integer.parseInt(ordinal);
         }
         oseeEnumEntries.add(enumTypeFactory.createEnumEntry(entryName, lastOrdinal, xEnumEntry.getDescription()));
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
            String description = addEnum.getDescription();
            XOseeEnumEntry xEnumEntry = OseeDslFactory.eINSTANCE.createXOseeEnumEntry();
            xEnumEntry.setName(entryName);
            xEnumEntry.setDescription(description);
            enumEntries.add(xEnumEntry);
            return super.caseAddEnum(addEnum);
         }

         @Override
         public Void caseRemoveEnum(RemoveEnum removeEnum) {
            XOseeEnumEntry enumEntry = removeEnum.getEnumEntry();
            String nameToMatch = enumEntry.getName();
            List<XOseeEnumEntry> toRemove = new LinkedList<>();
            for (XOseeEnumEntry item : enumEntries) {
               String itemId = item.getName();
               if (nameToMatch.equals(itemId)) {
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

   private void translateXAttributeType(TypeBuffer buffer, XAttributeType xAttributeType) {
      int min = Integer.parseInt(xAttributeType.getMin());
      int max = Integer.MAX_VALUE;
      if (!xAttributeType.getMax().equals("unlimited")) {
         max = Integer.parseInt(xAttributeType.getMax());
      }
      XOseeEnumType xEnumType = xAttributeType.getEnumType();
      OseeEnumType oseeEnumType = null;
      if (xEnumType != null) {
         Long enumUuid = Long.valueOf(xEnumType.getId());
         oseeEnumType = buffer.getEnumTypes().getByGuid(enumUuid);
      }

      Long attrUuid = Long.valueOf(xAttributeType.getId());
      attrTypeFactory.createOrUpdate(buffer.getAttrTypes(), //
         attrUuid, //
         xAttributeType.getName(), //
         getQualifiedTypeName(xAttributeType.getBaseAttributeType()), //
         getQualifiedTypeName(xAttributeType.getDataProvider()), //
         xAttributeType.getFileExtension(), //
         xAttributeType.getDefaultValue(), //
         oseeEnumType, //
         min, //
         max, //
         xAttributeType.getDescription(), //
         xAttributeType.getTaggerId(), //
         xAttributeType.getMediaType());
   }

   private String getQualifiedTypeName(String id) {
      String value = id;
      if (!value.contains(".")) {
         value = "org.eclipse.osee.framework.skynet.core." + id;
      }
      return value;
   }

   private void translateXRelationType(TypeBuffer buffer, XRelationType xRelationType) {
      RelationTypeMultiplicity multiplicity =
         RelationTypeMultiplicity.getFromString(xRelationType.getMultiplicity().name());

      String sideATypeName = xRelationType.getSideAArtifactType().getName();
      String sideBTypeName = xRelationType.getSideBArtifactType().getName();

      ArtifactType sideAType = buffer.getArtTypes().getByName(sideATypeName);
      ArtifactType sideBType = buffer.getArtTypes().getByName(sideBTypeName);

      Long relUuid = Long.valueOf(xRelationType.getId());
      relTypeFactory.createOrUpdate(buffer.getRelTypes(), //
         relUuid, //
         xRelationType.getName(), //
         xRelationType.getSideAName(), //
         xRelationType.getSideBName(), //
         sideAType, //
         sideBType, //
         multiplicity, //
         RelationSorter.valueOfName(xRelationType.getDefaultOrderType()));
   }

   private static final class TypeBuffer {
      private final ArtifactTypeCache artTypes = new ArtifactTypeCache();
      private final OseeEnumTypeCache enumTypes = new OseeEnumTypeCache();
      private final AttributeTypeCache attrTypes = new AttributeTypeCache();
      private final RelationTypeCache relTypes = new RelationTypeCache();

      public ArtifactTypeCache getArtTypes() {
         return artTypes;
      }

      public OseeEnumTypeCache getEnumTypes() {
         return enumTypes;
      }

      public AttributeTypeCache getAttrTypes() {
         return attrTypes;
      }

      public RelationTypeCache getRelTypes() {
         return relTypes;
      }

      public void copyArtTypes(ArtifactTypeCache dest) {
         copy(artTypes, dest);
      }

      public void copyEnumTypes(OseeEnumTypeCache dest) {
         copy(enumTypes, dest);
      }

      public void copyAttrTypes(AttributeTypeCache dest) {
         copy(attrTypes, dest);
      }

      public void copyRelTypes(RelationTypeCache dest) {
         copy(relTypes, dest);
      }

      private <T extends IOseeStorable> void copy(IOseeCache<T> src, IOseeCache<T> dest) {
         synchronized (dest) {
            dest.decacheAll();
            for (T type : src.getAll()) {
               type.clearDirty();
               dest.cache(type);
            }
         }
      }

   }
}