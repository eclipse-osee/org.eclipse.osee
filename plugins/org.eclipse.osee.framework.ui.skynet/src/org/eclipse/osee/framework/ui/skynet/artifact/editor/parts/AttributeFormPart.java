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

package org.eclipse.osee.framework.ui.skynet.artifact.editor.parts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.DisplayHint;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.help.ui.OseeHelpContext;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.utility.OseeInfo;
import org.eclipse.osee.framework.ui.plugin.util.HelpUtil;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditorProviders;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.IArtifactEditorProvider;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.sections.AttributeTypeUtil;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XTextWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidgetAccessDecorationProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidgetDecorator;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidgetUtility;
import org.eclipse.osee.framework.ui.skynet.widgets.util.AttributeXWidgetProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.util.IAttributeXWidgetProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetSwtRenderer;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetPage;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.FontManager;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.AbstractFormPart;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.events.IExpansionListener;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Roberto E. Escobar
 */
public class AttributeFormPart extends AbstractFormPart {

   private static final String ATTR_FORM_PART_LINE_LIMIT = "attr.form.part.line.limit";
   private final ArtifactEditor editor;
   private Composite composite;
   private final XWidgetDecorator decorator = new XWidgetDecorator();
   private final Map<AttributeTypeToken, Composite> xWidgetsMap = new HashMap<>();
   private final List<XWidget> allXWidgets = new ArrayList<XWidget>();
   private static Integer formLineLimit;

   private final XModifiedListener widgetModifiedListener = new XModifiedListener() {

      @Override
      public void widgetModified(XWidget widget) {
         editor.onDirtied();
      }
   };

   public AttributeFormPart(ArtifactEditor editor) {
      this.editor = editor;
      try {
         decorator.addProvider(new XWidgetAccessDecorationProvider());
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex.toString(), ex);
      }
   }

   @Override
   public void refresh() {
      super.refresh();//<--This method resets the dirty bits on all widgets, but does not implicitly revert their values. (see below)
      decorator.refresh();
      getManagedForm().getForm().getBody().layout(true, true);
   }

   public void computeTextSizesAndReflow() {
      for (XWidget widget : XWidgetUtility.findXWidgetsInControl(composite)) {
         if (widget instanceof XTextWidget) {
            computeXTextSize((XTextWidget) widget);
         }
      }
      getManagedForm().reflow(true);
   }

   public static void computeXTextSize(XTextWidget xText) {
      if (Widgets.isAccessible(xText.getStyledText())) {
         int lineCount = xText.getStyledText().getLineCount();
         int lineLimit = getLineLimit();
         lineCount = lineCount > lineLimit ? lineLimit : lineCount;
         int height = lineCount * xText.getStyledText().getLineHeight();
         GridData formTextGd = new GridData(SWT.FILL, SWT.FILL, true, true);
         if (xText.isFillVertically() && height < 60) {
            formTextGd.heightHint = 60;
         } else {
            formTextGd.heightHint = height;
         }
         formTextGd.widthHint = 200;
         xText.getStyledText().setLayoutData(formTextGd);
      }
   }

   private static int getLineLimit() {
      if (formLineLimit == null) {
         formLineLimit = 2000;
         if (ClientSessionManager.isSessionValid()) {
            try {
               String dbFormLineLimit = OseeInfo.getCachedValue(ATTR_FORM_PART_LINE_LIMIT);
               formLineLimit = Strings.isNumeric(dbFormLineLimit) ? Integer.valueOf(dbFormLineLimit) : formLineLimit;
            } catch (OseeCoreException ex) {
               // do nothing
            }
         }
      }
      return formLineLimit;
   }

   public void createContents(Composite composite) {
      this.composite = composite;
      decorator.dispose();

      composite.setLayout(ALayout.getZeroMarginLayout(1, false));
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      composite.setVisible(false);

      try {
         Artifact artifact = editor.getEditorInput().getArtifact();

         List<AttributeTypeToken> types = AttributeTypeUtil.getTypesWithData(artifact);
         addWidgetForAttributeType(types);

         addOtherWidgets(composite);

         layoutControls(composite);

         XWidgetUtility.setLabelFontsBold(allXWidgets);

      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, "Unable to access attribute types", ex);
      } finally {
         composite.setVisible(true);
      }
   }

   private Composite addOtherWidgets(Composite parent) {

      boolean applicable = false;
      for (IArtifactEditorProvider widgetProvider : ArtifactEditorProviders.getXWidgetProviders()) {
         if (widgetProvider.isApplicable(editor.getArtifactFromEditorInput())) {
            applicable = true;
            break;
         }
      }
      if (!applicable) {
         return null;
      }

      boolean isEditable = !editor.getEditorInput().getArtifact().isReadOnly();
      FormToolkit toolkit = getManagedForm().getToolkit();
      Artifact artifact = editor.getArtifactFromEditorInput();

      Composite internalComposite = toolkit.createComposite(parent, SWT.WRAP);
      GridLayout layout = new GridLayout(1, false);
      layout.marginLeft = 20;
      internalComposite.setLayout(layout);
      GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
      gd.widthHint = 100;
      gd.minimumWidth = 100;
      internalComposite.setLayoutData(gd);

      Group groupComp = new Group(internalComposite, SWT.None);
      groupComp.setToolTipText("Other Meta-Data not stored as attribute");
      groupComp.setText("Other");
      GridLayout gLayout = new GridLayout(1, false);
      layout.marginLeft = 10;
      groupComp.setLayout(gLayout);
      groupComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      groupComp.setFont(FontManager.getCourierNew12Bold());

      List<XWidget> widgets = new ArrayList<>();
      for (IArtifactEditorProvider widgetProvider : ArtifactEditorProviders.getXWidgetProviders()) {
         try {
            widgetProvider.addOtherWidgets(artifact, isEditable, editor, groupComp, widgets);
         } catch (OseeCoreException ex) {
            toolkit.createLabel(composite, String.format("Error creating controls for: [%s] [%s]",
               widgetProvider.getClass().getSimpleName(), ex.getLocalizedMessage()));
         }
      }

      allXWidgets.addAll(widgets);

      for (XWidget xWidget : widgets) {
         xWidget.setArtifact(artifact);
         decorator.addWidget(xWidget);
      }

      return internalComposite;
   }

   public void addWidgetForAttributeType(Collection<AttributeTypeToken> attributeTypes) {
      Artifact artifact = editor.getEditorInput().getArtifact();
      boolean isEditable = !artifact.isReadOnly();

      for (AttributeTypeToken attributeType : attributeTypes) {
         if (artifact.isOfType(CoreArtifactTypes.Markdown) && attributeType.equals(
            CoreAttributeTypes.MarkdownContent)) {
            continue;
         }
         Composite internalComposite;
         if (attributeType.getDisplayHints().contains(DisplayHint.MultiLine)) {
            internalComposite = createAttributeTypeControlsInSection(composite, attributeType, isEditable, 15);
         } else {
            internalComposite = createAttributeTypeControls(composite, artifact, attributeType, isEditable, false, 20);
         }
         HelpUtil.setHelp(internalComposite, OseeHelpContext.ARTIFACT_EDITOR__ATTRIBUTES);
         xWidgetsMap.put(attributeType, internalComposite);
      }
      //      refresh(); // <-- This call reverts unsaved changes to all widgets.  Not the behavior we want here.
      decorator.refresh();
      getManagedForm().getForm().getBody().layout(true, true);
      getManagedForm().getForm().layout(true, true);
      getManagedForm().reflow(true);
   }

   @Override
   public void dispose() {
      Widgets.disposeWidget(composite);
      super.dispose();
   }

   private void layoutControls(Control control) {
      if (control instanceof Label || control instanceof Button) {
         control.setLayoutData(new GridData(SWT.BEGINNING, SWT.FILL, false, false));
      }

      if (control instanceof Composite) {
         Composite container = (Composite) control;
         for (Control child : container.getChildren()) {
            layoutControls(child);
         }
      }
   }

   private Composite createAttributeTypeControls(Composite parent, Artifact artifact, AttributeTypeToken attributeType,
      boolean isEditable, boolean isExpandable, int leftMargin) {
      FormToolkit toolkit = getManagedForm().getToolkit();
      Composite internalComposite = toolkit.createComposite(parent, SWT.WRAP);

      GridLayout layout = ALayout.getZeroMarginLayout(1, false);
      layout.marginLeft = leftMargin;
      internalComposite.setLayout(layout);
      GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
      gridData.widthHint = 100;
      gridData.minimumWidth = 100;
      internalComposite.setLayoutData(gridData);

      try {
         IAttributeXWidgetProvider xWidgetProvider = AttributeXWidgetProvider.get();
         List<XWidgetData> concreteWidgets =
            xWidgetProvider.getDynamicXWidgetLayoutData(artifact.getArtifactType(), attributeType);

         //Set widget to label since non renderable attribute type should not be edited in artifact edtor
         if (attributeType.notRenderable()) {
            String attributeEditStatus = " (not editable here)";
            for (int i = 0; i < concreteWidgets.size(); i++) {
               concreteWidgets.get(i).setName(attributeType.getUnqualifiedName().concat(attributeEditStatus));
               concreteWidgets.get(i).setWidgetId(WidgetId.XLabelArtWidget);
            }
         }
         if (isExpandable) {
            for (XWidgetData widData : concreteWidgets) {
               widData.add(XOption.NO_LABEL);
            }
         }
         for (XWidgetData item : concreteWidgets) {
            if (item.getWidgetId().equals(WidgetId.XXTextWidget)) {
               if (!item.isFillVertically()) {
                  if (artifact.getArtifactType().getMax(attributeType) == 1) {
                     String value = artifact.getSoleAttributeValue(attributeType, "");
                     if (value != null && value.contains(System.getProperty("line.separator"))) {
                        item.setFillVertically(true);
                     }
                  }
               }
            }
         }
         XWidgetPage workPage = new XWidgetPage(concreteWidgets);

         XWidgetSwtRenderer swtXWidgetRenderer =
            workPage.createBody(getManagedForm(), internalComposite, artifact, widgetModifiedListener, isEditable);
         Collection<XWidget> widgets = swtXWidgetRenderer.getXWidgets();
         allXWidgets.addAll(widgets);

         for (XWidget xWidget : widgets) {
            decorator.addWidget(xWidget);
         }

      } catch (OseeCoreException ex) {
         toolkit.createLabel(parent,
            String.format("Error creating controls for: [%s] [%s]", attributeType.getName(), ex.getLocalizedMessage()));
      }
      return internalComposite;
   }

   private Composite createAttributeTypeControlsInSection(Composite parent, AttributeTypeToken attributeType,
      boolean isEditable, int leftMargin) {
      FormToolkit toolkit = getManagedForm().getToolkit();

      int style = ExpandableComposite.SHORT_TITLE_BAR | ExpandableComposite.TREE_NODE;
      ExpandableComposite expandable = toolkit.createExpandableComposite(parent, style);
      expandable.setText(attributeType.getName());

      GridLayout layout = ALayout.getZeroMarginLayout(1, false);
      expandable.setLayout(layout);
      GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
      gridData.widthHint = 100;
      gridData.minimumWidth = 100;
      expandable.setLayoutData(gridData);

      Artifact artifact = editor.getEditorInput().getArtifact();

      Composite composite =
         createAttributeTypeControls(expandable, artifact, attributeType, isEditable, true, leftMargin);
      expandable.setClient(composite);

      expandable.addExpansionListener(new IExpansionListener() {

         @Override
         public void expansionStateChanged(ExpansionEvent e) {
            getManagedForm().getForm().reflow(true);
         }

         @Override
         public void expansionStateChanging(ExpansionEvent e) {
            getManagedForm().getForm().reflow(false);
         }

      });
      toolkit.paintBordersFor(expandable);

      return expandable;
   }

   public void removeWidgetForAttributeType(Collection<? extends AttributeTypeId> attributeTypes) {
      for (AttributeTypeId attributeType : attributeTypes) {
         xWidgetsMap.remove(attributeType).dispose();
         //decorator.addWidget(xWidget)
      }
      if (attributeTypes.size() > 0) {
         markDirty();
      }
      //refresh(); <-- This call reverts unsaved changes to all widgets.  Not the behavior we want here.
      decorator.refresh();
      getManagedForm().getForm().getBody().layout(true, true);
   }

   //   private final class XWidgetValidationListener implements XModifiedListener {
   //
   //      @Override
   //      public void widgetModified(XWidget xWidget) {
   //         if (xWidget != null && xWidget instanceof ArtifactStoredWidget) {
   //            ArtifactStoredWidget aWidget = (ArtifactStoredWidget) xWidget;
   //            try {
   //               Result result = aWidget.isDirty();
   //               if (result.isTrue()) {
   //                  xWidget.setControlCausedMessage("attribute.dirty", "Dirty", IMessageProvider.INFORMATION);
   //                  if (!isDirty()) {
   //                     markDirty();
   //                  }
   //               } else {
   //                  xWidget.removeControlCausedMessage("attribute.dirty");
   //               }
   //            } catch (Exception ex) {
   //               xWidget.setControlCausedMessage("attribute.dirty", "Unable to compute isDirty", IMessageProvider.ERROR);
   //            }
   //         }
   //      }
   //   }
}
