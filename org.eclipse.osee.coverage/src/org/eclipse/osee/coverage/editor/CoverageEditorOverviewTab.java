/*
 * Created on Feb 5, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.coverage.model.CoverageOption;
import org.eclipse.osee.coverage.model.CoveragePackageBase;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.coverage.util.CoverageUtil;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.action.RefreshAction;
import org.eclipse.osee.framework.ui.skynet.action.RefreshAction.IRefreshActionHandler;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.osee.framework.ui.skynet.results.html.XResultsComposite;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * @author Donald G. Dunne
 */
public class CoverageEditorOverviewTab extends FormPage implements IRefreshActionHandler {

   private final CoverageEditor coverageEditor;
   private final CoveragePackageBase coveragePackageBase;
   XResultsComposite xResultsComp;

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
      String ALL_COVERAGE_METHODS = " ALL Coverage Methods";
      String ALL_TOP_FOLDERS = " ALL Top Folders";
      coveragePackageBase.getOverviewHtmlHeader(rd);
      rd.log("");
      rd.log(AHTML.getLabelValueStr("Total Coverage ",
            CoverageUtil.getPercent(coveragePackageBase.getCoverageItemsCovered().size(),
                  coveragePackageBase.getCoverageItems().size(), true).getSecond()));
      rd.log("");
      rd.log(AHTML.getLabelValueStr("Coverage Breakdown", ""));
      rd.addRaw(AHTML.beginMultiColumnTable(100, 1));

      // Create headers
      List<String> headers = new ArrayList<String>();
      headers.add("");
      headers.add(ALL_TOP_FOLDERS);
      Map<String, CoverageUnit> headerToCoverageUnit = new HashMap<String, CoverageUnit>();
      for (CoverageUnit coverageUnit : coveragePackageBase.getCoverageUnits()) {
         headers.add(coverageUnit.getName());
         headerToCoverageUnit.put(coverageUnit.getName(), coverageUnit);
      }
      String headersSorted[] = headers.toArray(new String[headers.size()]);
      Arrays.sort(headersSorted);
      rd.addRaw(AHTML.addHeaderRowMultiColumnTable(headersSorted));

      // Create rows
      List<String> rowNames = new ArrayList<String>();
      rowNames.add(ALL_COVERAGE_METHODS);
      Map<String, CoverageOption> rowToCoverageOption = new HashMap<String, CoverageOption>();
      for (CoverageOption option : coveragePackageBase.getCoverageOptionManager().get()) {
         rowNames.add(option.getName());
         rowToCoverageOption.put(option.getName(), option);
      }
      String rowsSorted[] = rowNames.toArray(new String[rowNames.size()]);
      Arrays.sort(rowsSorted);

      for (String rowName : rowsSorted) {
         List<String> values = new ArrayList<String>();
         for (String header : headersSorted) {
            if (header.equals("")) {
               values.add(AHTML.bold(rowName));
            }
            // Show totals for ALL and Each CoverageOption
            else if (header.equals(ALL_TOP_FOLDERS)) {
               // Show totals for full coverage package
               if (rowName.equals(ALL_COVERAGE_METHODS)) {
                  values.add(AHTML.bold(CoverageUtil.getPercent(coveragePackageBase.getCoverageItemsCovered().size(),
                        coveragePackageBase.getCoverageItems().size(), true).getSecond()));
               }
               // Show totals for each coverage method
               else {
                  int totalCoverageItems = coveragePackageBase.getCoverageItems().size();
                  if (totalCoverageItems == 0) {
                     values.add("0");
                  } else {
                     CoverageOption CoverageOption = rowToCoverageOption.get(rowName);
                     values.add(CoverageUtil.getPercent(
                           coveragePackageBase.getCoverageItemsCovered(CoverageOption).size(), totalCoverageItems,
                           false).getSecond());
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
               } else {
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
         rd.addRaw(AHTML.addRowMultiColumnTable(values.toArray(new String[values.size()])));
      }
      rd.addRaw(AHTML.endMultiColumnTable());
      if (coveragePackageBase.getLog() != null) {
         rd.log(AHTML.newline() + AHTML.bold("Log") + AHTML.newline());
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

   public void createToolBar() {
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

}
