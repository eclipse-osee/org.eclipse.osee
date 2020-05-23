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

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author Donald G. Dunne
 */
public class LabelComposite extends Composite {

   public LabelComposite(Composite parent, Image image, String label) {
      super(parent, SWT.BORDER);
      setLayout(new GridLayout(2, false));
      setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      setBackground(Displays.getSystemColor(SWT.COLOR_LIST_BACKGROUND));

      if (image != null) {
         Label icon = new Label(this, SWT.NONE);
         icon.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, true, false));
         icon.setBackground(Displays.getSystemColor(SWT.COLOR_LIST_BACKGROUND));
         icon.setImage(image);
      }

      Label text = new Label(this, SWT.WRAP);
      text.setFont(FontManager.getCourierNew12Bold());
      text.setBackground(Displays.getSystemColor(SWT.COLOR_LIST_BACKGROUND));
      text.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false));
      text.setText(label);
   }
}
