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

import org.eclipse.osee.framework.core.data.ArtifactChangeItem;
import org.eclipse.osee.framework.core.data.AttributeChangeItem;
import org.eclipse.osee.framework.core.data.ChangeItem;
import org.eclipse.osee.framework.core.data.ChangeVersion;
import org.eclipse.osee.framework.core.data.RelationChangeItem;
import org.eclipse.osee.framework.core.enums.ChangeItemType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.services.IDataTranslationService;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;

/**
 * @author Jeff C. Phillips
 */
public class ChangeItemTranslator implements ITranslator<ChangeItem> {

   private enum Entry {
      BASE_ENTRY,
      FIRST_CHANGE,
      CURRENT_ENTRY,
      DESTINATION_ENTRY,
      NET_ENTRY,
      ART_ID,
      B_ART_ID,
      TYPE,
      ITEM_ID,
      REL_TYPE_ID,
      RATIONALE;
   }

   private final IDataTranslationService service;

   public ChangeItemTranslator(IDataTranslationService service) {
      super();
      this.service = service;
   }

   @Override
   public ChangeItem convert(PropertyStore propertyStore) throws OseeCoreException {
      PropertyStore currentEntryStore = propertyStore.getPropertyStore(Entry.CURRENT_ENTRY.name());
      ChangeVersion currentEntry = service.convert(currentEntryStore, ChangeVersion.class);
      ChangeItem changeItem = createChangeItem(propertyStore, currentEntry);

      return populateChangeItem(changeItem, propertyStore, service);
   }

   @Override
   public PropertyStore convert(ChangeItem changeItem) throws OseeCoreException {
      PropertyStore store = new PropertyStore();

      if (changeItem instanceof ArtifactChangeItem) {
         store.put(Entry.TYPE.name(), ChangeItemType.ARTIFACT.name());
      } else if (changeItem instanceof AttributeChangeItem) {
         store.put(Entry.TYPE.name(), ChangeItemType.ATTRIBUTE.name());
      } else if (changeItem instanceof RelationChangeItem) {
         RelationChangeItem relationChangeItem = (RelationChangeItem) changeItem;

         store.put(Entry.TYPE.name(), ChangeItemType.RELATION.name());
         store.put(Entry.B_ART_ID.name(), relationChangeItem.getBArtId());
         store.put(Entry.REL_TYPE_ID.name(), relationChangeItem.getRelTypeId());
         store.put(Entry.RATIONALE.name(), relationChangeItem.getRationale());
      }

      store.put(Entry.ART_ID.name(), changeItem.getArtId());
      store.put(Entry.ITEM_ID.name(), changeItem.getItemId());
      store.put(Entry.BASE_ENTRY.name(), service.convert(changeItem.getBaselineVersion()));
      store.put(Entry.FIRST_CHANGE.name(), service.convert(changeItem.getFirstNonCurrentChange()));
      store.put(Entry.CURRENT_ENTRY.name(), service.convert(changeItem.getCurrentVersion()));
      store.put(Entry.DESTINATION_ENTRY.name(), service.convert(changeItem.getDestinationVersion()));
      store.put(Entry.NET_ENTRY.name(), service.convert(changeItem.getNetChange()));
      return store;
   }

   private ChangeItem createChangeItem(PropertyStore propertyStore, ChangeVersion currentChangeVersion) {
      ChangeItem changeItem = null;
      int itemId = Integer.parseInt(propertyStore.get(Entry.ITEM_ID.name()));
      ChangeItemType type = ChangeItemType.getType(propertyStore.get(Entry.TYPE.name()));
      int artId = Integer.parseInt(propertyStore.get(Entry.ART_ID.name()));

      switch (type) {
         case ARTIFACT:
            changeItem =
                  new ArtifactChangeItem(currentChangeVersion.getGammaId(), currentChangeVersion.getModType(),
                        currentChangeVersion.getTransactionNumber(), artId);
            break;
         case ATTRIBUTE:
            changeItem =
                  new AttributeChangeItem(currentChangeVersion.getGammaId(), currentChangeVersion.getModType(),
                        currentChangeVersion.getTransactionNumber(), itemId, artId, currentChangeVersion.getValue());
            break;
         case RELATION:
            int bArtId = Integer.parseInt(propertyStore.get(Entry.B_ART_ID.name()));
            int relTypeId = Integer.parseInt(propertyStore.get(Entry.REL_TYPE_ID.name()));
            String rationale = propertyStore.get(Entry.RATIONALE.name());
            changeItem =
                  new RelationChangeItem(currentChangeVersion.getGammaId(), currentChangeVersion.getModType(),
                        currentChangeVersion.getTransactionNumber(), artId, bArtId, itemId, relTypeId, rationale);
            break;
      }
      return changeItem;
   }

   private ChangeItem populateChangeItem(ChangeItem changeItem, PropertyStore propertyStore, IDataTranslationService service) throws OseeCoreException {
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

      setChangeVersionContent(changeItem.getCurrentVersion(), currentEntry);
      setChangeVersionContent(changeItem.getBaselineVersion(), baseEntry);
      setChangeVersionContent(changeItem.getDestinationVersion(), destinationEntry);
      setChangeVersionContent(changeItem.getFirstNonCurrentChange(), firstChange);
      setChangeVersionContent(changeItem.getNetChange(), netEntry);

      return changeItem;
   }

   private void setChangeVersionContent(ChangeVersion destination, ChangeVersion source) {
      if (source.isValid()) {
         destination.setGammaId(source.getGammaId());
         destination.setModType(source.getModType());
         destination.setTransactionNumber(source.getTransactionNumber());
         destination.setValue(source.getValue());
      }
   }
}
