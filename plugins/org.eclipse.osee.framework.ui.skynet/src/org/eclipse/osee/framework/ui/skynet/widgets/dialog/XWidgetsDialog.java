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
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.IShellCloseEvent;
import org.eclipse.osee.framework.ui.skynet.XWidgetParser;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.widgets.XCheckBox;
import org.eclipse.osee.framework.ui.skynet.widgets.XCombo;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XText;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.DefaultXWidgetOptionResolver;
import org.eclipse.osee.framework.ui.skynet.widgets.util.IDynamicWidgetLayoutListener;
import org.eclipse.osee.framework.ui.skynet.widgets.util.SwtXWidgetRenderer;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetPage;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetRendererItem;
import org.eclipse.osee.framework.ui.swt.Displays;
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
public abstract class XWidgetsDialog extends MessageDialog implements IDynamicWidgetLayoutListener {

   protected Composite areaComposite;
   private String errorString = "";
   protected Button ok;
   protected Label errorLabel;
   private final List<IShellCloseEvent> closeEventListeners = new ArrayList<>();
   private final List<XWidget> xWidgets = new ArrayList<>();
   private final Map<String, String> xTextKeyValueMap = new HashMap<>();
   private final Map<String, String> xComboKeyValueMap = new HashMap<>();
   private final Map<String, Boolean> xCheckBoxKeyValueMap = new HashMap<>();

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
   private SwtXWidgetRenderer widgetRenderer;

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
      for (Entry<String, String> entry : xTextKeyValueMap.entrySet()) {
         setXTextString(entry.getKey(), entry.getValue());
      }
      for (Entry<String, String> entry : xComboKeyValueMap.entrySet()) {
         setXComboString(entry.getKey(), entry.getValue());
      }
      for (Entry<String, Boolean> entry : xCheckBoxKeyValueMap.entrySet()) {
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

   protected void createWidgets(Composite parent) {
      try {
         List<XWidgetRendererItem> layoutDatas = getDynamicXWidgetLayouts();
         XWidgetPage workPage = new XWidgetPage(layoutDatas, new DefaultXWidgetOptionResolver(), this);
         workPage.createBody(null, parent, null, null, true);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   public List<XWidgetRendererItem> getLayoutDatas() {
      widgetRenderer = new SwtXWidgetRenderer();
      return XWidgetParser.extractWorkAttributes(widgetRenderer, getXWidgetsXml());
   }

   public abstract String getXWidgetsXml();

   private List<XWidgetRendererItem> getDynamicXWidgetLayouts() throws Exception {
      List<XWidgetRendererItem> itemsToReturn = new ArrayList<>();
      itemsToReturn.addAll(getLayoutDatas());
      return itemsToReturn;
   }

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

   public void handleModified() {
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

   public String getXtextString(String idOrLabel) {
      return ((XText) getXWidget(idOrLabel)).get();
   }

   public void setXTextString(String idOrLabel, String text) {
      xTextKeyValueMap.put(idOrLabel, text);
      if (getXWidget(idOrLabel) != null) {
         ((XText) getXWidget(idOrLabel)).set(text);
      }
   }

   public String getXComboString(String idOrLabel) {
      return ((XCombo) getXWidget(idOrLabel)).get();
   }

   public void setXComboString(String idOrLabel, String selected) {
      xComboKeyValueMap.put(idOrLabel, selected);
      if (getXWidget(idOrLabel) != null) {
         ((XCombo) getXWidget(idOrLabel)).setDefaultValue(selected);
      }
   }

   public boolean getXCheckBoxChecked(String idOrLabel) {
      return ((XCheckBox) getXWidget(idOrLabel)).isChecked();
   }

   public void setXCheckBoxChecked(String idOrLabel, boolean checked) {
      xCheckBoxKeyValueMap.put(idOrLabel, checked);
      if (getXWidget(idOrLabel) != null) {
         ((XCheckBox) getXWidget(idOrLabel)).set(checked);
      }
   }

   public XWidget getXWidget(String idOrLabel) {
      for (XWidget xWidget : xWidgets) {
         if (idOrLabel.equals(xWidget.getId()) || idOrLabel.equals(xWidget.getLabel())) {
            return xWidget;
         }
      }
      return null;
   }

   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, SwtXWidgetRenderer dynamicXWidgetLayout, XModifiedListener xModListener, boolean isEditable) {
      xWidgets.add(xWidget);
   }

}
