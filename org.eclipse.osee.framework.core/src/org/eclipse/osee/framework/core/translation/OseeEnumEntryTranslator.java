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
import org.eclipse.osee.framework.core.model.OseeEnumEntry;
import org.eclipse.osee.framework.core.model.OseeEnumTypeFactory;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryServiceProvider;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;

/**
 * @author Roberto E. Escobar
 */
public class OseeEnumEntryTranslator implements ITranslator<OseeEnumEntry> {

   private enum Entry {
      GUID,
      UNIQUE_ID,
      NAME,
      MOD_TYPE,
      ORDINAL;
   }

   private final IOseeModelFactoryServiceProvider provider;

   public OseeEnumEntryTranslator(IOseeModelFactoryServiceProvider provider) {
      this.provider = provider;
   }

   @Override
   public OseeEnumEntry convert(PropertyStore store) throws OseeCoreException {
      String guid = store.get(Entry.GUID.name());
      int uniqueId = store.getInt(Entry.UNIQUE_ID.name());
      String name = store.get(Entry.NAME.name());
      ModificationType modType = ModificationType.valueOf(store.get(Entry.MOD_TYPE.name()));
      int ordinal = store.getInt(Entry.ORDINAL.name());

      OseeEnumTypeFactory factory = provider.getOseeFactoryService().getOseeEnumTypeFactory();
      OseeEnumEntry enumEntry = factory.createEnumEntry(guid, name, ordinal);
      enumEntry.setId(uniqueId);
      enumEntry.setModificationType(modType);
      return enumEntry;
   }

   @Override
   public PropertyStore convert(OseeEnumEntry type) throws OseeCoreException {
      PropertyStore store = new PropertyStore();
      store.put(Entry.GUID.name(), type.getGuid());
      store.put(Entry.UNIQUE_ID.name(), type.getId());
      store.put(Entry.NAME.name(), type.getName());
      store.put(Entry.MOD_TYPE.name(), type.getModificationType().name());
      store.put(Entry.ORDINAL.name(), type.ordinal());
      return store;
   }
}
