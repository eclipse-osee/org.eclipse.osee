/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.reports;

import org.eclipse.osee.ats.reports.internal.BirtReportViewerTab;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorTab;

/**
 * @author Roberto Escobar
 */
public class ReportTabFactory {

   private ReportTabFactory() {
      //
   }

   public static IResultsEditorTab createBirtReportTab(String bundleId, String tabName, String rptDesingPath) {
      return new BirtReportViewerTab(bundleId, tabName, rptDesingPath);
   }

}
