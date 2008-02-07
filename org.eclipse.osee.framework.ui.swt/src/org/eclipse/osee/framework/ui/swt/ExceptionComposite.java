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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * @author Ryan D. Brooks
 */
public class ExceptionComposite extends Composite {

   /**
    * @param parent
    * @param style
    */
   public ExceptionComposite(Composite parent, Exception ex) {
      super(parent, SWT.NONE);
      setLayout(ALayout.getZeroMarginLayout(1, true));
      setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, true, true));
      Text text = new Text(this, SWT.WRAP);
      text.setText(ex.getLocalizedMessage());
      text.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, true, true));
   }
}
