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
package org.eclipse.osee.framework.ui.skynet.widgets.xx.flat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.core.util.CoreImage;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.core.widget.validate.IValueValidator;
import org.eclipse.osee.framework.core.widget.validate.XWidgetStatus;
import org.eclipse.osee.framework.core.widget.validate.XXNoOpValidator;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.DateSelectionDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXAbstractBaseXWidget;
import org.eclipse.osee.framework.ui.swt.FontManager;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.osgi.service.component.annotations.Component;

/**
 * Multi-value attribute widget. Displays a bold label with Add/Multi-Edit toolbar, then a bordered composite with one
 * row per attribute value. Each row has a hyperlink (click to edit) and a red X (to delete).
 *
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XXFlatAttrsWidget extends XXAbstractBaseXWidget<String> {

   public static final WidgetId ID = WidgetId.XXFlatAttrsWidget;

   private Composite entriesComp;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXFlatAttrsWidget() {
      this(ID, "");
   }

   public XXFlatAttrsWidget(WidgetId widgetId, String displayLabel) {
      super(widgetId, displayLabel);
      set(XOption.FILL_HORZ, XOption.VERT_LABEL, XOption.NO_LABEL, XOption.VAL_SINGLE_TEXT);
   }

   //////////////////////////////////////////
   // UI Construction
   //////////////////////////////////////////

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      super.createControls(parent, horizontalSpan);
   }

   @Override
   protected void createValueWidget() {
      // Label row: label on left, toolbar on right
      Composite labelRow = new Composite(comp, SWT.NONE);
      GridLayout labelLayout = new GridLayout(2, false);
      labelLayout.marginWidth = 0;
      labelLayout.marginHeight = 2;
      labelRow.setLayout(labelLayout);
      labelRow.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
      if (getToolkit() != null) {
         getToolkit().adapt(labelRow);
      }

      Label label = new Label(labelRow, SWT.NONE);
      label.setText(getLabel() + ":");
      label.setFont(FontManager.getDefaultLabelFont());
      label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
      if (getToolkit() != null) {
         getToolkit().adapt(label, true, true);
      }
      labelWidget = label;
      // Ctrl+Click opens the widget debug report (this widget is self-labeling, so it wires this itself)
      label.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseDown(MouseEvent event) {
            if (event.button == 1 && (event.stateMask & SWT.MODIFIER_MASK) == SWT.CTRL) {
               openWidgetDebug();
            }
         }
      });

      // Toolbar: Add + Multi-Edit (only if not READ)
      if (isNot(XOption.READ)) {
         ToolBar toolbar = new ToolBar(labelRow, SWT.FLAT | SWT.HORIZONTAL);
         toolbar.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
         ToolBarManager manager = new ToolBarManager(toolbar);
         manager.add(new Action("Add", ImageManager.getImageDescriptor(CoreImage.ADD_GREEN)) {
            {
               setToolTipText("Add new " + getAttributeType().getUnqualifiedName());
            }

            @Override
            public void run() {
               handleAdd();
            }
         });
         manager.add(new Action("Multi-Line Edit", ImageManager.getImageDescriptor(CoreImage.EDIT)) {
            {
               setToolTipText("Edit all values as multi-line text");
            }

            @Override
            public void run() {
               handleMultiLineEdit();
            }
         });
         manager.update(true);
         if (getToolkit() != null) {
            getToolkit().adapt(toolbar, true, true);
         }
      }

      // Bordered entries area
      entriesComp = new Composite(comp, SWT.BORDER);
      GridLayout entriesLayout = new GridLayout(1, false);
      entriesLayout.marginWidth = 0;
      entriesLayout.marginHeight = 0;
      entriesLayout.verticalSpacing = 0;
      entriesComp.setLayout(entriesLayout);
      entriesComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      if (getToolkit() != null) {
         getToolkit().adapt(entriesComp);
      }

      populateEntries();
   }

   //////////////////////////////////////////
   // Storage
   //////////////////////////////////////////

   public Collection<String> getStored() {
      if (getArtifact() != null && getAttributeType() != null && getAttributeType().isValid()) {
         return getArtifact().getAttributesToStringList(getAttributeType());
      }
      return Collections.emptyList();
   }

   @Override
   public String getDisplayValue() {
      int count = 0;
      if (getArtifact() != null && getAttributeType() != null && getAttributeType().isValid()) {
         count = getArtifact().getAttributes(getAttributeType()).size();
      }
      return count + " Item(s)";
   }

   @Override
   public String getSentinel() {
      return "";
   }

   //////////////////////////////////////////
   // Validation
   //////////////////////////////////////////

   @Override
   public XResultData isValid() {
      return XWidgetStatus.OK;
   }

   @Override
   protected IValueValidator getValidator() {
      return XXNoOpValidator.instance;
   }

   //////////////////////////////////////////
   // Refresh / Lifecycle
   //////////////////////////////////////////

   @Override
   public void refresh() {
      super.refresh();
      if (Widgets.isAccessible(entriesComp)) {
         populateEntries();
      }
   }

   @Override
   public void dispose() {
      Widgets.disposeWidget(entriesComp);
      super.dispose();
   }

   @Override
   public Control getControl() {
      return entriesComp;
   }

   //////////////////////////////////////////
   // Misc
   //////////////////////////////////////////

   private void populateEntries() {
      if (!Widgets.isAccessible(entriesComp)) {
         return;
      }

      // Clear existing
      for (Control child : entriesComp.getChildren()) {
         child.dispose();
      }

      Artifact artifact = getArtifact();
      if (artifact != null && getAttributeType() != null && getAttributeType().isValid()) {
         List<Attribute<Object>> attrs = artifact.getAttributes(getAttributeType());
         int minCount = artifact.getArtifactType().getMin(getAttributeType());

         for (int i = 0; i < attrs.size(); i++) {
            Attribute<Object> attr = attrs.get(i);
            createEntryRow(attr, attrs.size() > minCount);
            // Separator between entries (not after last)
            if (i < attrs.size() - 1) {
               Label separator = new Label(entriesComp, SWT.SEPARATOR | SWT.HORIZONTAL);
               separator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            }
         }
      }

      entriesComp.layout(true, true);
      if (entriesComp.getParent() != null) {
         entriesComp.getParent().layout(true, true);
      }
      if (getManagedForm() != null) {
         getManagedForm().reflow(true);
      }
   }

   private void createEntryRow(Attribute<Object> attr, boolean canDelete) {
      boolean readOnly = is(XOption.READ);
      int columns = readOnly ? 1 : (canDelete ? 3 : 2);

      Composite row = new Composite(entriesComp, SWT.NONE);
      GridLayout rowLayout = new GridLayout(columns, false);
      rowLayout.marginWidth = 5;
      rowLayout.marginHeight = 3;
      rowLayout.horizontalSpacing = 5;
      row.setLayout(rowLayout);
      row.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
      if (getToolkit() != null) {
         getToolkit().adapt(row);
      }

      // Edit icon (pen) on the left — only if not READ
      if (!readOnly) {
         Label editLabel = new Label(row, SWT.NONE);
         editLabel.setImage(ImageManager.getImage(CoreImage.EDIT));
         editLabel.setToolTipText("Edit this value");
         editLabel.setCursor(row.getDisplay().getSystemCursor(SWT.CURSOR_HAND));
         editLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
         if (getToolkit() != null) {
            getToolkit().adapt(editLabel, true, true);
         }
         editLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {
               handleEdit(attr);
            }
         });
      }

      // Value as styled text
      String displayValue = String.valueOf(attr.getValue());
      if (!Strings.isValid(displayValue)) {
         displayValue = "<empty>";
      }
      StyledText valueText = new StyledText(row, SWT.READ_ONLY | SWT.SINGLE);
      valueText.setText(displayValue);
      valueText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
      valueText.setEditable(false);
      valueText.setCaret(null);
      if (getToolkit() != null) {
         getToolkit().adapt(valueText);
      }

      // Red X to delete — only if not READ and canDelete
      if (!readOnly && canDelete) {
         Label deleteLabel = new Label(row, SWT.NONE);
         deleteLabel.setText(" X ");
         deleteLabel.setForeground(row.getDisplay().getSystemColor(SWT.COLOR_RED));
         deleteLabel.setToolTipText("Delete this value");
         deleteLabel.setCursor(row.getDisplay().getSystemCursor(SWT.CURSOR_HAND));
         deleteLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
         deleteLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {
               handleDelete(attr);
            }
         });
      }
   }

   private void handleAdd() {
      Artifact artifact = getArtifact();
      String result = promptForValue("Add " + getAttributeType().getUnqualifiedName(), "");
      if (result != null) {
         artifact.addAttributeFromString(getAttributeType(), result);
         artifact.persist("Add " + getAttributeType().getUnqualifiedName());
         populateEntries();
         notifyXModifiedListeners();
      }
   }

   private void handleEdit(Attribute<Object> attr) {
      String currentValue = String.valueOf(attr.getValue());
      String result = promptForValue("Edit " + getAttributeType().getUnqualifiedName(), currentValue);
      if (result != null) {
         attr.setFromString(result);
         getArtifact().persist("Edit " + getAttributeType().getUnqualifiedName());
         populateEntries();
         notifyXModifiedListeners();
      }
   }

   /**
    * Opens the appropriate dialog for the attribute type and returns the string value, or null if cancelled.
    */
   private String promptForValue(String title, String currentValue) {
      String attrName = getAttributeType().getUnqualifiedName();
      if (getAttributeType().isDate()) {
         Date initialDate = Strings.isValid(currentValue) ? new Date(Long.parseLong(currentValue)) : new Date();
         DateSelectionDialog dialog = new DateSelectionDialog(title, initialDate);
         if (dialog.open() == Window.OK && !dialog.isNoneSelected()) {
            return String.valueOf(dialog.getSelectedDate().getTime());
         }
         return null;
      } else if (getAttributeType().isBoolean()) {
         String[] options = {"True", "False", "Cancel"};
         MessageDialog dialog = new MessageDialog(entriesComp.getShell(), title, null, "Select " + attrName + " value:",
            MessageDialog.QUESTION, options, 0);
         int result = dialog.open();
         if (result == 0) {
            return "true";
         } else if (result == 1) {
            return "false";
         }
         return null;
      } else if (getAttributeType().isInteger()) {
         EntryDialog dialog = new EntryDialog(title, "Enter " + attrName + " (integer):");
         dialog.setEntry(currentValue);
         if (dialog.open() == Window.OK && Strings.isValid(dialog.getEntry())) {
            try {
               Integer.parseInt(dialog.getEntry().trim());
               return dialog.getEntry().trim();
            } catch (NumberFormatException ex) {
               AWorkbench.popup("Invalid integer value for " + attrName);
               return null;
            }
         }
         return null;
      } else if (getAttributeType().isLong()) {
         EntryDialog dialog = new EntryDialog(title, "Enter " + attrName + " (long):");
         dialog.setEntry(currentValue);
         if (dialog.open() == Window.OK && Strings.isValid(dialog.getEntry())) {
            try {
               Long.parseLong(dialog.getEntry().trim());
               return dialog.getEntry().trim();
            } catch (NumberFormatException ex) {
               AWorkbench.popup("Invalid long value for " + attrName);
               return null;
            }
         }
         return null;
      } else if (getAttributeType().isDouble()) {
         EntryDialog dialog = new EntryDialog(title, "Enter " + attrName + " (decimal):");
         dialog.setEntry(currentValue);
         if (dialog.open() == Window.OK && Strings.isValid(dialog.getEntry())) {
            try {
               Double.parseDouble(dialog.getEntry().trim());
               return dialog.getEntry().trim();
            } catch (NumberFormatException ex) {
               AWorkbench.popup("Invalid decimal value for " + attrName);
               return null;
            }
         }
         return null;
      } else {
         // Default: string entry
         EntryDialog dialog = new EntryDialog(title, "Enter " + attrName + ":");
         dialog.setEntry(currentValue);
         if (dialog.open() == Window.OK && Strings.isValid(dialog.getEntry())) {
            return dialog.getEntry();
         }
         return null;
      }
   }

   private void handleDelete(Attribute<Object> attr) {
      if (MessageDialog.openConfirm(entriesComp.getShell(), "Delete Value",
         "Are you sure you want to delete \"" + attr.getValue() + "\"?")) {
         getArtifact().deleteAttribute(attr);
         getArtifact().persist("Delete " + getAttributeType().getUnqualifiedName());
         populateEntries();
         notifyXModifiedListeners();
      }
   }

   private void handleMultiLineEdit() {
      Artifact artifact = getArtifact();
      List<String> currentValues = artifact.getAttributesToStringList(getAttributeType());
      String joined = String.join("\n", currentValues);

      EntryDialog dialog =
         new EntryDialog("Edit All - " + getAttributeType().getUnqualifiedName(), "One value per line:");
      dialog.setEntry(joined);
      dialog.setFillVertically(true);
      if (dialog.open() == Window.OK) {
         String text = dialog.getEntry();
         List<String> newValues = new ArrayList<>();
         if (Strings.isValid(text)) {
            for (String line : text.split("\n")) {
               String trimmed = line.trim();
               if (Strings.isValid(trimmed)) {
                  newValues.add(trimmed);
               }
            }
         }
         artifact.setAttributeFromValues(getAttributeType(), newValues);
         artifact.persist("Multi-line edit " + getAttributeType().getUnqualifiedName());
         populateEntries();
         notifyXModifiedListeners();
      }
   }

   //////////////////////////////////////////
   // Initialization
   //////////////////////////////////////////

   @Override
   public void setArtifact(Artifact artifact) {
      super.setArtifact(artifact);
   }

   @Override
   protected void initializeSelected() {
      if (xxArt.isArtifact() && getAttributeType().isValid()) {
         setSelected(getArtifact().getAttributesToStringList(getAttributeType()), false);
      }
   }

}
