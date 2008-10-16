/*
 * Created on Oct 15, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.widgets.dialog;

import org.eclipse.osee.framework.ui.skynet.widgets.XCheckBox;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Donald G. Dunne
 */
public class EntryCheckDialog extends EntryDialog {

   private final String checkBoxMessage;
   private boolean checked = false;

   /**
    * @param dialogTitle
    * @param dialogMessage
    */
   public EntryCheckDialog(String dialogTitle, String dialogMessage, String checkBoxMessage) {
      super(dialogTitle, dialogMessage);
      this.checkBoxMessage = checkBoxMessage;
   }

   /**
    * @param parentShell
    * @param dialogTitle
    * @param dialogTitleImage
    * @param dialogMessage
    * @param dialogImageType
    * @param dialogButtonLabels
    * @param defaultIndex
    */
   public EntryCheckDialog(Shell parentShell, String dialogTitle, Image dialogTitleImage, String dialogMessage, String checkBoxMessage, int dialogImageType, String[] dialogButtonLabels, int defaultIndex) {
      super(parentShell, dialogTitle, dialogTitleImage, dialogMessage, dialogImageType, dialogButtonLabels,
            defaultIndex);
      this.checkBoxMessage = checkBoxMessage;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog#createCustomArea(org.eclipse.swt.widgets.Composite)
    */
   @Override
   protected void createExtendedArea(Composite parent) {

      final XCheckBox text = new XCheckBox(checkBoxMessage);
      text.setFillHorizontally(true);
      text.setFocus();
      text.setDisplayLabel(false);
      text.set(checked);
      text.createWidgets(parent, 2);

      SelectionListener selectionListener = new SelectionListener() {

         @Override
         public void widgetDefaultSelected(SelectionEvent e) {
         }

         @Override
         public void widgetSelected(SelectionEvent e) {
            handleModified();
            checked = text.isSelected();
         }
      };
      text.addSelectionListener(selectionListener);

   }

   /**
    * @return the checked
    */
   public boolean isChecked() {
      return checked;
   }

   /**
    * @param checked the checked to set
    */
   public void setChecked(boolean checked) {
      this.checked = checked;
   }

}
