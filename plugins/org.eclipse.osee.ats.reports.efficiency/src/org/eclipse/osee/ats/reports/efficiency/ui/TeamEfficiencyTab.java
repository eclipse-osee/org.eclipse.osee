/*
 * Copyright (c) 2012 Robert Bosch Engineering and Business Solutions Ltd India. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.osee.ats.reports.efficiency.ui;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import org.eclipse.birt.report.viewer.utilities.WebViewer;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.ats.reports.efficiency.Activator;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorTab;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.osgi.framework.Bundle;

/**
 * Class to create the tab in UI
 * 
 * @author Praveen Joseph
 */
public class TeamEfficiencyTab implements IResultsEditorTab {

   private Browser browser;

   @SuppressWarnings("deprecation")
   @Override
   public Composite createTab(final Composite parent, final ResultsEditor resultsEditor) {
      String path = getReportPath();
      this.browser = new Browser(parent, SWT.NONE);
      WebViewer.display(path, WebViewer.HTML, this.browser, "frameset");
      this.browser.refresh();
      return this.browser;
   }

   /**
    * Method to return the report path
    * 
    * @return the report path
    */
   public String getReportPath() {
      String path = null;
      try {
         Bundle bundle = Platform.getBundle(getPluginID());
         URL url = FileLocator.find(bundle, new Path("reports/" + getReport()), null);
         path = FileLocator.toFileURL(url).getPath();
      } catch (MalformedURLException me) {
         System.out.println("Fehler bei URL " + me.getStackTrace());
         return null;
      } catch (IOException e) {
         e.printStackTrace();
         return null;
      }
      return path;
   }

   /**
    * @return the plugin ID
    */
   public String getPluginID() {
      return Activator.PLUGIN_ID;
   }

   /**
    * @return the report name
    */
   public String getReport() {
      return "TeamEfficiencyBar.rptdesign";
   }

   @Override
   public String getTabName() {
      return "Team Efficiency";
   }

}
