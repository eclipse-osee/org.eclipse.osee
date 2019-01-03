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
package org.eclipse.osee.ats.ide.workdef.viewer.model;

import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;

/**
 * @author Donald G. Dunne
 */
public class DefaultTransitionConnection extends TransitionConnection {

   public DefaultTransitionConnection(Shape source, Shape target) {
      super(source, target);
   }

   @Override
   public Color getForegroundColor() {
      return Displays.getSystemColor(SWT.COLOR_DARK_GREEN);
   }

   @Override
   public int getLineWidth() {
      return 2;
   }

   @Override
   public String getLabel() {
      return "Default Transition";
   }

}
