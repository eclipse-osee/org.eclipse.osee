/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.coverage.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.coverage.action.GenerateDetailedCoverageReportAction;
import org.eclipse.osee.coverage.action.ICoveragePackageHandler;
import org.eclipse.osee.coverage.model.CoverageOption;
import org.eclipse.osee.coverage.model.CoverageOptionManager;
import org.eclipse.osee.coverage.model.CoveragePackageBase;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.coverage.util.CoverageUtil;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.ui.skynet.action.RefreshAction;
import org.eclipse.osee.framework.ui.skynet.action.RefreshAction.IRefreshActionHandler;
import org.eclipse.osee.framework.ui.skynet.action.browser.BrowserPrintAction;
import org.eclipse.osee.framework.ui.skynet.action.browser.IBrowserActionHandler;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.osee.framework.ui.skynet.results.html.XResultsComposite;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * @author Donald G. Dunne
 */
public class CoverageEditorOverviewTab extends FormPage implements IRefreshActionHandler, ICoveragePackageHandler, IBrowserActionHandler {

   private final CoverageEditor coverageEditor;
   private final CoveragePackageBase coveragePackageBase;
   XResultsComposite xResultsComp;
   private static String ALL_COVERAGE_METHODS = " ALL Coverage Methods";
   private static String ALL_TOP_FOLDERS = " ALL Top Folders";

   public CoverageEditorOverviewTab(String name, CoverageEditor coverageEditor, CoveragePackageBase provider) {
      super(coverageEditor, name, name);
      this.coverageEditor = coverageEditor;
      this.coveragePackageBase = provider;
   }

   @Override
   protected void createFormContent(IManagedForm managedForm) {
      super.createFormContent(managedForm);

      final ScrolledForm scrolledForm = managedForm.getForm();
      scrolledForm.setText(coveragePackageBase.getName());
      scrolledForm.setImage(ImageManager.getImage(CoverageUtil.getCoveragePackageBaseImage(coveragePackageBase)));

      scrolledForm.getBody().setLayout(ALayout.getZeroMarginLayout());
      createToolBar();
      Composite composite = scrolledForm.getBody();
      composite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));

      xResultsComp = new XResultsComposite(composite, SWT.NONE);
      xResultsComp.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_BEGINNING));
      GridData gd = new GridData(GridData.FILL_BOTH);
      gd.heightHint = 500;
      xResultsComp.setLayoutData(gd);
      coverageEditor.getToolkit().adapt(xResultsComp);

      refreshHtml();
   }

   public void refreshHtml() {
      final XResultData rd = new XResultData(false);
      coveragePackageBase.getOverviewHtmlHeader(rd);
      rd.log("");
      rd.log(AHTML.getLabelValueStr(
         "Total Coverage ",
         CoverageUtil.getPercent(coveragePackageBase.getCoverageItemsCovered().size(),
            coveragePackageBase.getCoverageItems().size(), true).getSecond()));
      rd.log("");
      rd.log(AHTML.getLabelValueStr("Coverage Breakdown", ""));
      rd.addRaw(AHTML.beginMultiColumnTable(100, 1));

      // Create headers
      List<String> sortedHeaders = getSortedHeaders(coveragePackageBase);
      rd.addRaw(AHTML.addHeaderRowMultiColumnTable(sortedHeaders));

      for (String[] values : getRows(sortedHeaders, coveragePackageBase, true)) {
         rd.addRaw(AHTML.addRowMultiColumnTable(values));
      }

      rd.addRaw(AHTML.endMultiColumnTable());
      String branchName = null;
      try {
         branchName = coverageEditor.getBranch().getName();
      } catch (Exception ex) {
         branchName = "Exception: " + ex.getLocalizedMessage();
      }
      rd.addRaw(AHTML.newline());
      rd.addRaw(AHTML.getLabelValueStr("\nEditor Branch", branchName));
      if (coveragePackageBase.getLog() != null) {
         rd.log(AHTML.newline() + AHTML.bold("Log:") + AHTML.newline());
         rd.addRaw(coveragePackageBase.getLog().getReport("").getManipulatedHtml());
      }

      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            xResultsComp.setHtmlText(rd.getReport(coveragePackageBase.getName()).getManipulatedHtml(),
               coveragePackageBase.getName());
         }
      });
   }

   public static List<String[]> getRows(List<String> sortedHeaders, CoveragePackageBase coveragePackageBase, boolean html) {

      Map<String, CoverageUnit> headerToCoverageUnit = new HashMap<String, CoverageUnit>();
      for (CoverageUnit coverageUnit : coveragePackageBase.getCoverageUnits()) {
         headerToCoverageUnit.put(coverageUnit.getName(), coverageUnit);
      }

      // Create rows
      String rowsSorted[] = getRowNamesSorted(coveragePackageBase);
      Map<String, CoverageOption> rowToCoverageOption = new HashMap<String, CoverageOption>();
      for (CoverageOption option : coveragePackageBase.getCoverageOptionManager().get()) {
         rowToCoverageOption.put(option.getName(), option);
      }

      List<String[]> rows = new ArrayList<String[]>();
      for (String rowName : rowsSorted) {
         List<String> values = new ArrayList<String>();
         for (String header : sortedHeaders) {
            if (header.equals("")) {
               if (html) {
                  values.add(AHTML.bold(rowName));
               } else {
                  values.add(rowName);
               }
            }
            // Show totals for ALL and Each CoverageOption
            else if (header.equals(ALL_TOP_FOLDERS)) {
               // Show totals for full coverage package
               if (rowName.equals(ALL_COVERAGE_METHODS)) {
                  String percent =
                     CoverageUtil.getPercent(coveragePackageBase.getCoverageItemsCovered().size(),
                        coveragePackageBase.getCoverageItems().size(), true).getSecond();
                  if (html) {
                     values.add(AHTML.bold(percent));
                  } else {
                     values.add(percent);
                  }
               }
               // Show totals for each coverage method
               else if (!rowName.equals(CoverageOptionManager.Not_Covered.getName())) {
                  int totalCoverageItems = coveragePackageBase.getCoverageItems().size();
                  if (totalCoverageItems == 0) {
                     values.add("0");
                  } else {
                     CoverageOption CoverageOption = rowToCoverageOption.get(rowName);
                     values.add(CoverageUtil.getPercent(
                        coveragePackageBase.getCoverageItemsCovered(CoverageOption).size(), totalCoverageItems, false).getSecond());
                  }
               }
            }
            // Show totals for ALL by top CoverageUnit and each CoverageOption by top CoverageUnit
            else {
               CoverageUnit coverageUnit = headerToCoverageUnit.get(header);
               if (rowName.equals(ALL_COVERAGE_METHODS)) {
                  int totalCoverageItems = coverageUnit.getCoverageItems(true).size();
                  if (totalCoverageItems == 0) {
                     values.add("0");
                  } else {
                     values.add(CoverageUtil.getPercent(coverageUnit.getCoverageItemsCovered(true).size(),
                        totalCoverageItems, false).getSecond());
                  }
               } else if (!rowName.equals(CoverageOptionManager.Not_Covered.getName())) {
                  CoverageOption CoverageOption = rowToCoverageOption.get(rowName);
                  int totalCoverageItems = coverageUnit.getCoverageItems(true).size();
                  if (totalCoverageItems == 0) {
                     values.add("0");
                  } else {
                     values.add(CoverageUtil.getPercent(
                        coverageUnit.getCoverageItemsCovered(true, CoverageOption).size(), totalCoverageItems, false).getSecond());
                  }
               }
            }
         }
         rows.add(values.toArray(new String[values.size()]));
      }
      return rows;
   }

   public static String[] getRowNamesSorted(CoveragePackageBase coveragePackageBase) {
      List<String> rowNames = new ArrayList<String>();
      rowNames.add(ALL_COVERAGE_METHODS);
      for (CoverageOption option : coveragePackageBase.getCoverageOptionManager().get()) {
         if (!option.getName().equals(CoverageOptionManager.Not_Covered.getName())) {
            rowNames.add(option.getName());
         }
      }
      String rowsSorted[] = rowNames.toArray(new String[rowNames.size()]);
      Arrays.sort(rowsSorted);
      return rowsSorted;
   }

   public static List<String> getSortedHeaders(CoveragePackageBase coveragePackageBase) {
      List<String> headers = new ArrayList<String>();
      headers.add("");
      if (coveragePackageBase.getChildren().size() > 1) {
         headers.add(ALL_TOP_FOLDERS);
      }
      for (CoverageUnit coverageUnit : coveragePackageBase.getCoverageUnits()) {
         headers.add(coverageUnit.getName());
      }
      String headersSorted[] = headers.toArray(new String[headers.size()]);
      Arrays.sort(headersSorted);
      return headers;
   }

   public void createToolBar() {
      getManagedForm().getForm().getToolBarManager().add(new GenerateDetailedCoverageReportAction(this));
      getManagedForm().getForm().getToolBarManager().add(new BrowserPrintAction(this));
      getManagedForm().getForm().getToolBarManager().add(new RefreshAction(this));
      CoverageEditor.addToToolBar(getManagedForm().getForm().getToolBarManager(), coverageEditor);
   }

   @Override
   public FormEditor getEditor() {
      return super.getEditor();
   }

   @Override
   public void refreshActionHandler() {
      refreshHtml();
   }

   @Override
   public Browser getBrowser() {
      return xResultsComp.getBrowser();
   }

   @Override
   public CoveragePackageBase getCoveragePackageBase() {
      return coveragePackageBase;
   }

}
