/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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
