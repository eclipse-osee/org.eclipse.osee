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

package org.eclipse.osee.framework.ui.skynet.widgets.xviewer;

import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerTreeReport;
import org.eclipse.osee.framework.jdk.core.result.Manipulations;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;

public class OseeXViewerTreeReport extends XViewerTreeReport {

   private IOseeTreeReportProvider provider;

   public OseeXViewerTreeReport(String title, XViewer treeViewer) {
      super(title, treeViewer);
   }

   public OseeXViewerTreeReport(XViewer xViewer, IOseeTreeReportProvider provider) {
      super("Table View Report", xViewer);
      this.provider = provider;
   }

   public OseeXViewerTreeReport(XViewer xViewer) {
      this(xViewer, null);
   }

   @Override
   public void open(String defaultString) {
      try {
         String html = getHtml(xViewer.getTree().getItems());
         if (provider != null && Strings.isValid(provider.getReportTitle())) {
            html = html.replaceFirst("<body>", "<body><b>" + provider.getReportTitle() + "</b></br>");
         }
         XResultData xResultData = new XResultData();
         xResultData.addRaw(html);
         String useTitle = getUseTitle();
         XResultDataUI.report(xResultData, useTitle, Manipulations.RAW_HTML);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   private String getUseTitle() {
      if (Strings.isValid(provider.getEditorTitle())) {
         return provider.getEditorTitle();
      } else if (Strings.isValid(title)) {
         return title;
      }
      return "Table Report";
   }

}
