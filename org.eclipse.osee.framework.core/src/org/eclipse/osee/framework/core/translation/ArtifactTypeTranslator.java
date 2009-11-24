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
package org.eclipse.osee.framework.core.translation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import org.eclipse.osee.framework.core.enums.CoreTranslationIds;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.ArtifactType;
import org.eclipse.osee.framework.core.model.ArtifactTypeFactory;
import org.eclipse.osee.framework.core.model.AttributeType;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.services.IDataTranslationService;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryServiceProvider;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactTypeTranslator implements ITranslator<ArtifactType> {

   private enum Fields {
      GUID,
      UNIQUE_ID,
      NAME,
      MOD_TYPE,
      IS_ABSTRACT,
      SUPER_TYPES_COUNT,
      BRANCH_COUNT;
   }

   private final IDataTranslationService service;
   private final IOseeModelFactoryServiceProvider provider;

   public ArtifactTypeTranslator(IDataTranslationService service, IOseeModelFactoryServiceProvider provider) {
      this.service = service;
      this.provider = provider;
   }

   @Override
   public ArtifactType convert(PropertyStore store) throws OseeCoreException {
      String guid = store.get(Fields.GUID.name());
      int uniqueId = store.getInt(Fields.UNIQUE_ID.name());
      String name = store.get(Fields.NAME.name());
      ModificationType modType = ModificationType.valueOf(store.get(Fields.MOD_TYPE.name()));
      boolean isAbstract = store.getBoolean(Fields.IS_ABSTRACT.name());

      ArtifactTypeFactory factory = provider.getOseeFactoryService().getArtifactTypeFactory();
      ArtifactType type = factory.create(guid, isAbstract, name);
      type.setId(uniqueId);
      type.setModificationType(modType);

      int superTypeCount = store.getInt(Fields.SUPER_TYPES_COUNT.name());
      Set<ArtifactType> superTypes = new HashSet<ArtifactType>();
      for (int index = 0; index < superTypeCount; index++) {
         ArtifactType artType =
               service.convert(store.getPropertyStore(createSuperTypeKey(index)), CoreTranslationIds.ARTIFACT_TYPE);
         superTypes.add(artType);
      }
      type.setSuperType(superTypes);

      int branchCount = store.getInt(Fields.BRANCH_COUNT.name());
      for (int index = 0; index < branchCount; index++) {
         Branch branch = service.convert(store.getPropertyStore(createBranchKey(index)), CoreTranslationIds.BRANCH);
         int attrTypeCount = store.getInt(createAttrTypeCountKey(index));
         List<AttributeType> attrTypes = new ArrayList<AttributeType>();
         for (int attrIndex = 0; attrIndex < attrTypeCount; attrIndex++) {
            AttributeType attrType =
                  service.convert(store.getPropertyStore(createAttrTypeKey(index, attrTypeCount)),
                        CoreTranslationIds.ATTRIBUTE_TYPE);
            attrTypes.add(attrType);
         }
         type.setAttributeTypes(attrTypes, branch);
      }
      return type;
   }

   @Override
   public PropertyStore convert(ArtifactType type) throws OseeCoreException {
      PropertyStore store = new PropertyStore();
      store.put(Fields.GUID.name(), type.getGuid());
      store.put(Fields.UNIQUE_ID.name(), type.getId());
      store.put(Fields.NAME.name(), type.getName());
      store.put(Fields.MOD_TYPE.name(), type.getModificationType().name());
      store.put(Fields.IS_ABSTRACT.name(), type.isAbstract());

      int superTypeCount = 0;
      for (ArtifactType superType : type.getSuperArtifactTypes()) {
         store.put(createSuperTypeKey(superTypeCount), service.convert(superType, CoreTranslationIds.ARTIFACT_TYPE));
         superTypeCount++;
      }
      store.put(Fields.SUPER_TYPES_COUNT.name(), superTypeCount);

      int branchCount = 0;
      for (Entry<Branch, Collection<AttributeType>> entries : type.getLocalAttributeTypes().entrySet()) {
         Branch branch = entries.getKey();
         store.put(createBranchKey(branchCount), service.convert(branch, CoreTranslationIds.BRANCH));

         int attrTypeCount = 0;
         for (AttributeType attributeType : entries.getValue()) {
            store.put(createAttrTypeKey(branchCount, attrTypeCount), service.convert(attributeType,
                  CoreTranslationIds.ATTRIBUTE_TYPE));
            attrTypeCount++;
         }
         store.put(createAttrTypeCountKey(branchCount), attrTypeCount);
         branchCount++;
      }
      store.put(Fields.BRANCH_COUNT.name(), branchCount);
      return store;
   }

   private String createBranchKey(int index) {
      return "branch_" + index;
   }

   private String createSuperTypeKey(int index) {
      return "superType_" + index;
   }

   private String createAttrTypeKey(int branchIndex, int attrIndex) {
      return "attrType_" + branchIndex + "_" + attrIndex;
   }

   private String createAttrTypeCountKey(int branchIndex) {
      return "attrTypeCount_" + branchIndex;
   }
}
