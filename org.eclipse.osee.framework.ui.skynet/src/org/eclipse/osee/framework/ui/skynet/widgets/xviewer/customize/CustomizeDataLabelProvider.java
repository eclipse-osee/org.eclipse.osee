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
package org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewer;
import org.eclipse.swt.graphics.Image;

/**
 * Generic Label Provider showing Descriptive Name as text
 * 
 * @author Donald G. Dunne
 */
public class CustomizeDataLabelProvider implements ILabelProvider {

   private final XViewer xViewer;

   public CustomizeDataLabelProvider(XViewer xViewer) {
      this.xViewer = xViewer;
   }

   public Image getImage(Object arg0) {
      return ((CustomizeData) arg0).getImage(xViewer.getCustomizeMgr().isCustomizationUserDefault((CustomizeData) arg0));
   }

   public String getText(Object arg0) {
      return (xViewer.getCustomizeMgr().isCustomizationUserDefault((CustomizeData) arg0) ? "(Default) " : "") + (((CustomizeData) arg0)).getName();
   }

   public void addListener(ILabelProviderListener arg0) {
   }

   public void dispose() {
   }

   public boolean isLabelProperty(Object arg0, String arg1) {
      return false;
   }

   public void removeListener(ILabelProviderListener arg0) {
   }

}
