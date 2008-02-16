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
package org.eclipse.osee.define.blam.operation;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.ui.plugin.util.db.ConnectionHandler;
import org.eclipse.osee.framework.ui.plugin.util.db.ConnectionHandlerStatement;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;
import org.eclipse.osee.framework.ui.skynet.blam.operation.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultData;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultPage.Manipulations;

/**
 * @author Jeff C. Phillips
 */
public class CheckValidType extends AbstractBlam {
   private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm");
   private final String sql;
   private final String[] headers;
   private final String[] colNames;
   private final Logger logger;

   /**
    * @param sql
    */
   public CheckValidType(final String sql, final String[] colNames, final String[] headers, final Logger logger) {
      super();
      this.sql = sql;
      this.headers = headers;
      this.colNames = colNames;
      this.logger = logger;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#runOperation(org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap, org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.core.runtime.IProgressMonitor)
    */
   public void runOperation(BlamVariableMap variableMap, IProgressMonitor monitor) throws Exception {
      ConnectionHandlerStatement chStmt = ConnectionHandler.runPreparedQuery(sql);
      Calendar cal = Calendar.getInstance(TimeZone.getDefault());
      List<String> datas = new LinkedList<String>();
      XResultData rd = new XResultData(logger);
      ResultSet rSet = chStmt.getRset();
      ;
      int count = 0;

      while (rSet.next()) {

         for (String colName : colNames) {
            datas.add(rSet.getString(colName));
         }
         count++;
      }
      rd.addRaw("Results: " + count + "<br></br>Date: " + dateFormat.format(cal.getTime()) + "<br></br><br></br>" + AHTML.createTable(
            datas, headers, headers.length, 1, 3));
      rd.report("The report", Manipulations.RAW_HTML);
   }
}