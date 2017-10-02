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
