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
package org.eclipse.osee.jdbc.internal.schema;

import java.util.concurrent.Callable;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcLogger;

/**
 * @author Roberto E. Escobar
 */
public abstract class DatabaseCallable<T> implements Callable<T> {

   private final JdbcLogger logger;
   private final JdbcClient client;

   protected DatabaseCallable(JdbcLogger logger, JdbcClient client) {
      this.logger = logger;
      this.client = client;
   }

   protected JdbcClient getJdbcClient() {
      return client;
   }

   protected JdbcLogger getLogger() {
      return logger;
   }

   protected void error(String msg, Object... args) {
      logger.error(msg, args);
   }
}
