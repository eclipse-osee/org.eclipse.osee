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
package org.eclipse.osee.framework.ui.skynet.widgets.xx.stack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
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
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
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
 * Multi-value attribute widget for multi-line text. Shows one page at a time with forward/back navigation. Each page is
 * a multi-line StyledText editor. Toolbar provides: Back, Forward, page counter, Add (+), Delete (X).
 * <p>
 * Auto-saves on page change and when navigating away.
 *
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XXStackedWidget extends XXAbstractBaseXWidget<String> {

   public static final WidgetId ID = WidgetId.XXStackedWidget;
   private static final int WORD_WRAP_LIMIT = 5000;

   private Composite bodyComp;
   private StyledText textWidget;
   private Label pageLabel;
   private final List<Attribute<Object>> attrs = new ArrayList<>();
   private int currentIndex = 0;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXStackedWidget() {
      this(ID, "");
   }

   public XXStackedWidget(WidgetId widgetId, String displayLabel) {
      super(widgetId, displayLabel);
      set(XOption.FILL_HORZ, XOption.VERT_LABEL, XOption.NO_LABEL, XOption.VAL_MULTI_TEXT);
   }

   //////////////////////////////////////////
   // UI Construction
   //////////////////////////////////////////

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      super.createControls(parent, horizontalSpan);
      // Ensure stacked widget fills both directions in all parent layouts
      GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
      gd.horizontalSpan = horizontalSpan;
      comp.setLayoutData(gd);
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
      label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
      if (getToolkit() != null) {
         getToolkit().adapt(label, true, true);
      }
      // Ctrl+Click opens the widget debug report (this widget is self-labeling, so it wires this itself)
      label.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseDown(MouseEvent event) {
            if (event.button == 1 && (event.stateMask & SWT.MODIFIER_MASK) == SWT.CTRL) {
               openWidgetDebug();
            }
         }
      });
      String toolTip = getToolTip();
      if (isEditable()) {
         label.setToolTipText(
            Strings.isValid(toolTip) ? getSelectToModifyMsg() + "\n\n" + toolTip : getSelectToModifyMsg());
      } else {
         label.setToolTipText(Strings.isValid(toolTip) ? READ_ONLY + "\n\n" + toolTip : READ_ONLY);
      }

      // Toolbar: Back, Forward, page label, Add, Delete
      ToolBar toolbar = new ToolBar(labelRow, SWT.FLAT | SWT.HORIZONTAL);
      toolbar.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
      ToolBarManager manager = new ToolBarManager(toolbar);

      manager.add(new Action("Back", ImageManager.getImageDescriptor(CoreImage.ARROW_LEFT_YELLOW)) {
         {
            setToolTipText("Previous page");
         }

         @Override
         public void run() {
            if (currentIndex > 0) {
               currentIndex--;
               showCurrentPage();
            }
         }
      });
      manager.add(new Action("Forward", ImageManager.getImageDescriptor(CoreImage.ARROW_RIGHT_YELLOW)) {
         {
            setToolTipText("Next page");
         }

         @Override
         public void run() {
            if (currentIndex < attrs.size() - 1) {
               currentIndex++;
               showCurrentPage();
            }
         }
      });
      manager.add(new Separator());
      if (isEditable()) {
         manager.add(new Action("Add", ImageManager.getImageDescriptor(CoreImage.ADD_GREEN)) {
            {
               setToolTipText("Add new page");
            }

            @Override
            public void run() {
               handleAdd();
            }
         });
         manager.add(new Action("Delete", ImageManager.getImageDescriptor(CoreImage.X_RED)) {
            {
               setToolTipText("Delete current page");
            }

            @Override
            public void run() {
               handleDelete();
            }
         });
      }
      manager.update(true);
      if (getToolkit() != null) {
         getToolkit().adapt(toolbar, true, true);
      }

      // Body: bordered area with edit icon + page counter + read-only StyledText
      bodyComp = new Composite(comp, SWT.BORDER);
      GridLayout bodyLayout = new GridLayout(1, false);
      bodyLayout.marginWidth = 5;
      bodyLayout.marginHeight = 5;
      bodyComp.setLayout(bodyLayout);
      bodyComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      if (getToolkit() != null) {
         getToolkit().adapt(bodyComp);
      }

      // Row: edit icon + page counter
      Composite headerRow = new Composite(bodyComp, SWT.NONE);
      GridLayout headerLayout = new GridLayout(2, false);
      headerLayout.marginWidth = 0;
      headerLayout.marginHeight = 0;
      headerRow.setLayout(headerLayout);
      headerRow.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
      if (getToolkit() != null) {
         getToolkit().adapt(headerRow);
      }

      // Edit icon (only if editable)
      if (isEditable()) {
         Label editIcon = new Label(headerRow, SWT.NONE);
         editIcon.setImage(ImageManager.getImage(CoreImage.EDIT));
         editIcon.setToolTipText("Edit current page");
         editIcon.setCursor(headerRow.getDisplay().getSystemCursor(SWT.CURSOR_HAND));
         editIcon.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
         if (getToolkit() != null) {
            getToolkit().adapt(editIcon, true, true);
         }
         editIcon.addMouseListener(new org.eclipse.swt.events.MouseAdapter() {
            @Override
            public void mouseUp(org.eclipse.swt.events.MouseEvent e) {
               handleEdit();
            }
         });
      }

      // Page counter
      pageLabel = new Label(headerRow, SWT.NONE);
      pageLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
      pageLabel.setFont(FontManager.getDefaultLabelFont());
      if (getToolkit() != null) {
         getToolkit().adapt(pageLabel, true, true);
      }

      // Read-only multi-line text display
      textWidget = new StyledText(bodyComp, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.READ_ONLY);
      GridData textGd = new GridData(SWT.FILL, SWT.FILL, true, true);
      textGd.heightHint = 100;
      textGd.widthHint = 100;
      textGd.minimumHeight = 60;
      textWidget.setLayoutData(textGd);
      textWidget.setEditable(false);
      textWidget.setCaret(null);
      textWidget.setBackground(bodyComp.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
      if (getToolkit() != null) {
         getToolkit().adapt(textWidget);
         textWidget.setBackground(bodyComp.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
      }

      loadAttributes();
      showCurrentPage();
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
      return count + " Page(s)";
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
      if (Widgets.isAccessible(bodyComp)) {
         loadAttributes();
         showCurrentPage();
      }
   }

   @Override
   public void dispose() {
      Widgets.disposeWidget(bodyComp);
      super.dispose();
   }

   @Override
   public Control getControl() {
      return bodyComp;
   }

   //////////////////////////////////////////
   // Misc
   //////////////////////////////////////////

   private void loadAttributes() {
      attrs.clear();
      Artifact artifact = getArtifact();
      if (artifact != null && getAttributeType() != null && getAttributeType().isValid()) {
         attrs.addAll(artifact.getAttributes(getAttributeType()));
      }
   }

   private void showCurrentPage() {
      if (!Widgets.isAccessible(textWidget)) {
         return;
      }
      if (attrs.isEmpty()) {
         textWidget.setText("");
         updatePageLabel();
         return;
      }
      if (currentIndex >= attrs.size()) {
         currentIndex = attrs.size() - 1;
      }
      if (currentIndex < 0) {
         currentIndex = 0;
      }

      Attribute<Object> attr = attrs.get(currentIndex);
      String value = attr.getDisplayableString();
      if (!Strings.isValid(value)) {
         value = "";
      }
      textWidget.setText(value);

      // Disable word wrap for very large text (performance)
      textWidget.setWordWrap(value.length() <= WORD_WRAP_LIMIT);

      updatePageLabel();
   }

   private void updatePageLabel() {
      if (Widgets.isAccessible(pageLabel)) {
         if (attrs.isEmpty()) {
            pageLabel.setText("0 of 0");
         } else {
            pageLabel.setText((currentIndex + 1) + " of " + attrs.size());
         }
         pageLabel.getParent().layout(true);
      }
   }

   private void handleEdit() {
      if (attrs.isEmpty() || currentIndex < 0 || currentIndex >= attrs.size()) {
         return;
      }
      Attribute<Object> attr = attrs.get(currentIndex);
      String currentValue = attr.getDisplayableString();
      if (!Strings.isValid(currentValue)) {
         currentValue = "";
      }

      org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog dialog =
         new org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog(
            "Edit " + getAttributeType().getUnqualifiedName(), "Edit value:");
      dialog.setEntry(currentValue);
      dialog.setFillVertically(true);
      if (dialog.open() == org.eclipse.jface.window.Window.OK) {
         String newValue = dialog.getEntry();
         // ORCS rule: never store null/empty — delete the attribute instead
         if (!Strings.isValid(newValue)) {
            int minCount = getArtifact().getArtifactType().getMin(getAttributeType());
            if (attrs.size() > minCount) {
               getArtifact().deleteAttribute(attr);
               getArtifact().persist("Delete empty " + getAttributeType().getUnqualifiedName());
               loadAttributes();
               if (currentIndex >= attrs.size()) {
                  currentIndex = Math.max(0, attrs.size() - 1);
               }
            }
         } else if (!newValue.equals(currentValue)) {
            attr.setFromString(newValue);
            getArtifact().persist("Edit " + getAttributeType().getUnqualifiedName());
         }
         showCurrentPage();
         notifyXModifiedListeners();
      }
   }

   private void handleAdd() {
      org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog dialog =
         new org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog(
            "Add " + getAttributeType().getUnqualifiedName(), "Enter value:");
      dialog.setFillVertically(true);
      if (dialog.open() == org.eclipse.jface.window.Window.OK) {
         String value = dialog.getEntry();
         if (Strings.isValid(value)) {
            Artifact artifact = getArtifact();
            artifact.addAttributeFromString(getAttributeType(), value);
            artifact.persist("Add " + getAttributeType().getUnqualifiedName());
            loadAttributes();
            currentIndex = attrs.size() - 1;
            showCurrentPage();
            notifyXModifiedListeners();
         }
      }
   }

   private void handleDelete() {
      if (attrs.isEmpty()) {
         return;
      }
      int minCount = getArtifact().getArtifactType().getMin(getAttributeType());
      if (attrs.size() <= minCount) {
         MessageDialog.openInformation(bodyComp.getShell(), "Cannot Delete",
            "Must have at least " + minCount + " " + getAttributeType().getUnqualifiedName() + " value(s).");
         return;
      }
      Attribute<Object> attr = attrs.get(currentIndex);
      if (MessageDialog.openConfirm(bodyComp.getShell(), "Delete Page",
         "Are you sure you want to delete this " + getAttributeType().getUnqualifiedName() + " value?")) {
         getArtifact().deleteAttribute(attr);
         getArtifact().persist("Delete " + getAttributeType().getUnqualifiedName());
         loadAttributes();
         if (currentIndex >= attrs.size()) {
            currentIndex = Math.max(0, attrs.size() - 1);
         }
         showCurrentPage();
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

}
