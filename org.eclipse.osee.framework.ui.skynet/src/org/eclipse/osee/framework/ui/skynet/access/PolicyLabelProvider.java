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
package org.eclipse.osee.framework.ui.skynet.access;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.osee.framework.skynet.core.access.AccessControlData;
import org.eclipse.osee.framework.ui.plugin.OseePluginUiActivator;
import org.eclipse.swt.graphics.Image;

public class PolicyLabelProvider implements ITableLabelProvider {

   public PolicyLabelProvider() {
      super();
   };

   /**
    * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
    */
   public String getColumnText(Object element, int columnIndex) {
      String result = "";

      if (element instanceof AccessControlData) {
         AccessControlData data = (AccessControlData) element;

         switch (columnIndex) {
            case PolicyTableViewer.DELETE_NUM:
               // This only has an image
               break;
            case PolicyTableViewer.PERSON_NUM:
               result = data.getSubject().getDescriptiveName();
               break;
            case PolicyTableViewer.ARTIFACT_POLICY_LEVEL_NUM:
               result = String.valueOf(data.getPermission().getName());
               break;
            default:
               break;
         }
      }
      return result;
   }

   /**
    * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
    */
   public Image getColumnImage(Object element, int columnIndex) {
      Image result = null;

      switch (columnIndex) {
         case PolicyTableViewer.DELETE_NUM:
            result = OseePluginUiActivator.getInstance().getImage("remove.gif");
      }
      return result;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
    */
   public void dispose() {
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
    */
   public void removeListener(ILabelProviderListener listener) {
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
    */
   public void addListener(ILabelProviderListener listener) {
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object,
    *      java.lang.String)
    */
   public boolean isLabelProperty(Object element, String property) {
      return true;
   }
}