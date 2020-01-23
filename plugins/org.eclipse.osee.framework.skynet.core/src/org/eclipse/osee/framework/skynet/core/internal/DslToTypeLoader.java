/*********************************************************************
 * Copyright (c) 2015 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.skynet.core.internal;

import com.google.common.io.ByteSource;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.emf.common.util.EList;
import org.eclipse.osee.framework.core.OrcsTokenService;
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
import org.eclipse.osee.framework.core.dsl.oseeDsl.util.OseeDslSwitch;
import org.eclipse.osee.framework.core.model.IOseeStorable;
import org.eclipse.osee.framework.core.model.OseeEnumEntry;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.model.cache.IOseeCache;
import org.eclipse.osee.framework.core.model.cache.OseeEnumTypeCache;
import org.eclipse.osee.framework.core.model.type.OseeEnumType;
import org.eclipse.osee.framework.core.model.type.OseeEnumTypeFactory;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.internal.ClientCachingServiceProxy.TypesLoader;

/**
 * @author Roberto E. Escobar
 */
public class DslToTypeLoader implements TypesLoader {
   private final OseeEnumTypeFactory enumTypeFactory = new OseeEnumTypeFactory();
   private final OrcsTokenService tokenService;

   private final BranchCache branchCache;

   public DslToTypeLoader(BranchCache branchCache, OrcsTokenService tokenService) {
      this.branchCache = branchCache;
      this.tokenService = tokenService;
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
   }

   private void loadTypes(TypeBuffer buffer, BranchCache branchCache, OseeDsl model) {
      for (XOseeArtifactTypeOverride xArtifactTypeOverride : model.getArtifactTypeOverrides()) {
         translateXArtifactTypeOverride(xArtifactTypeOverride);
      }

      for (XOseeEnumOverride xEnumOverride : model.getEnumOverrides()) {
         translateXEnumOverride(xEnumOverride);
      }

      for (XOseeEnumType xEnumType : model.getEnumTypes()) {
         translateXEnumType(buffer, xEnumType);
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

   private static final class TypeBuffer {
      private final OseeEnumTypeCache enumTypes = new OseeEnumTypeCache();

      public OseeEnumTypeCache getEnumTypes() {
         return enumTypes;
      }

      public void copyEnumTypes(OseeEnumTypeCache dest) {
         copy(enumTypes, dest);
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