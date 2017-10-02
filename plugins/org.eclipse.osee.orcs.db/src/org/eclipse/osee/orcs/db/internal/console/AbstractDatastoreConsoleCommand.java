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
package org.eclipse.osee.orcs.db.internal.console;

import org.eclipse.osee.console.admin.ConsoleCommand;
import org.eclipse.osee.framework.jdk.core.type.BaseIdentity;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractDatastoreConsoleCommand implements ConsoleCommand {

   private Log logger;
   private JdbcClient jdbcClient;

   public Log getLogger() {
      return logger;
   }

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public JdbcClient getJdbcClient() {
      return jdbcClient;
   }

   public void setJdbcService(JdbcService jdbcService) {
      this.jdbcClient = jdbcService.getClient();
   }

   public OrcsSession getSession() {
      String id = String.format("console_cmd_%s", GUID.create());
      return new ConsoleSession(id);
   }

   private static final class ConsoleSession extends BaseIdentity<String> implements OrcsSession {
      public ConsoleSession(String id) {
         super(id);
      }
   }
}
