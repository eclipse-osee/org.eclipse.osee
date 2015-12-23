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
package org.eclipse.osee.framework.ui.skynet.widgets;

import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

/**
 * This class provides &quot;general&quot; functionality to widgets having the need to override ViewPart methods and
 * providing blank functionality. Subclasses must call setFocusWidget() in their implementation for createPartControl
 * <br/>
 * 
 * @author Karol M. Wilk
 */
public abstract class GenericViewPart extends ViewPart {
   private Control focusWidget;

   @Override
   public void setFocus() {
      if (focusWidget != null && !focusWidget.isDisposed()) {
         focusWidget.setFocus();
      }
   }

   /**
    * @param general way to handle a widget that will be called when a focus event is received
    */
   protected void setFocusWidget(Control widget) {
      focusWidget = widget;
   }

   @Override
   public void init(IViewSite site) throws PartInitException {
      super.init(site);
   }
}
