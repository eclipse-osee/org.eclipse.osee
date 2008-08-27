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

package org.eclipse.osee.framework.db.connection;

import org.eclipse.osee.framework.db.connection.info.DbInformation;

/**
 * @author Andrew M. Finkbeiner
 */
public class OseeDb {

   public static DbInformation getDefaultDatabaseService() {
      return Activator.getInstance().getDbConnectionInformation().getSelectedDatabaseInfo();
   }

   public static DbInformation getDatabaseService(String id) {
      return Activator.getInstance().getDbConnectionInformation().getDatabaseInfo(id);
   }

}
