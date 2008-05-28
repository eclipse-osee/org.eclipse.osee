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
package org.eclipse.osee.framework.skynet.core.artifact;

import java.util.Collection;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IconAndMessageDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.ui.plugin.util.ObjectList;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class ArtifactDescriptorDialog extends IconAndMessageDialog {
   public final static int NONE = 0;
   public final static int ERROR = 1;
   public final static int INFORMATION = 2;
   public final static int QUESTION = 3;
   public final static int WARNING = 4;
   private String[] buttonLabels;
   private Button[] buttons;
   private int defaultButtonIndex;
   private String title;
   private Image titleImage;
   private Image image = null;
   private Label errorLabel;
   private Composite composite;
   private Collection<ArtifactType> descriptors;
   private ObjectList<ArtifactType> descriptorsList;
   private ArtifactType entry = null;
   String textField = "";
   String validationRegularExpression = null;
   String validationErrorString = "";

   /**
    * The custom dialog area.
    */
   private Control customArea;

   /**
    * Create a message dialog. Note that the dialog will have no visual representation (no widgets) until it is told to
    * open.
    * <p>
    * The labels of the buttons to appear in the button bar are supplied in this constructor as an array. The
    * <code>open</code> method will return the index of the label in this array corresponding to the button that was
    * pressed to close the dialog. If the dialog was dismissed without pressing a button (ESC, etc.) then -1 is
    * returned. Note that the <code>open</code> method blocks.
    * </p>
    * 
    * @param parentShell the parent shell
    * @param dialogTitle the dialog title, or <code>null</code> if none
    * @param dialogTitleImage the dialog title image, or <code>null</code> if none
    * @param dialogMessage the dialog message
    * @param dialogImageType one of the following values:
    *           <ul>
    *           <li><code>MessageDialog.NONE</code> for a dialog with no image</li>
    *           <li><code>MessageDialog.ERROR</code> for a dialog with an error image</li>
    *           <li><code>MessageDialog.INFORMATION</code> for a dialog with an information image</li>
    *           <li><code>MessageDialog.QUESTION </code> for a dialog with a question image</li>
    *           <li><code>MessageDialog.WARNING</code> for a dialog with a warning image</li>
    *           </ul>
    * @param dialogButtonLabels an array of labels for the buttons in the button bar
    * @param defaultIndex the index in the button label array of the default button
    */
   public ArtifactDescriptorDialog(Shell parentShell, String dialogTitle, Image dialogTitleImage, String dialogMessage, int dialogImageType, String[] dialogButtonLabels, int defaultIndex, Collection<ArtifactType> descriptors) {
      super(parentShell);
      this.title = dialogTitle;
      this.titleImage = dialogTitleImage;
      this.message = dialogMessage;
      switch (dialogImageType) {
         case ERROR: {
            this.image = parentShell.getDisplay().getSystemImage(SWT.ICON_ERROR);
            break;
         }
         case INFORMATION: {
            this.image = parentShell.getDisplay().getSystemImage(SWT.ICON_INFORMATION);
            break;
         }
         case QUESTION: {
            this.image = parentShell.getDisplay().getSystemImage(SWT.ICON_QUESTION);
            break;
         }
         case WARNING: {
            this.image = parentShell.getDisplay().getSystemImage(SWT.ICON_WARNING);
            break;
         }
      }
      this.buttonLabels = dialogButtonLabels;
      this.defaultButtonIndex = defaultIndex;
      this.descriptors = descriptors;
   }

   public void setSelectionListener(SelectionListener listener) {
      for (int i = 0; i < buttons.length; i++) {
         Button button = buttons[i];
         button.addSelectionListener(listener);
      }
   }

   /**
    * Calling will enable dialog to loose focus
    */
   public void setModeless() {
      setShellStyle(SWT.DIALOG_TRIM | SWT.MODELESS);
      setBlockOnOpen(false);
   }

   /**
    * override this method to make own checks on entry this will be called with every keystroke
    * 
    * @return true if entry is valid
    */
   public boolean isEntryValid() {
      if (validationRegularExpression == null) {
         return true;
      }
      return true;
   }

   public void setValidationRegularExpression(String regExp) {
      validationRegularExpression = regExp;
   }

   public void setValidationErrorString(String errorText) {
      validationErrorString = errorText;
   }

   /*
    * (non-Javadoc) Method declared on Dialog.
    */
   protected void buttonPressed(int buttonId) {
      setReturnCode(buttonId);
      close();
   }

   /*
    * (non-Javadoc) Method declared in Window.
    */
   protected void configureShell(Shell shell) {
      super.configureShell(shell);
      if (title != null) shell.setText(title);
      if (titleImage != null) shell.setImage(titleImage);
   }

   /*
    * (non-Javadoc) Method declared on Dialog.
    */
   protected void createButtonsForButtonBar(Composite parent) {
      buttons = new Button[buttonLabels.length];
      for (int i = 0; i < buttonLabels.length; i++) {
         String label = buttonLabels[i];
         Button button = createButton(parent, i, label, defaultButtonIndex == i);
         buttons[i] = button;
      }
      updateButtons();
   }

   /**
    * Creates and returns the contents of an area of the dialog which appears below the message and above the button
    * bar.
    * <p>
    * The default implementation of this framework method returns <code>null</code>. Subclasses may override.
    * </p>
    * 
    * @param parent parent composite to contain the custom area
    * @return Control custom area control, or <code>null</code>
    */
   protected Control createCustomArea(Composite parent) {
      return null;
   }

   /**
    * This implementation of the <code>Dialog</code> framework method creates and lays out a composite and calls
    * <code>createMessageArea</code> and <code>createCustomArea</code> to populate it. Subclasses should override
    * <code>createCustomArea</code> to add contents below the message.
    */
   @SuppressWarnings("unchecked")
   protected Control createDialogArea(Composite parent) {

      // create message area
      createMessageArea(parent);

      // create the top level composite for the dialog area
      composite = new Composite(parent, SWT.NONE);
      GridLayout layout = new GridLayout();
      layout.marginHeight = 0;
      layout.marginWidth = 0;
      composite.setLayout(layout);

      // Create error label
      errorLabel = new Label(composite, SWT.NONE);
      errorLabel.setSize(errorLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT));
      errorLabel.setText("");

      new Label(composite, SWT.NULL).setText("Select artifact descriptor:");
      descriptorsList =
            new ObjectList<ArtifactType>(composite, SWT.BORDER | SWT.READ_ONLY | SWT.SCROLL_PAGE);
      descriptorsList.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, true, true));

      if (descriptors != null) {
         for (ArtifactType descriptor : descriptors) {
            descriptorsList.add(descriptor, descriptor.getName());
         }
         descriptorsList.select(0);
      }

      descriptorsList.addSelectionListener(new SelectionListener() {
         public void widgetSelected(SelectionEvent e) {
            updateButtons();
         }

         public void widgetDefaultSelected(SelectionEvent e) {
         }
      });
      descriptorsList.setFocus();

      GridData data = new GridData(GridData.FILL_BOTH);
      data.horizontalSpan = 2;

      composite.setLayoutData(data);
      composite.setFont(parent.getFont());

      // allow subclasses to add custom controls
      customArea = createCustomArea(composite);

      // If it is null create a dummy label for spacing purposes
      if (customArea == null) customArea = new Label(composite, SWT.NULL);
      return composite;
   }

   @SuppressWarnings("unchecked")
   private void updateButtons() {
      if (descriptorsList != null) {
         entry = descriptorsList.getSelectedItem();

         if (entry == null || !isEntryValid()) {
            buttons[defaultButtonIndex].setEnabled(false);
            errorLabel.setText(validationErrorString);
            errorLabel.update();
            composite.layout();
         } else {
            buttons[defaultButtonIndex].setEnabled(true);
            errorLabel.setText("");
            errorLabel.update();
            composite.layout();
         }
      }
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

   /*
    * (non-Javadoc) Method declared on Dialog. Sets a return code of -1 since none of the dialog
    * buttons were pressed to close the dialog.
    */
   protected void handleShellCloseEvent() {
      super.handleShellCloseEvent();
      setReturnCode(-1);
   }

   /**
    * Convenience method to open a simple confirm (OK/Cancel) dialog.
    * 
    * @param parent the parent shell of the dialog, or <code>null</code> if none
    * @param title the dialog's title, or <code>null</code> if none
    * @param message the message
    * @return <code>true</code> if the user presses the OK button, <code>false</code> otherwise
    */
   public static boolean openTextEntry(Shell parent, String title, String message) {
      MessageDialog dialog = new MessageDialog(parent, title, null,
      // accept the default window icon
            message, QUESTION, new String[] {IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL}, 0);
      // OK is the default
      return dialog.open() == 0;
   }

   public ArtifactType getEntry() {
      return entry;
   }

   public String getTextField() {
      return textField;
   }

   public void setEntry(String entry) {
      // text.setText(entry);
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

   /*
    * @see IconAndMessageDialog#getImage()
    */
   public Image getImage() {
      return image;
   }
}
