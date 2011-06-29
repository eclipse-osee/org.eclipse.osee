/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.coverage.util.dialog;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.osee.coverage.model.CoverageOption;
import org.eclipse.swt.graphics.Image;

public class CoverageMethodLabelProvider implements ILabelProvider {

   @Override
   public Image getImage(Object element) {
      return null;
   }

   @Override
   public String getText(Object element) {
      if (element instanceof CoverageOption) {
         CoverageOption option = (CoverageOption) element;
         return option.getName();
      }
      return "Unknown";
   }

   @Override
   public void addListener(ILabelProviderListener listener) {
      // do nothing
   }

   @Override
   public void dispose() {
      // do nothing
   }

   @Override
   public boolean isLabelProperty(Object element, String property) {
      return false;
   }

   @Override
   public void removeListener(ILabelProviderListener listener) {
      // do nothing
   }

}
