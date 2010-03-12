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
package org.eclipse.osee.framework.ui.swt;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IconAndMessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class OseeMessageDialog extends IconAndMessageDialog {

   private String[] buttonLabels;
   private Button[] buttons;
   private int defaultButtonIndex;
   private String title;
   private Image titleImage;
   private Image image = null;
   private Control customArea;

   public OseeMessageDialog(Shell parentShell, String dialogTitle, Image dialogTitleImage, String dialogMessage, Image dialogImage, String[] dialogButtonLabels, int defaultIndex) {
      super(parentShell);
      this.title = dialogTitle;
      this.titleImage = dialogTitleImage;
      this.message = dialogMessage;
      this.image = dialogImage;
      this.buttonLabels = dialogButtonLabels;
      this.defaultButtonIndex = defaultIndex;
   }

   protected void buttonPressed(int buttonId) {
      setReturnCode(buttonId);
      close();
   }

   protected void configureShell(Shell shell) {
      super.configureShell(shell);
      if (title != null) shell.setText(title);
      if (titleImage != null) shell.setImage(titleImage);
   }

   protected void createButtonsForButtonBar(Composite parent) {
      buttons = new Button[buttonLabels.length];
      for (int i = 0; i < buttonLabels.length; i++) {
         String label = buttonLabels[i];
         Button button = createButton(parent, i, label, defaultButtonIndex == i);
         buttons[i] = button;
      }
   }

   /**
    * Creates and returns the contents of an area of the dialog which appears below the message and above the button
    * bar.
    * <p>
    * The default implementation of this framework method returns <code>null</code>. Subclasses may override.
    * </p>
    * 
    * @param parent parent composite to contain the custom area
    * @return the custom area control, or <code>null</code>
    */
   protected Control createCustomArea(Composite parent) {
      return null;
   }

   /**
    * This implementation of the <code>Dialog</code> framework method creates and lays out a composite and calls
    * <code>createMessageArea</code> and <code>createCustomArea</code> to populate it. Subclasses should override
    * <code>createCustomArea</code> to add contents below the message.
    */
   protected Control createDialogArea(Composite parent) {
      // create message area
      createMessageArea(parent);
      // create the top level composite for the dialog area
      Composite composite = new Composite(parent, SWT.NONE);
      GridLayout layout = new GridLayout();
      layout.marginHeight = 0;
      layout.marginWidth = 0;
      composite.setLayout(layout);
      GridData data = new GridData(GridData.FILL_BOTH);
      data.horizontalSpan = 2;
      composite.setLayoutData(data);
      // allow subclasses to add custom controls
      customArea = createCustomArea(composite);
      // If it is null create a dummy label for spacing purposes
      if (customArea == null) customArea = new Label(composite, SWT.NULL);
      return composite;
   }

   /**
    * Gets a button in this dialog's button bar.
    * 
    * @param index the index of the button in the dialog's button bar
    * @return a button in the dialog's button bar
    */
   protected Button getButton(int index) {
      return buttons[index];
   }

   /**
    * Returns the minimum message area width in pixels This determines the minimum width of the dialog.
    * <p>
    * Subclasses may override.
    * </p>
    * 
    * @return the minimum message area width (in pixels)
    */
   protected int getMinimumMessageWidth() {
      return convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
   }

   /**
    * Handle the shell close. Set the return code to <code>SWT.DEFAULT</code> as there has been no explicit close by
    * the user.
    * 
    * @see org.eclipse.jface.window.Window#handleShellCloseEvent()
    */
   protected void handleShellCloseEvent() {
      // Sets a return code of SWT.DEFAULT since none of the dialog buttons
      // were pressed to close the dialog.
      super.handleShellCloseEvent();
      setReturnCode(SWT.DEFAULT);
   }

   /*
    * @see org.eclipse.jface.dialogs.Dialog#createButton(org.eclipse.swt.widgets.Composite, int,
    *      java.lang.String, boolean)
    */
   protected Button createButton(Composite parent, int id, String label, boolean defaultButton) {
      Button button = super.createButton(parent, id, label, defaultButton);
      // Be sure to set the focus if the custom area cannot so as not
      // to lose the defaultButton.
      if (defaultButton && !customShouldTakeFocus()) button.setFocus();
      return button;
   }

   /**
    * Return whether or not we should apply the workaround where we take focus for the default button or if that should
    * be determined by the dialog. By default only return true if the custom area is a label or CLabel that cannot take
    * focus.
    * 
    * @return boolean
    */
   protected boolean customShouldTakeFocus() {
      if (customArea instanceof Label) return false;
      if (customArea instanceof CLabel) return (customArea.getStyle() & SWT.NO_FOCUS) > 0;
      return true;
   }

   public Image getImage() {
      return image;
   }

   /**
    * An accessor for the labels to use on the buttons.
    * 
    * @return The button labels to used; never <code>null</code>.
    */
   protected String[] getButtonLabels() {
      return buttonLabels;
   }

   /**
    * An accessor for the index of the default button in the button array.
    * 
    * @return The default button index.
    */
   protected int getDefaultButtonIndex() {
      return defaultButtonIndex;
   }

   /**
    * A mutator for the array of buttons in the button bar.
    * 
    * @param buttons The buttons in the button bar; must not be <code>null</code>.
    */
   protected void setButtons(Button[] buttons) {
      if (buttons == null) {
         throw new NullPointerException("The array of buttons cannot be null.");} //$NON-NLS-1$
      this.buttons = buttons;
   }

   /**
    * A mutator for the button labels.
    * 
    * @param buttonLabels The button labels to use; must not be <code>null</code>.
    */
   protected void setButtonLabels(String[] buttonLabels) {
      if (buttonLabels == null) {
         throw new NullPointerException("The array of button labels cannot be null.");} //$NON-NLS-1$
      this.buttonLabels = buttonLabels;
   }
}
