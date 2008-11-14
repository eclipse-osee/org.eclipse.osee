/*
 * Created on Nov 6, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.db.connection.internal;

import org.eclipse.osee.framework.db.connection.IApplicationDatabaseInfoProvider;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;

/**
 * @author Roberto E. Escobar
 */
public interface IApplicationDatabaseManager {

   public IApplicationDatabaseInfoProvider getProvider() throws OseeDataStoreException;

   public void removeDatabaseProvider(IApplicationDatabaseInfoProvider provider);

   public void addDatabaseProvider(IApplicationDatabaseInfoProvider provider);
}
