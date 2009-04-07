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
package org.eclipse.osee.framework.ui.skynet.widgets.hex;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;

public class DividerLabel extends ColumnLabelProvider {

   public DividerLabel() {
      super();
   }

   public String getToolTipText(Object element) {

      return null;
   }

   public Point getToolTipShift(Object object) {
      return new Point(12, 12);
   }

   public int getToolTipDisplayDelayTime(Object object) {
      return 125;
   }

   public int getToolTipTimeDisplayed(Object object) {
      return 5000;
   }

   @Override
   public Color getBackground(Object element) {
      return Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
   }

   @Override
   public String getText(Object element) {

      return null;
   }

}