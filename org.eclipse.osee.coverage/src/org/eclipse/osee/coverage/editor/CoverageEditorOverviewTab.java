/*
 * Created on Feb 5, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.editor;

import org.eclipse.osee.coverage.model.CoverageMethodEnum;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.osee.framework.ui.skynet.results.html.XResultsComposite;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * @author Donald G. Dunne
 */
public class CoverageEditorOverviewTab extends FormPage {

   private final CoverageEditor coverageEditor;

   public CoverageEditorOverviewTab(CoverageEditor coverageEditor) {
      super(coverageEditor, "Overview", "Overview");
      this.coverageEditor = coverageEditor;
   }

   @Override
   protected void createFormContent(IManagedForm managedForm) {
      super.createFormContent(managedForm);

      final ScrolledForm form = managedForm.getForm();

      form.getBody().setLayout(new GridLayout(2, false));
      CoverageEditor.addToToolBar(form.getToolBarManager(), coverageEditor);
      Composite composite = form.getBody();
      composite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));

      XResultsComposite xResultsComp = new XResultsComposite(composite, SWT.BORDER);
      xResultsComp.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_BEGINNING));
      GridData gd = new GridData(GridData.FILL_BOTH);
      gd.heightHint = 500;
      xResultsComp.setLayoutData(gd);

      ICoverageEditorProvider provider = coverageEditor.getCoverageEditorProvider();
      XResultData rd = new XResultData();
      rd.log(AHTML.bold("Coverage Import for " + XDate.getDateStr(provider.getRunDate(), XDate.HHMMSSSS)) + AHTML.newline());
      rd.log(AHTML.getLabelValueStr("Coverage Units", String.valueOf(provider.getCoverageUnits().size())));
      rd.log(AHTML.getLabelValueStr("Coverage Items", String.valueOf(provider.getCoverageItems().size())));
      rd.log(AHTML.getLabelValueStr("Coverage Percent", String.format("%d", provider.getPercentCoverage())));
      rd.log(AHTML.getLabelValueStr("Coverage Method Breakdown", ""));
      for (CoverageMethodEnum coverageMethodEnum : CoverageMethodEnum.values()) {
         rd.log("  - " + coverageMethodEnum + " " + String.valueOf(provider.getCoverageItemsCovered(coverageMethodEnum).size()));
      }
      if (provider.getLog() != null) {
         rd.log(AHTML.newline() + AHTML.bold("Log") + AHTML.newline());
         rd.addRaw(provider.getLog().getReport("").getManipulatedHtml());
      }
      xResultsComp.setHtmlText(rd.getReport(provider.getName()).getManipulatedHtml(), provider.getName());
   }

   @Override
   public FormEditor getEditor() {
      return super.getEditor();
   }

}
