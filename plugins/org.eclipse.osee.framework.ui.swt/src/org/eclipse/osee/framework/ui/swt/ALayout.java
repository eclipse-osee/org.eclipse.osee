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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;

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

   public static Composite createCommonPageComposite(Composite parent) {
      Composite composite = new Composite(parent, SWT.NONE);
      GridLayout layout = new GridLayout(1, false);
      layout.marginHeight = 0;
      layout.marginWidth = 0;
      layout.verticalSpacing = 0;
      composite.setLayout(layout);

      return composite;
   }

   public static ToolBar createCommonToolBar(Composite parent) {
      Composite toolBarComposite = new Composite(parent, SWT.BORDER);
      GridData gridData = new GridData(SWT.FILL, SWT.BEGINNING, true, false, 1, 1);
      toolBarComposite.setLayoutData(gridData);
      GridLayout layout = new GridLayout(2, false);
      layout.marginHeight = 0;
      layout.marginWidth = 0;
      toolBarComposite.setLayout(layout);

      ToolBar toolBar = new ToolBar(toolBarComposite, SWT.FLAT | SWT.RIGHT);
      gridData = new GridData(SWT.FILL, SWT.BEGINNING, true, true, 1, 1);
      toolBar.setLayoutData(gridData);
      return toolBar;
   }

}
