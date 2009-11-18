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
import org.eclipse.osee.framework.core.data.ArtifactChangeItem;
import org.eclipse.osee.framework.core.data.AttributeChangeItem;
import org.eclipse.osee.framework.core.data.ChangeItem;
import org.eclipse.osee.framework.core.data.RelationChangeItem;
import org.eclipse.osee.framework.core.enums.ChangeItemType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.ChangeItemBuilder;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;


/**
 * @author Jeff C. Phillips
 */
public class ChangeItemTranslator implements IDataTranslator<ChangeItem> {
   public enum Entry {
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
      return ChangeItemBuilder.buildChangeItem(propertyStore, service);
   }

   @Override
   public PropertyStore convert(ChangeItem changeItem) throws OseeCoreException {
      PropertyStore store = new PropertyStore();
      
      if(changeItem instanceof ArtifactChangeItem){
         store.put(Entry.TYPE.name(), ChangeItemType.ARTIFACT.name());
      } else if (changeItem instanceof AttributeChangeItem){
         store.put(Entry.TYPE.name(), ChangeItemType.ATTRIBUTE.name());
      } else if (changeItem instanceof RelationChangeItem){
         RelationChangeItem relationChangeItem = (RelationChangeItem) changeItem;
         
         store.put(Entry.TYPE.name(), ChangeItemType.RELATION.name());
         store.put(Entry.B_ART_ID.name(), relationChangeItem.getBArtId());
         store.put(Entry.REL_TYPE_ID.name(), relationChangeItem.getRelTypeId());
         store.put(Entry.RATIONALE.name(), relationChangeItem.getRationale());
      }
      
      store.put(Entry.ART_ID.name(), changeItem.getArtId());
      store.put(Entry.ITEM_ID.name(), changeItem.getItemId());
      store.put(Entry.BASE_ENTRY.name(), service.convert(changeItem.getBaselineVersion()));
      store.put(Entry.FIRST_CHANGE.name(),  service.convert(changeItem.getFirstNonCurrentChange()));
      store.put(Entry.CURRENT_ENTRY.name(), service.convert(changeItem.getCurrentVersion()));
      store.put(Entry.DESTINATION_ENTRY.name(), service.convert(changeItem.getDestinationVersion()));
      store.put(Entry.NET_ENTRY.name(), service.convert(changeItem.getNetChange()));
      
      return store;
   }
}
