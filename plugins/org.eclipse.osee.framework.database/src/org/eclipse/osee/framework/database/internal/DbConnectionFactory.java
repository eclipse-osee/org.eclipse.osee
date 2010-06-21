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

package org.eclipse.osee.framework.database.internal;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.database.core.IConnectionFactory;

/**
 * @author Andrew M. Finkbeiner
 */
public class DbConnectionFactory implements IDbConnectionFactory {

   private final List<IConnectionFactory> connectionProviders;

   public DbConnectionFactory() {
      connectionProviders = new CopyOnWriteArrayList<IConnectionFactory>();
   }

   @Override
   public IConnectionFactory get(String driver) throws OseeCoreException {
      for (IConnectionFactory connectionFactory : connectionProviders) {
         if (connectionFactory.getDriver().equals(driver)) {
            return connectionFactory;
         }
      }
      return null;
   }

   @Override
   public void bind(IConnectionFactory connection) {
      connectionProviders.add(connection);
   }

   @Override
   public void unbind(IConnectionFactory connection) {
      connectionProviders.remove(connection);
   }

}
