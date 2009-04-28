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

import java.util.List;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.WordAttribute;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.implementations.NewArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.sections.AttributeTypeUtil;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XSelectFromDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.XStackedWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XTextDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidgetUtility;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.AttributeXWidgetManager;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DefaultXWidgetOptionResolver;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayoutData;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.IAttributeXWidgetProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPage;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.StackedViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
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

   private final NewArtifactEditor editor;
   private Font defaultLabelFont;
   private Composite composite;

   public AttributeFormPart(NewArtifactEditor editor) {
      this.editor = editor;
   }

   public void createContents(Composite parent) {
      final FormToolkit toolkit = getManagedForm().getToolkit();
      composite = toolkit.createComposite(parent, SWT.WRAP);
      composite.setLayout(new GridLayout());
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));

      try {
         Artifact artifact = editor.getEditorInput().getArtifact();
         for (AttributeType attributeType : AttributeTypeUtil.getTypesWithData(artifact)) {
            if (attributeType.getBaseAttributeClass().equals(WordAttribute.class)) {
               createAttributeTypeControlsInSection(parent, toolkit, attributeType, false);
            } else {
               createAttributeTypeControls(composite, toolkit, artifact, attributeType, true);
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, "Unable to access attribute types", ex);
      }
      setLabelFonts(composite, getBoldLabelFont());
      layoutControls(composite);
      composite.layout();

      for (XWidget xWidget : XWidgetUtility.findXWidgetsInControl(composite)) {
         xWidget.addXModifiedListener(new XWidgetValidationListener());
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.forms.AbstractFormPart#dispose()
    */
   @Override
   public void dispose() {
      disposeControl(composite);
      super.dispose();
   }

   private void disposeControl(Control control) {
      if (control != null && !control.isDisposed()) {
         if (control instanceof Composite) {
            for (Control child : ((Composite) control).getChildren()) {
               disposeControl(child);
            }
         }
         control.dispose();
      }
   }

   private Font getBoldLabelFont() {
      if (defaultLabelFont == null) {
         Font baseFont = JFaceResources.getDefaultFont();
         FontData[] fontDatas = baseFont.getFontData();
         FontData fontData = fontDatas.length > 0 ? fontDatas[0] : new FontData("arial", 12, SWT.BOLD);
         defaultLabelFont = new Font(baseFont.getDevice(), fontData.getName(), fontData.getHeight(), SWT.BOLD);
      }
      return defaultLabelFont;
   }

   private void setLabelFonts(Control parent, Font font) {
      if (parent instanceof Label) {
         ((Label) parent).setFont(font);
      }
      if (parent instanceof Composite) {
         Composite container = (Composite) parent;
         for (Control child : container.getChildren()) {
            setLabelFonts(child, font);
         }
      }
   }

   private void layoutControls(Control parent) {
      if ((parent instanceof Label)) {
         parent.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, false, false));
      } else if (parent instanceof Button) {
         parent.setLayoutData(new GridData(SWT.END, SWT.BEGINNING, false, false));
      } else {
         XWidget xWidget = XWidgetUtility.asXWidget(parent);
         if (!(xWidget instanceof XSelectFromDialog<?>) && !(xWidget instanceof XStackedWidget)) {
            if (xWidget instanceof XTextDam) {
               XTextDam dam = (XTextDam) xWidget;
               if (!dam.isEditable()) {
                  parent.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, true));
               } else {
                  parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
               }
            } else {
               parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
            }
         }
      }
      if (parent instanceof Composite && !(parent instanceof StackedViewer)) {
         Composite container = (Composite) parent;
         for (Control child : container.getChildren()) {
            if (!(child instanceof Canvas)) {
               layoutControls(child);
            } else {
               System.out.println("Skipped : " + ((Canvas) child).getToolTipText());
            }
         }
      }
   }

   private Composite createAttributeTypeControls(Composite parent, FormToolkit toolkit, Artifact artifact, AttributeType attributeType, boolean isEditable) {
      Composite internalComposite = toolkit.createComposite(parent, SWT.WRAP);
      internalComposite.setLayout(ALayout.getZeroMarginLayout(1, false));
      internalComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

      IAttributeXWidgetProvider xWidgetProvider = AttributeXWidgetManager.getAttributeXWidgetProvider(attributeType);
      List<DynamicXWidgetLayoutData> concreteWidgets = xWidgetProvider.getDynamicXWidgetLayoutData(attributeType);
      try {
         WorkPage workPage = new WorkPage(concreteWidgets, new DefaultXWidgetOptionResolver());
         workPage.createBody(getManagedForm(), internalComposite, artifact, null, isEditable);
      } catch (OseeCoreException ex) {
         toolkit.createLabel(parent, String.format("Error creating controls for: [%s]", attributeType.getName()));
      }
      return internalComposite;
   }

   private void createAttributeTypeControlsInSection(Composite parent, FormToolkit toolkit, AttributeType attributeType, boolean isEditable) {
      int style = ExpandableComposite.COMPACT | ExpandableComposite.TREE_NODE;

      Composite internalComposite = toolkit.createComposite(parent, SWT.WRAP);
      internalComposite.setLayout(new GridLayout());
      internalComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

      ExpandableComposite expandable = toolkit.createExpandableComposite(internalComposite, style);
      expandable.setText(attributeType.getName());
      expandable.setLayout(new GridLayout());
      expandable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      Artifact artifact = editor.getEditorInput().getArtifact();

      Composite composite = createAttributeTypeControls(expandable, toolkit, artifact, attributeType, isEditable);
      //      composite.setLayout(new GridLayout());
      //      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

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

   private final class XWidgetValidationListener implements XModifiedListener {
      int count = 0;

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener#widgetModified(org.eclipse.osee.framework.ui.skynet.widgets.XWidget)
       */
      @Override
      public void widgetModified(XWidget xWidget) {
         if (xWidget != null) {
            switch (count) {
               case 0:
                  xWidget.setControlCausedMessage("None Message", IMessageProvider.NONE);
                  break;
               case 1:
                  xWidget.setControlCausedMessage("Info Message", IMessageProvider.INFORMATION);
                  break;
               case 2:
                  xWidget.setControlCausedMessage("Warning Message", IMessageProvider.WARNING);
                  break;
               case 3:
                  xWidget.setControlCausedMessage("Error Message", IMessageProvider.ERROR);
                  break;
               default:
                  xWidget.removeControlCausedMessage();
                  count = -1;
                  break;
            }
            count++;
         }
      }
   }
}
