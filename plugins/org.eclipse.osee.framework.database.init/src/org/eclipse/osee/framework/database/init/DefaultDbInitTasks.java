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
package org.eclipse.osee.framework.database.init;

/**
 * @author Roberto E. Escobar
 */
public enum DefaultDbInitTasks {
   DB_INIT_TASK("org.eclipse.osee.framework.database.init.DatabaseInitializationTask"),
   BOOTSTRAP_TASK("org.eclipse.osee.framework.database.init.DbBootstrapTask"),
   DB_USER_CLEANUP("org.eclipse.osee.framework.database.init.PostDbUserCleanUp"),
   BRANCH_DATA_IMPORT("org.eclipse.osee.framework.database.init.SkynetDbBranchDataImport"),
   DB_STATS("org.eclipse.osee.framework.database.init.PostDatabaseInitialization"),

   SIMPLE_TEMPLATE_PROVIDER("org.eclipse.osee.framework.database.init.SimpleTemplateProviderTask");

   private String extensionId;
   public static final String PREVIEW_ALL_RECURSE = "PREVIEW_ALL_RECURSE";

   private DefaultDbInitTasks(String extensionId) {
      this.extensionId = extensionId;
   }

   public String getExtensionId() {
      return extensionId;
   }
}