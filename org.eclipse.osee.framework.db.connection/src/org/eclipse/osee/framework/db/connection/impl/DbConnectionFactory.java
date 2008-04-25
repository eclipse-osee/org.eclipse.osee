/*
 * Created on Apr 23, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.db.connection.impl;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.osee.framework.db.connection.IBind;
import org.eclipse.osee.framework.db.connection.IConnection;
import org.eclipse.osee.framework.db.connection.IDbConnectionFactory;

/**
 * @author b1528444
 */
public class DbConnectionFactory implements IDbConnectionFactory, IBind {

   private List<IConnection> connectionProviders;

   public DbConnectionFactory() {
      connectionProviders = new CopyOnWriteArrayList<IConnection>();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.db.connection.IDbConnectionFactory#get(java.lang.String)
    */
   @Override
   public IConnection get(String driver) {
      for (IConnection connection : connectionProviders) {
         if (connection.getDriver().equals(driver)) {
            return connection;
         }
      }
      throw new IllegalStateException(String.format("Unable to find matching driver provider for [%s].", driver));
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.db.connection.IBind#bind(java.lang.Object)
    */
   @Override
   public void bind(Object connection) {
      connectionProviders.add((IConnection) connection);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.db.connection.IBind#unbind(java.lang.Object)
    */
   @Override
   public void unbind(Object connection) {
      connectionProviders.remove((IConnection) connection);
   }

}
