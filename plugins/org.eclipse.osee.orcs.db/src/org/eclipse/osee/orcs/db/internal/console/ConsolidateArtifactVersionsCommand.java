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

import java.util.concurrent.Callable;
import org.eclipse.osee.console.admin.Console;
import org.eclipse.osee.console.admin.ConsoleParameters;
import org.eclipse.osee.orcs.core.SystemPreferences;
import org.eclipse.osee.orcs.db.internal.callable.ConsolidateArtifactVersionDatabaseTxCallable;

/**
 * @author Roberto E. Escobar
 */
public class ConsolidateArtifactVersionsCommand extends AbstractDatastoreConsoleCommand {

   private SystemPreferences preferences;

   public void setSystemPreferences(SystemPreferences preferences) {
      this.preferences = preferences;
   }

   @Override
   public String getName() {
      return "db_consolidate_artifact_versions";
   }

   @Override
   public String getDescription() {
      return "Consolidate artifact versions - used to migrate to 0.9.2 database schema";
   }

   @Override
   public String getUsage() {
      return "";
   }

   @Override
   public Callable<?> createCallable(Console console, ConsoleParameters params) {
      return new ConsolidateArtifactVersionDatabaseTxCallable(getLogger(), getSession(), getDatabaseService(),
         preferences, console);
   }
}
