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
package org.eclipse.osee.framework.ui.skynet.widgets.workflow;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class WorkPageDefinitionLabelProvider implements ILabelProvider {

   @Override
   public Image getImage(Object arg0) {
      WorkPageDefinition page = (WorkPageDefinition) arg0;
      if (page.isCancelledPage()) {
         return ImageManager.getImage(FrameworkImage.X_RED);
      }
      if (page.isCompletedPage()) {
         return ImageManager.getImage(FrameworkImage.GREEN_PLUS);
      }
      return null;
   }

   @Override
   public String getText(Object arg0) {
      return ((WorkPageDefinition) arg0).getPageName() + getCompletedAppend((WorkPageDefinition) arg0);
   }

   private String getCompletedAppend(WorkPageDefinition page) {
      if (page.isCompletedPage()) {
         if (!page.getPageName().equals("Completed")) {
            return " (Completed)";
         }
      }
      return "";
   }

   @Override
   public void addListener(ILabelProviderListener arg0) {
      // do nothing
   }

   @Override
   public void dispose() {
      // do nothing
   }

   @Override
   public boolean isLabelProperty(Object arg0, String arg1) {
      return false;
   }

   @Override
   public void removeListener(ILabelProviderListener arg0) {
      // do nothing
   }
}