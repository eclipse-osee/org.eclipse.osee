/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.jdbc.internal;

import java.sql.Connection;
import java.util.Collections;
import java.util.Map;
import org.eclipse.osee.jdbc.JdbcException;

/**
 * @author Roberto E. Escobar
 */
public class SimpleConnectionProvider implements JdbcConnectionProvider {

   private final JdbcConnectionFactoryManager manager;

   public SimpleConnectionProvider(JdbcConnectionFactoryManager manager) {
      this.manager = manager;
   }

   @Override
   public JdbcConnectionImpl getConnection(JdbcConnectionInfo dbInfo) throws JdbcException {
      JdbcConnectionFactory factory = manager.getFactory(dbInfo.getDriver());
      Connection connection = factory.getConnection(dbInfo);
      return new JdbcConnectionImpl(connection);
   }

   @Override
   public void dispose() {
      //
   }

   @Override
   public Map<String, String> getStatistics() {
      return Collections.emptyMap();
   }

}
