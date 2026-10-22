/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.skynet.widgets.dialog;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.core.widget.validate.IValueValidator;
import org.eclipse.osee.framework.core.widget.validate.XXNoOpValidator;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.plugin.util.IShellCloseEvent;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXAbstractSTextWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXTextWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.util.listen.XModifiedListener;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.FontManager;
import org.eclipse.osee.framework.ui.swt.HyperLinkLabel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Donald G. Dunne
 */
public class EntryDialog extends MessageDialog {

   protected XXTextWidget textWidget;
   protected Composite areaComposite;
   protected String entryText = "";
   private String errorString = "";
   protected Button okButton;
   protected boolean okSelected = false;
   protected Button clearButton;
   protected boolean clearSelected = false;
   protected boolean includeClear = false;
   protected Label errorLabel;
   protected boolean fillVertically = false;
   private Button fontButton;
   private String label;
   protected Integer textHeight = null;
   protected boolean addOpenInEditorOption = true;
   protected Composite customAreaParent;
   protected IValueValidator validator = XXNoOpValidator.instance;
   private boolean spellCheck = true;

   private final List<IShellCloseEvent> closeEventListeners = new ArrayList<>();
   private final String dialogTitle;

   public EntryDialog(String dialogTitle, String dialogMessage) {
      this(dialogTitle, dialogMessage, false);
   }

   public EntryDialog(String dialogTitle, String dialogMessage, boolean includeClear) {
      this(Displays.getActiveShell(), dialogTitle, null, dialogMessage, MessageDialog.QUESTION,
         (includeClear ? new String[] {"OK", "Cancel", "Clear"} : new String[] {"OK", "Cancel"}), 0);
      this.includeClear = includeClear;
   }

   private EntryDialog(Shell parentShell, String dialogTitle, Image dialogTitleImage, String dialogMessage, int dialogImageType, String[] dialogButtonLabels, int defaultIndex) {
      super(parentShell, dialogTitle, dialogTitleImage, dialogMessage, dialogImageType, dialogButtonLabels,
         defaultIndex);
      this.dialogTitle = dialogTitle;
   }

   private final XModifiedListener textModifyListener = new XModifiedListener() {

      @Override
      public void widgetModified(XWidget widget) {
         handleModified();
      }
   };

   protected final MouseMoveListener compListener = new MouseMoveListener() {
      @Override
      public void mouseMove(MouseEvent e) {
         setInitialButtonState();
      }
   };

   @Override
   protected Control createCustomArea(Composite parent) {
      this.customAreaParent = parent;
      areaComposite = new Composite(parent, SWT.NONE);
      areaComposite.setLayout(new GridLayout(1, false));
      GridData gd = new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL);
      gd.widthHint = 600;
      areaComposite.setLayoutData(gd);
      areaComposite.addMouseMoveListener(compListener);

      createErrorLabel(areaComposite);
      createTextBox();

      if (isAddOpenInEditorOption()) {
         createOpenInEditorHyperlink(parent);
      }
      createExtendedArea(areaComposite);
      areaComposite.layout();
      parent.layout();
      return areaComposite;
   }

   @Override
   protected Control createButtonBar(Composite parent) {
      Control c = super.createButtonBar(parent);
      okButton = getButton(0);
      okButton.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            okSelected = true;
         }
      });
      if (includeClear) {
         clearButton = getButton(2);
         clearButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
               clearSelected = true;
            }
         });
      }
      return c;
   }

   protected void createOpenInEditorHyperlink(Composite parent) {
      if (isFillVertically()) {
         HyperLinkLabel edit = new HyperLinkLabel(parent, SWT.None);
         edit.setText("open in editor");
         edit.addListener(SWT.MouseUp, new Listener() {

            @Override
            public void handleEvent(Event event) {
               XResultData resultData = new XResultData();
               resultData.addRaw(entryText);
               XResultDataUI.report(resultData, dialogTitle);
               close();
            }
         });
      }
   }

   protected void createErrorLabel(Composite parent) {
      Composite composite = new Composite(parent, SWT.NONE);
      composite.setLayout(new GridLayout(3, false));
      GridData gd1 = new GridData(GridData.FILL_HORIZONTAL);
      gd1.horizontalSpan = 2;
      composite.setLayoutData(gd1);

      if (fillVertically) {
         createClearFixedFontWidgets(composite);
      }

      errorLabel = new Label(composite, SWT.NONE);
      errorLabel.setSize(errorLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT));
      errorLabel.setText("");
      if (!fillVertically) {
         GridData gd = new GridData(GridData.FILL_HORIZONTAL);
         gd.horizontalSpan = 3;
         errorLabel.setLayoutData(gd);
      }
   }

   protected void createClearFixedFontWidgets(Composite headerComp) {
      // Create error label
      Button button = new Button(headerComp, SWT.PUSH);
      button.setText("Clear");
      button.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            super.widgetSelected(e);
            textWidget.setSelected("");
         }
      });

      Button copyButton = new Button(headerComp, SWT.PUSH);
      copyButton.setText("Copy");
      copyButton.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            Clipboard clipboard = new Clipboard(Display.getCurrent());
            TextTransfer textTransfer = TextTransfer.getInstance();
            clipboard.setContents(new Object[] {textWidget.getSelectedFirst()}, new Transfer[] {textTransfer});
         }
      });

      fontButton = new Button(headerComp, SWT.CHECK);
      fontButton.setText("Fixed Font");
      fontButton.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            super.widgetSelected(e);
            if (fontButton.getSelection()) {
               textWidget.setFont(getFont());
            } else {
               textWidget.setFont(null);
            }
         }
      });
   }

   protected Font getFont() {
      return FontManager.getFont("Courier New", 8, SWT.NORMAL);
   }

   protected void createTextBox() {
      String labelStr = Strings.isValid(label) ? label : "";

      textWidget = new XXTextWidget(labelStr);
      textWidget.setFocus();
      textWidget.setSpellCheck(spellCheck);
      if (!Strings.isValid(label)) {
         textWidget.setDisplayLabel(false);
      }
      if (fillVertically) {
         textWidget.setFillVertically(true);
         textWidget.setHeight(textHeight == null ? 200 : textHeight);
      }
      textWidget.createWidgets(areaComposite, 2);
      textWidget.setFocus();
      if (Strings.isValid(entryText)) {
         textWidget.setSelected(entryText);
      }
      textWidget.addXModifiedListener(textModifyListener);
      addContextMenu(textWidget.getStyledText());
   }

   @Override
   protected boolean isResizable() {
      return true;
   }

   protected void createExtendedArea(Composite parent) {
      // provided for subclass implementation
   }

   public void setInitialButtonState() {
      if (okButton == null) {
         okButton = getButton(0);
         handleModified();
      }
      areaComposite.removeMouseMoveListener(compListener);
   }

   public void handleModified() {
      if (textWidget != null) {
         entryText = textWidget.getSelectedFirst();
         if (!isEntryValid()) {
            getButton(getDefaultButtonIndex()).setEnabled(false);
         } else {
            getButton(getDefaultButtonIndex()).setEnabled(true);
         }
      }
   }

   public String getEntry() {
      return entryText;
   }

   public void setEntry(String entry) {
      if (textWidget != null) {
         textWidget.setSelected(entry);
      }
      this.entryText = entry;
   }

   /**
    * override this method to make own checks on entry this will be called with every keystroke
    *
    * @return true if entry is valid
    */
   public boolean isEntryValid() {
      if (validator == null) {
         return true;
      }
      try {
         String selectedFirst = textWidget.getSelectedFirst();
         XResultData valid = validator.isValid(selectedFirst);
         return valid.isOK();
      } catch (Exception ex) {
         return false;
      }
   }

   public void setValidationErrorString(String errorString) {
      this.errorString = errorString;
   }

   /**
    * Calling will enable dialog to loose focus
    */
   public void setModeless() {
      setShellStyle(SWT.DIALOG_TRIM | SWT.MODELESS);
      setBlockOnOpen(false);
   }

   public void setSelectionListener(SelectionListener listener) {
      for (int i = 0; i < getButtonLabels().length; i++) {
         Button button = getButton(i);
         button.addSelectionListener(listener);
      }
   }

   public boolean isFillVertically() {
      return fillVertically;
   }

   public void setFillVertically(boolean fillVertically) {
      this.fillVertically = fillVertically;
   }

   @Override
   protected void handleShellCloseEvent() {
      super.handleShellCloseEvent();
      for (IShellCloseEvent event : closeEventListeners) {
         event.onClose();
      }
   }

   public void addShellCloseEventListeners(IShellCloseEvent event) {
      closeEventListeners.add(event);
   }

   protected XXAbstractSTextWidget getText() {
      return textWidget;
   }

   public void setLabel(String label) {
      this.label = label;
   }

   public void setTextHeight(Integer textHeight) {
      this.textHeight = textHeight;
   }

   public String getErrorString() {
      return errorString;
   }

   public void setErrorString(String errorString) {
      this.errorString = errorString;
   }

   public Label getMessageLabel() {
      return messageLabel;
   }

   public Label getErrorLabel() {
      return errorLabel;
   }

   /**
    * Since adding new menu replaces the default menu, we must re-create the default copy/paste options
    */
   protected void addContextMenu(final StyledText control) {
      Menu menu = new Menu(control);
      MenuItem item = new MenuItem(menu, SWT.PUSH);
      item.setText("Cut");
      item.addListener(SWT.Selection, new Listener() {
         @Override
         public void handleEvent(Event event) {
            control.cut();
         }
      });
      item = new MenuItem(menu, SWT.PUSH);
      item.setText("Copy");
      item.addListener(SWT.Selection, new Listener() {
         @Override
         public void handleEvent(Event event) {
            control.copy();
         }
      });
      item = new MenuItem(menu, SWT.PUSH);
      item.setText("Paste");
      item.addListener(SWT.Selection, new Listener() {
         @Override
         public void handleEvent(Event event) {
            control.paste();
         }
      });
      // Add Paste-and-Go menu option
      item = new MenuItem(menu, SWT.PUSH);
      item.setText("Paste-and-Go");
      item.addListener(SWT.Selection, new Listener() {
         @Override
         public void handleEvent(Event event) {
            control.setText("");
            control.paste();
            okPressed();
         }
      });

      item = new MenuItem(menu, SWT.PUSH);
      item.setText("Select All");
      item.addListener(SWT.Selection, new Listener() {
         @Override
         public void handleEvent(Event event) {
            control.selectAll();
         }
      });

      control.setMenu(menu);
   }

   protected void updateErrorLabel(boolean error, String text) {
      if (error) {
         getButton(getDefaultButtonIndex()).setEnabled(false);
         errorLabel.setText(text);
         errorLabel.update();
         areaComposite.layout();
         errorLabel.setForeground(Displays.getSystemColor(SWT.COLOR_RED));
      } else {
         getButton(getDefaultButtonIndex()).setEnabled(true);
         errorLabel.setText(text);
         errorLabel.update();
         errorLabel.setForeground(Displays.getSystemColor(SWT.COLOR_BLACK));
         areaComposite.layout();
      }
   }

   public boolean isAddOpenInEditorOption() {
      return addOpenInEditorOption;
   }

   public void setAddOpenInEditorOption(boolean addOpenInEditorOption) {
      this.addOpenInEditorOption = addOpenInEditorOption;
   }

   public void setMultiline(boolean multiline) {
      setFillVertically(multiline);
   }

   public void setSpellCheck(boolean spellCheck) {
      this.spellCheck = spellCheck;
      if (textWidget != null) {
         textWidget.setSpellCheck(spellCheck);
      }
   }

   public IValueValidator getValidator() {
      return validator;
   }

   public void setValidator(IValueValidator validator) {
      this.validator = validator;
   }

   public boolean isOkSelected() {
      return okSelected;
   }

   public boolean isClearSelected() {
      return clearSelected;
   }

}
