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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
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

   /**
    * @param sql
    */
   public CheckValidType(final String sql, final String[] colNames, final String[] headers) {
      super();
      this.sql = sql;
      this.headers = headers;
      this.colNames = colNames;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#runOperation(org.eclipse.osee.framework.ui.skynet.blam.VariableMap, org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.core.runtime.IProgressMonitor)
    */
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      try {
         chStmt.runPreparedQuery(sql);
         Calendar cal = Calendar.getInstance(TimeZone.getDefault());
         List<String> datas = new LinkedList<String>();
         XResultData rd = new XResultData();
         int count = 0;
         while (chStmt.next()) {
            for (String colName : colNames) {
               datas.add(chStmt.getString(colName));
            }
            count++;
         }
         rd.addRaw("Results: " + count + "<br></br>Date: " + dateFormat.format(cal.getTime()) + "<br></br><br></br>" + AHTML.createTable(
               datas, headers, headers.length, 1, 3));
         rd.report("The report", Manipulations.RAW_HTML);
      } finally {
         chStmt.close();
      }
   }
}