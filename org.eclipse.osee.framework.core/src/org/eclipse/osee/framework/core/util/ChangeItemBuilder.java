/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.util;

import org.eclipse.osee.framework.core.IDataTranslationService;
import org.eclipse.osee.framework.core.data.ArtifactChangeItem;
import org.eclipse.osee.framework.core.data.AttributeChangeItem;
import org.eclipse.osee.framework.core.data.ChangeItem;
import org.eclipse.osee.framework.core.data.ChangeVersion;
import org.eclipse.osee.framework.core.data.RelationChangeItem;
import org.eclipse.osee.framework.core.enums.ChangeItemType;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exchange.ChangeItemTranslator.Entry;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;

/**
 * @author Jeff C. Phillips
 */
public class ChangeItemBuilder {
   
   public static ChangeItem buildChangeItem(PropertyStore propertyStore,IDataTranslationService service) throws OseeCoreException{
      PropertyStore currentEntryStore = propertyStore.getPropertyStore(Entry.CURRENT_ENTRY.name());
      ChangeVersion currentEntry = service.convert(currentEntryStore, ChangeVersion.class);
      ChangeItem changeItem = createChangeItem(propertyStore, currentEntry);
      
      return populateChangeItem(changeItem, propertyStore, service);
   }
   
   private static ChangeItem createChangeItem(PropertyStore propertyStore, ChangeVersion currentChangeVersion){
      ChangeItem changeItem = null;
      int itemId = Integer.parseInt(propertyStore.get(Entry.ITEM_ID.name()));
      ChangeItemType type = ChangeItemType.getType(propertyStore.get(Entry.TYPE.name()));
      int artId = Integer.parseInt(propertyStore.get(Entry.ART_ID.name()));
      
      switch (type){
         case ARTIFACT:
            changeItem = new ArtifactChangeItem(currentChangeVersion.getGammaId(), currentChangeVersion.getModType(), currentChangeVersion.getTransactionNumber(), artId);
            break;
         case ATTRIBUTE:
            changeItem = new AttributeChangeItem(currentChangeVersion.getGammaId(), currentChangeVersion.getModType(), currentChangeVersion.getTransactionNumber(), itemId, artId, currentChangeVersion.getValue());
            break;
         case RELATION:
            int bArtId = Integer.parseInt(propertyStore.get(Entry.B_ART_ID.name()));
            int relTypeId = Integer.parseInt(propertyStore.get(Entry.REL_TYPE_ID.name()));
            String rationale = propertyStore.get(Entry.RATIONALE.name());
            changeItem = new RelationChangeItem(currentChangeVersion.getGammaId(), currentChangeVersion.getModType(), currentChangeVersion.getTransactionNumber(), artId, bArtId, itemId, relTypeId, rationale);
            break;
      }
      return changeItem;
   }
   
   private static ChangeItem populateChangeItem(ChangeItem changeItem, PropertyStore propertyStore,IDataTranslationService service) throws OseeCoreException{
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
   
   private static void setChangeVersionContent(ChangeVersion destination, ChangeVersion source){
      if(source.isValid()){
         destination.setGammaId(source.getGammaId());
         destination.setModType(source.getModType());
         destination.setTransactionNumber(source.getTransactionNumber());
         destination.setValue(source.getValue());
      }
   }
   
   public static ChangeItem buildTestChangeItem() throws OseeArgumentException{
      ChangeItem changeItem = new ArtifactChangeItem(1L, ModificationType.getMod(1), 12, 13);
      changeItem.getDestinationVersion().setGammaId(11L);
      changeItem.getDestinationVersion().setModType(ModificationType.getMod(1));
      changeItem.getDestinationVersion().setTransactionNumber(1);
      changeItem.getDestinationVersion().setValue("hi");
      return changeItem;
   }
}
