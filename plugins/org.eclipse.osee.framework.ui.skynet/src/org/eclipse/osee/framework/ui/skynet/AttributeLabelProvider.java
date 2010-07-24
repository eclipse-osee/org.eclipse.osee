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
package org.eclipse.osee.framework.ui.skynet;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.swt.graphics.Image;

/**
 * @author Ryan D. Brooks
 */
public class AttributeLabelProvider implements ITableLabelProvider {

   /**
    * 
    */
   public AttributeLabelProvider() {
      super();
   }

   @Override
   public Image getColumnImage(Object element, int columnIndex) {
      return null;
   }

   @Override
   public String getColumnText(Object element, int columnIndex) {
      Attribute<?> attribute = (Attribute<?>) element;
      if (columnIndex == 0) {
         return attribute.getAttributeType().getName();
      }
      try {
         return attribute.getDisplayableString();
      } catch (OseeCoreException ex) {
         return Lib.exceptionToString(ex);
      }
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

}
