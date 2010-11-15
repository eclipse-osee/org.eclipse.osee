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
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.WordAttribute;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.sections.AttributeTypeUtil;
import org.eclipse.osee.framework.ui.skynet.widgets.IArtifactStoredWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XOption;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidgetAccessDecorationProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidgetDecorator;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidgetUtility;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.AttributeXWidgetManager;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DefaultXWidgetOptionResolver;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayout;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayoutData;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.IAttributeXWidgetProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPage;
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

   private final ArtifactEditor editor;
   private Composite composite;
   private final XWidgetDecorator decorator = new XWidgetDecorator();
   private final Map<IAttributeType, Composite> xWidgetsMap = new HashMap<IAttributeType, Composite>();

   public AttributeFormPart(ArtifactEditor editor) {
      this.editor = editor;
      try {
         decorator.addProvider(new XWidgetAccessDecorationProvider(
            SkynetGuiPlugin.getInstance().getPolicyHandlerService()));
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex.toString(), ex);
      }
   }

   @Override
   public void refresh() {
      super.refresh();
      getManagedForm().getForm().getBody().layout(true);
   }

   public void createContents(Composite composite) {
      this.composite = composite;
      decorator.dispose();

      composite.setLayout(ALayout.getZeroMarginLayout(1, false));
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      composite.setVisible(false);

      try {
         Artifact artifact = editor.getEditorInput().getArtifact();

         List<AttributeType> types = AttributeTypeUtil.getTypesWithData(artifact);
         addWidgetForAttributeType(types);

         layoutControls(composite);

         decorator.refresh();
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, "Unable to access attribute types", ex);
      } finally {
         composite.setVisible(true);
      }
   }

   public void addWidgetForAttributeType(Collection<? extends IAttributeType> attributeTypes) throws OseeCoreException {
      Artifact artifact = editor.getEditorInput().getArtifact();
      boolean isEditable = !artifact.isReadOnly();

      for (IAttributeType attributeType : attributeTypes) {
         Composite internalComposite;
         if (shouldEncloseInSection(attributeType)) {
            internalComposite = createAttributeTypeControlsInSection(composite, attributeType, false, 15);
         } else {
            internalComposite = createAttributeTypeControls(composite, artifact, attributeType, isEditable, false, 20);
         }
         setLabelFonts(internalComposite, FontManager.getDefaultLabelFont());
         xWidgetsMap.put(attributeType, internalComposite);
      }
      refresh();
   }

   private boolean shouldEncloseInSection(IAttributeType attributeType) throws OseeCoreException {
      return CoreAttributeTypes.RelationOrder.equals(attributeType) || AttributeTypeManager.isBaseTypeCompatible(
         WordAttribute.class, attributeType);
   }

   @Override
   public void dispose() {
      Widgets.disposeWidget(composite);
      super.dispose();
   }

   private void setLabelFonts(Control parent, Font font) {
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

   private Composite createAttributeTypeControls(Composite parent, Artifact artifact, IAttributeType attributeType, boolean isEditable, boolean isExpandable, int leftMargin) {
      FormToolkit toolkit = getManagedForm().getToolkit();
      Composite internalComposite = toolkit.createComposite(parent, SWT.WRAP);

      GridLayout layout = ALayout.getZeroMarginLayout(1, false);
      layout.marginLeft = leftMargin;
      internalComposite.setLayout(layout);
      internalComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      try {
         IAttributeXWidgetProvider xWidgetProvider = AttributeXWidgetManager.getAttributeXWidgetProvider(attributeType);
         List<DynamicXWidgetLayoutData> concreteWidgets = xWidgetProvider.getDynamicXWidgetLayoutData(attributeType);
         if (isExpandable) {
            for (DynamicXWidgetLayoutData data : concreteWidgets) {
               data.getXOptionHandler().add(XOption.NO_LABEL);
            }
         }
         WorkPage workPage = new WorkPage(concreteWidgets, new DefaultXWidgetOptionResolver());

         DynamicXWidgetLayout xWidgetLayout =
            workPage.createBody(getManagedForm(), internalComposite, artifact, widgetModifiedListener, isEditable);
         Collection<XWidget> xWidgets = xWidgetLayout.getXWidgets();

         for (XWidget xWidget : xWidgets) {
            xWidget.addXModifiedListener(new XWidgetValidationListener());
            decorator.addWidget(xWidget);
         }
      } catch (OseeCoreException ex) {
         toolkit.createLabel(parent, String.format("Error creating controls for: [%s]", attributeType.getName()));
      }
      return internalComposite;
   }

   private Composite createAttributeTypeControlsInSection(Composite parent, IAttributeType attributeType, boolean isEditable, int leftMargin) {
      FormToolkit toolkit = getManagedForm().getToolkit();

      int style = ExpandableComposite.SHORT_TITLE_BAR | ExpandableComposite.TREE_NODE;
      ExpandableComposite expandable = toolkit.createExpandableComposite(parent, style);
      expandable.setText(attributeType.getName());

      GridLayout layout = ALayout.getZeroMarginLayout(1, false);
      expandable.setLayout(layout);
      expandable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

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
   private final XModifiedListener widgetModifiedListener = new XModifiedListener() {

      @Override
      public void widgetModified(XWidget widget) {
         editor.onDirtied();
      }
   };

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
                  OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex.toString(), ex);
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

   public void removeWidgetForAttributeType(Collection<? extends IAttributeType> attributeTypes) {
      for (IAttributeType attributeType : attributeTypes) {
         xWidgetsMap.remove(attributeType).dispose();
      }
      refresh();
   }
}