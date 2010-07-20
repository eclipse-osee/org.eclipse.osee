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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

/**
 * @author Donald G. Dunne
 */
public class LabelComposite extends Composite {

   public LabelComposite(Composite parent, Image image, String label) {
      super(parent, SWT.NONE);
      setLayout(new GridLayout(2, false));
      setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      setBackground(Displays.getSystemColor(SWT.COLOR_LIST_BACKGROUND));

      Label text = new Label(this, SWT.WRAP);
      text.setFont(parent.getFont());
      text.setBackground(Displays.getSystemColor(SWT.COLOR_LIST_BACKGROUND));
      text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      text.setText(label);
   }
}
