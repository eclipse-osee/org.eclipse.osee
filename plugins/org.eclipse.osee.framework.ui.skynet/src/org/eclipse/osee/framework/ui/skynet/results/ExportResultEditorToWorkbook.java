/*********************************************************************
 * Copyright (c) 2025 Boeing
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

package org.eclipse.osee.framework.ui.skynet.results;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.framework.core.data.OseeData;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.io.CharBackedInputStream;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ISheetWriter;
import org.eclipse.osee.framework.plugin.core.util.AIFile;
import org.eclipse.osee.framework.ui.skynet.results.html.ResultsEditorHtmlTab;
import org.eclipse.osee.framework.ui.skynet.results.table.IResultsXViewerRow;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsEditorTableTab;

public class ExportResultEditorToWorkbook {

   private final ResultsEditor editor;

   public ExportResultEditorToWorkbook(ResultsEditor editor) {
      this.editor = editor;
   }

   public void run() {
      try {
         CharBackedInputStream charBak = new CharBackedInputStream();
         ISheetWriter excelWriter = new ExcelXmlWriter(charBak.getWriter());

         for (IResultsEditorTab rTab : editor.getResultsEditorProvider().getResultsEditorTabs()) {
            if (rTab instanceof ResultsEditorHtmlTab) {
               excelWriter.startSheet(rTab.getTabName(), 1);
               String reportHtml = ((ResultsEditorHtmlTab) rTab).getReportHtml();
               reportHtml = AHTML.htmlToPlainText(reportHtml);
               excelWriter.writeRow(AHTML.htmlToText(reportHtml));
               excelWriter.endSheet();
            } else if (rTab instanceof ResultsEditorTableTab) {
               ResultsEditorTableTab tableTab = (ResultsEditorTableTab) rTab;
               excelWriter.startSheet(rTab.getTabName(), tableTab.getTableRows().iterator().next().values().length);
               List<String> headers = new ArrayList<>();
               for (XViewerColumn col : tableTab.getTableColumns()) {
                  headers.add(col.getName());
               }
               excelWriter.writeRow(headers);
               for (IResultsXViewerRow row : tableTab.getTableRows()) {
                  List<String> values = new ArrayList<>();
                  for (Object val : row.values()) {
                     if (Strings.isInvalid(val.toString())) {
                        values.add(" ");
                     } else {
                        values.add(val.toString());
                     }
                  }
                  excelWriter.writeRow(values);
               }
               excelWriter.endSheet();
            }
         }
         excelWriter.endWorkbook();

         DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
         Date date = new Date();
         IFile iFile = OseeData.getIFile(getClass().getSimpleName() + "_" + dateFormat.format(date) + ".xml");
         AIFile.writeToFile(iFile, charBak);

         String command = "cmd /c start excel \"" + iFile.getLocation().toOSString() + "\"";
         Runtime.getRuntime().exec(command);

      } catch (Exception ex) {
         XResultData rd = new XResultData();
         rd.errorf("Exception %s", Lib.exceptionToString(ex));
         XResultDataUI.report(rd, getClass().getSimpleName());
      }
   }
}
