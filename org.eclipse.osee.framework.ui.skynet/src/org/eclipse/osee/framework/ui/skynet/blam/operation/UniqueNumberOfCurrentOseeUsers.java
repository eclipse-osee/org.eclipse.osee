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

import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.ui.plugin.util.db.ConnectionHandler;
import org.eclipse.osee.framework.ui.plugin.util.db.ConnectionHandlerStatement;
import org.eclipse.osee.framework.ui.plugin.util.db.DbUtil;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;

/**
 * @author Ryan D. Brooks
 */
public class UniqueNumberOfCurrentOseeUsers extends AbstractBlam {
   private static final String SELECT_USER_COUNT =
         "select count(*) from v$session t1 where t1.username='OSEE_CLIENT' and not exists (select null from v$session t2 where t1.machine=t2.machine and t2.sid < t1.sid)";
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(UniqueNumberOfCurrentOseeUsers.class);

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#runOperation(org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap, org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.core.runtime.IProgressMonitor)
    */
   public void runOperation(BlamVariableMap variableMap, IProgressMonitor monitor) throws Exception {

      /**
       * must be connected using a admin schema
       */
      // removeColonFromActionNames
      monitor.beginTask("Counting Users", IProgressMonitor.UNKNOWN);

      ConnectionHandlerStatement chStmt = null;
      try {
         chStmt = ConnectionHandler.runPreparedQuery(SELECT_USER_COUNT);
         ResultSet rSet = chStmt.getRset();
         if (rSet.next()) {
            logger.log(Level.INFO, "active user count: " + rSet.getInt("user_count"));
         }
      } finally {
         DbUtil.close(chStmt);
      }
      monitor.done();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#getXWidgetXml()
    */
   public String getXWidgetsXml() {
      return emptyXWidgetsXml;
   }
}
