/*********************************************************************
 * Copyright (c) 2020 Boeing
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.IShellCloseEvent;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.widgets.XCheckBoxWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XTextWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.builder.XWidgetBuilder;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetPage;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetSwtRenderer;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetSwtRendererListener;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * Generic Dialog that provides its UI through XWidget xml specification. Overrides and provide getXWidgetXml.
 *
 * @author Donald G. Dunne
 */
public abstract class XWidgetsDialog extends MessageDialog implements XWidgetSwtRendererListener {

   protected Composite areaComposite;
   private String errorString = "";
   protected Button ok;
   protected Label errorLabel;
   private final List<IShellCloseEvent> closeEventListeners = new ArrayList<>();
   private final List<XWidget> xWidgets = new ArrayList<>();
   private final Map<Long, String> xTextKeyValueMap = new HashMap<>();
   private final Map<Long, String> xComboKeyValueMap = new HashMap<>();
   private final Map<Long, Boolean> xCheckBoxKeyValueMap = new HashMap<>();

   public XWidgetsDialog(String dialogTitle, String dialogMessage) {
      this(Displays.getActiveShell(), dialogTitle, null, dialogMessage, MessageDialog.QUESTION,
         new String[] {"OK", "Cancel"}, 0);
   }

   public XWidgetsDialog(Shell parentShell, String dialogTitle, Image dialogTitleImage, String dialogMessage, int dialogImageType, String[] dialogButtonLabels, int defaultIndex) {
      super(parentShell, dialogTitle, dialogTitleImage, dialogMessage, dialogImageType, dialogButtonLabels,
         defaultIndex);
   }

   protected final MouseMoveListener compListener = new MouseMoveListener() {
      @Override
      public void mouseMove(MouseEvent e) {
         setInitialButtonState();
      }
   };
   protected Composite customAreaParent;
   protected XWidgetBuilder wb;

   @Override
   protected Control createCustomArea(Composite parent) {
      this.customAreaParent = parent;
      areaComposite = new Composite(parent, SWT.NONE);
      areaComposite.setLayout(new GridLayout(2, false));
      GridData gd = new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL);
      gd.widthHint = 600;
      areaComposite.setLayoutData(gd);
      areaComposite.addMouseMoveListener(compListener);

      createErrorLabel(areaComposite);

      createWidgets(areaComposite);

      createExtendedArea(areaComposite);

      populateWidgets();

      areaComposite.layout();
      parent.layout();
      return areaComposite;
   }

   private void populateWidgets() {
      for (Entry<Long, String> entry : xTextKeyValueMap.entrySet()) {
         setXTextString(entry.getKey(), entry.getValue());
      }
      for (Entry<Long, String> entry : xComboKeyValueMap.entrySet()) {
         setXComboString(entry.getKey(), entry.getValue());
      }
      for (Entry<Long, Boolean> entry : xCheckBoxKeyValueMap.entrySet()) {
         setXCheckBoxChecked(entry.getKey(), entry.getValue());
      }
   }

   protected void createErrorLabel(Composite parent) {
      Composite composite = new Composite(parent, SWT.NONE);
      composite.setLayout(new GridLayout(3, false));
      GridData gd1 = new GridData(GridData.FILL_HORIZONTAL);
      gd1.horizontalSpan = 2;
      composite.setLayoutData(gd1);

      errorLabel = new Label(composite, SWT.NONE);
      errorLabel.setSize(errorLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT));
      errorLabel.setText("");
   }

   protected XWidgetBuilder createWidgetBuilder() {
      if (wb == null) {
         wb = new XWidgetBuilder();
      }
      return wb;
   }

   // Return artifact if widgets art artifact based
   protected Artifact getArtifact() {
      return null;
   }

   protected void createWidgets(Composite parent) {
      try {
         List<XWidgetData> widDatas = getXWidgetItems();
         XWidgetPage workPage = new XWidgetPage(widDatas, this);
         workPage.createBody(null, parent, getArtifact(), null, true);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   public abstract List<XWidgetData> getXWidgetItems();

   @Override
   protected boolean isResizable() {
      return true;
   }

   protected void createExtendedArea(Composite parent) {
      // provided for subclass implementation
   }

   public void setInitialButtonState() {
      if (ok == null) {
         ok = getButton(0);
         handleModified();
      }
      areaComposite.removeMouseMoveListener(compListener);
   }

   /**
    * override this method to make own checks on entry this will be called with every keystroke
    *
    * @return true if entry is valid
    */
   public boolean isEntryValid() {
      return true;
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

   public void handleModified() {
      boolean valid = isEntryValid();
      if (Widgets.isAccessible(getButton(getDefaultButtonIndex()))) {
         getButton(getDefaultButtonIndex()).setEnabled(valid);
      }
      errorLabel.setText(errorString);
      errorLabel.update();
      errorLabel.getParent().layout();
      areaComposite.layout();
      if (valid) {
         errorLabel.setForeground(Displays.getSystemColor(SWT.COLOR_BLACK));
      } else {
         errorLabel.setForeground(Displays.getSystemColor(SWT.COLOR_RED));
      }
   }

   protected void logError(String text) {
      if (Strings.isValid(text)) {
         errorString = text;
      } else {
         errorString = "";
      }
   }

   public String getXtextString(Long id) {
      return ((XTextWidget) getXWidget(id)).get();
   }

   public void setXTextString(Long id, String text) {
      xTextKeyValueMap.put(id, text);
      if (getXWidget(id) != null) {
         ((XTextWidget) getXWidget(id)).set(text);
      }
   }

   public String getXComboString(Long id) {
      return ((XComboWidget) getXWidget(id)).get();
   }

   public void setXComboString(Long id, String selected) {
      xComboKeyValueMap.put(id, selected);
      if (getXWidget(id) != null) {
         ((XComboWidget) getXWidget(id)).setDefaultValue(selected);
      }
   }

   public boolean getXCheckBoxChecked(Long id) {
      return ((XCheckBoxWidget) getXWidget(id)).isChecked();
   }

   public void setXCheckBoxChecked(Long id, boolean checked) {
      xCheckBoxKeyValueMap.put(id, checked);
      if (getXWidget(id) != null) {
         ((XCheckBoxWidget) getXWidget(id)).set(checked);
      }
   }

   public XWidget getXWidget(Long id) {
      for (XWidget xWidget : xWidgets) {
         if (id.equals(xWidget.getId())) {
            return xWidget;
         }
      }
      return null;
   }

   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, XWidgetSwtRenderer swtXWidgetRenderer,
      XModifiedListener xModListener, boolean isEditable) {
      xWidgets.add(xWidget);
   }

}
