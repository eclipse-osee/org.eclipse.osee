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
package org.eclipse.osee.ote.ui.test.manager.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author David Diepenbrock
 */
public class WidgetFactory {

   /**
    * Creates a <code>Label</code> for use on a GridLayout.
    * 
    * @param comp The composite this label will be on.
    * @param horizontalSpan The number of columns this Label should span in the grid
    * @return The created label - with no text
    */
   public static Label createLabelForGrid(Composite comp, int horizontalSpan) {
      Label label = new Label(comp, SWT.NONE);
      GridData gd = new GridData();
      gd.horizontalSpan = horizontalSpan;
      label.setLayoutData(gd);
      return label;
   }

   /**
    * Creates a <code>Label</code> for use on a GridLayout. The <code>Label</code> will only
    * span 1 column in the grid.
    * 
    * @param comp The composite this label will be on.
    * @param str The text for the label
    * @return The created label
    */
   public static Label createLabelForGrid(Composite comp, String str) {
      return createLabelForGrid(comp, str, 1);
   }

   /**
    * Creates a <code>Label</code> for use on a GridLayout.
    * 
    * @param comp The composite this label will be on.
    * @param labelText The text for the label
    * @param horizontalSpan The number of columns this Label should span in the grid
    * @return The created label
    */
   public static Label createLabelForGrid(Composite comp, String labelText, int horizontalSpan) {
      Label label = createLabelForGrid(comp, horizontalSpan);
      label.setText(labelText);
      return label;
   }

}
