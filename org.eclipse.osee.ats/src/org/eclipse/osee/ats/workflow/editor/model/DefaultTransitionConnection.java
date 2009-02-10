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
package org.eclipse.osee.ats.workflow.editor.model;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class DefaultTransitionConnection extends TransitionConnection {

   /**
    * @param source
    * @param target
    */
   public DefaultTransitionConnection(Shape source, Shape target) {
      super(source, target);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.config.editor.model.Connection#getForegroundColor()
    */
   @Override
   public Color getForegroundColor() {
      return Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GREEN);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.config.editor.model.Connection#getLineWidth()
    */
   @Override
   public int getLineWidth() {
      return 3;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.config.editor.model.Connection#getLabel()
    */
   @Override
   public String getLabel() {
      return "Default Transition";
   }

}
