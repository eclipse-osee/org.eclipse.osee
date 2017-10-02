/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.loader.executors;

import java.util.concurrent.CancellationException;
import org.eclipse.osee.executor.admin.HasCancellation;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.orcs.core.ds.LoadDataHandler;
import org.eclipse.osee.orcs.core.ds.Options;
import org.eclipse.osee.orcs.db.internal.loader.SqlObjectLoader;
import org.eclipse.osee.orcs.db.internal.loader.criteria.CriteriaOrcsLoad;

/**
 * @author Andrew M. Finkbeiner
 */
public abstract class AbstractLoadExecutor {

   private final SqlObjectLoader loader;
   private final JdbcClient jdbcClient;

   protected AbstractLoadExecutor(SqlObjectLoader loader, JdbcClient jdbcClient) {
      super();
      this.loader = loader;
      this.jdbcClient = jdbcClient;
   }

   public abstract void load(HasCancellation cancellation, LoadDataHandler handler, CriteriaOrcsLoad criteria, Options options) ;

   protected JdbcClient getJdbcClient() {
      return jdbcClient;
   }

   protected SqlObjectLoader getLoader() {
      return loader;
   }

   protected void checkCancelled(HasCancellation cancellation) throws CancellationException {
      if (cancellation != null) {
         cancellation.checkForCancelled();
      }
   }

}
