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
import org.eclipse.osee.framework.ui.skynet.FontManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;

public class OffsetColumnLabelProvider extends ColumnLabelProvider {

   public OffsetColumnLabelProvider() {
      super();
   }

   @Override
   public String getText(Object element) {
      return Integer.toString(((HexTableRow) element).getOffset());
   }

   @Override
   public Color getBackground(Object element) {
      return Display.getDefault().getSystemColor(SWT.COLOR_YELLOW);
   }

   @Override
   public Color getForeground(Object element) {
      return Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
   }

   @Override
   public Font getFont(Object element) {
      return FontManager.getCourierNew8();
   }

   @Override
   public void dispose() {
      super.dispose();
   }

}