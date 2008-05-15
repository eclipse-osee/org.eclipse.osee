/*
 * Created on Mar 6, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.mergeWizard;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author Theron Virgin
 *
 */

public class EmbeddedBooleanEditor {
   Button booleanValue;
   String dialogMessage;


   public EmbeddedBooleanEditor(String dialogMessage) {
      this.dialogMessage = dialogMessage;
   }


   public void createEditor(Composite composite, GridData gd) {

     new Label(composite, SWT.NONE).setText(dialogMessage);
     booleanValue = new Button(composite, SWT.CHECK);
     booleanValue.setText("");
     composite.layout();
   }

   public boolean getEntry() {
     return booleanValue.getSelection();
   }

   public void setEntry(boolean entry) {
         booleanValue.setSelection(entry);
   }

}
