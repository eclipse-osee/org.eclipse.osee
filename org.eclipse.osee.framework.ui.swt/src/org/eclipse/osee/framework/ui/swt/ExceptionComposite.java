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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * @author Ryan D. Brooks
 */
public class ExceptionComposite extends Composite {

   public ExceptionComposite(Composite parent, Exception ex) {
      super(parent, SWT.NONE);
      setLayout(ALayout.getZeroMarginLayout(1, true));
      setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      createErrorArea(this, ex);
   }

   private Control createErrorArea(Composite parent, Exception ex) {
      Composite composite = new Composite(parent, SWT.BORDER);
      composite.setLayout(new GridLayout(2, false));
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      composite.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND));

      Label imageLabel = new Label(composite, SWT.NONE);
      imageLabel.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, false, true));
      Image image = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_ERROR_TSK);
      imageLabel.setImage(image);
      imageLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND));

      Text text = new Text(composite, SWT.WRAP);
      text.setFont(parent.getFont());
      text.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_RED));
      text.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
      text.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, true));
      text.setText(ex.getLocalizedMessage());
      return composite;
   }
}
