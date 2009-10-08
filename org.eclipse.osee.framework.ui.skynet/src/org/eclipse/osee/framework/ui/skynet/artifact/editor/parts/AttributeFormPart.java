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

import java.util.Arrays;
import java.util.List;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.CoreAttributes;
import org.eclipse.osee.framework.skynet.core.attribute.WordAttribute;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.FontManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.sections.AttributeTypeUtil;
import org.eclipse.osee.framework.ui.skynet.widgets.IArtifactWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XOption;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidgetUtility;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.AttributeXWidgetManager;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DefaultXWidgetOptionResolver;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayoutData;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.IAttributeXWidgetProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPage;
import org.eclipse.osee.framework.ui.swt.ALayout;
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

   public AttributeFormPart(ArtifactEditor editor) {
      this.editor = editor;
   }

   public void createContents(Composite parent) {
      final FormToolkit toolkit = getManagedForm().getToolkit();
      composite = toolkit.createComposite(parent, SWT.WRAP);
      composite.setLayout(ALayout.getZeroMarginLayout(1, false));
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      composite.setVisible(false);

      try {
         Artifact artifact = editor.getEditorInput().getArtifact();
         boolean isEditable = !artifact.isReadOnly();

         List<AttributeType> types = Arrays.asList(AttributeTypeUtil.getTypesWithData(artifact));
         boolean willHaveASection = hasWordAttribute(types);
         for (AttributeType attributeType : types) {
            if (attributeType.getBaseAttributeClass().equals(WordAttribute.class) || CoreAttributes.RELATION_ORDER.getGuid().equals(
                  attributeType.getGuid())) {
               createAttributeTypeControlsInSection(parent, toolkit, attributeType, willHaveASection, false);
            } else {
               createAttributeTypeControls(composite, toolkit, artifact, attributeType, willHaveASection, isEditable,
                     false);
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, "Unable to access attribute types", ex);
      }
      setLabelFonts(composite, FontManager.getDefaultLabelFont());
      layoutControls(composite);

      for (XWidget xWidget : XWidgetUtility.findXWidgetsInControl(composite)) {
         xWidget.addXModifiedListener(new XWidgetValidationListener());
      }
      composite.setVisible(true);
   }

   private boolean hasWordAttribute(List<AttributeType> types) {
      for (AttributeType attributeType : types) {
         if (attributeType.getBaseAttributeClass().equals(WordAttribute.class)) {
            return true;
         }
      }
      return false;
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
         control.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
      }

      if (control instanceof Composite) {
         Composite container = (Composite) control;
         for (Control child : container.getChildren()) {
            layoutControls(child);
         }
      }
   }

   private Composite createAttributeTypeControls(Composite parent, FormToolkit toolkit, Artifact artifact, AttributeType attributeType, boolean willHaveASection, boolean isEditable, boolean isExpandable) {
      Composite internalComposite = toolkit.createComposite(parent, SWT.WRAP);
      GridLayout layout = ALayout.getZeroMarginLayout(1, false);
      if (willHaveASection) {
         layout.marginLeft = 18;
      }
      internalComposite.setLayout(layout);

      internalComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      IAttributeXWidgetProvider xWidgetProvider = AttributeXWidgetManager.getAttributeXWidgetProvider(attributeType);
      List<DynamicXWidgetLayoutData> concreteWidgets = xWidgetProvider.getDynamicXWidgetLayoutData(attributeType);
      try {
         if (isExpandable) {
            for (DynamicXWidgetLayoutData data : concreteWidgets) {
               data.getXOptionHandler().add(XOption.NO_LABEL);
            }
         }
         WorkPage workPage = new WorkPage(concreteWidgets, new DefaultXWidgetOptionResolver());
         workPage.createBody(getManagedForm(), internalComposite, artifact, null, isEditable);
      } catch (OseeCoreException ex) {
         toolkit.createLabel(parent, String.format("Error creating controls for: [%s]", attributeType.getName()));
      }
      return internalComposite;
   }

   private void createAttributeTypeControlsInSection(Composite parent, FormToolkit toolkit, AttributeType attributeType, boolean willHaveASection, boolean isEditable) {
      int style = ExpandableComposite.COMPACT | ExpandableComposite.TREE_NODE;

      Composite internalComposite = toolkit.createComposite(parent, SWT.WRAP);
      internalComposite.setLayout(new GridLayout());
      internalComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

      ExpandableComposite expandable = toolkit.createExpandableComposite(internalComposite, style);
      expandable.setText(attributeType.getName());
      expandable.setLayout(new GridLayout());
      expandable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      Artifact artifact = editor.getEditorInput().getArtifact();

      Composite composite =
            createAttributeTypeControls(expandable, toolkit, artifact, attributeType, willHaveASection, isEditable,
                  true);
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
   }

   @Override
   public void commit(boolean onSave) {
      int saveCount = 0;
      List<XWidget> widgets = XWidgetUtility.findXWidgetsInControl(composite);
      for (XWidget xWidget : widgets) {
         if (xWidget.isEditable()) {
            if (xWidget instanceof IArtifactWidget) {
               IArtifactWidget aWidget = (IArtifactWidget) xWidget;
               try {
                  if (aWidget.isDirty().isTrue()) {
                     aWidget.saveToArtifact();
                     xWidget.removeControlCausedMessage("attribute.dirty");
                     saveCount++;
                  } else {
                     saveCount++;
                  }
               } catch (OseeCoreException ex) {
                  ex.printStackTrace();
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
         if (xWidget != null && xWidget instanceof IArtifactWidget) {
            IArtifactWidget aWidget = (IArtifactWidget) xWidget;
            try {
               Result result = aWidget.isDirty();
               if (result.isTrue()) {
                  xWidget.setControlCausedMessage("attribute.dirty", "Dirty", IMessageProvider.WARNING);
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
