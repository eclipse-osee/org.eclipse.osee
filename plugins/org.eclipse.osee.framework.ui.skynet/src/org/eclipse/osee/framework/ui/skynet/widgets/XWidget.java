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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.jdk.core.type.MutableBoolean;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
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
   private String toolTip = null;
   private boolean requiredEntry = false;
   private boolean editable = true;
   private boolean useToStringSorter = false;
   private final MutableBoolean isNotificationAllowed = new MutableBoolean(true);

   protected boolean verticalLabel = false;
   protected boolean fillVertically = false;
   protected boolean fillHorizontally = false;

   private boolean displayLabel = true;
   private final Set<XModifiedListener> modifiedListeners = new LinkedHashSet<>();
   private MouseListener mouseLabelListener;
   protected FormToolkit toolkit;
   private Object object;
   private ILabelProvider labelProvider;
   private ArtifactTypeId artifactType;

   public XWidget(String label) {
      this.label = label;
   }

   public boolean isFillHorizontally() {
      return fillHorizontally;
   }

   public void setToolTip(String toolTip) {
      this.toolTip = toolTip;
      if (labelWidget != null && !labelWidget.isDisposed()) {
         labelWidget.setToolTipText(toolTip);
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
      return managedForm == null ? true : !managedForm.getForm().isDisposed();
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
         try {
            messageManager.addMessage(this, messageText, null, type, getErrorMessageControl());
         } catch (SWTException ex) {
            //Do nothing
         }
      }
   }

   public void removeControlCausedMessageByObject() {
      IMessageManager messageManager = getMessageManager();
      if (messageManager != null && isFormReady()) {
         if (Widgets.isAccessible(getErrorMessageControl())) {
            messageManager.removeMessage(this, getErrorMessageControl());
         }
      }
   }

   public void removeControlCausedMessage(String messageId) {
      IMessageManager messageManager = getMessageManager();
      if (messageManager != null && isFormReady()) {
         if (Widgets.isAccessible(getErrorMessageControl())) {
            messageManager.removeMessage(messageId, getErrorMessageControl());
         }
      }
   }

   public void validate() {
      Control control = getControl();
      if (Widgets.isAccessible(control) && isFormReady() && areNotificationsAllowed()) {
         IStatus status = isValid();
         if (isInForm()) {
            XWidgetValidateUtility.setStatus(status, this);
         } else {
            if (Widgets.isAccessible(labelWidget)) {
               labelWidget.setForeground(status.isOK() ? null : Displays.getSystemColor(SWT.COLOR_RED));
               if (mouseLabelListener == null) {
                  mouseLabelListener = new MouseListener() {
                     @Override
                     public void mouseDoubleClick(MouseEvent e) {
                        openHelp();
                     }

                     @Override
                     public void mouseDown(MouseEvent e) {
                        // do nothing
                     }

                     @Override
                     public void mouseUp(MouseEvent e) {
                        // do nothing
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
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   protected void setNotificationsAllowed(boolean areAllowed) {
      isNotificationAllowed.setValue(areAllowed);
   }

   protected abstract void createControls(Composite parent, int horizontalSpan);

   protected void createControlsAfterLabel(Composite parent, int horizontalSpan) {
      // Used for widgets desiring to add controls just after the label.
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
      if (managedForm != null && Widgets.isAccessible(managedForm.getForm())) {
         removeControlCausedMessageByObject();
      }
   }

   public abstract void setFocus();

   public void refresh() {
      // provided for subclass implementation
   }

   public IStatus isValid() {
      return Status.OK_STATUS;
   }

   public String toHTML(String labelFont) {
      return "";
   }

   public void setDisplayLabel(String displayLabel) {
      label = displayLabel;
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

   protected String getReportData() {
      return "";
   }

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

   public Object getData() {
      return null;
   }

   public boolean isDisplayLabel() {
      return displayLabel;
   }

   public Collection<? extends XWidget> getChildrenXWidgets() {
      return Collections.emptyList();
   }

   public abstract boolean isEmpty();

   /**
    * Generic object set by provider of XWidget
    */
   public void setObject(Object object) {
      this.object = object;
   }

   public Object getObject() {
      return object;
   }

   public ILabelProvider getLabelProvider() {
      return labelProvider;
   }

   public void setLabelProvider(ILabelProvider labelProvider) {
      this.labelProvider = labelProvider;
   }

   public boolean isUseToStringSorter() {
      return useToStringSorter;
   }

   public void setUseToStringSorter(boolean useToStringSorter) {
      this.useToStringSorter = useToStringSorter;
   }

   public void setToolkit(FormToolkit toolkit) {
      this.toolkit = toolkit;
   }

   /**
    * @return artifactType that may or may not be the storage artifact type. Can be used by any widget and only the
    * widget knows what to do with this value.
    */
   public ArtifactTypeId getArtifactType() {
      return artifactType;
   }

   /**
    * @param artifactType that may or may not be the storage artifact type. Can be used by any widget and only the
    * widget knows what to do with this value.
    */
   public void setArtifactType(ArtifactTypeId artifactType) {
      this.artifactType = artifactType;
   }
}