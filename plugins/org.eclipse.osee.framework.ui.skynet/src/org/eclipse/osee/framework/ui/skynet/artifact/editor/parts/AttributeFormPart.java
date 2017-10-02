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
package org.eclipse.osee.framework.ui.skynet.artifact.editor.parts;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.help.ui.OseeHelpContext;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.AccessPolicy;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.utility.OseeInfo;
import org.eclipse.osee.framework.ui.plugin.util.HelpUtil;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.sections.AttributeTypeUtil;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.internal.DslGrammarManager;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.framework.ui.skynet.widgets.IArtifactStoredWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XOption;
import org.eclipse.osee.framework.ui.skynet.widgets.XText;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidgetAccessDecorationProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidgetDecorator;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidgetUtility;
import org.eclipse.osee.framework.ui.skynet.widgets.util.AttributeXWidgetManager;
import org.eclipse.osee.framework.ui.skynet.widgets.util.DefaultAttributeXWidgetProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.util.DefaultXWidgetOptionResolver;
import org.eclipse.osee.framework.ui.skynet.widgets.util.IAttributeXWidgetProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.util.SwtXWidgetRenderer;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetPage;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetRendererItem;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.FontManager;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
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

   private final XModifiedListener widgetModifiedListener = new XModifiedListener() {

      @Override
      public void widgetModified(XWidget widget) {
         editor.onDirtied();
      }
   };

   public AttributeFormPart(ArtifactEditor editor) {
      this.editor = editor;
      try {
         AccessPolicy policy = ServiceUtil.getAccessPolicy();
         decorator.addProvider(new XWidgetAccessDecorationProvider(policy));
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex.toString(), ex);
      }
   }

   @Override
   public void refresh() {
      super.refresh();//<--This method resets the dirty bits on all widgets, but does not implicitly revert their values. (see below)
      decorator.refresh();
      getManagedForm().getForm().getBody().layout(true, true);

      //Revert any unsaved changes in the widgets.
      List<XWidget> widgets = XWidgetUtility.findXWidgetsInControl(composite);
      for (XWidget xWidget : widgets) {
         if (xWidget.isEditable()) {
            if (xWidget instanceof IArtifactStoredWidget) {
               IArtifactStoredWidget aWidget = (IArtifactStoredWidget) xWidget;
               try {
                  aWidget.revert();
               } catch (OseeCoreException ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, ex);
               }
            }
         }
      }
   }

   public void computeTextSizesAndReflow() {
      for (XWidget widget : XWidgetUtility.findXWidgetsInControl(composite)) {
         if (widget instanceof XText) {
            computeXTextSize((XText) widget);
         }
      }
      getManagedForm().reflow(true);
   }

   public static void computeXTextSize(XText xText) {
      if (Widgets.isAccessible(xText.getStyledText())) {
         int lineCount = xText.getStyledText().getLineCount();
         String formLineLimit = OseeInfo.getCachedValue(ATTR_FORM_PART_LINE_LIMIT);
         int lineLimit = Strings.isNumeric(formLineLimit) ? Integer.valueOf(formLineLimit) : 2000;
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

         layoutControls(composite);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, "Unable to access attribute types", ex);
      } finally {
         composite.setVisible(true);
      }
   }

   public void addWidgetForAttributeType(Collection<AttributeTypeToken> attributeTypes) {
      Artifact artifact = editor.getEditorInput().getArtifact();
      boolean isEditable = !artifact.isReadOnly();

      for (AttributeTypeToken attributeType : attributeTypes) {
         Composite internalComposite;
         if (DefaultAttributeXWidgetProvider.useMultiLineWidget(attributeType) || DslGrammarManager.isDslAttributeType(
            attributeType)) {
            internalComposite = createAttributeTypeControlsInSection(composite, attributeType, isEditable, 15);
         } else {
            internalComposite = createAttributeTypeControls(composite, artifact, attributeType, isEditable, false, 20);
         }
         setLabelFonts(internalComposite, FontManager.getDefaultLabelFont());
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

   public static void setLabelFonts(Control parent, Font font) {
      if (parent instanceof Label) {
         Label label = (Label) parent;
         label.setFont(font);
      }
      if (parent instanceof Composite) {
         Composite container = (Composite) parent;
         for (Control child : container.getChildren()) {
            setLabelFonts(child, font);
         }
         container.layout();
      }
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

   private Composite createAttributeTypeControls(Composite parent, Artifact artifact, AttributeTypeToken attributeType, boolean isEditable, boolean isExpandable, int leftMargin) {
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
         IAttributeXWidgetProvider xWidgetProvider = AttributeXWidgetManager.getAttributeXWidgetProvider(attributeType);
         List<XWidgetRendererItem> concreteWidgets = xWidgetProvider.getDynamicXWidgetLayoutData(attributeType);
         if (isExpandable) {
            for (XWidgetRendererItem data : concreteWidgets) {
               data.getXOptionHandler().add(XOption.NO_LABEL);
            }
         }
         for (XWidgetRendererItem item : concreteWidgets) {
            if (item.getXWidgetName().equals("XTextDam")) {
               if (!item.isFillVertically()) {
                  AttributeType attrType = AttributeTypeManager.getType(attributeType);
                  if (attrType.getMaxOccurrences() == 1) {
                     String value = artifact.getSoleAttributeValue(attributeType, "");
                     if (value != null && value.contains(System.getProperty("line.separator"))) {
                        item.setFillVertically(true);
                     }
                  }
               }
            }
         }
         XWidgetPage workPage = new XWidgetPage(concreteWidgets, new DefaultXWidgetOptionResolver());

         SwtXWidgetRenderer xWidgetLayout =
            workPage.createBody(getManagedForm(), internalComposite, artifact, widgetModifiedListener, isEditable);
         Collection<XWidget> xWidgets = xWidgetLayout.getXWidgets();

         for (XWidget xWidget : xWidgets) {
            xWidget.addXModifiedListener(new XWidgetValidationListener());
            decorator.addWidget(xWidget);
         }
      } catch (OseeCoreException ex) {
         toolkit.createLabel(parent,
            String.format("Error creating controls for: [%s] [%s]", attributeType.getName(), ex.getLocalizedMessage()));
      }
      return internalComposite;
   }

   private Composite createAttributeTypeControlsInSection(Composite parent, AttributeTypeToken attributeType, boolean isEditable, int leftMargin) {
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

   @Override
   public void commit(boolean onSave) {
      int saveCount = 0;
      List<XWidget> widgets = XWidgetUtility.findXWidgetsInControl(composite);
      for (XWidget xWidget : widgets) {
         if (xWidget.isEditable()) {
            if (xWidget instanceof IArtifactStoredWidget) {
               IArtifactStoredWidget aWidget = (IArtifactStoredWidget) xWidget;
               try {
                  if (aWidget.isDirty().isTrue()) {
                     aWidget.saveToArtifact();
                     xWidget.removeControlCausedMessage("attribute.dirty");
                     saveCount++;
                  } else {
                     saveCount++;
                  }
               } catch (OseeCoreException ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, ex.toString(), ex);
               }
            }
         } else {
            saveCount++;
         }
      }

      // Ensure all changes saved
      if (saveCount == widgets.size()) {
         super.commit(onSave);
      }
      refresh();
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

   private final class XWidgetValidationListener implements XModifiedListener {

      @Override
      public void widgetModified(XWidget xWidget) {
         if (xWidget != null && xWidget instanceof IArtifactStoredWidget) {
            IArtifactStoredWidget aWidget = (IArtifactStoredWidget) xWidget;
            try {
               Result result = aWidget.isDirty();
               if (result.isTrue()) {
                  xWidget.setControlCausedMessage("attribute.dirty", "Dirty", IMessageProvider.INFORMATION);
                  if (!isDirty()) {
                     markDirty();
                  }
               } else {
                  xWidget.removeControlCausedMessage("attribute.dirty");
               }
            } catch (Exception ex) {
               xWidget.setControlCausedMessage("attribute.dirty", "Unable to compute isDirty", IMessageProvider.ERROR);
            }
         }
      }
   }
}