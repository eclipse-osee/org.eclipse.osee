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
package org.eclipse.osee.framework.plugin.core.config;

import java.sql.SQLException;
import org.eclipse.osee.framework.db.connection.info.SupportedDatabase;

public abstract class BaseConfigurationFactory implements IOseeConfigurationFactory {

   private DBErrorHandler dbErrorHandler;

   public DbErrorCodeLevel getDbErrorCodeLevel(SQLException ex) {
      if (dbErrorHandler == null) {
         switch (SupportedDatabase.getDatabaseType()) {
            case oracle:
               dbErrorHandler = new DbErrorHandlerOracle();
               break;
            case derby:
               dbErrorHandler = new DbErrorHandlerDerby();
               break;
         }
      }
      return dbErrorHandler.getErrorLevel(ex);
   }
}
