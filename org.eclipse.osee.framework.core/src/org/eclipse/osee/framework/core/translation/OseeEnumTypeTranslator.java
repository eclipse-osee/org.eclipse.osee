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
import java.util.List;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.OseeEnumEntry;
import org.eclipse.osee.framework.core.model.OseeEnumType;
import org.eclipse.osee.framework.core.model.OseeEnumTypeFactory;
import org.eclipse.osee.framework.core.services.IDataTranslationService;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryServiceProvider;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;

/**
 * @author Roberto E. Escobar
 */
public class OseeEnumTypeTranslator implements ITranslator<OseeEnumType> {

   private enum Entry {
      GUID,
      UNIQUE_ID,
      NAME,
      MOD_TYPE,
      ENTRY_COUNT;
   }

   private final IDataTranslationService service;
   private final IOseeModelFactoryServiceProvider provider;

   public OseeEnumTypeTranslator(IDataTranslationService service, IOseeModelFactoryServiceProvider provider) {
      this.service = service;
      this.provider = provider;
   }

   @Override
   public OseeEnumType convert(PropertyStore store) throws OseeCoreException {
      String guid = store.get(Entry.GUID.name());
      int uniqueId = store.getInt(Entry.UNIQUE_ID.name());
      String name = store.get(Entry.NAME.name());

      ModificationType modType = ModificationType.valueOf(store.get(Entry.MOD_TYPE.name()));

      OseeEnumTypeFactory factory = provider.getOseeFactoryService().getOseeEnumTypeFactory();
      OseeEnumType enumType = factory.createEnumType(guid, name);
      enumType.setId(uniqueId);
      enumType.setModificationType(modType);

      List<OseeEnumEntry> entries = new ArrayList<OseeEnumEntry>();
      int numberOfEntries = store.getInt(Entry.ENTRY_COUNT.name());
      for (int index = 0; index < numberOfEntries; index++) {
         PropertyStore innerStore = store.getPropertyStore(createKey(index));
         OseeEnumEntry entry = service.convert(innerStore, OseeEnumEntry.class);
         entries.add(entry);
      }
      enumType.setEntries(entries);
      return enumType;
   }

   @Override
   public PropertyStore convert(OseeEnumType type) throws OseeCoreException {
      PropertyStore store = new PropertyStore();
      store.put(Entry.GUID.name(), type.getGuid());
      store.put(Entry.UNIQUE_ID.name(), type.getId());
      store.put(Entry.NAME.name(), type.getName());
      store.put(Entry.MOD_TYPE.name(), type.getModificationType().name());

      OseeEnumEntry[] entries = type.values();
      store.put(Entry.ENTRY_COUNT.name(), entries.length);

      for (int index = 0; index < entries.length; index++) {
         OseeEnumEntry entry = entries[index];
         store.put(createKey(index), service.convert(entry));
      }
      return store;
   }

   private String createKey(int index) {
      return "EnumEntry_" + index;
   }
}
