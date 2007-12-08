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

public class DbErrorHandlerOracle implements DBErrorHandler {

   public DbErrorCodeLevel getErrorLevel(SQLException e) {
      if ((e.getErrorCode() == 1012) || (e.getErrorCode() == 1033) || (e.getErrorCode() == 1034) || (e.getErrorCode() == 1089) || (e.getErrorCode() == 3113) || (e.getErrorCode() == 3114) || (e.getErrorCode() == 12203) || (e.getErrorCode() == 12500) || (e.getErrorCode() == 12571) || (e.getErrorCode() == 17002) || (e.getErrorCode() == 17008)) {
         return DbErrorCodeLevel.warning;
      } else {
         return DbErrorCodeLevel.severe;
      }
   }

}
