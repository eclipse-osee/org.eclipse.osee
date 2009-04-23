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
package org.eclipse.osee.framework.ui.skynet.artifact.editor.panels;

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
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XSelectFromDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidgetUtility;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.AttributeXWidgetManager;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DefaultXWidgetOptionResolver;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayoutData;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.IAttributeXWidgetProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPage;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.AbstractFormPart;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Roberto E. Escobar
 */
public class AttributeDataPage extends AbstractFormPart {
   private Font defaultLabelFont;
   private NewArtifactEditor editor;

   public AttributeDataPage(NewArtifactEditor editor) {
      this.editor = editor;
   }

   public void createContents(Composite parent) {
      final FormToolkit toolkit = getManagedForm().getToolkit();
      Composite composite = toolkit.createComposite(parent, SWT.WRAP);
      composite.setLayout(new GridLayout(1, false));
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      Artifact artifact = editor.getEditorInput().getArtifact();
      try {
         for (AttributeType attributeType : AttributeTypeUtil.getTypesWithData(artifact)) {
            if (false && attributeType.getBaseAttributeClass().equals(WordAttribute.class)) {
               //            createCollapsibleAttributeDataComposite(parent, attributeType);
            } else {
               createAttributeTypeControls(composite, toolkit, artifact, attributeType);
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, "Unable to access attribute types", ex);
      }

      for (XWidget xWidget : XWidgetUtility.findXWidgetsInControl(composite)) {
         xWidget.addXModifiedListener(new XWidgetValidationListener());
      }
      setAllLabelFonts(composite, getBoldLabelFont());
      setGrabAllLayout(composite);
   }

   private void setAllLabelFonts(Control parent, Font font) {
      if (parent instanceof Label) {
         ((Label) parent).setFont(font);
      }
      if (parent instanceof Composite) {
         Composite container = (Composite) parent;
         for (Control child : container.getChildren()) {
            setAllLabelFonts(child, font);
         }
      }
   }

   private void setGrabAllLayout(Control parent) {
      if ((parent instanceof Label)) {
         parent.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, false, false));
      } else if (!(parent instanceof Button)) {
         XWidget xWidget = XWidgetUtility.asXWidget(parent);
         if (!(xWidget instanceof XSelectFromDialog<?>)) {
            parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
         }
      }
      if (parent instanceof Composite) {
         Composite container = (Composite) parent;
         for (Control child : container.getChildren()) {
            setGrabAllLayout(child);
         }
      }
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

   private Font getBoldLabelFont() {
      if (defaultLabelFont == null) {
         Font baseFont = JFaceResources.getDefaultFont();
         FontData[] fontDatas = baseFont.getFontData();
         FontData fontData = fontDatas.length > 0 ? fontDatas[0] : new FontData("arial", 12, SWT.BOLD);
         defaultLabelFont = new Font(baseFont.getDevice(), fontData.getName(), fontData.getHeight(), SWT.BOLD);
      }
      return defaultLabelFont;
   }

   private Composite createAttributeTypeControls(Composite parent, FormToolkit toolkit, Artifact artifact, AttributeType attributeType) {
      Composite internalComposite = toolkit.createComposite(parent, SWT.WRAP);
      internalComposite.setLayout(ALayout.getZeroMarginLayout(1, false));
      internalComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

      IAttributeXWidgetProvider xWidgetProvider = AttributeXWidgetManager.getAttributeXWidgetProvider(attributeType);
      List<DynamicXWidgetLayoutData> concreteWidgets = xWidgetProvider.getDynamicXWidgetLayoutData(attributeType);
      try {
         WorkPage workPage = new WorkPage(concreteWidgets, new DefaultXWidgetOptionResolver());
         workPage.createBody(getManagedForm(), internalComposite, artifact, null, true);
      } catch (OseeCoreException ex) {
         toolkit.createLabel(parent, String.format("Error creating controls for: [%s]", attributeType.getName()));
      }
      return internalComposite;
   }
}
