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
import org.eclipse.osee.framework.core.data.TableData;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;

/**
 * @author Roberto E. Escobar
 */
public class TableDataTranslator implements ITranslator<TableData> {

   private enum Entry {
      TABLE,
      TITLE,
      HEADERS,
      ROW,
      ROW_COUNT;
   }

   public TableDataTranslator() {
      super();
   }

   @Override
   public TableData convert(PropertyStore store) throws OseeCoreException {
      String title = store.get(Entry.TITLE.name());
      String[] columns = store.getArray(Entry.HEADERS.name());

      List<String[]> rows = new ArrayList<String[]>();
      int numberOfRows = store.getInt(Entry.ROW_COUNT.name());
      for (int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {
         rows.add(store.getArray(TranslationUtil.createKey(Entry.ROW, rowIndex)));
      }
      return new TableData(title, columns, rows);
   }

   @Override
   public PropertyStore convert(TableData data) throws OseeCoreException {
      PropertyStore store = new PropertyStore();
      store.put(Entry.TITLE.name(), data.getTitle());
      store.put(Entry.HEADERS.name(), data.getColumns());

      List<String[]> rows = data.getRows();
      for (int index = 0; index < rows.size(); index++) {
         store.put(TranslationUtil.createKey(Entry.ROW, index), rows.get(index));
      }
      store.put(Entry.ROW_COUNT.name(), rows.size());
      return store;
   }

}
