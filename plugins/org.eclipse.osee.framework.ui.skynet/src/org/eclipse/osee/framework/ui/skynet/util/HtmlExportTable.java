/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.ui.skynet.util;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.FileDialog;

/**
 * @author Donald G. Dunne
 */
public class HtmlExportTable {

   private static String elementTags[] = new String[] {"th", "td"};
   private final String title;
   private final String html;
   private final boolean openInSystem;
   private final boolean popupConfirm;
   private boolean excelTextFields = false;

   /**
    * Given html and title, export embedded table into csv file and open in system editor
    *
    * @param shell used to request where to save file
    * @param title for the top of the exported file
    * @param html html that contains table - only first table will be exported
    * @param openInSystem true if desire to open resulting file in operating system editor upon completion
    */
   public HtmlExportTable(String title, String html, boolean openInSystem) {
      this(title, html, openInSystem, true);
   }

   public HtmlExportTable(String title, String html, boolean openInSystem, boolean popupConfirm) {
      super();
      this.title = title;
      this.html = html;
      this.openInSystem = openInSystem;
      this.popupConfirm = popupConfirm;
   }

   public Result exportCsv() {
      return export(",", "csv");
   }

   public Result exportExcelXml() {
      excelTextFields = true;
      return exportAsExcelXml(",", "xml");
   }

   public Result exportTsv() {
      return export("\t", "tsv");
   }

   public Result export(String speratorChar, String fileExtension) {
      if (!popupConfirm || popupConfirm && MessageDialog.openConfirm(Displays.getActiveShell(), "Export Table",
         "Export Table to CSV?")) {
         StringBuilder sb = new StringBuilder();
         sb.append(title + "\n");
         String htmlStr = AHTML.htmlToText(html);
         Matcher m =
            Pattern.compile("<table.*?</table>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE).matcher(
               htmlStr);
         if (m.find()) {
            String csv = m.group();
            Matcher rowM = Pattern.compile("<tr.*?>(.*?)</tr>",
               Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE).matcher(csv);
            while (rowM.find()) {
               String row = rowM.group(1);
               // Handle all the headers
               for (String tag : elementTags) {
                  Matcher thM = Pattern.compile("<" + tag + ".*?>(.*?)</" + tag + ">",
                     Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE).matcher(row);
                  String csvRow = "";
                  while (thM.find()) {
                     String cellStr = removeLeadTrailSpaces(thM.group(1));
                     String excelTextPrefix = excelTextFields && !cellStr.contains(",") ? "=" : "";
                     csvRow += excelTextPrefix + "\"" + cellStr + "\"" + speratorChar;
                  }
                  if (!csvRow.equals("")) {
                     csvRow = csvRow.replaceFirst(speratorChar + "$", "\n");
                     csvRow = csvRow.replaceAll("<br/>", "\n");
                     sb.append(csvRow);
                  }
               }
            }
            String path = "";
            if (popupConfirm) {
               FileDialog dialog = new FileDialog(Displays.getActiveShell(), SWT.SAVE | SWT.SINGLE);
               dialog.setFilterExtensions(new String[] {"*." + fileExtension});
               dialog.setFilterPath(System.getProperty("user.home"));
               dialog.setFileName("table.csv");
               path = dialog.open();
            } else {
               path = System.getProperty("user.home") + File.separator + "table." + fileExtension;
            }

            if (path != null) {
               try {
                  File file = new File(path);
                  Lib.writeStringToFile(sb.toString(), file);
                  if (openInSystem) {
                     Program.launch(file.getAbsolutePath());
                  }
                  return Result.TrueResult;
               } catch (IOException ex) {
                  OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         } else {
            AWorkbench.popup("ERROR", "Can't find table in results.\n\nNothing to export");
         }
      }
      return Result.FalseResult;
   }

   public Result exportAsExcelXml(String speratorChar, String fileExtension) {
      if (!popupConfirm || popupConfirm && MessageDialog.openConfirm(Displays.getActiveShell(), "Export Table",
         String.format("Export Table to %s ?", fileExtension))) {
         String htmlStr = AHTML.htmlToText(html);
         Matcher m =
            Pattern.compile("<table.*?</table>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE).matcher(
               htmlStr);
         if (m.find()) {
            String path = "";
            if (popupConfirm) {
               FileDialog dialog = new FileDialog(Displays.getActiveShell(), SWT.SAVE | SWT.SINGLE);
               dialog.setFilterExtensions(new String[] {"*." + fileExtension});
               dialog.setFilterPath(System.getProperty("user.home"));
               dialog.setFileName("table." + fileExtension);
               path = dialog.open();
            } else {
               path = System.getProperty("user.home") + File.separator + "table." + fileExtension;
            }

            if (path != null) {
               try {
                  String csv = m.group();
                  Matcher rowM = Pattern.compile("<tr.*?>(.*?)</tr>",
                     Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE).matcher(csv);
                  File file = new File(path);
                  ExcelXmlWriter writer = null;

                  while (rowM.find()) {
                     String row = rowM.group(1);
                     List<String> cells = new LinkedList<>();
                     // Handle all the headers
                     for (String tag : elementTags) {
                        Matcher thM = Pattern.compile("<" + tag + ".*?>(.*?)</" + tag + ">",
                           Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE).matcher(row);
                        while (thM.find()) {
                           String cellStr = thM.group(1);
                           cellStr = cellStr.replaceAll("<br/>", "\n");
                           cells.add(cellStr);
                        }
                     }
                     if (writer == null) {
                        writer = new ExcelXmlWriter(file);
                        writer.startSheet(title, cells.size());
                        writer.writeRow(title);
                     }
                     for (String cell : cells) {
                        writer.writeCell(cell);
                     }
                     writer.endRow();
                  }
                  if (writer != null) {
                     writer.endSheet();
                     writer.endWorkbook();
                  }

                  if (openInSystem) {
                     Program.launch(file.getAbsolutePath());
                  }
                  return Result.TrueResult;
               } catch (IOException ex) {
                  OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         } else {
            AWorkbench.popup("ERROR", "Can't find table in results.\n\nNothing to export");
         }
      }
      return Result.FalseResult;
   }

   private String removeLeadTrailSpaces(String inStr) {
      String str = inStr;
      str = str.replaceAll("^ *", "");
      str = str.replaceAll(" *$", "");
      str = str.replaceAll("\"", "'");
      str = str.replaceAll("\n", " ");
      return str;
   }

}
