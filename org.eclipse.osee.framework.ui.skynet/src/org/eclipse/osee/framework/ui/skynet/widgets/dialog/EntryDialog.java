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
package org.eclipse.osee.framework.ui.skynet.widgets.dialog;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.IShellCloseEvent;
import org.eclipse.osee.framework.ui.skynet.FontManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.osee.framework.ui.skynet.widgets.HyperLinkLabel;
import org.eclipse.osee.framework.ui.skynet.widgets.XText;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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
import org.eclipse.swt.widgets.Shell;

public class EntryDialog extends MessageDialog {

   private XText text;
   private Composite areaComposite;
   private String entryText = "";
   private NumberFormat numberFormat;
   private String errorString = "";
   private Button ok;
   private Label errorLabel;
   private boolean fillVertically = false;
   private Button fontButton;

   private final List<IShellCloseEvent> closeEventListeners = new ArrayList<IShellCloseEvent>();
   private final String dialogTitle;

   public EntryDialog(String dialogTitle, String dialogMessage) {
      super(Display.getCurrent().getActiveShell(), dialogTitle, null, dialogMessage, MessageDialog.QUESTION,
            new String[] {"OK", "Cancel"}, 0);
      this.dialogTitle = dialogTitle;
   }

   public EntryDialog(Shell parentShell, String dialogTitle, Image dialogTitleImage, String dialogMessage, int dialogImageType, String[] dialogButtonLabels, int defaultIndex) {
      super(parentShell, dialogTitle, dialogTitleImage, dialogMessage, dialogImageType, dialogButtonLabels,
            defaultIndex);
      this.dialogTitle = dialogTitle;
   }

   private final ModifyListener textModifyListener = new ModifyListener() {
      public void modifyText(ModifyEvent e) {
         handleModified();
      }
   };

   private final MouseMoveListener compListener = new MouseMoveListener() {
      public void mouseMove(MouseEvent e) {
         setInitialButtonState();
      }
   };

   @Override
   protected Control createCustomArea(Composite parent) {
      areaComposite = new Composite(parent, SWT.NONE);
      areaComposite.setLayout(new GridLayout(2, false));
      areaComposite.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));
      areaComposite.addMouseMoveListener(compListener);

      createErrorLabel(areaComposite);
      createTextBox();

      if (isFillVertically()) {
         HyperLinkLabel edit = new HyperLinkLabel(parent, SWT.None);
         edit.setText("open in editor");
         edit.addListener(SWT.MouseUp, new Listener() {

            @Override
            public void handleEvent(Event event) {
               XResultData resultData = new XResultData();
               resultData.addRaw(entryText);
               try {
                  resultData.report(dialogTitle);
               } catch (OseeCoreException ex) {
                  OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
               }
               close();
            }
         });
      }
      createExtendedArea(areaComposite);
      areaComposite.layout();
      parent.layout();
      return areaComposite;
   }

   private void createErrorLabel(Composite parent) {
      Composite composite = new Composite(parent, SWT.NONE);
      composite.setLayout(new GridLayout(3, false));
      GridData gd1 = new GridData(GridData.FILL_HORIZONTAL);
      gd1.horizontalSpan = 2;
      composite.setLayoutData(gd1);

      if (fillVertically) {
         createVerticalFill(composite);
      }

      errorLabel = new Label(composite, SWT.NONE);
      errorLabel.setSize(errorLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT));
      errorLabel.setText("");
      if (!fillVertically) {
         GridData gd = new GridData();
         gd.horizontalSpan = 3;
         errorLabel.setLayoutData(gd);
      }
   }

   private void createVerticalFill(Composite headerComp) {
      // Create error label
      Button button = new Button(headerComp, SWT.PUSH);
      button.setText("Clear");
      button.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            super.widgetSelected(e);
            text.setText("");
         }
      });

      fontButton = new Button(headerComp, SWT.CHECK);
      fontButton.setText("Fixed Font");
      fontButton.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            super.widgetSelected(e);
            if (fontButton.getSelection()) {
               text.setFont(getFont());
            } else {
               text.setFont(null);
            }
         }
      });
   }

   private Font getFont() {
      return FontManager.getFont("Courier New", 8, SWT.NORMAL);
   }

   private void createTextBox() {
      text = new XText();
      text.setFillHorizontally(true);
      text.setFocus();
      text.setDisplayLabel(false);
      if (fillVertically) {
         text.setFillVertically(true);
         text.setHeight(200);
         text.setFont(getFont());
      }
      text.createWidgets(areaComposite, 2);
      text.setFocus();
      if (!entryText.equals("")) {
         text.set(entryText);
      }

      text.addModifyListener(textModifyListener);
   }

   @Override
   protected boolean isResizable() {
      return true;
   }

   protected void createExtendedArea(Composite parent) {
   }

   public void setInitialButtonState() {
      if (ok == null) {
         ok = getButton(0);
         handleModified();
      }
      areaComposite.removeMouseMoveListener(compListener);
   }

   public void handleModified() {
      if (text != null) {
         entryText = text.get();
         if (!isEntryValid()) {
            getButton(getDefaultButtonIndex()).setEnabled(false);
            errorLabel.setText(errorString);
            errorLabel.update();
            areaComposite.layout();
         } else {
            getButton(getDefaultButtonIndex()).setEnabled(true);
            errorLabel.setText("");
            errorLabel.update();
            areaComposite.layout();
         }
      }
   }

   public String getEntry() {
      return entryText;
   }

   public void setEntry(String entry) {
      if (text != null) {
         text.set(entry);
      }
      this.entryText = entry;
   }

   /**
    * override this method to make own checks on entry this will be called with every keystroke
    * 
    * @return true if entry is valid
    */
   public boolean isEntryValid() {
      if (numberFormat == null) {
         return true;
      }

      try {
         numberFormat.parse(text.get());
      } catch (ParseException ex) {
         return false;
      }

      return true;
   }

   public void setValidationErrorString(String errorString) {
      this.errorString = errorString;
   }

   public void setNumberFormat(NumberFormat numberFormat) {
      this.numberFormat = numberFormat;
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

}
