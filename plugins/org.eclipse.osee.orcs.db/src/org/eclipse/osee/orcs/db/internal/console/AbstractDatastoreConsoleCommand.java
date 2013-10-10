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
import org.eclipse.osee.framework.core.data.BaseIdentity;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractDatastoreConsoleCommand implements ConsoleCommand {

   private Log logger;
   private IOseeDatabaseService dbService;

   public Log getLogger() {
      return logger;
   }

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public IOseeDatabaseService getDatabaseService() {
      return dbService;
   }

   public void setDatabaseService(IOseeDatabaseService dbService) {
      this.dbService = dbService;
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
