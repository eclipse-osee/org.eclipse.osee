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

import org.eclipse.swt.layout.GridLayout;

/**
 * @author Donald G. Dunne
 */
public class ALayout {

   public static GridLayout getZeroMarginLayout(int numColumns, boolean equalColumnWidth) {
      GridLayout layout = new GridLayout();
      layout.numColumns = numColumns;
      layout.makeColumnsEqualWidth = equalColumnWidth;
      layout.verticalSpacing = 0;
      layout.marginWidth = 0;
      layout.marginHeight = 0;
      return layout;
   }

   public static GridLayout getZeroMarginLayout() {
      return getZeroMarginLayout(1, false);
   }

}
