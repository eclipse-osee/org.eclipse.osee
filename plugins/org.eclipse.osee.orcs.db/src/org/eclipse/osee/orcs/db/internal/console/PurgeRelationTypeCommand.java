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
package org.eclipse.osee.orcs.db.internal.console;

import java.util.concurrent.Callable;
import org.eclipse.osee.console.admin.Console;
import org.eclipse.osee.console.admin.ConsoleCommand;
import org.eclipse.osee.console.admin.ConsoleParameters;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.core.services.IdentityService;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.db.internal.callable.PurgeRelationTypeDatabaseTxCallable;

/**
 * @author Roberto E. Escobar
 */
public class PurgeRelationTypeCommand implements ConsoleCommand {

   private Log logger;
   private IOseeDatabaseService dbService;
   private IOseeCachingService cachingService;
   private IdentityService identityService;

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

   public IOseeCachingService getCachingService() {
      return cachingService;
   }

   public void setCachingService(IOseeCachingService cachingService) {
      this.cachingService = cachingService;
   }

   public IdentityService getIdentityService() {
      return identityService;
   }

   public void setIdentityService(IdentityService identityService) {
      this.identityService = identityService;
   }

   @Override
   public String getName() {
      return "db_purge_relation_type";
   }

   @Override
   public String getDescription() {
      return "Purges relation types from the database";
   }

   @Override
   public String getUsage() {
      return "[force=<TRUE|FALSE>] relTypes=<RELATION_TYPES,...>";
   }

   @Override
   public Callable<?> createCallable(Console console, ConsoleParameters params) {
      boolean force = params.getBoolean("force");
      String[] typesToPurge = params.getArray("relTypes");

      return new PurgeRelationTypeDatabaseTxCallable(getLogger(), getDatabaseService(), getIdentityService(),
         getCachingService().getRelationTypeCache(), console, force, typesToPurge);
   }

}
