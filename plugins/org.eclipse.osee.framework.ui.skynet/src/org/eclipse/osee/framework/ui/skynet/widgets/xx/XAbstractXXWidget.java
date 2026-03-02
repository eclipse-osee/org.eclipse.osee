/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets.xx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.core.data.DisplayHint;
import org.eclipse.osee.framework.core.data.Multiplicity;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.plugin.util.NoOpViewerComparator;
import org.eclipse.osee.framework.ui.plugin.util.StringLabelProvider;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.util.StringNameComparator;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidgetUtility;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredCheckboxTreeDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredTreeDialog;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;

/**
 * One Widget to rule them all... <br/>
 * <br/>
 * This widget is intended to replace most other widgets. It handles the functionality necessary for most use cases.
 * This includes handling if it resides in an editor backed by and artifact (eg: Artifact Editor) or not (eg: BLAM or
 * Dialog). Most widgets should NOT need to extend this separately. All features should be provided by setting options
 * in base XWidget class and also available through WidgetBuilders.<br/>
 * <br/>
 * Features:<br/>
 * - Shows "Not Set" if no value is selected<br/>
 * - Selectable hyperlink label if editable<br/>
 * - Non-Selectable link label if readonly<br/>
 * - Ability to set selectable and default selected<br/>
 * - Default of enumerated attribute to provide selectable<br/>
 * - Filterable Dialog with multi-select (checkboxes) or single select (list)<br/>
 * - Ability to have clear button<br/>
 * - Right-click provides clear by default<br/>
 * - Double-click shows tooltip dialog, if tooltip is provided<br/>
 * - Selectable items sorted by default<br/>
 * - Validation provided for most use cases by isValid so don't need separate validators<br/>
 * - Ability to have an icon for a Widget via has<br/>
 *
 * @author Donald G. Dunne
 */
public abstract class XAbstractXXWidget<T> extends XWidget {

   public final static String NOT_SET = Widgets.NOT_SET;

   protected Label valueLabel;
   protected Label iconLabel;
   protected Composite comp;
   protected Collection<T> selected = new ArrayList<>();
   protected Collection<T> selectable = new ArrayList<>();
   private final int ClearButtonNum = 2;
   protected boolean includeColon = true;
   private ILabelProvider labelProvider;

   /**
    * NOTE: Don't set any widData options during construction as it may get overwritten after construction. Overriding
    * XWidget.setWidData and setting values after super.setWidData is called will work.
    */

   public XAbstractXXWidget(XWidgetData widData) {
      this(widData.getWidgetId(), widData.getName(), new StringLabelProvider());
   }

   public XAbstractXXWidget(WidgetId widgetId, String label) {
      this(widgetId, label, new StringLabelProvider());
   }

   public XAbstractXXWidget(WidgetId widgetId, String label, ILabelProvider labelProvider) {
      super(widgetId, label);
      this.labelProvider = labelProvider;
   }

   public String getCurrentValue() {
      if (selected.isEmpty()) {
         return NOT_SET;
      }
      return Collections.toString("; ", selected);
   }

   public Collection<T> getSelectable() {
      return selectable;
   }

   // Override to provide default
   public T getDefaultSelected() {
      return null;
   }

   public T getSelectedFirst() {
      if (selected.isEmpty()) {
         return getSentinel();
      }
      return selected.iterator().next();
   }

   protected abstract T getSentinel();

   @Override
   public boolean handleClear() {
      if (widData.is(XOption.CLEARABLE) && !selected.isEmpty()) {
         selected.clear();
         handleSelectedPersist();
         notifyXModifiedListeners();
         return true;
      }
      return false;
   }

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      int numCols = 2;
      if (isWidgetIcon()) {
         numCols++;
      }

      comp = new Composite(parent, SWT.NONE);
      GridLayout layout = new GridLayout(numCols, false);
      layout.marginHeight = 1;
      comp.setLayout(layout);
      if (isFillHorizontally()) {
         comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
      } else {
         comp.setLayoutData(new GridData());
      }
      if (getToolkit() != null) {
         getToolkit().adapt(comp);
      }

      createLabelWidget();
      createIconWidget();
      createValueWidget();

      refresh();
   }

   protected void createLabelWidget() {
      if (widData.is(XOption.NO_LABEL)) {
         return;
      }
      String labelStr = getLabel() + (isIncludeColon() ? ":" : "");
      if (isEditable()) {
         if (getToolkit() == null) {
            labelHyperlink = new Hyperlink(comp, SWT.NONE);
            labelHyperlink.setText(labelStr);
            labelHyperlink.setForeground(Displays.getSystemColor(SWT.COLOR_BLUE));
            labelHyperlink.setUnderlined(true);
         } else {
            labelHyperlink = getToolkit().createHyperlink(comp, labelStr, SWT.NONE);
         }
         labelHyperlink.setToolTipText(Strings.isValid(getToolTip()) ? getToolTip() : "Select to Modify");
         labelHyperlink.setLayoutData(new GridData());
         if (getToolTip() != null) {
            labelHyperlink.setToolTipText(getToolTip());
         }
         labelHyperlink.addListener(SWT.MouseUp, new Listener() {
            @Override
            public void handleEvent(Event event) {
               if (event.button == 1 && handleSelection()) {
                  refresh();
                  notifyXModifiedListeners();
               } else if (event.button == 3) {
                  handleClear();
               }
            }
         });
      } else {
         if (getToolkit() == null) {
            labelWidget = new Label(comp, SWT.NONE);
            labelWidget.setText(labelStr);
         } else {
            labelWidget = getToolkit().createLabel(comp, labelStr, SWT.NONE);
         }
         XWidgetUtility.setLabelFontsBold(labelWidget);
         labelWidget.setLayoutData(new GridData());
      }
   }

   private void createIconWidget() {
      if (isWidgetIcon()) {
         iconLabel = new Label(comp, SWT.NONE);
         iconLabel.setLayoutData(new GridData(SWT.NONE, SWT.NONE, false, false));
         if (getToolkit() != null) {
            getToolkit().adapt(iconLabel, false, false);
         }
      }
   }

   protected boolean isTextWidget() {
      return false;
   }

   protected void createValueWidget() {
      valueLabel = new Label(comp, SWT.NONE);
      valueLabel.setText(getLabel() + ":");
      if (getToolkit() != null) {
         getToolkit().adapt(valueLabel, false, false);
      }
      GridData gd = new GridData();
      if (isFillHorizontally()) {
         gd.grabExcessHorizontalSpace = true;
         gd.horizontalAlignment = SWT.FILL;
      }
      valueLabel.setLayoutData(gd);
      if (getToolTip() != null) {
         valueLabel.setToolTipText(getToolTip());
      }
   }

   protected boolean isWidgetIcon() {
      return widData.getOseeImage() != null;
   }

   protected Image getWidgetIcon() {
      if (getOseeImage() != null) {
         return ImageManager.getImage(getOseeImage());
      }
      return null;
   }

   @Override
   public void refresh() {
      if (!Widgets.isAccessible(comp)) {
         return;
      }
      if (Widgets.isAccessible(valueLabel)) {
         String currentValue = getCurrentValue();
         if (!currentValue.equals(valueLabel.getText())) {
            valueLabel.setText(currentValue);
            valueLabel.update();
            valueLabel.getParent().update();
            valueLabel.getParent().getParent().layout();
         }
      }
      if (isWidgetIcon() && Widgets.isAccessible(iconLabel)) {
         iconLabel.setImage(getWidgetIcon());
      }
      validate();
   }

   protected boolean isSelectable() {
      return true;
   }

   /**
    * called if multi-select
    */
   protected void handleSelectedPersist() {
      if (getArtifact() != null && getAttributeType().isValid()) {
         SkynetTransaction tx = TransactionManager.createTransaction(getArtifact().getBranch(), "Set");
         List<String> values = new ArrayList<>();
         for (Object obj : selected) {
            values.add(obj.toString());
         }
         if (values.isEmpty()) {
            getArtifact().deleteAttributes(getAttributeType());
         } else {
            getArtifact().setAttributeValues(getAttributeType(), values);
         }
         tx.addArtifact(getArtifact());
         tx.execute();
      }
   }

   public boolean handleSelection() {
      try {
         if (!isSelectable()) {
            return false;
         }

         boolean addClearButton = widData.is(XOption.CLEARABLE);
         if (isMultiSelect()) {
            FilteredCheckboxTreeDialog<T> dialog = new FilteredCheckboxTreeDialog<T>("Select " + getLabel(),
               "Select " + getLabel(), new ArrayTreeContentProvider(), labelProvider, getComparator(), addClearButton);
            dialog.setInput(getSelectable());
            T defaultSelected = getDefaultSelected();
            if (defaultSelected != null) {
               dialog.setInitialSelections(Arrays.asList(defaultSelected));
            }
            int result = dialog.open();
            if (result == Window.OK) {
               selected = dialog.getChecked();
               handleSelectedPersist();
               return true;
            }
            if (result == 2) {
               selected = java.util.Collections.emptyList();
               handleSelectedPersist();
               handleClear();
               return true;
            }
         } else {
            FilteredTreeDialog dialog = new FilteredTreeDialog("Select " + getLabel(), "Select " + getLabel(),
               new ArrayTreeContentProvider(), labelProvider, getComparator(), addClearButton);
            dialog.setInput(getSelectable());
            T defaultSelected = getDefaultSelected();
            if (defaultSelected != null) {
               dialog.setInitialSelections(Arrays.asList(defaultSelected));
            }
            int result = dialog.open();
            if (result == Window.OK) {
               selected = dialog.getSelected();
               handleSelectedPersist();
               return true;
            } else if (result == ClearButtonNum) {
               selected = java.util.Collections.emptyList();
               handleSelectedPersist();
               handleClear();
               return true;
            }
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   public boolean isIncludeColon() {
      return includeColon;
   }

   public void setIncludeColon(boolean includeColon) {
      this.includeColon = includeColon;
   }

   protected ViewerComparator getComparator() {
      if (getAttributeType().hasDisplayHint(DisplayHint.InOrder)) {
         // Do not sort, use enum as supplied to dialogs
         return NoOpViewerComparator.instance;
      }
      return StringNameComparator.instance;
   }

   public void setSelected(Collection<T> selected) {
      this.selected = selected;
      refresh();
   }

   @SuppressWarnings("unchecked")
   public void setSelected(Object selected) {
      this.selected = new ArrayList<T>();
      if (selected instanceof Collection) {
         this.selectable.addAll((Collection<T>) selected);
      } else {
         this.selected.add((T) selected);
      }
      refresh();
   }

   @Override
   public boolean isEmpty() {
      if (isMultiSelect()) {
         return selected.isEmpty();
      }
      return selected == null;
   }

   @Override
   public Object getData() {
      if (isMultiSelect()) {
         return selected;
      }
      return selected.isEmpty() ? null : selected.iterator().next();
   }

   public Collection<T> getSelected() {
      return selected;
   }

   public void setSelectable(Collection<T> selectable) {
      this.selectable = selectable;
   }

   @Override
   public String toHTML(String labelFont) {
      return AHTML.getLabelValueStr(AHTML.LABEL_FONT, getLabel(), getCurrentValue());
   }

   @Override
   public Control getControl() {
      if (labelWidget != null) {
         return labelWidget;
      } else if (labelHyperlink != null) {
         return labelHyperlink;
      }
      return comp;
   }

   public void addLabelWidgetListener(MouseListener listener) {
      if (Widgets.isAccessible(labelHyperlink)) {
         labelHyperlink.addMouseListener(listener);
      }
   }

   @Override
   public void adaptControls(FormToolkit toolkit) {
      if (getToolkit() != null) {
         if (Widgets.isAccessible(labelWidget)) {
            getToolkit().adapt(labelWidget, true, true);
         }
         if (Widgets.isAccessible(labelHyperlink)) {
            getToolkit().adapt(labelHyperlink, true, true);
         }
         if (Widgets.isAccessible(comp)) {
            getToolkit().adapt(comp);
         }
      }
   }

   public boolean hasArtifact() {
      return getArtifact() != null && getArtifact().isValid();
   }

   @Override
   public void setArtifact(Artifact artifact) {
      super.setArtifact(artifact);
      this.selected = getSelected();
   }

   @Override
   public boolean isSingleSelect() {
      if (hasArtifact() && getAttributeType().isValid()) {
         Multiplicity multiplicity = getArtifact().getArtifactType().getMultiplicity(getAttributeType());
         if (multiplicity.isValid()) {
            return multiplicity.matches(Multiplicity.EXACTLY_ONE);
         }
      }
      return super.isSingleSelect();
   }

   @Override
   public boolean isMultiSelect() {
      if (hasArtifact() && getAttributeType().isValid()) {
         Multiplicity multiplicity = getArtifact().getArtifactType().getMultiplicity(getAttributeType());
         if (multiplicity.isValid()) {
            return multiplicity.matches(Multiplicity.ANY) || multiplicity.matches(Multiplicity.AT_LEAST_ONE);
         }
      }
      return super.isMultiSelect();
   }

   @Override
   public IStatus isValid() {
      IStatus status = Status.OK_STATUS;
      try {
         if (getArtifact() != null && getAttributeType() != null) {
            String currValue = getCurrentValue();
            if (NOT_SET.equals(currValue)) {
               currValue = "";
            }
            if (isRequiredEntry() && Strings.isInValid(currValue)) {
               status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
                  String.format("Must select [%s]", getAttributeType().getUnqualifiedName()));
            }
         } else {
            if (isRequiredEntry() && isEmpty() && getCurrentValue().isEmpty()) {
               return new Status(IStatus.ERROR, Activator.PLUGIN_ID, getLabel() + " must be selected.");
            }
            if (isRequiredEntry() && getCurrentValue().equals(Widgets.NOT_SET)) {
               return new Status(IStatus.ERROR, Activator.PLUGIN_ID, getLabel() + " is Not Set.");
            }
            return Status.OK_STATUS;
         }
      } catch (OseeCoreException ex) {
         status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Error getting Artifact", ex);
      }
      return status;
   }

}
