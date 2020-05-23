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

package org.eclipse.osee.orcs.db.internal.callable;

import com.google.common.collect.Lists;
import java.util.List;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcConnection;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;

public class CompositeDatastoreTxCallable extends AbstractDatastoreTxCallable<Void> {

   private final List<AbstractDatastoreTxCallable<?>> callables;

   public CompositeDatastoreTxCallable(Log logger, OrcsSession session, JdbcClient jdbcClient, AbstractDatastoreTxCallable<?>... callables) {
      super(logger, session, jdbcClient);
      this.callables = Lists.newArrayList(callables);
   }

   @Override
   protected Void handleTxWork(JdbcConnection connection) {
      for (AbstractDatastoreTxCallable<?> callable : callables) {
         callable.handleTxWork(connection);
      }
      return null;
   }

}
