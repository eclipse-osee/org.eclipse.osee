/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.message.internal.translation;

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.message.DatastoreInitRequest;
import org.eclipse.osee.framework.core.translation.ITranslator;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;

public class DatastoreInitRequestTranslator implements ITranslator<DatastoreInitRequest> {

   private static enum Entry {
      INDEX_DATA_SPACE,
      TABLE_DATA_SPACE,
      USE_FILE_SPECIFIED_SCHEMAS;
   }

   @Override
   public DatastoreInitRequest convert(PropertyStore store) throws OseeCoreException {
      String tableDataSpace = store.get(Entry.TABLE_DATA_SPACE.name());
      String indexDataSpace = store.get(Entry.INDEX_DATA_SPACE.name());
      boolean useSchemasSpecified = store.getBoolean(Entry.USE_FILE_SPECIFIED_SCHEMAS.name());
      return new DatastoreInitRequest(tableDataSpace, indexDataSpace, useSchemasSpecified);
   }

   @Override
   public PropertyStore convert(DatastoreInitRequest object) throws OseeCoreException {
      PropertyStore store = new PropertyStore();
      store.put(Entry.INDEX_DATA_SPACE.name(), object.getIndexDataSpace());
      store.put(Entry.TABLE_DATA_SPACE.name(), object.getTableDataSpace());
      store.put(Entry.USE_FILE_SPECIFIED_SCHEMAS.name(), object.isUseFileSpecifiedSchemas());
      return store;
   }
}
