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
package org.eclipse.osee.framework.ui.plugin.views.property;

import java.io.File;
import java.net.MalformedURLException;
import java.util.logging.Level;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.OseeUiActivator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.views.properties.PropertyDescriptor;

/**
 * @author Roberto E. Escobar
 */
public class ImagePropertyDescriptor extends PropertyDescriptor {

   public ImagePropertyDescriptor(PropertyId propertyId) {
      super(propertyId, propertyId.getDisplayName());
      setCategory(propertyId.getCategoryName());
      setLabelProvider(new ILabelProvider() {
         public Image getImage(Object element) {
            return null;
         }

         public String getText(Object element) {
            return "Image";
         }

         @Override
         public void addListener(ILabelProviderListener listener) {
         }

         @Override
         public void dispose() {
         }

         @Override
         public boolean isLabelProperty(Object element, String property) {
            return false;
         }

         @Override
         public void removeListener(ILabelProviderListener listener) {
         }
      });
   }

   public static Object fromModel(Image image) {
      return image;
   }

   public static Image toModel(Object object) {
      return (Image) object;
   }

   public CellEditor createPropertyEditor(Composite parent) {

      CellEditor editor = new DialogCellEditor(parent) {
         protected Object openDialogBox(Control cellEditorWindow) {
            FileDialog dialog = new FileDialog(cellEditorWindow.getShell(), SWT.OPEN);
            dialog.setFilterExtensions(new String[] {"jpg", "bmp", "png"});
            dialog.open();
            String fileName = dialog.getFileName();
            if (Strings.isValid(fileName)) {
               try {
                  Image image = ImageDescriptor.createFromURL(new File(fileName).toURI().toURL()).createImage();
                  return image;
               } catch (MalformedURLException ex) {
                  OseeLog.log(OseeUiActivator.class, Level.SEVERE, ex);
               }
            }
            return null;
         }

         protected void updateContents(Object value) {
            Label label = getDefaultLabel();
            if (label == null) return;
            String text = "";//$NON-NLS-1$
            if (value != null) text = getLabelProvider().getText(value);
            label.setText(text);
         }
      };
      if (getValidator() != null) editor.setValidator(getValidator());
      return editor;
   }
}
