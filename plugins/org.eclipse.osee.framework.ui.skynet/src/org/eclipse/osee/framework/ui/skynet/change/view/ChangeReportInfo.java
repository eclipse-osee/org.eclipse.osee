/*
 * Created on Apr 29, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.change.view;

import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.skynet.change.presenter.ChangeReportInfoPresenter;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.KeyedImage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

public class ChangeReportInfo implements ChangeReportInfoPresenter.Display {

   private FormText formText;
   private Label label;
   private ScrolledForm form;

   public ChangeReportInfo() {
   }

   @Override
   public void onCreate(IManagedForm managedForm, Composite parent) {
      FormToolkit toolkit = managedForm.getToolkit();
      form = managedForm.getForm();
      form.getBody().setLayout(new GridLayout());
      form.getBody().setBackground(parent.getBackground());

      Composite composite = toolkit.createComposite(parent, SWT.WRAP);
      composite.setLayout(ALayout.getZeroMarginLayout(2, false));
      composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

      label = toolkit.createLabel(composite, "", SWT.NONE);
      label.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));

      formText = toolkit.createFormText(composite, true);
      GridData gd = new GridData(SWT.FILL, SWT.TOP, true, false);
      gd.widthHint = 200;
      formText.setLayoutData(gd);
      formText.layout();
      toolkit.paintBordersFor(form.getBody());
   }

   @Override
   public void setImage(KeyedImage imageKey) {
      label.setImage(ImageManager.getImage(imageKey));
   }

   @Override
   public void setText(String value) {
      String data = value;
      try {
         formText.setText(data, true, true);
      } catch (Exception ex) {
         data = ex.toString();
         formText.setText(data, false, false);
      }
      int size = Strings.isValid(data) ? data.split("<br/>").length : 0;
      // FormText doesn't size correctly, so determine it's height
      GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
      gridData.heightHint = 8 * (2 + size);
      formText.setLayoutData(gridData);
   }

}
