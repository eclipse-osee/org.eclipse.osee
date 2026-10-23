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

package org.eclipse.osee.framework.ui.skynet.artifact.editor.tab.art;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.DisplayHint;
import org.eclipse.osee.framework.core.data.Multiplicity;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.util.AttributeTypeUtil;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.AttributeXWidgetProviderIde;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetPage;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetSwtRendererListener;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXLabelValueWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXTextWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.util.XWidgetAccessDecorationProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.util.XWidgetDecorator;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.util.XWidgetUtility;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.util.listen.XModifiedListener;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.AbstractFormPart;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * Creates and manages XX widgets for each attribute type in the Artifact Editor.
 *
 * @author Roberto E. Escobar
 * @author Donald G. Dunne
 */
public class AttributeFormPart extends AbstractFormPart implements XWidgetSwtRendererListener {

   private final ArtifactEditor editor;
   private Composite bodyComp;
   private final XWidgetDecorator decorator = new XWidgetDecorator();
   private final Map<AttributeTypeToken, Composite> xWidgetsMap = new HashMap<>();
   private final List<XWidget> allXWidgets = new ArrayList<>();
   private final XModifiedListener widgetModifiedListener;
   private final List<String[]> widgetDebugRows = new ArrayList<>();

   public AttributeFormPart(ArtifactEditor editor) {
      this.editor = editor;
      this.widgetModifiedListener = new XModifiedListener() {
         @Override
         public void widgetModified(XWidget widget) {
            editor.onDirtied();
         }
      };
      try {
         decorator.addProvider(new XWidgetAccessDecorationProvider());
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex.toString(), ex);
      }
   }

   public void createContents(Composite parent) {
      decorator.dispose();

      parent.setLayout(new GridLayout(1, false));
      parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      FormToolkit toolkit = getManagedForm().getToolkit();

      bodyComp = toolkit.createComposite(parent, SWT.NONE);
      GridLayout layout = new GridLayout(1, false);
      bodyComp.setLayout(layout);
      bodyComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      try {
         Artifact artifact = editor.getEditorInput().getArtifact();
         List<AttributeTypeToken> types = AttributeTypeUtil.getTypesWithData(artifact);
         addWidgetForAttributeType(types);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, "Unable to access attribute types", ex);
      }
   }

   public void addWidgetForAttributeType(Collection<AttributeTypeToken> attributeTypes) {
      Artifact artifact = editor.getEditorInput().getArtifact();
      boolean isEditable = !artifact.isReadOnly();

      widgetDebugRows.clear();
      for (AttributeTypeToken attributeType : attributeTypes) {
         if (artifact.isOfType(CoreArtifactTypes.Markdown) && attributeType.equals(
            CoreAttributeTypes.MarkdownContent)) {
            continue;
         }
         try {
            Composite widgetComp = createWidget(bodyComp, artifact, attributeType, isEditable);
            xWidgetsMap.put(attributeType, widgetComp);
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, "Error creating widget for: " + attributeType.getName(), ex);
            createErrorWidget(bodyComp, attributeType);
         }
         if (CoreAttributeTypes.Name.equals(attributeType)) {
            getManagedForm().getToolkit().createSeparator(bodyComp, SWT.HORIZONTAL).setLayoutData(
               new GridData(SWT.FILL, SWT.CENTER, true, false));
         }
      }
      decorator.refresh();
      getManagedForm().reflow(true);
      if (OseeProperties.isWidgetDebug() && !widgetDebugRows.isEmpty()) {
         reportWidgetDebug(artifact.getArtifactType());
      }
   }

   /**
    * Captures the resolved widget choice for an attribute so the widget-debug report can show which XWidget/XOptions
    * the provider+merge chose for each attribute (WidgetId, base type, multiplicity, and final XOptions).
    */
   private void captureWidgetDebug(ArtifactTypeToken artType, AttributeTypeToken attributeType,
      Collection<XWidget> widgets) {
      int min = artType.getMin(attributeType);
      int max = artType.getMax(attributeType);
      String multStr = min + ".." + (max == Integer.MAX_VALUE ? "n" : String.valueOf(max));
      String baseType = attributeType.isEnumerated() ? "Enum" : //
         attributeType.isString() ? (attributeType.isMultiLine() ? "String(multi-line)" : "String(single-line)") : //
            attributeType.isBoolean() ? "Boolean" : attributeType.isDate() ? "Date" : //
               attributeType.isInteger() ? "Integer" : attributeType.isLong() ? "Long" : //
                  attributeType.isDouble() ? "Double" : attributeType.isArtifactId() ? "ArtifactId" : "Other";
      String widgetId = "?";
      String valOption = "";
      String editMode = "";
      String otherOpts = "";
      for (XWidget xWidget : widgets) {
         XWidgetData widData = xWidget.getWidData();
         widgetId = widData.getWidgetId().getName();
         List<String> others = new ArrayList<>();
         for (XOption opt : widData.getXOptionHandler().getXOptions()) {
            String name = opt.name();
            if (name.startsWith("VAL_")) {
               valOption = name;
            } else if (name.equals("READ") || name.equals("WRITE")) {
               editMode = name;
            } else {
               others.add(name);
            }
         }
         otherOpts = others.toString();
      }
      widgetDebugRows.add(
         new String[] {attributeType.getUnqualifiedName(), baseType, multStr, widgetId, valOption, editMode, otherOpts});
   }

   private void reportWidgetDebug(ArtifactTypeToken artType) {
      XResultData rd = new XResultData();
      rd.logf("Artifact Editor Widget Debug for Artifact Type: %s\n\n", artType.toStringWithId());
      rd.addRaw(AHTML.beginMultiColumnTable(100, 1));
      rd.addRaw(AHTML.addHeaderRowMultiColumnTable(new String[] {
         "Attribute",
         "Base Type",
         "Multiplicity (min..max)",
         "WidgetId",
         "VAL_ Mode",
         "Read/Write",
         "Other XOptions"}));
      for (String[] row : widgetDebugRows) {
         rd.addRaw(AHTML.addRowMultiColumnTable(row));
      }
      rd.addRaw(AHTML.endMultiColumnTable());
      XResultDataUI.report(rd, "Artifact Editor Widget Debug");
   }

   private void createErrorWidget(Composite parent, AttributeTypeToken attributeType) {
      XXLabelValueWidget errorWidget =
         new XXLabelValueWidget(attributeType.getUnqualifiedName(), "Error creating widget... see error log");
      errorWidget.createWidgets(getManagedForm(), parent, 1);
      errorWidget.setValueFgColor(SWT.COLOR_RED);
   }

   public void removeWidgetForAttributeType(Collection<? extends AttributeTypeId> attributeTypes) {
      for (AttributeTypeId attributeType : attributeTypes) {
         Composite comp = xWidgetsMap.remove(attributeType);
         if (comp != null) {
            comp.dispose();
         }
      }
      if (!attributeTypes.isEmpty()) {
         markDirty();
      }
      decorator.refresh();
      getManagedForm().reflow(true);
   }

   /**
    * Clear all attribute widgets and re-create from the artifact's current state.
    */
   private void recreateContents() {
      // Dispose all existing widgets
      allXWidgets.clear();
      xWidgetsMap.clear();
      decorator.dispose();
      for (org.eclipse.swt.widgets.Control child : bodyComp.getChildren()) {
         child.dispose();
      }

      // Re-create from artifact
      Artifact artifact = editor.getEditorInput().getArtifact();
      java.util.List<AttributeTypeToken> types =
         org.eclipse.osee.framework.ui.skynet.artifact.editor.util.AttributeTypeUtil.getTypesWithData(artifact);
      addWidgetForAttributeType(types);
      bodyComp.layout(true, true);
      getManagedForm().reflow(true);
   }

   @Override
   public void dispose() {
      Widgets.disposeWidget(bodyComp);
      super.dispose();
   }

   @Override
   public void refresh() {
      super.refresh();
      decorator.refresh();
      getManagedForm().reflow(true);
   }

   @Override
   public XWidgetData updateMergedXWidgetData(XWidgetData widData) {
      widData.set(XOption.NO_INLINE_EDIT);
      widData.set(XOption.FILL_HORZ);
      AttributeTypeToken attributeType = widData.getAttributeType();
      if (attributeType.isValid()) {
         if (attributeType.getDisplayHints().stream().anyMatch(DisplayHint::isReadonly)) {
            widData.set(XOption.READ);
         }
         ArtifactTypeToken artType = widData.getArtifactType();
         if (artType.isValid()) {
            Multiplicity mult = artType.getMultiplicity(attributeType);
            if (mult.is(Multiplicity.ZERO_OR_ONE, Multiplicity.EXACTLY_ONE)) {
               // Single-value: use text entry for strings/numbers/dates, selection for enums/tokens
               if (attributeType.isString() || attributeType.isDate() || attributeType.isInteger() || attributeType.isLong() || attributeType.isDouble() || attributeType.isBoolean()) {
                  widData.set(XOption.VAL_SINGLE_TEXT);
               } else {
                  widData.set(XOption.VAL_SINGLE_SEL);
               }
            } else {
               widData.set(XOption.VAL_MULTI_SEL);
            }
         }
      }
      return widData;
   }

   //////////////////////////////////////////
   // Widget Creation
   //////////////////////////////////////////

   private Composite createWidget(Composite parent, Artifact artifact, AttributeTypeToken attributeType,
      boolean isEditable) {
      FormToolkit toolkit = getManagedForm().getToolkit();

      // Row composite: widget on left, delete icon on right
      Composite rowComp = toolkit.createComposite(parent, SWT.NONE);
      GridLayout rowLayout = new GridLayout(2, false);
      rowLayout.marginWidth = 0;
      rowLayout.marginHeight = 2;
      rowLayout.marginLeft = 5;
      rowComp.setLayout(rowLayout);
      rowComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      // Widget area
      Composite widgetComp = toolkit.createComposite(rowComp, SWT.NONE);
      GridLayout widgetLayout = new GridLayout(1, false);
      widgetLayout.marginWidth = 0;
      widgetLayout.marginHeight = 0;
      widgetComp.setLayout(widgetLayout);
      widgetComp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

      try {
         XWidgetData widData =
            AttributeXWidgetProviderIde.get().getDefaultXWidgetData(artifact.getArtifactType(), attributeType);
         XWidgetPage page = new XWidgetPage(Arrays.asList(widData), this);
         page.createIfMissing();
         page.createBody(getManagedForm(), widgetComp, artifact, widgetModifiedListener, isEditable);
         Collection<XWidget> widgets = page.getSwtXWidgetRenderer().getXWidgets();
         allXWidgets.addAll(widgets);
         for (XWidget xWidget : widgets) {
            decorator.addWidget(xWidget);
         }
         if (OseeProperties.isWidgetDebug()) {
            captureWidgetDebug(artifact.getArtifactType(), attributeType, widgets);
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, "Error creating widget for: " + attributeType.getName(), ex);
         org.eclipse.osee.framework.ui.skynet.widgets.xx.XXLabelValueWidget errorWidget =
            new org.eclipse.osee.framework.ui.skynet.widgets.xx.XXLabelValueWidget(attributeType.getUnqualifiedName(),
               "Error creating widget... see error log");
         errorWidget.createWidgets(getManagedForm(), widgetComp, 1);
         errorWidget.setValueFgColor(SWT.COLOR_RED);
      }

      // Delete (X) button on right — only if editable, not readonly, and not EXACTLY_ONE
      Multiplicity mult = artifact.getArtifactType().getMultiplicity(attributeType);
      boolean isReadOnly = attributeType.getDisplayHints().stream().anyMatch(DisplayHint::isReadonly);
      boolean canDelete = isEditable && !isReadOnly && !mult.is(Multiplicity.EXACTLY_ONE);
      if (canDelete) {
         Label deleteLabel = toolkit.createLabel(rowComp, "  X  ");
         deleteLabel.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_RED));
         deleteLabel.setToolTipText("Delete " + attributeType.getUnqualifiedName());
         deleteLabel.setCursor(parent.getDisplay().getSystemCursor(SWT.CURSOR_HAND));
         deleteLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
         deleteLabel.addMouseListener(new org.eclipse.swt.events.MouseAdapter() {
            @Override
            public void mouseUp(org.eclipse.swt.events.MouseEvent e) {
               if (org.eclipse.jface.dialogs.MessageDialog.openConfirm(parent.getShell(), "Delete Attribute",
                  "Are you sure you want to delete \"" + attributeType.getUnqualifiedName() + "\"?")) {
                  artifact.deleteAttributes(attributeType);
                  artifact.persist("Delete attribute: " + attributeType.getUnqualifiedName());
                  recreateContents();
               }
            }
         });
      } else {
         // Spacer so layout stays consistent
         toolkit.createLabel(rowComp, "");
      }

      // Separator line below
      Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
      separator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

      return rowComp;
   }

   //////////////////////////////////////////
   // Text Size Computation
   //////////////////////////////////////////

   public void computeTextSizesAndReflow() {
      for (XWidget widget : XWidgetUtility.findXWidgetsInControl(bodyComp)) {
         if (widget instanceof XXTextWidget) {
            XWidgetUtility.computeXTextSize((XXTextWidget) widget);
         }
      }
      getManagedForm().reflow(true);
   }

}
