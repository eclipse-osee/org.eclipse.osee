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

package org.eclipse.osee.framework.ui.skynet.widgets.xresults;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultPage.Manipulations;
import org.eclipse.swt.widgets.Display;

/**
 * Used to log Info, Warning and Errors to multiple locations (logger, stderr/out and XResultView). Upon completion, a
 * call to report(title) will open results in the ResultsView
 * 
 * @author Donald G. Dunne
 */
public class XResultData {

   StringBuffer sb = new StringBuffer();
   private final Logger logger;
   private static enum Type {
      Severe, Warning, Info;
   }

   public static void runExample() {
      XResultData rd = new XResultData(SkynetGuiPlugin.getLogger());
      rd.log("This is just a normal log message");
      rd.logWarning("This is a warning");
      rd.logError("This is an error");
      rd.report("This is my report title");

      rd = new XResultData(SkynetGuiPlugin.getLogger());
      rd.log("Here is a nice table");
      StringBuffer sb = new StringBuffer();
      sb.append(AHTML.beginMultiColumnTable(95, 1));
      sb.append(AHTML.addHeaderRowMultiColumnTable(new String[] {"Type", "Title", "Status"}));
      for (int x = 0; x < 3; x++)
         sb.append(AHTML.addRowMultiColumnTable(new String[] {"Type " + x, "Title " + x, x + ""}));
      sb.append(AHTML.endMultiColumnTable());
      rd.addRaw(sb.toString().replaceAll("\n", ""));
      rd.report("This is my report title");
   }

   public XResultData(Logger logger) {
      this.logger = logger;
   }

   public void addRaw(String str) {
      sb.append(str);
   }

   public void log(String str) {
      logStr(Type.Info, str + "\n", null);
   }

   public void log(String str, IProgressMonitor monitor) {
      logStr(Type.Info, str + "\n", monitor);
   }

   public void logError(String str) {
      logStr(Type.Severe, str + "\n", null);
   }

   public void logWarning(String str) {
      logStr(Type.Warning, str + "\n", null);
   }

   public boolean isEmpty() {
      return toString().equals("");
   }

   public void logStr(Type type, final String str, final IProgressMonitor monitor) {
      String resultStr = "";
      if (type == Type.Warning)
         resultStr = "Warning: " + str;
      else if (type == Type.Severe)
         resultStr = "Error: " + str;
      else
         resultStr = str;
      sb.append(resultStr);
      if (logger != null)
         logger.log(Level.parse(type.name().toUpperCase()), resultStr);
      else {
         if (type == Type.Info)
            System.out.println(resultStr);
         else
            System.err.println(resultStr);
      }
      if (monitor != null) {
         Displays.ensureInDisplayThread(new Runnable() {
            public void run() {
               monitor.subTask(str);
            }
         });
      }
   }

   public String toString() {
      return sb.toString();
   }

   public void report(final String title) {
      report(title, Manipulations.ALL);
   }

   public void report(final String title, final Manipulations... manipulations) {
      final List<Manipulations> manips = Collections.getAggregate(manipulations);
      Display.getDefault().asyncExec(new Runnable() {
         public void run() {
            XResultView.getResultView().addResultPage(getReport(title, manipulations));
            if (!manips.contains(Manipulations.NO_POPUP)) AWorkbench.popup("Complete",
                  title + " Complete...Results in Result View");
         }
      });
   }

   public XResultPage getReport(final String title) {
      return getReport(title, Manipulations.ALL);
   }

   public XResultPage getReport(final String title, Manipulations... manipulations) {
      return new XResultPage(title + " - " + XDate.getDateNow(XDate.MMDDYYHHMM),
            (sb.toString().equals("") ? "Nothing Logged" : sb.toString()), manipulations);
   }
}
