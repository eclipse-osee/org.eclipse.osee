/*
 * Created on Jun 23, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
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
