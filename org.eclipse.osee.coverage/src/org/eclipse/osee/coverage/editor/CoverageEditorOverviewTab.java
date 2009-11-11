/*
 * Created on Feb 5, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.editor;

import org.eclipse.osee.coverage.model.CoverageMethodEnum;
import org.eclipse.osee.coverage.model.CoveragePackageBase;
import org.eclipse.osee.coverage.util.CoverageMetrics;
import org.eclipse.osee.coverage.util.CoverageUtil;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
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
      XResultData rd = new XResultData(false);
      coveragePackageBase.getOverviewHtmlHeader(rd);
      rd.log("");
      rd.log(AHTML.getLabelValueStr("Coverage Items - Covered",
            String.valueOf(coveragePackageBase.getCoverageItemsCovered().size())));
      rd.log(AHTML.getLabelValueStr("Coverage Items - Total",
            String.valueOf(coveragePackageBase.getCoverageItems().size())));
      rd.log(AHTML.getLabelValueStr("Coverage Percent", String.format("%d", coveragePackageBase.getCoveragePercent())));
      rd.log("");
      rd.log(AHTML.getLabelValueStr("Covered Items by Coverage Type", ""));
      int totalCoverageItems = coveragePackageBase.getCoverageItems().size();
      for (CoverageMethodEnum coverageMethodEnum : CoverageMethodEnum.values()) {
         rd.log("  - " + coverageMethodEnum + " - " + CoverageMetrics.getPercent(
               coveragePackageBase.getCoverageItemsCovered(coverageMethodEnum).size(), totalCoverageItems).getSecond());
      }
      if (coveragePackageBase.getLog() != null) {
         rd.log(AHTML.newline() + AHTML.bold("Log") + AHTML.newline());
         rd.addRaw(coveragePackageBase.getLog().getReport("").getManipulatedHtml());
      }
      xResultsComp.setHtmlText(rd.getReport(coveragePackageBase.getName()).getManipulatedHtml(),
            coveragePackageBase.getName());
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
