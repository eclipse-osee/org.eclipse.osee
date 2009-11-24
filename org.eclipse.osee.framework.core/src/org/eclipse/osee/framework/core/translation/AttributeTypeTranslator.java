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

import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.AttributeType;
import org.eclipse.osee.framework.core.model.AttributeTypeFactory;
import org.eclipse.osee.framework.core.model.OseeEnumType;
import org.eclipse.osee.framework.core.services.IDataTranslationService;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryServiceProvider;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;

/**
 * @author Roberto E. Escobar
 */
public class AttributeTypeTranslator implements ITranslator<AttributeType> {

   private enum Entry {
      GUID,
      UNIQUE_ID,
      NAME,
      MOD_TYPE,
      BASE_TYPE_ID,
      PROVIDER_ID,
      DEFAULT_VALUE,
      ENUM_TYPE,
      MIN_OCCURRENCE,
      MAX_OCCURRENCE,
      DESCRIPTION,
      FILE_EXT,
      TAGGER_ID;
   }

   private final IDataTranslationService service;
   private final IOseeModelFactoryServiceProvider provider;

   public AttributeTypeTranslator(IDataTranslationService service, IOseeModelFactoryServiceProvider provider) {
      this.service = service;
      this.provider = provider;
   }

   @Override
   public AttributeType convert(PropertyStore store) throws OseeCoreException {
      String guid = store.get(Entry.GUID.name());
      int uniqueId = store.getInt(Entry.UNIQUE_ID.name());
      String name = store.get(Entry.NAME.name());
      ModificationType modType = ModificationType.valueOf(store.get(Entry.MOD_TYPE.name()));

      int minOccurrences = store.getInt(Entry.MIN_OCCURRENCE.name());
      int maxOccurrences = store.getInt(Entry.MAX_OCCURRENCE.name());

      String taggerId = store.get(Entry.TAGGER_ID.name());
      String attributeProviderNameId = store.get(Entry.PROVIDER_ID.name());
      String baseAttributeTypeId = store.get(Entry.BASE_TYPE_ID.name());
      String defaultValue = store.get(Entry.DEFAULT_VALUE.name());
      String tipText = store.get(Entry.DESCRIPTION.name());
      String fileTypeExtension = store.get(Entry.FILE_EXT.name());

      PropertyStore innerStore = store.getPropertyStore(Entry.ENUM_TYPE.name());
      OseeEnumType oseeEnumType = service.convert(innerStore, OseeEnumType.class);

      AttributeTypeFactory factory = provider.getOseeFactoryService().getAttributeTypeFactory();
      AttributeType type =
            factory.create(guid, name, baseAttributeTypeId, attributeProviderNameId, fileTypeExtension, defaultValue,
                  oseeEnumType, minOccurrences, maxOccurrences, tipText, taggerId);
      type.setId(uniqueId);
      type.setModificationType(modType);
      return type;
   }

   @Override
   public PropertyStore convert(AttributeType type) throws OseeCoreException {
      PropertyStore store = new PropertyStore();
      store.put(Entry.GUID.name(), type.getGuid());
      store.put(Entry.UNIQUE_ID.name(), type.getId());
      store.put(Entry.NAME.name(), type.getName());
      store.put(Entry.MOD_TYPE.name(), type.getModificationType().name());

      store.put(Entry.MIN_OCCURRENCE.name(), type.getMinOccurrences());
      store.put(Entry.MAX_OCCURRENCE.name(), type.getMaxOccurrences());

      store.put(Entry.TAGGER_ID.name(), type.getTaggerId());
      store.put(Entry.PROVIDER_ID.name(), type.getAttributeProviderId());
      store.put(Entry.BASE_TYPE_ID.name(), type.getBaseAttributeTypeId());
      store.put(Entry.DEFAULT_VALUE.name(), type.getDefaultValue());
      store.put(Entry.DESCRIPTION.name(), type.getDescription());
      store.put(Entry.FILE_EXT.name(), type.getFileTypeExtension());

      store.put(Entry.ENUM_TYPE.name(), service.convert(type.getOseeEnumType()));
      return store;
   }
}
