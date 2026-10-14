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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.DisplayHint;
import org.eclipse.osee.framework.core.data.Multiplicity;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.conditions.ConditionalRule;
import org.eclipse.osee.framework.core.data.conditions.EnableIfAttrValueCondition;
import org.eclipse.osee.framework.core.enums.OseeImage;
import org.eclipse.osee.framework.core.widget.ISelectableValueProvider;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.core.widget.validate.XWidgetStatus;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.result.XResultType;
import org.eclipse.osee.framework.jdk.core.type.MutableBoolean;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.util.XWidgetUtility;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.util.XXArtData;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.util.listen.XModifiedListener;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.Widgets;
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
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;

/**
 * Abstract class for all widgets used in Wizards and Editors
 */
public abstract class XWidget {
   public final static String XWIDGET_DATA_KEY = "xWidget";
   public final static XResultData debugXWidgets = new XResultData(false); // Master switch for XWidget diagnostics

   /** Lightweight duration summaries; safe for performance measurements when lifecycle/event tracing is disabled. */
   public static boolean debugXWidgetPerformance = true;
   /** Detailed lifecycle state logging. Disable while measuring performance because console I/O skews timings. */
   public static boolean debugXWidgetLifecycle = false;
   /** Include compact call stacks with lifecycle logs. Requires {@link #debugXWidgetLifecycle}. */
   public static boolean debugXWidgetStacks = false;
   /** Trace SWT paint/resize/move/show/hide/modify events. Expensive; requires lifecycle tracing. */
   public static boolean debugXWidgetSwtEvents = false;

   protected Hyperlink labelLinkWidget;
   protected Label labelWidget;
   private final MutableBoolean isNotificationAllowed = new MutableBoolean(true);
   private final Set<XModifiedListener> modifiedListeners = new LinkedHashSet<>();
   protected MouseListener helpLabelListener;
   private boolean useToStringSorter = false;
   private boolean useLabelFont = true;
   protected XWidgetData widData = new XWidgetData();
   private XResultData isNotValid;
   private boolean listenerRefresh = true;
   protected ILabelProvider labelProvider;
   protected XXArtData xxArt = XXArtData.SENTINEL;
   private XResultData widgetReport = XResultData.EMPTY_RD;
   /**
    * NOTE: Don't set any widData options during construction as it may get overwritten after construction. Overriding
    * XWidget.setWidData and setting values after super.setWidData is called will work.
    */
   public XWidget(WidgetId widgetId) {
      this(widgetId, "");
   }

   public XWidget(WidgetId widgetId, String label) {
      widData.setWidgetId(widgetId);
      setLabel(label);
      setLabel(label);
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

   public FormToolkit getToolkit() {
      return (FormToolkit) widData.getFormToolkit();
   }

   public ManagedForm getManagedForm() {
      return (ManagedForm) widData.getManagedForm();
   }

   public final void createWidgets(IManagedForm managedForm, Composite parent, int horizontalSpan) {
      if (managedForm != null) {
         widData.setManagedForm(managedForm);
         widData.setFormToolkit(managedForm.getToolkit());
      }
      createWidgets(parent, horizontalSpan);
      if (getToolkit() != null) {
         adaptControls(getToolkit());
      }
      // Added to be able to operate on XWidget who create the control
      Control internalControl = getControl();
      if (internalControl != null) {
         internalControl.setData(XWIDGET_DATA_KEY, this);
      }
   }

   public void adaptControls(FormToolkit toolkit) {
      if (getToolkit() != null) {
         if (getControl() != null) {
            getToolkit().adapt(getControl(), true, false);
         }
         if (Widgets.isAccessible(labelWidget)) {
            getToolkit().adapt(labelWidget, true, true);
            getToolkit().adapt(labelWidget.getParent(), true, true);
         }
      }
   }

   public void setControlBackground(int swtColor) {
      Control control = getControl();
      if (Widgets.isAccessible(control)) {
         control.setBackground(Displays.getSystemColor(swtColor));
         if (control.getParent() != null) {
            control.getParent().setBackground(Displays.getSystemColor(swtColor));
         }
      }
      if (Widgets.isAccessible(labelWidget)) {
         labelWidget.setBackground(Displays.getSystemColor(swtColor));
      }
   }

   @Override
   public String toString() {
      return String.format("Label: [%s] \n%s \nXOpts: %s \nAttr Type: %s", getLabel(), getWidgetId(),
         widData.getXOptionHandler().getXOptions(), widData.getAttributeType());
   }

   public boolean handleClear() {
      return false;
   }

   public void internalSetWidData(XWidgetData widData) {
      this.widData = widData;
   }

   public void setArtifact(Artifact artifact) {
      xxArt = XXArtData.get(artifact);
      widData.setArtifact(artifact);
      if (getArtifact() != null && getArtifact().isValid() && getAttributeType().isValid()) {
         if (getAttributeType().hasDisplayHint(DisplayHint.MultiLine)) {
            widData.set(XOption.VAL_MULTI_TEXT);
         }
         Multiplicity multiplicity = getArtifact().getArtifactType().getMultiplicity(getAttributeType());
         XOption option = XOption.multiplicityToXOption(multiplicity);
         widData.set(option);
      }
   }

   // Create only once for performance
   protected XResultData getIsNotValidResult() {
      if (isNotValid == null) {
         isNotValid = new XResultData(XResultType.Error, getLabel() + " is not valid.");
      }
      return isNotValid;
   }

   public void addLabelMouseListener(MouseAdapter mouseAdapter) {
      if (Widgets.isAccessible(labelLinkWidget)) {
         labelLinkWidget.addMouseListener(mouseAdapter);
      } else if (Widgets.isAccessible(labelWidget)) {
         labelWidget.addMouseListener(mouseAdapter);
      }
   }

   public Collection<? extends XWidget> getChildrenXWidgets() {
      return Collections.emptyList();
   }

   public Object getData() {
      return null;
   }

   public void validate() {
      Control control = getControl();
      if (Widgets.isAccessible(control) && isFormReady() && areNotificationsAllowed()) {
         XResultData status = isValid();
         if (isInForm()) {
            XWidgetUtility.setStatus(status, this);
         } else {
            if (Widgets.isAccessible(labelWidget)) {
               if (isTooltip() && helpLabelListener == null) {
                  helpLabelListener = new MouseAdapter() {
                     @Override
                     public void mouseDoubleClick(MouseEvent e) {
                        openHelp();
                     }
                  };
                  labelWidget.addMouseListener(helpLabelListener);
               }
            }
         }
      }
      if (Widgets.isAccessible(control) && getConditions().size() > 0) {
         for (ConditionalRule rule : getConditions()) {
            if (rule instanceof EnableIfAttrValueCondition) {
               if (rule.isDisabled(
                  getArtifact().getAttributesToStringList(((EnableIfAttrValueCondition) rule).getAttrType()))) {
                  control.setEnabled(false);
                  setEditable(false);
                  break;
               } else {
                  control.setEnabled(true);
                  setEditable(true);
               }
            }
         }
      }
   }
   public MouseAdapter HelpMouseListener = new MouseAdapter() {
      @Override
      public void mouseDoubleClick(MouseEvent e) {
         openHelp();
      }
   };
   public abstract Control getControl();

   public boolean isTooltip() {
      return Strings.isValid(widData.getToolTip()) && Strings.isValid(widData.getLabel());
   }

   public void openHelp() {
      try {
         if (widData.getToolTip() != null && widData.getLabel() != null) {
            MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
               widData.getLabel() + " Tool Tip", widData.getToolTip());
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   public void setNotificationsAllowed(boolean areAllowed) {
      isNotificationAllowed.setValue(areAllowed);
   }

   /**
    * Create Widgets used to display label and entry for wizards and editors
    */
   public void dispose() {
      if (getManagedForm() != null && Widgets.isAccessible(getManagedForm().getForm())) {
         removeControlCausedMessageByObject();
      }
      if (Widgets.isAccessible(labelWidget)) {
         labelWidget.dispose();
      }
      if (Widgets.isAccessible(labelLinkWidget)) {
         labelLinkWidget.dispose();
      }
   }

   /**
    * Subclasses must provide implementation of getControl() that returns appropriate widget.
    */
   public void setFocus() {
      Control control = getControl();
      if (control != null && !control.isDisposed()) {
         control.setFocus();
      }
   }

   public boolean isEmpty() {
      return false;
   }

   public void refresh() {
      // provided for subclass implementation
   }

   public XResultData isValid() {
      return XWidgetStatus.OK;
   }

   public String toHTML(String labelFont) {
      return "";
   }

   protected String getReportData() {
      return "";
   }

   ////////////////////////////////////////////////////
   // Listener registration and notification
   ////////////////////////////////////////////////////
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

   ////////////////////////////////////////////////////
   // XWidgetData convenience methods here
   ////////////////////////////////////////////////////
   public void setToolTip(String toolTip) {
      widData.setToolTip(toolTip);
      if (Widgets.isAccessible(labelWidget)) {
         labelWidget.setToolTipText(toolTip);
      } else if (Widgets.isAccessible(labelLinkWidget)) {
         labelLinkWidget.setToolTipText(toolTip);
      }
   }

   public boolean isVerticalLabel() {
      return widData.is(XOption.VERT_LABEL);
   }

   public void setVerticalLabel(boolean verticalLabel) {
      if (verticalLabel) {
         widData.set(XOption.VERT_LABEL);
      }
   }

   public String getToolTip() {
      String tooltip = widData.getToolTip();
      if (Strings.isInvalid(tooltip)) {
         if (getAttributeType().isValid() && Strings.isValid(getAttributeType().getDescription())) {
            tooltip = getAttributeType().getDescription();
         }
      }
      return tooltip;
   }

   public String getLabel() {
      return widData.getLabel();
   }

   public void setLabel(String label) {
      widData.setLabel(label);
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
      return widData.is(XOption.REQUIRED);
   }

   public void setRequiredEntry(boolean requiredEntry) {
      if (requiredEntry) {
         widData.set(XOption.REQUIRED);
      } else {
         widData.set(XOption.NO_REQUIRED);
      }
   }

   public void setDisplayLabel(boolean displayLabel) {
      if (!displayLabel) {
         widData.set(XOption.NO_LABEL);
      }
   }

   public boolean isDisplayLabel() {
      return !widData.is(XOption.NO_LABEL);
   }

   /**
    * Generic object set by provider of XWidget; XWidget knows what it is and what to do with it.
    */
   public void setObject(Object object) {
      widData.setObject(object);
   }

   public Object getObject() {
      return widData.getObject();
   }

   /**
    * @return artifactType that may or may not be the storage artifact type. Can be used by any widget and only the
    * widget knows what to do with this value.
    */
   public ArtifactTypeToken getArtifactType() {
      return widData.getArtifactType();
   }

   /**
    * @param artifactType that may or may not be the storage artifact type. Can be used by any widget and only the
    * widget knows what to do with this value.
    */
   public void setArtifactType(ArtifactTypeToken artifactType) {
      widData.setArtifactType(artifactType);
   }

   public Long getId() {
      return widData.getId();
   }

   /**
    * @param id temporary id so widget can be indexed and found
    */
   public void setId(Long id) {
      widData.setId(id);
   }

   public boolean isAutoSave() {
      return widData.is(XOption.SAVE);
   }

   public boolean isValidateDate() {
      return widData.is(XOption.VALIDATE_DATE);
   }

   public boolean isUseLabelFont() {
      return useLabelFont;
   }

   public void setUseLabelFont(boolean useLabelFont) {
      this.useLabelFont = useLabelFont;
   }

   public ISelectableValueProvider getValueProvider() {
      return widData.getValueProvider();
   }

   public void setValueProvider(ISelectableValueProvider valueProvider) {
      widData.setValueProvider(valueProvider);
   }

   public Collection<Object> getValues() {
      return widData.getValues();
   }

   public void setValues(Collection<? extends Object> values) {
      widData.setValues(org.eclipse.osee.framework.jdk.core.util.Collections.castAll(values));
   }

   public List<ConditionalRule> getConditions() {
      return widData.getConditions();
   }

   public void setConditions(List<ConditionalRule> conditions) {
      widData.setConditions(conditions);
   }

   public AttributeTypeToken getAttributeType() {
      return widData.getAttributeType();
   }

   public RelationTypeSide getRelationTypeSide() {
      return widData.getRelationTypeSide();
   }

   public void setAttributeType(AttributeTypeToken attributeType) {
      if (attributeType.isValid()) {
         widData.setAttributeType(attributeType);
         if (widData.getLabel().isBlank()) {
            widData.setLabel(attributeType.getUnqualifiedName());
         }
      }
   }

   public ArtifactId getTeamId() {
      return widData.getTeamId();
   }

   public void setTeamId(ArtifactId teamId) {
      widData.setTeamId(teamId);
   }

   public AttributeTypeToken getAttributeType2() {
      return widData.getAttributeType2();
   }

   public void setAttributeType2(AttributeTypeToken attributeType2) {
      if (attributeType2.isValid()) {
         widData.setAttributeType2(attributeType2);
      }
   }

   public OseeImage getOseeImage() {
      return widData.getOseeImage();
   }

   public void setOseeImage(OseeImage oseeImage) {
      widData.setOseeImage(oseeImage);
   }

   public Map<String, Object> getParameters() {
      return widData.getParameters();
   }

   public void addParameter(String key, Object value) {
      widData.getParameters().put(key, value);
   }

   public ArtifactToken getEnumeratedArt() {
      return widData.getEnumeratedArt();
   }

   public void setEnumeratedArt(ArtifactToken enumeratedArt) {
      Conditions.requireNonNull(enumeratedArt, "Enumerated Art");
      widData.setEnumeratedArt(enumeratedArt);
   }

   public Hyperlink getLabelLinkWidget() {
      return labelLinkWidget;
   }

   public boolean isFillHorizontally() {
      return widData.isAny(XOption.FILL_HORZ, XOption.FILL_BOTH);
   }

   public void setFillHorizontally(boolean fillHorizontally) {
      if (fillHorizontally) {
         widData.set(XOption.FILL_HORZ);
      } else {
         widData.getXOptionHandler().remove(XOption.FILL_HORZ);
      }
   }

   public boolean isFillVertically() {
      return widData.isAny(XOption.FILL_VERT, XOption.FILL_BOTH, XOption.VAL_MULTI_TEXT) || //
         (getAttributeType().isValid() && getAttributeType().hasDisplayHint(DisplayHint.MultiLine));
   }

   public void setFillVertically(boolean fillVertically) {
      if (fillVertically) {
         widData.set(XOption.FILL_VERT);
      } else {
         widData.getXOptionHandler().remove(XOption.FILL_VERT);
      }
   }

   public boolean isValueMultiSelectFromSelectable() {
      return widData.getXOptionHandler().is(XOption.VAL_MULTI_SEL);
   }

   public boolean isValueMultiLineText() {
      return widData.getXOptionHandler().is(XOption.VAL_MULTI_TEXT);
   }

   public boolean isValueSingleLineText() {
      return widData.getXOptionHandler().is(XOption.VAL_SINGLE_TEXT);
   }

   public void setValueMultiSelect(boolean multiSelect) {
      widData.set(XOption.VAL_MULTI_SEL);
   }

   public boolean isValueSingleSelectFromSelectable() {
      return widData.getXOptionHandler().is(XOption.VAL_SINGLE_SEL);
   }

   public void setValueSingleSelect(boolean singleSelect) {
      widData.set(XOption.VAL_SINGLE_SEL);
   }

   public boolean isEditable() {
      return widData.getXOptionHandler().is(XOption.WRITE) || !widData.getXOptionHandler().is(XOption.READ);
   }

   public boolean isNotEditable() {
      return widData.getXOptionHandler().is(XOption.READ) || !widData.getXOptionHandler().is(XOption.WRITE);
   }

   public void setEditable(boolean editable) {
      if (editable) {
         widData.set(XOption.WRITE);
      } else {
         widData.set(XOption.READ);
      }
   }

   public WidgetId getWidgetId() {
      return widData.getWidgetId();
   }

   public XWidgetData getWidData() {
      return widData;
   }

   public boolean isWidget(WidgetId widgetId) {
      return widData.getWidgetId().equals(widgetId);
   }

   public void setToolkit(FormToolkit formToolkit) {
      widData.setFormToolkit(formToolkit);
   }

   public void setManagedForm(IManagedForm managedForm) {
      widData.setManagedForm(managedForm);
   }

   public boolean isUseToStringSorter() {
      return useToStringSorter;
   }

   public void setUseToStringSorter(boolean useToStringSorter) {
      this.useToStringSorter = useToStringSorter;
   }

   public Artifact getArtifact() {
      if (widData.getArtifact() instanceof Artifact) {
         return (Artifact) widData.getArtifact();
      }
      return null;
   }

   public boolean isListenerRefresh() {
      return listenerRefresh;
   }

   public void setListenerRefresh(boolean listenerRefresh) {
      this.listenerRefresh = listenerRefresh;
   }

   ////////////////////////////////////////////////////
   // XOption convenience methods
   ////////////////////////////////////////////////////
   public void set(XOption... xOption) {
      widData.set(xOption);
   }

   public void remove(XOption xOption) {
      widData.remove(xOption);
   }

   /**
    * @return true if any match
    */
   public boolean is(XOption... options) {
      return widData.is(options);
   }

   public boolean isNot(XOption... options) {
      return !is(options);
   }

   public boolean isAll(XOption... xOptions) {
      return widData.isAll(xOptions);
   }

   public boolean isAny(XOption... xOptions) {
      return widData.isAny(xOptions);
   }

   public void setDefaultValue(Object value) {
      widData.setDefaultValue(value);
   }

   ////////////////////////////////////////////////////
   // Validity decoration and messaging here
   ////////////////////////////////////////////////////
   public boolean isInForm() {
      return getManagedForm() != null;
   }

   protected IMessageManager getMessageManager() {
      return getManagedForm() != null ? getManagedForm().getMessageManager() : null;
   }

   public void setMessage(String messageId, String messageText, int type) {
      IMessageManager messageManager = getMessageManager();
      if (messageManager != null && isFormReady()) {
         messageManager.addMessage(messageId, messageText, null, type);
      }
   }

   /**
    * Return the control that the error message is to be placed. By default the getControl() will be used. Override to
    * change.
    */
   public Control getErrorMessageControl() {
      return getControl();
   }

   public boolean isFormReady() {
      // Set to true if outside of a form;
      return getManagedForm() == null ? true : !getManagedForm().getForm().isDisposed();
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

   public ILabelProvider getLabelProvider() {
      return labelProvider;
   }

   public void setLabelProvider(ILabelProvider labelProvider) {
      this.labelProvider = labelProvider;
   }

   public void setAsProviderFor(AttributeTypeToken... attrTypes) {
      widData.setProviderfor(attrTypes);
   }

   public void setRelationTypeSide(RelationTypeSide relType) {
      widData.setRelationTypeSide(relType);
   }

   public XResultData getWidgetReport() {
      return widgetReport;
   }

   public void setWidgetReport(XResultData widgetReport) {
      this.widgetReport = widgetReport;
   }

}
