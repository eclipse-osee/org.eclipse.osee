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

package org.eclipse.osee.framework.ui.skynet.widgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.conditions.ConditionalRule;
import org.eclipse.osee.framework.jdk.core.type.MutableBoolean;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;

/**
 * Abstract class for all widgets used in Wizards and Editors
 */
public abstract class XWidget {
   public final static String XWIDGET_DATA_KEY = "xWidget";

   private IManagedForm managedForm;

   protected Hyperlink labelHyperlink;
   protected Label labelWidget = null;
   protected String label = "";
   private String toolTip = null;
   private boolean requiredEntry = false;
   private boolean editable = true;
   private boolean useToStringSorter = false;
   private final MutableBoolean isNotificationAllowed = new MutableBoolean(true);

   protected boolean verticalLabel = false;
   protected boolean fillVertically = false;
   protected boolean fillHorizontally = false;
   private boolean noSelect = false;
   private boolean multiSelect = false;
   private boolean singleSelect = false;
   private boolean displayLabel = true;
   private final Set<XModifiedListener> modifiedListeners = new LinkedHashSet<>();
   private MouseListener mouseLabelListener;
   protected FormToolkit toolkit;
   private Object object;
   private ILabelProvider labelProvider;
   private ArtifactTypeToken artifactType = ArtifactTypeToken.SENTINEL;
   private AttributeTypeToken attributeType = AttributeTypeToken.SENTINEL;
   private String id;
   protected Object defaultValueObj;
   private boolean autoSave = false;
   private boolean validateDate = false;
   private boolean useLabelFont = true;
   private ISelectableValueProvider valueProvider;
   private Collection<? extends Object> values = new ArrayList<Object>();
   private List<ConditionalRule> conditions = new ArrayList<>();
   private ArtifactId teamId = ArtifactId.SENTINEL;
   private List<WidgetHint> widgetHints = new ArrayList<>();

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

   public void notifyRightClickListeners() {
      if (areNotificationsAllowed()) {
         for (XModifiedListener listener : modifiedListeners) {
            listener.handleRightClick(this);
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
            XWidgetUtility.setStatus(status, this);
         } else {
            if (Widgets.isAccessible(labelHyperlink)) {
               labelHyperlink.setForeground(status.isOK() ? null : Displays.getSystemColor(SWT.COLOR_RED));
               if (mouseLabelListener == null) {
                  mouseLabelListener = new MouseAdapter() {
                     @Override
                     public void mouseDoubleClick(MouseEvent e) {
                        openHelp();
                     }
                  };
                  labelHyperlink.addMouseListener(mouseLabelListener);
               }
            }
            if (Widgets.isAccessible(labelWidget)) {
               labelWidget.setForeground(status.isOK() ? null : Displays.getSystemColor(SWT.COLOR_RED));
               if (mouseLabelListener == null) {
                  mouseLabelListener = new MouseAdapter() {
                     @Override
                     public void mouseDoubleClick(MouseEvent e) {
                        openHelp();
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
      if (toolkit != null) {
         adaptControls(toolkit);
      }

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
      return String.format("%s: %s", getLabel(), getReportData());
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
   public ArtifactTypeToken getArtifactType() {
      return artifactType;
   }

   /**
    * @param artifactType that may or may not be the storage artifact type. Can be used by any widget and only the
    * widget knows what to do with this value.
    */
   public void setArtifactType(ArtifactTypeToken artifactType) {
      this.artifactType = artifactType;
   }

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public boolean isNoSelect() {
      return noSelect;
   }

   public void setNoSelect(boolean noSelect) {
      this.noSelect = noSelect;
   }

   public Object getDefaultValueObj() {
      return defaultValueObj;
   }

   public void setDefaultValueObj(Object defaultValueObj) {
      this.defaultValueObj = defaultValueObj;
   }

   public boolean isAutoSave() {
      return autoSave;
   }

   public void setAutoSave(boolean autoSave) {
      this.autoSave = autoSave;
   }

   public boolean isValidateDate() {
      return validateDate;
   }

   public void setValidateDate(boolean validateDate) {
      this.validateDate = validateDate;
   }

   public boolean isUseLabelFont() {
      return useLabelFont;
   }

   public void setUseLabelFont(boolean useLabelFont) {
      this.useLabelFont = useLabelFont;
   }

   public ISelectableValueProvider getValueProvider() {
      return valueProvider;
   }

   public void setValueProvider(ISelectableValueProvider valueProvider) {
      this.valueProvider = valueProvider;
   }

   public Collection<? extends Object> getValues() {
      return values;
   }

   public void setValues(Collection<? extends Object> values) {
      this.values = values;
   }

   public List<ConditionalRule> getConditions() {
      return conditions;
   }

   public void setConditions(List<ConditionalRule> conditions) {
      this.conditions = conditions;
   }

   public boolean isMultiSelect() {
      return multiSelect;
   }

   public void setMultiSelect(boolean multiSelect) {
      this.multiSelect = multiSelect;
   }

   public boolean isSingleSelect() {
      return singleSelect;
   }

   public void setSingleSelect(boolean singleSelect) {
      this.singleSelect = singleSelect;
   }

   public AttributeTypeToken getAttributeType() {
      return attributeType;
   }

   public void setAttributeType(AttributeTypeToken attributeType) {
      this.attributeType = attributeType;
   }

   public ArtifactId getTeamId() {
      return teamId;
   }

   public void setTeamId(ArtifactId teamId) {
      this.teamId = teamId;
   }

   public List<WidgetHint> getWidgetHints() {
      return widgetHints;
   }

   public void setWidgetHints(List<WidgetHint> widgetHints) {
      this.widgetHints = widgetHints;
   }

   public boolean hasWidgetHint(WidgetHint widgetHint) {
      return this.widgetHints.contains(widgetHint);
   }

}