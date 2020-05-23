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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * @author Ryan D. Brooks
 */
public class ExceptionComposite extends Composite {

   public ExceptionComposite(Composite parent, Exception ex) {
      this(parent, ex.toString());
   }

   public ExceptionComposite(Composite parent, String message) {
      super(parent, SWT.NONE);
      setLayout(ALayout.getZeroMarginLayout(1, true));
      setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      createErrorArea(this, message);
   }

   private Control createErrorArea(Composite parent, String message) {
      Composite composite = new Composite(parent, SWT.BORDER);
      composite.setLayout(new GridLayout(2, false));
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      composite.setBackground(Displays.getSystemColor(SWT.COLOR_LIST_BACKGROUND));

      Label imageLabel = new Label(composite, SWT.NONE);
      imageLabel.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, false, true));
      Image image = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_ERROR_TSK);
      imageLabel.setImage(image);
      imageLabel.setBackground(Displays.getSystemColor(SWT.COLOR_LIST_BACKGROUND));

      Text text = new Text(composite, SWT.WRAP);
      text.setFont(parent.getFont());
      text.setForeground(Displays.getSystemColor(SWT.COLOR_DARK_RED));
      text.setBackground(Displays.getSystemColor(SWT.COLOR_LIST_BACKGROUND));
      text.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, true));
      text.setText(message);
      return composite;
   }
}
