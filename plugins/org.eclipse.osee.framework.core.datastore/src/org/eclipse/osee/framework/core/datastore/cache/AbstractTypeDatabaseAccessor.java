/*
 * Created on Aug 19, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.datastore.cache;

import org.eclipse.osee.framework.core.model.IOseeStorable;
import org.eclipse.osee.framework.database.IOseeDatabaseService;

public abstract class AbstractTypeDatabaseAccessor<T extends IOseeStorable> extends AbstractDatabaseAccessor<String, T> {

   protected AbstractTypeDatabaseAccessor(IOseeDatabaseService databaseService) {
      super(databaseService);
   }

}
