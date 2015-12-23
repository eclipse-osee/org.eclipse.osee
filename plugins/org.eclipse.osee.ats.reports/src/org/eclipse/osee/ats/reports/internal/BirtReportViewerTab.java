/*
 * Copyright (c) 2012 Robert Bosch Engineering and Business Solutions Ltd India. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.osee.ats.reports.internal;

import java.net.URL;
import org.eclipse.birt.report.viewer.utilities.WebViewer;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorTab;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.osgi.framework.Bundle;

/**
 * Class that implements IResultsEditorTab to crate the tab in the UI
 * 
 * @author Praveen Joseph
 */
public class BirtReportViewerTab implements IResultsEditorTab {

   private final String tabName;
   private final String bundleId;
   private final String rptDesingPath;

   public BirtReportViewerTab(String bundleId, String tabName, String rptDesingPath) {
      this.tabName = tabName;
      this.bundleId = bundleId;
      this.rptDesingPath = rptDesingPath;
   }

   @SuppressWarnings("deprecation")
   @Override
   public Composite createTab(final Composite parent, final ResultsEditor resultsEditor) {
      Browser browser = new Browser(parent, SWT.NONE);
      try {
         URL reportLocation = getReportPath(bundleId, rptDesingPath);
         WebViewer.display(reportLocation.getPath(), WebViewer.HTML, browser, "frameset");
         browser.refresh();
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return browser;
   }

   @Override
   public String getTabName() {
      return tabName;
   }

   private URL getReportPath(String bundleId, String reportPath) throws OseeCoreException {
      try {
         Bundle bundle = Platform.getBundle(bundleId);
         URL url = FileLocator.find(bundle, new Path(reportPath), null);
         return FileLocator.toFileURL(url);
      } catch (Exception ex) {
         throw OseeCoreException.wrap(ex);
      }
   }
}
