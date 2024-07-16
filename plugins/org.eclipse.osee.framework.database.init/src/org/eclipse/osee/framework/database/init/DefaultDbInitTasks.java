/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.framework.database.init;

/**
 * @author Roberto E. Escobar
 */
public enum DefaultDbInitTasks {
   DB_INIT_TASK("org.eclipse.osee.framework.database.init.DatabaseInitializationTask"),
   BOOTSTRAP_TASK("org.eclipse.osee.framework.database.init.DbBootstrapTask"),
   DB_USER_CLEANUP("org.eclipse.osee.framework.database.init.PostDbUserCleanUp");

   private String extensionId;
   public static final String PREVIEW_ALL_RECURSE = "PREVIEW_ALL_RECURSE";

   private DefaultDbInitTasks(String extensionId) {
      this.extensionId = extensionId;
   }

   public String getExtensionId() {
      return extensionId;
   }
}