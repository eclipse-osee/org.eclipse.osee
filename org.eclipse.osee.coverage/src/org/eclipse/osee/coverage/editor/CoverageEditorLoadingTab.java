/*
 * Created on Feb 5, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.editor;

import org.eclipse.osee.coverage.util.CoverageImage;
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
public class CoverageEditorLoadingTab extends FormPage implements IRefreshActionHandler {

   private final CoverageEditor coverageEditor;
   XResultsComposite xResultsComp;

   public CoverageEditorLoadingTab(String name, CoverageEditor coverageEditor) {
      super(coverageEditor, name, name);
      this.coverageEditor = coverageEditor;
   }

   @Override
   protected void createFormContent(IManagedForm managedForm) {
      super.createFormContent(managedForm);

      final ScrolledForm scrolledForm = managedForm.getForm();
      scrolledForm.setText(getTitle());
      scrolledForm.setImage(ImageManager.getImage(CoverageImage.COVERAGE));

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
      getManagedForm().getForm().getForm().setBusy(true);

      refreshHtml();
   }

   public void refreshHtml() {
      final XResultData rd = new XResultData(false);
      rd.log(getTitle());
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            xResultsComp.setHtmlText(rd.getReport("").getManipulatedHtml(), "");
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
