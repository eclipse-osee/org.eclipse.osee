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
package org.eclipse.osee.framework.ui.swt;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

/**
 * A composite that hides when setVisible is set to false.
 * 
 * @author Roberto E. Escobar
 */
public class HidingComposite extends Composite {

   public HidingComposite(Composite parent, int style) {
      super(parent, style);
   }

   @Override
   public Point computeSize(int wHint, int hHint, boolean changed) {
      if (!isVisible()) {
         return new Point(0, 0);
      } else {
         return super.computeSize(wHint, hHint, changed);
      }
   }
}
