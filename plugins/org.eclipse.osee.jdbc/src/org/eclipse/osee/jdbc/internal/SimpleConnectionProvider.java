/*********************************************************************
 * Copyright (c) 2014 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

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
