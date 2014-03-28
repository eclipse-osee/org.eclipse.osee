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
package org.eclipse.osee.ats.impl.internal;

import org.eclipse.osee.framework.database.IOseeDatabaseService;

/**
 * @author Donald G. Dunne
 */
public class OseeDatabaseService {

   private static IOseeDatabaseService dbService;

   public static IOseeDatabaseService getDbService() {
      return dbService;
   }

   public void setDatabaseService(IOseeDatabaseService dbService) {
      OseeDatabaseService.dbService = dbService;
   }

}
