/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.message.internal;

/**
 * Temporary till all code uses branch uuid. Remove after 0.17.0
 * 
 * @author Donald G Dunne
 */
public class DatabaseService {

   public static org.eclipse.osee.framework.database.DatabaseService databaseService;

   public static org.eclipse.osee.framework.database.DatabaseService getDatabaseService() {
      return databaseService;
   }

   public static void setDatabaseService(org.eclipse.osee.framework.database.DatabaseService databaseService) {
      DatabaseService.databaseService = databaseService;
   }

}
