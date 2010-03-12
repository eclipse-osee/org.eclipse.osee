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

package org.eclipse.osee.framework.ui.skynet.widgets;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.jdk.core.type.MutableBoolean;
import org.eclipse.osee.framework.jdk.core.util.AXml;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * Abstract class for all widgets used in Wizards and Editors
 */
public abstract class XWidget {
   public final static String XWIDGET_DATA_KEY = "xWidget";

   private IManagedForm managedForm;

   protected Label labelWidget = null;
   private String label = "";
   private String xmlRoot = "";
   private String xmlSubRoot = "";
   private String toolTip = null;
   private boolean requiredEntry = false;
   private boolean editable = true;
   private final MutableBoolean isNotificationAllowed = new MutableBoolean(true);

   protected boolean verticalLabel = false;
   protected boolean fillVertically = false;
   protected boolean fillHorizontally = false;

   public boolean isFillHorizontally() {
      return fillHorizontally;
   }

   private boolean displayLabel = true;
   private final Set<XModifiedListener> modifiedListeners = new LinkedHashSet<XModifiedListener>();
   private MouseListener mouseLabelListener;
   protected FormToolkit toolkit;

   public XWidget(String label) {
      this.label = label;
   }

   public XWidget(String label, String xmlRoot) {
      this.label = label;
      this.xmlRoot = xmlRoot;
   }

   public XWidget(String label, String xmlRoot, String xmlSubRoot) {
      this.label = label;
      this.xmlRoot = xmlRoot;
      this.xmlSubRoot = xmlSubRoot;
   }

   public void setToolTip(String toolTip) {
      this.toolTip = toolTip;
      if (this.labelWidget != null && !labelWidget.isDisposed()) {
         this.labelWidget.setToolTipText(toolTip);
      }
   }

   public void addXModifiedListener(XModifiedListener listener) {
      modifiedListeners.add(listener);
   }

   public void notifyXModifiedListeners() {
      if (areNotificationsAllowed()) {
         for (XModifiedListener listener : modifiedListeners) {
            listener.widgetModified(this);
         }
      }
   }

   public boolean areNotificationsAllowed() {
      return isNotificationAllowed.getValue();
   }

   protected IManagedForm getManagedForm() {
      return managedForm;
   }

   public boolean isInForm() {
      return getManagedForm() != null;
   }

   protected IMessageManager getMessageManager() {
      return getManagedForm() != null ? managedForm.getMessageManager() : null;
   }

   public void setMessage(String messageId, String messageText, int type) {
      IMessageManager messageManager = getMessageManager();
      if (messageManager != null && isFormReady()) {
         messageManager.addMessage(messageId, messageText, null, type);
      }
   }

   public boolean isFormReady() {
      // Set to true if outside of a form;
      boolean result = managedForm == null;
      if (managedForm != null) {
         result = !managedForm.getForm().isDisposed();
      }
      return result;
   }

   public void setControlCausedMessage(String messageId, String messageText, int type) {
      IMessageManager messageManager = getMessageManager();
      if (messageManager != null && isFormReady()) {
         messageManager.addMessage(messageId, messageText, null, type, getErrorMessageControl());
      }
   }

   public void setControlCausedMessageByObject(String messageText, int type) {
      IMessageManager messageManager = getMessageManager();
      if (messageManager != null && isFormReady()) {
         messageManager.addMessage(this, messageText, null, type, getErrorMessageControl());
      }
   }

   public void removeControlCausedMessageByObject() {
      IMessageManager messageManager = getMessageManager();
      if (messageManager != null && isFormReady()) {
         messageManager.removeMessage(this, getErrorMessageControl());
      }
   }

   public void removeControlCausedMessage(String messageId) {
      IMessageManager messageManager = getMessageManager();
      if (messageManager != null && isFormReady()) {
         messageManager.removeMessage(messageId, getErrorMessageControl());
      }
   }

   public void removeControlCausedMessages() {
      IMessageManager messageManager = getMessageManager();
      if (messageManager != null && isFormReady()) {
         messageManager.removeMessage(getErrorMessageControl());
      }
   }

   public void removeMessage(String messageId) {
      IMessageManager messageManager = getMessageManager();
      if (messageManager != null && isFormReady()) {
         messageManager.removeMessage(messageId);
      }
   }

   public void validate() {
      if (isEditable() && Widgets.isAccessible(getControl()) && isFormReady() && areNotificationsAllowed()) {
         IStatus status = isValid();
         if (isInForm()) {
            XWidgetValidateUtility.setStatus(status, this);
         } else {
            if (Widgets.isAccessible(labelWidget)) {
               labelWidget.setForeground(status.isOK() ? null : Display.getCurrent().getSystemColor(SWT.COLOR_RED));
               if (mouseLabelListener == null) {
                  mouseLabelListener = new MouseListener() {
                     public void mouseDoubleClick(MouseEvent e) {
                        openHelp();
                     }

                     public void mouseDown(MouseEvent e) {
                     }

                     public void mouseUp(MouseEvent e) {
                     }
                  };
                  labelWidget.addMouseListener(mouseLabelListener);
               }
            }
         }
      }
   }

   /**
    * Return the control that the error message is to be placed. By default the getControl() will be used. Override to
    * change.
    * 
    * @return control
    */
   public Control getErrorMessageControl() {
      return getControl();
   }

   public abstract Control getControl();

   public void openHelp() {
      try {
         if (toolTip != null && label != null) {
            MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                  label + " Tool Tip", toolTip);
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
   }

   protected void setNotificationsAllowed(boolean areAllowed) {
      this.isNotificationAllowed.setValue(areAllowed);
   }

   protected void createControls(Composite parent, int horizontalSpan) {

   }

   public final void createWidgets(Composite parent, int horizontalSpan) {
      setNotificationsAllowed(false);
      try {
         createControls(parent, horizontalSpan);
      } finally {
         setNotificationsAllowed(true);
      }
   }

   public final void createWidgets(IManagedForm managedForm, Composite parent, int horizontalSpan) {
      if (managedForm != null) {
         this.toolkit = managedForm.getToolkit();
         this.managedForm = managedForm;
      }
      createWidgets(parent, horizontalSpan);
      adaptControls(toolkit);

      // Added to be able to operate on XWidget who create the control
      Control internalControl = getControl();
      if (internalControl != null) {
         internalControl.setData(XWIDGET_DATA_KEY, this);
      }
   }

   public void adaptControls(FormToolkit toolkit) {
      if (toolkit != null) {
         if (getControl() != null) {
            toolkit.adapt(getControl(), true, false);
         }
         if (labelWidget != null) {
            toolkit.adapt(labelWidget, true, true);
            toolkit.adapt(labelWidget.getParent(), true, true);
         }
      }
   }

   /**
    * Create Widgets used to display label and entry for wizards and editors
    */
   public void dispose() {
      if (Widgets.isAccessible(managedForm.getForm())) {
         removeControlCausedMessageByObject();
      }
   }

   public abstract void setFocus();

   public abstract void refresh();

   public abstract IStatus isValid();

   /**
    * Called with string found between xml tags Used by setFromXml() String will be sent through AXml.xmlToText() before
    * being sent to setXmlData implementation. Used by: setFromXml
    * 
    * @param str - value to set
    */
   public abstract void setXmlData(String str);

   /**
    * Return string to save off between xml tags Used by call to toXml() String returned will be sent through
    * AXml.textToXml() before being saved Used by: toXml
    * 
    * @return Return Xml data string.
    */
   protected abstract String getXmlData();

   public abstract String toHTML(String labelFont);

   protected String toXml() throws Exception {
      if (xmlSubRoot.equals("")) {
         return toXml(xmlRoot);
      } else {
         return toXml(xmlRoot, xmlSubRoot);
      }
   }

   protected String toXml(String xmlRoot) throws Exception {
      String s = "<" + xmlRoot + ">" + AXml.textToXml(getXmlData()) + "</" + xmlRoot + ">\n";
      return s;
   }

   public String toXml(String xmlRoot, String xmlSubRoot) throws Exception {
      String s =
            "<" + xmlRoot + ">" + "<" + xmlSubRoot + ">" + AXml.textToXml(getXmlData()) + "</" + xmlSubRoot + ">" + "</" + xmlRoot + ">\n";
      return s;
   }

   public void setFromXml(String xml) throws IllegalStateException {
      Matcher m;
      m = Pattern.compile("<" + xmlRoot + ">(.*?)</" + xmlRoot + ">", Pattern.MULTILINE | Pattern.DOTALL).matcher(xml);
      if (m.find()) setXmlData(AXml.xmlToText(m.group(1)));
   }

   public Vector<String> getDisplayLabels() {
      Vector<String> l = new Vector<String>();
      l.add(label);
      return l;
   }

   public void setDisplayLabel(String displayLabel) {
      this.label = displayLabel;
   }

   public boolean isEditable() {
      return editable;
   }

   public void setEditable(boolean editable) {
      this.editable = editable;
   }

   public boolean isVerticalLabel() {
      return verticalLabel;
   }

   public void setVerticalLabel(boolean verticalLabel) {
      this.verticalLabel = verticalLabel;
   }

   public String getXmlRoot() {
      return xmlRoot;
   }

   public void setXmlRoot(String xmlRoot) {
      this.xmlRoot = xmlRoot;
   }

   public String getXmlSubRoot() {
      return xmlSubRoot;
   }

   public void setXmlSubRoot(String xmlSubRoot) {
      this.xmlSubRoot = xmlSubRoot;
   }

   public String getToolTip() {
      return toolTip;
   }

   public boolean isFillVertically() {
      return fillVertically;
   }

   public void setFillVertically(boolean fillVertically) {
      this.fillVertically = fillVertically;
   }

   public String getLabel() {
      return label;
   }

   public void setLabel(String label) {
      this.label = label;
      if (labelWidget != null && !labelWidget.isDisposed()) {
         labelWidget.setText(label);
      }
   }

   public Label getLabelWidget() {
      return labelWidget;
   }

   protected void setLabelWidget(Label labelWidget) {
      this.labelWidget = labelWidget;
   }

   public boolean isRequiredEntry() {
      return requiredEntry;
   }

   public void setRequiredEntry(boolean requiredEntry) {
      this.requiredEntry = requiredEntry;
   }

   protected abstract String getReportData();

   @Override
   public String toString() {
      return String.format("%s: %s\n\n", getLabel(), getReportData());
   }

   public void setDisplayLabel(boolean displayLabel) {
      this.displayLabel = displayLabel;
   }

   public void setFillHorizontally(boolean fillHorizontally) {
      this.fillHorizontally = fillHorizontally;
   }

   public abstract Object getData();

   public boolean isDisplayLabel() {
      return displayLabel;
   }
}