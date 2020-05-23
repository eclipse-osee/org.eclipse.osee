/*********************************************************************
 * Copyright (c) 2011 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.skynet.widgets;

import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.utility.OseeInfo;
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
   private static final int DEFAULT_WAIT = 500;
   private static final String VIEW_PART_WAIT_LIMIT = "generic.view.part.wait.limit";

   private Control focusWidget;

   @Override
   public void setFocus() {
      try {
         String limit = OseeInfo.getCachedValue(VIEW_PART_WAIT_LIMIT);
         int waitLimit = Strings.isNumeric(limit) ? Integer.valueOf(limit) : DEFAULT_WAIT;
         Thread.sleep(waitLimit);
         if (focusWidget != null && !focusWidget.isDisposed()) {
            focusWidget.setFocus();
         }
      } catch (InterruptedException ex) {
         //
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
