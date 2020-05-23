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

package org.eclipse.osee.framework.ui.swt.hex;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.FontManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;

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
      return Displays.getSystemColor(SWT.COLOR_YELLOW);
   }

   @Override
   public Color getForeground(Object element) {
      return Displays.getSystemColor(SWT.COLOR_BLACK);
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