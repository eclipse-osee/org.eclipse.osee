/*
 * Created on Feb 5, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.editor;

import org.eclipse.osee.coverage.model.CoverageMethodEnum;
import org.eclipse.osee.coverage.util.CoverageMetrics;
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
   private final ICoverageEditorProvider provider;
   XResultsComposite xResultsComp;

   public CoverageEditorOverviewTab(String name, CoverageEditor coverageEditor, ICoverageEditorProvider provider) {
      super(coverageEditor, name, name);
      this.coverageEditor = coverageEditor;
      this.provider = provider;
   }

   @Override
   protected void createFormContent(IManagedForm managedForm) {
      super.createFormContent(managedForm);

      final ScrolledForm form = managedForm.getForm();
      form.setText(provider.getName());
      form.setImage(ImageManager.getImage(provider.getTitleImage()));

      form.getBody().setLayout(ALayout.getZeroMarginLayout());
      createToolBar();
      Composite composite = form.getBody();
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
      XResultData rd = new XResultData();
      provider.getOverviewHtmlHeader(rd);
      rd.log("");
      rd.log(AHTML.getLabelValueStr("Coverage Items - Covered",
            String.valueOf(provider.getCoverageItemsCovered().size())));
      rd.log(AHTML.getLabelValueStr("Coverage Items - Total", String.valueOf(provider.getCoverageItems().size())));
      rd.log(AHTML.getLabelValueStr("Coverage Percent", String.format("%d", provider.getCoveragePercent())));
      rd.log("");
      rd.log(AHTML.getLabelValueStr("Covered Items by Coverage Type", ""));
      int totalCoverageItems = provider.getCoverageItems().size();
      for (CoverageMethodEnum coverageMethodEnum : CoverageMethodEnum.values()) {
         rd.log("  - " + coverageMethodEnum + " - " + CoverageMetrics.getPercent(
               provider.getCoverageItemsCovered(coverageMethodEnum).size(), totalCoverageItems).getSecond());
      }
      if (provider.getLog() != null) {
         rd.log(AHTML.newline() + AHTML.bold("Log") + AHTML.newline());
         rd.addRaw(provider.getLog().getReport("").getManipulatedHtml());
      }
      xResultsComp.setHtmlText(rd.getReport(provider.getName()).getManipulatedHtml(), provider.getName());
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
