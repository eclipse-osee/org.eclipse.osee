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
package org.eclipse.osee.framework.core.message.internal.translation;

import java.util.List;
import org.eclipse.osee.framework.core.enums.ChangeItemType;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.message.ChangeReportResponse;
import org.eclipse.osee.framework.core.message.TranslationUtil;
import org.eclipse.osee.framework.core.model.change.ArtifactChangeItem;
import org.eclipse.osee.framework.core.model.change.AttributeChangeItem;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.core.model.change.ChangeVersion;
import org.eclipse.osee.framework.core.model.change.RelationChangeItem;
import org.eclipse.osee.framework.core.translation.ITranslator;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;

/**
 * @author Jeff C. Phillips
 */
public class ChangeReportResponseTranslator implements ITranslator<ChangeReportResponse> {

   private enum Fields {
      COUNT,
      ROW,
      CURR,
      BASE,
      FIRST,
      DEST,
      NET;
   }

   public ChangeReportResponseTranslator() {
      super();
   }

   @Override
   public ChangeReportResponse convert(PropertyStore store) throws OseeCoreException {
      ChangeReportResponse response = new ChangeReportResponse();

      int rowCount = store.getInt(Fields.COUNT.name());
      for (int index = 0; index < rowCount; index++) {
         String key = TranslationUtil.createKey(Fields.ROW, index);
         String[] rowData = store.getArray(key);

         ChangeItem item = fromArray(rowData);
         load(store, Fields.CURR, index, item.getCurrentVersion());
         load(store, Fields.BASE, index, item.getBaselineVersion());
         load(store, Fields.FIRST, index, item.getFirstNonCurrentChange());
         load(store, Fields.DEST, index, item.getDestinationVersion());
         load(store, Fields.NET, index, item.getNetChange());

         response.addItem(item);
      }
      return response;
   }

   @Override
   public PropertyStore convert(ChangeReportResponse data) throws OseeCoreException {
      PropertyStore store = new PropertyStore();

      List<ChangeItem> items = data.getChangeItems();
      for (int index = 0; index < items.size(); index++) {
         ChangeItem item = items.get(index);
         String key = TranslationUtil.createKey(Fields.ROW, index);
         store.put(key, toArray(item));

         store(store, Fields.CURR, index, item.getCurrentVersion());
         store(store, Fields.BASE, index, item.getBaselineVersion());
         store(store, Fields.FIRST, index, item.getFirstNonCurrentChange());
         store(store, Fields.DEST, index, item.getDestinationVersion());
         store(store, Fields.NET, index, item.getNetChange());
      }
      store.put(Fields.COUNT.name(), items.size());
      return store;
   }

   private static void load(PropertyStore store, Enum<?> prefix, int index, ChangeVersion version) throws OseeArgumentException, NumberFormatException {
      String key = TranslationUtil.createKey(prefix, index);
      String[] data = store.getArray(key);
      if (data != null && data.length > 0) {
         Long gammaId = Long.parseLong(data[0]);
         ModificationType modificationType = ModificationType.getMod(Integer.parseInt(data[1]));
         String value = data[2];

         version.setGammaId(gammaId);
         version.setModType(modificationType);
         version.setValue(value);
      }
   }

   private static void store(PropertyStore store, Enum<?> prefix, int index, ChangeVersion version) {
      if (version != null && version.isValid()) {
         String[] row = new String[3];
         row[0] = String.valueOf(version.getGammaId());
         row[1] = String.valueOf(version.getModType().getValue());
         row[2] = version.getValue();

         String key = TranslationUtil.createKey(prefix, index);
         store.put(key, row);
      }
   }

   private static String[] toArray(ChangeItem item) throws OseeStateException {
      String[] row;
      if (item instanceof ArtifactChangeItem) {
         row = new String[4];
         row[0] = ChangeItemType.ARTIFACT.name();
         row[1] = String.valueOf(item.getItemId());
         row[2] = String.valueOf(item.getItemTypeId());
         row[3] = String.valueOf(item.isSynthetic());
      } else if (item instanceof AttributeChangeItem) {
         row = new String[4];
         row[0] = ChangeItemType.ATTRIBUTE.name();
         row[1] = String.valueOf(item.getItemId());
         row[2] = String.valueOf(item.getItemTypeId());
         row[3] = String.valueOf(item.getArtId());
      } else if (item instanceof RelationChangeItem) {
         row = new String[6];
         RelationChangeItem relationChangeItem = (RelationChangeItem) item;
         row[0] = ChangeItemType.RELATION.name();
         row[1] = String.valueOf(item.getItemId());
         row[2] = String.valueOf(item.getItemTypeId());
         row[3] = String.valueOf(relationChangeItem.getArtId());
         row[4] = String.valueOf(relationChangeItem.getBArtId());
         row[5] = relationChangeItem.getRationale();
      } else {
         throw new OseeStateException("Invalid change item type");
      }
      return row;

   }

   private static ChangeItem fromArray(String[] row) throws OseeStateException {
      ChangeItem changeItem = null;

      ChangeItemType type = ChangeItemType.getType(row[0]);
      int itemId = Integer.parseInt(row[1]);
      long itemTypeId = Long.parseLong(row[2]);
      switch (type) {
         case ARTIFACT:
            boolean synthetic = Boolean.parseBoolean(row[3]);
            changeItem = new ArtifactChangeItem(itemId, itemTypeId, -1, ModificationType.NEW);
            changeItem.setSynthetic(synthetic);
            break;
         case ATTRIBUTE:
            int artId = Integer.parseInt(row[3]);
            changeItem = new AttributeChangeItem(itemId, itemTypeId, artId, -1, ModificationType.NEW, null);
            break;
         case RELATION:
            int aArtId = Integer.parseInt(row[3]);
            int bArtId = Integer.parseInt(row[4]);
            String rationale = row[5];

            changeItem =
               new RelationChangeItem(itemId, itemTypeId, -1, ModificationType.NEW, aArtId, bArtId, rationale);
            break;
         default:
            throw new OseeStateException("Invalid change item type");
      }
      return changeItem;
   }
}
