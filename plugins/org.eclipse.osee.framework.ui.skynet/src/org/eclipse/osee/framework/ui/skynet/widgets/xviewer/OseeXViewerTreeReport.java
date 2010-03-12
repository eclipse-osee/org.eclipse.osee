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
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.osee.framework.ui.skynet.results.html.XResultPage.Manipulations;
import org.eclipse.swt.widgets.TreeItem;

public class OseeXViewerTreeReport extends XViewerTreeReport {

   public OseeXViewerTreeReport(String title, XViewer treeViewer) {
      super(title, treeViewer);
   }

   public OseeXViewerTreeReport(XViewer xViewer) {
      super("Table View Report", xViewer);
   }

   @Override
   public void open(TreeItem items[], String defaultString) {
      try {
         String html = getHtml(items);
         XResultData xResultData = new XResultData();
         xResultData.addRaw(html);
         xResultData.report(title, Manipulations.RAW_HTML);
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

}
