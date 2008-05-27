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

package org.eclipse.osee.framework.db.connection.impl;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.osee.framework.db.connection.IBind;
import org.eclipse.osee.framework.db.connection.IConnection;
import org.eclipse.osee.framework.db.connection.IDbConnectionFactory;

/**
 * @author Andrew M. Finkbeiner
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
