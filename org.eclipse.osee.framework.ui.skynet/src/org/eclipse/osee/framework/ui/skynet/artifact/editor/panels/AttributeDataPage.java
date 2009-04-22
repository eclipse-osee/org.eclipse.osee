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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.WordAttribute;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
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
   private ArtifactEditor editor;

   public AttributeDataPage(ArtifactEditor editor) {
      this.editor = editor;
   }

   public void createContents(Composite parent) {
      final FormToolkit toolkit = getManagedForm().getToolkit();
      Composite composite = toolkit.createComposite(parent, SWT.WRAP);
      composite.setLayout(new GridLayout(1, false));
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      Artifact artifact = editor.getEditorInput().getArtifact();
      for (AttributeType attributeType : getValidTypes(artifact)) {
         if (false && attributeType.getBaseAttributeClass().equals(WordAttribute.class)) {
            //                     createCollapsibleAttributeDataComposite(parent, attributeType);
         } else {
            createAttributeTypeControls(composite, toolkit, artifact, attributeType);
         }
      }

      setGrabAllLayout(composite);
   }

   private void setGrabAllLayout(Composite target) {
      for (Control child : target.getChildren()) {
         if (!(child instanceof Label)) {
            child.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
         } else {
            ((Label) child).setFont(getBoldLabelFont());
         }
         if (child instanceof Composite) {
            setGrabAllLayout((Composite) child);
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

   private List<AttributeType> getValidTypes(Artifact artifact) {
      List<AttributeType> attributeType = new ArrayList<AttributeType>();
      try {
         AttributeType nameType = null;
         for (AttributeType type : artifact.getAttributeTypes()) {
            if (type.getName().equals("Name")) {
               nameType = type;
            } else if (!artifact.getAttributes(type.getName()).isEmpty()) {
               attributeType.add(type);
            }
         }
         Collections.sort(attributeType);
         if (nameType != null) {
            attributeType.add(0, nameType);
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return attributeType;
   }
}
