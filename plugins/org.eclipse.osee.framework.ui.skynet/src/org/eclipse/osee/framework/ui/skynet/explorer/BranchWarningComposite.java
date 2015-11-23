/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.explorer;

import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.FontManager;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author Donald G. Dunne
 */
public class BranchWarningComposite extends Composite {

   private final Label branchWarningLabel;

   public BranchWarningComposite(Composite parent) {
      super(parent, SWT.BORDER);
      setLayout(new GridLayout(2, false));
      setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      setBackground(Displays.getSystemColor(SWT.COLOR_INFO_BACKGROUND));

      Label image = new Label(this, SWT.NONE);
      image.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
      image.setImage(ImageManager.getImage(FrameworkImage.LOCKED_KEY));
      image.setBackground(Displays.getSystemColor(SWT.COLOR_INFO_BACKGROUND));

      branchWarningLabel = new Label(this, SWT.NONE);
      branchWarningLabel.setFont(FontManager.getFont("Courier New", 10, SWT.BOLD));
      branchWarningLabel.setForeground(Displays.getSystemColor(SWT.COLOR_DARK_RED));
      branchWarningLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
      branchWarningLabel.setText("None");
      branchWarningLabel.setBackground(Displays.getSystemColor(SWT.COLOR_INFO_BACKGROUND));

   }

   public void updateLabel(String warningStr) {
      if (warningStr != null) {
         branchWarningLabel.setText(warningStr);
         branchWarningLabel.update();
         update();
      }
   }

   public Label getBranchWarningLabel() {
      return branchWarningLabel;
   }

}
