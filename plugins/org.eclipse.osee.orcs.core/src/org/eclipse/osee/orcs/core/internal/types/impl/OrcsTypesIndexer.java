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
package org.eclipse.osee.orcs.core.internal.types.impl;

import com.google.common.collect.Sets;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.emf.common.util.EList;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.data.TokenFactory;
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
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.internal.types.BranchHierarchyProvider;
import org.eclipse.osee.orcs.core.internal.types.OrcsTypesIndex;
import org.eclipse.osee.orcs.data.EnumEntry;
import org.eclipse.osee.orcs.data.EnumType;

/**
 * @author Ryan D. Brooks
 * @author Roberto E. Escobar
 */
public class OrcsTypesIndexer {

   private final Log logger;
   private final BranchHierarchyProvider hierarchyProvider;

   public OrcsTypesIndexer(Log logger, BranchHierarchyProvider hierarchyProvider) {
      super();
      this.logger = logger;
      this.hierarchyProvider = hierarchyProvider;
   }

   public OrcsTypesIndex index(IResource source) throws Exception {
      OseeDslResource resource = null;
      InputStream inputStream = null;
      try {
         inputStream = source.getContent();
         inputStream = upConvertTo17(inputStream);
         resource = OseeDslResourceUtil.loadModel(source.getLocation().toASCIIString(), inputStream);
      } finally {
         Lib.close(inputStream);
      }

      Conditions.checkNotNull(resource, "osee dsl model", "Error reading osee dsl resource");
      OseeDsl model = resource.getModel();
      ArtifactTypeIndex artifactTypeIndex = new ArtifactTypeIndex(hierarchyProvider);
      AttributeTypeIndex attributeTypeIndex = new AttributeTypeIndex();
      EnumTypeIndex enumTypeIndex = new EnumTypeIndex();
      RelationTypeIndex relationTypeIndex = new RelationTypeIndex(artifactTypeIndex);
      OrcsIndeces index =
         new OrcsIndeces(source, artifactTypeIndex, attributeTypeIndex, enumTypeIndex, relationTypeIndex);

      for (XOseeArtifactTypeOverride xArtifactTypeOverride : model.getArtifactTypeOverrides()) {
         applyArtifactTypeOverrides(xArtifactTypeOverride);
      }

      for (XOseeEnumOverride xEnumOverride : model.getEnumOverrides()) {
         applyEnumOverrides(xEnumOverride);
      }

      for (XAttributeType dslType : model.getAttributeTypes()) {
         getOrCreateToken(attributeTypeIndex, dslType);
      }

      for (XArtifactType dslType : model.getArtifactTypes()) {
         IArtifactType token = getOrCreateToken(artifactTypeIndex, dslType);
         indexSuperTypes(artifactTypeIndex, token, dslType);
         indexAttributes(artifactTypeIndex, attributeTypeIndex, dslType);
      }

      for (XRelationType dslType : model.getRelationTypes()) {
         getOrCreateToken(relationTypeIndex, dslType);
      }

      for (XOseeEnumType dslType : model.getEnumTypes()) {
         getOrCreateEnumType(enumTypeIndex, dslType);
      }
      return index;
   }

   private InputStream upConvertTo17(InputStream inputStream) throws Exception {
      return OseeDslResourceUtil.upConvertTo17(inputStream);
   }

   private void indexSuperTypes(ArtifactTypeIndex artifactTypeIndex, IArtifactType token, XArtifactType dslType) {
      Set<IArtifactType> tokenSuperTypes = Sets.newLinkedHashSet();
      for (XArtifactType superTypes : dslType.getSuperArtifactTypes()) {
         IArtifactType superToken = getOrCreateToken(artifactTypeIndex, superTypes);
         tokenSuperTypes.add(superToken);
      }
      if (!tokenSuperTypes.isEmpty()) {
         artifactTypeIndex.put(token, tokenSuperTypes);
      }
   }

   private void indexAttributes(ArtifactTypeIndex artifactTypeIndex, AttributeTypeIndex attributeTypeIndex, XArtifactType dslType) {
      Map<BranchId, Collection<AttributeTypeToken>> validAttributes = new HashMap<>();
      for (XAttributeTypeRef xAttributeTypeRef : dslType.getValidAttributeTypes()) {
         XAttributeType xAttributeType = xAttributeTypeRef.getValidAttributeType();
         BranchId branch = getAttributeBranch(xAttributeTypeRef);

         AttributeTypeToken attributeType = attributeTypeIndex.getTokenByDslType(xAttributeType);
         if (attributeType != null && attributeType.isValid()) {
            Collection<AttributeTypeToken> listOfAllowedAttributes = validAttributes.get(branch);
            if (listOfAllowedAttributes == null) {
               listOfAllowedAttributes = Sets.newHashSet();
               validAttributes.put(branch, listOfAllowedAttributes);
            }
            listOfAllowedAttributes.add(attributeType);
         } else {
            logger.warn("Type was null for [%s]", dslType.getName());
         }
      }
      IArtifactType token = getOrCreateToken(artifactTypeIndex, dslType);
      artifactTypeIndex.put(token, validAttributes);
   }

   private IArtifactType getOrCreateToken(ArtifactTypeIndex index, XArtifactType dslType) {
      IArtifactType token = index.getTokenByDslType(dslType);
      if (token == null) {
         long id = Long.valueOf(dslType.getId());
         token = TokenFactory.createArtifactType(id, dslType.getName());
         index.put(token, dslType);
      }
      return token;
   }

   private AttributeTypeId getOrCreateToken(AttributeTypeIndex index, XAttributeType dslType) {
      AttributeTypeToken token = index.getTokenByDslType(dslType);
      if (token == null) {
         long id = Long.valueOf(dslType.getId());
         token = AttributeTypeToken.valueOf(id, dslType.getName());
         index.put(token, dslType);
      }
      return token;
   }

   private IRelationType getOrCreateToken(RelationTypeIndex index, XRelationType dslType) {
      RelationTypeToken token = index.getTokenByDslType(dslType);
      if (token == null) {
         long id = Long.valueOf(dslType.getId());
         token = TokenFactory.createRelationType(id, dslType.getName());
         index.put(token, dslType);
      }
      return token;
   }

   private EnumType getOrCreateEnumType(EnumTypeIndex index, XOseeEnumType dslType) {
      EnumType item = index.getTokenByDslType(dslType);
      if (item == null) {
         item = createEnumType(dslType);
         index.put(item, dslType);
      }
      return item;
   }

   private EnumType createEnumType(XOseeEnumType dslType) {
      int lastOrdinal = 0;
      List<EnumEntry> entries = new LinkedList<>();
      for (XOseeEnumEntry entry : dslType.getEnumEntries()) {
         String ordinal = entry.getOrdinal();
         if (Strings.isValid(ordinal)) {
            lastOrdinal = Integer.parseInt(ordinal);
         }

         String description = entry.getDescription();
         if (description == null) {
            description = Strings.emptyString();
         }

         EnumEntry enumEntry = new EnumEntryImpl(entry.getName(), lastOrdinal, description);
         entries.add(enumEntry);
         lastOrdinal++;
      }
      Collections.sort(entries);
      Long uuid = Long.valueOf(dslType.getId());
      return new EnumTypeImpl(uuid, dslType.getName(), entries);
   }

   private BranchId getAttributeBranch(XAttributeTypeRef xAttributeTypeRef) {
      BranchId branchToken = CoreBranches.SYSTEM_ROOT;
      if (Strings.isValid(xAttributeTypeRef.getBranchUuid())) {
         Long branchId = Long.valueOf(xAttributeTypeRef.getBranchUuid());
         if (branchId > 0) {
            branchToken = BranchId.valueOf(branchId);
         }
      }
      return branchToken;
   }

   private void applyArtifactTypeOverrides(XOseeArtifactTypeOverride xArtTypeOverride) {
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

   private void applyEnumOverrides(XOseeEnumOverride xEnumOverride) {
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
               String toMatch = item.getName();
               if (nameToMatch.equals(toMatch)) {
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

   private static final class OrcsIndeces implements OrcsTypesIndex {

      private final IResource resource;
      private final ArtifactTypeIndex artifactTypeIndex;
      private final AttributeTypeIndex attributeTypeIndex;
      private final EnumTypeIndex enumTypeIndex;
      private final RelationTypeIndex relationTypeIndex;

      public OrcsIndeces(IResource resource, ArtifactTypeIndex artifactTypeIndex, AttributeTypeIndex attributeTypeIndex, EnumTypeIndex enumTypeIndex, RelationTypeIndex relationTypeIndex) {
         super();
         this.resource = resource;
         this.artifactTypeIndex = artifactTypeIndex;
         this.attributeTypeIndex = attributeTypeIndex;
         this.enumTypeIndex = enumTypeIndex;
         this.relationTypeIndex = relationTypeIndex;
      }

      @Override
      public ArtifactTypeIndex getArtifactTypeIndex() {
         return artifactTypeIndex;
      }

      @Override
      public AttributeTypeIndex getAttributeTypeIndex() {
         return attributeTypeIndex;
      }

      @Override
      public RelationTypeIndex getRelationTypeIndex() {
         return relationTypeIndex;
      }

      @Override
      public EnumTypeIndex getEnumTypeIndex() {
         return enumTypeIndex;
      }

      @Override
      public IResource getOrcsTypesResource() {
         return resource;
      }

   }
}
