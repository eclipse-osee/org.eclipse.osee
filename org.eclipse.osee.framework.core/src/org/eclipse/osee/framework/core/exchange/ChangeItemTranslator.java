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
package org.eclipse.osee.framework.core.exchange;

import org.eclipse.osee.framework.core.IDataTranslationService;
import org.eclipse.osee.framework.core.data.ChangeItem;
import org.eclipse.osee.framework.core.data.ChangeVersion;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;

/**
 * @author Jeff C. Phillips
 */
public class ChangeItemTranslator implements IDataTranslator<ChangeItem> {
   private enum Type{
      ARTIFACT,
      ATTRIBUTE,
      RELATION;
   }
   
   private enum Entry {
      BASE_ENTRY,
      FIRST_CHANGE,
      CURRENT_ENTRY,
      DESTINATION_ENTRY,
      NET_ENTRY,
      ART_ID,
      ITEM_ID;
   }

   private final IDataTranslationService service;
   
   public ChangeItemTranslator(IDataTranslationService service) {
      super();
      this.service = service;
   }

   @Override
   public ChangeItem convert(PropertyStore propertyStore) throws OseeCoreException {
      PropertyStore baseEntryStore = propertyStore.getPropertyStore(Entry.BASE_ENTRY.name());
      PropertyStore firstChangeStore = propertyStore.getPropertyStore(Entry.FIRST_CHANGE.name());
      PropertyStore currentEntryStore = propertyStore.getPropertyStore(Entry.CURRENT_ENTRY.name());
      PropertyStore destinationEntryStore = propertyStore.getPropertyStore(Entry.DESTINATION_ENTRY.name());
      PropertyStore netEntryStore = propertyStore.getPropertyStore(Entry.NET_ENTRY.name());

      ChangeVersion baseEntry = service.convert(baseEntryStore, ChangeVersion.class);
      ChangeVersion firstChange = service.convert(firstChangeStore, ChangeVersion.class);
      ChangeVersion currentEntry = service.convert(currentEntryStore, ChangeVersion.class);
      ChangeVersion destinationEntry = service.convert(destinationEntryStore, ChangeVersion.class);
      ChangeVersion netEntry = service.convert(netEntryStore, ChangeVersion.class);

      return null;
   }

   @Override
   public PropertyStore convert(ChangeItem changeItem) throws OseeCoreException {
      PropertyStore store = new PropertyStore();
      return store;
   }

}
