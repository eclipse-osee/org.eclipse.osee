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

import org.eclipse.draw2d.Graphics;

/**
 * @author Donald G. Dunne
 */
public class ReturnTransitionConnection extends TransitionConnection {

   public ReturnTransitionConnection(Shape source, Shape target) {
      super(source, target);
   }

   @Override
   public int getLineStyle() {
      return Graphics.LINE_DASH;
   }

   @Override
   public String getLabel() {
      return "Return Transition";
   }
}
