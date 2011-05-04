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

import org.eclipse.osee.framework.plugin.core.IActionable;
import org.eclipse.swt.widgets.Control;

public abstract class GenericXWidget extends XWidget implements IActionable {

   public GenericXWidget() {
      this("");
   }

   public GenericXWidget(String label) {
      super(label);
   }

   /**
    * Subclasses must provide implementation of getControl() that returns appropriate widget.
    */
   @Override
   public void setFocus() {
      Control control = getControl();
      if (control != null && !control.isDisposed()) {
         control.setFocus();
      }
   }

   @Override
   public String getActionDescription() {
      return "";
   }

   @Override
   public boolean isEmpty() {
      return false;
   }

}