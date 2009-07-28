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
package org.eclipse.osee.framework.ui.skynet.blam.operation;

import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.database.core.ConnectionHandlerStatement;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;

/**
 * @author Ryan D. Brooks
 */
public class UniqueNumberOfCurrentOseeUsers extends AbstractBlam {
   private static final String SELECT_USER_COUNT =
         "select count(*) from v$session t1 where t1.username='OSEE_CLIENT' and not exists (select null from v$session t2 where t1.machine=t2.machine and t2.sid < t1.sid)";

   @Override
   public String getName() {
      return "Unique Number Of Current Osee Users";
   }

   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {

      /**
       * must be connected using a admin schema
       */
      // removeColonFromActionNames
      monitor.beginTask("Counting Users", IProgressMonitor.UNKNOWN);

      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      try {
         chStmt.runPreparedQuery(SELECT_USER_COUNT);
         if (chStmt.next()) {
            OseeLog.log(SkynetGuiPlugin.class, Level.INFO, "active user count: " + chStmt.getInt("user_count"));
         }
      } finally {
         chStmt.close();
      }
      monitor.done();
   }

   @Override
   public String getXWidgetsXml() {
      return AbstractBlam.emptyXWidgetsXml;
   }

   public Collection<String> getCategories() {
      return Arrays.asList("Util");
   }
}
