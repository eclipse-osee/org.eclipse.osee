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
package org.eclipse.osee.coverage.results;

import org.eclipse.osee.coverage.model.CoverageImport;
import org.eclipse.osee.coverage.model.CoverageMethodEnum;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.osee.framework.ui.skynet.results.html.ResultsEditorHtmlTab;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;

/**
 * @author Donald G. Dunne
 */
public class CoverageImportOverviewResultsEditorTab extends ResultsEditorHtmlTab {

   public CoverageImportOverviewResultsEditorTab(CoverageImport coverageImport) {
      super("Overview");

      XResultData rd = new XResultData();
      rd.log(AHTML.bold("Coverage Import for " + XDate.getDateStr(coverageImport.getRunDate(), XDate.HHMMSSSS)) + AHTML.newline());
      rd.log(AHTML.getLabelValueStr("Coverage Units", String.valueOf(coverageImport.getCoverageUnits().size())));
      rd.log(AHTML.getLabelValueStr("Coverage Items", String.valueOf(coverageImport.getCoverageItems().size())));
      rd.log(AHTML.getLabelValueStr("Coverage Percent", String.format("%d", coverageImport.getPercentCoverage())));
      rd.log(AHTML.getLabelValueStr("Coverage Method Breakdown", ""));
      for (CoverageMethodEnum coverageMethodEnum : CoverageMethodEnum.values()) {
         rd.log("  - " + coverageMethodEnum + " " + String.valueOf(coverageImport.getCoverageItemsCovered(
               coverageMethodEnum).size()));
      }
      rd.log(AHTML.newline() + AHTML.bold("Log") + AHTML.newline());
      rd.addRaw(coverageImport.getLog().getReport("").getManipulatedHtml());
      setHtml(coverageImport.getName(), rd.getReport(coverageImport.getName()).getManipulatedHtml());
   }
}
